package xyz.nextalone.nagram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.SingleLineTransformationMethod;
import android.text.style.CharacterStyle;
import android.text.style.ParagraphStyle;
import android.text.style.ReplacementSpan;
import android.text.style.SuggestionSpan;
import android.view.Gravity;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.EditTextEffects;

import java.util.ArrayList;
import java.util.Random;

import xyz.nextalone.nagram.NaConfig;

/** Visual-only effects: keep the editable, shaping, selection and IME owned by TextView. */
public final class InputTextAnimation implements TextWatcher {
    private final EditTextEffects view;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path insertionPath = new Path();
    private final ArrayList<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    private Bitmap insertion;
    private int start, end, snapshotX, snapshotY;
    private long insertedAt;
    private float insertionStrength = 1f;
    private boolean pending, eligible;
    private String replacedText = "";
    private Bitmap replacedBitmap;
    private int replacedX, replacedY;
    private final ArrayList<GlyphRegion> replacedRegions = new ArrayList<>();

    public InputTextAnimation(EditTextEffects view) {
        this.view = view;
    }

    private float strength() {
        switch (Math.max(0, Math.min(4, NaConfig.INSTANCE.getInputAnimationStrength().Int()))) {
            case 0: return .35f;
            case 1: return .65f;
            case 3: return 1.5f;
            case 4: return 2f;
            default: return 1f;
        }
    }

    private float insertionDuration() {
        return 150f * (.7f + .3f * insertionStrength);
    }

    private boolean enabled(CharSequence text) {
        return NaConfig.INSTANCE.getInputTextAnimations().Bool()
                && view.isShown() && !view.suppressOnTextChanged
                && !view.isInputAnimationSuppressed()
                && (view.getTransformationMethod() == null
                    || view.getTransformationMethod() instanceof SingleLineTransformationMethod)
                && supportsStyles(text);
    }

    private boolean supportsStyles(CharSequence text) {
        if (!(text instanceof Spanned)) return true;
        Spanned spans = (Spanned) text;
        for (CharacterStyle style : spans.getSpans(0, text.length(), CharacterStyle.class)) {
            // IME underlines and suggestions are text decorations, not app rich text.
            if (style instanceof ReplacementSpan) return false;
            if ((spans.getSpanFlags(style) & Spanned.SPAN_COMPOSING) == 0
                    && !(style instanceof SuggestionSpan)) return false;
        }
        return spans.getSpans(0, text.length(), ParagraphStyle.class).length == 0;
    }

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        clearReplacement();
        // The custom phone keyboard suppresses business watchers during real edits.
        // This independently registered visual watcher must still observe those edits.
        eligible = enabled(text) && (view.isFocused() || view.animateInputWithoutFocus());
        if (!eligible) {
            clear();
            return;
        }
        replacedText = text.subSequence(start, start + count).toString();
        if (count > 0) {
            replacedBitmap = snapshot(start, start + count, new Path());
            if (replacedBitmap != null) {
                replacedX = view.getScrollX();
                replacedY = view.getScrollY();
                Layout layout = view.getLayout();
                float top = layoutTop(layout);
                int firstLine = layout.getLineForVertical((int) (replacedY - top));
                int lastLine = layout.getLineForVertical((int) (replacedY + view.getHeight() - top));
                int limit = Math.min(start + count, layout.getLineEnd(lastLine));
                Region viewport = new Region(replacedX, replacedY,
                        replacedX + view.getWidth(), replacedY + view.getHeight());
                for (int i = Math.max(start, layout.getLineStart(firstLine)); i < limit;) {
                    int next = Math.min(limit, i + Character.charCount(Character.codePointAt(text, i)));
                    Path glyph = new Path();
                    layout.getSelectionPath(i, next, glyph);
                    glyph.offset(view.getCompoundPaddingLeft(), top);
                    Region region = new Region();
                    region.setPath(glyph, viewport);
                    if (!region.isEmpty()) replacedRegions.add(new GlyphRegion(i - start, next - start, region));
                    i = next;
                }
            }
        }
    }

    private void createDeleteParticles(int from, int to) {
        Bitmap deleted = replacedBitmap;
        if (deleted != null && from < to) {
            float strength = strength();
            Region removed = new Region();
            for (GlyphRegion glyph : replacedRegions) {
                if (glyph.start < to && glyph.end > from) removed.op(glyph.region, Region.Op.UNION);
            }
            // Bound raster sampling and live particles for paste/clear-all and long messages.
            int step = Math.max(AndroidUtilities.dp(1), (int) Math.sqrt(deleted.getWidth() * (double) deleted.getHeight() / 4096));
            long now = SystemClock.uptimeMillis();
            for (int y = 0; y < deleted.getHeight(); y += step) {
                for (int x = 0; x < deleted.getWidth(); x += step) {
                    int color = deleted.getPixel(x, y);
                    if (!removed.contains(x + replacedX, y + replacedY)
                            || Color.alpha(color) < 48 || random.nextFloat() > .5f * strength) continue;
                    if (particles.size() >= Math.round(360 * strength)) particles.remove(0);
                    Particle p = new Particle();
                    p.x = x + replacedX;
                    p.y = y + replacedY;
                    p.strength = strength;
                    p.dx = AndroidUtilities.dpf2(random.nextFloat() * 18 - 9) * strength;
                    p.dy = AndroidUtilities.dpf2(random.nextFloat() * 18 - 9) * strength;
                    p.color = color;
                    p.time = now;
                    particles.add(p);
                }
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
        if (!eligible || !enabled(text)) {
            clear();
            return;
        }
        String replacement = text.subSequence(start, start + count).toString();
        int prefix = 0;
        while (prefix < replacedText.length() && prefix < replacement.length()) {
            int cp = replacedText.codePointAt(prefix);
            if (cp != replacement.codePointAt(prefix)) break;
            prefix += Character.charCount(cp);
        }
        int oldEnd = replacedText.length(), newEnd = replacement.length();
        while (oldEnd > prefix && newEnd > prefix) {
            int cp = replacedText.codePointBefore(oldEnd);
            if (cp != replacement.codePointBefore(newEnd)) break;
            oldEnd -= Character.charCount(cp);
            newEnd -= Character.charCount(cp);
        }
        if (prefix == oldEnd && prefix == newEnd) {
            // Phone formatting and hint refreshes can write the identical text again.
            clearReplacement();
            return;
        }
        int pendingStart = this.start, pendingEnd = end;
        boolean hadPending = pending;
        if (hadPending) {
            pendingStart = mapOffset(pendingStart, start, before, count, false);
            pendingEnd = mapOffset(pendingEnd, start, before, count, true);
        }
        clearInsertion();
        createDeleteParticles(prefix, oldEnd);
        this.start = start + prefix;
        end = start + newEnd;
        if (hadPending && pendingEnd > pendingStart) {
            this.start = Math.min(this.start, pendingStart);
            end = Math.max(end, pendingEnd);
        }
        pending = end > this.start;
        clearReplacement();
    }

    private static int mapOffset(int offset, int start, int before, int count, boolean end) {
        if (offset <= start) return offset;
        if (offset >= start + before) return offset + count - before;
        return start + (end ? count : 0);
    }

    @Override
    public void afterTextChanged(Editable text) {
        if (!enabled(text)) {
            clear();
        }
        view.invalidate();
    }

    private float layoutTop(Layout layout) {
        int top = view.getExtendedPaddingTop();
        int spare = view.getHeight() - top - view.getExtendedPaddingBottom() - layout.getHeight();
        int gravity = view.getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
        return top + (gravity == Gravity.BOTTOM ? Math.max(0, spare)
                : gravity == Gravity.CENTER_VERTICAL ? Math.max(0, spare) / 2f : 0);
    }

    private Bitmap snapshot(int from, int to, Path selection) {
        Layout layout = view.getLayout();
        if (layout == null || from < 0 || to > layout.getText().length() || from >= to
                || view.getWidth() <= 0 || view.getHeight() <= 0) return null;
        selection.reset();
        layout.getSelectionPath(from, to, selection);
        selection.offset(view.getCompoundPaddingLeft(), layoutTop(layout));
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(-view.getScrollX(), -view.getScrollY());
        canvas.clipPath(selection);
        canvas.translate(view.getCompoundPaddingLeft(), layoutTop(layout));
        layout.draw(canvas);
        return bitmap;
    }

    public void beforeDraw(Canvas canvas) {
        if (!enabled(view.getText())) clear();
        if (pending) {
            pending = false;
            insertion = snapshot(start, end, insertionPath);
            insertedAt = SystemClock.uptimeMillis();
            insertionStrength = strength();
            snapshotX = view.getScrollX();
            snapshotY = view.getScrollY();
        }
        canvas.save();
        if (insertion != null) {
            if (SystemClock.uptimeMillis() - insertedAt >= insertionDuration()
                    || snapshotX != view.getScrollX() || snapshotY != view.getScrollY()) {
                clearInsertion();
            } else {
                canvas.clipPath(insertionPath, Region.Op.DIFFERENCE);
            }
        }
    }

    public void afterDraw(Canvas canvas) {
        canvas.restore();
        long now = SystemClock.uptimeMillis();
        if (insertion != null) {
            float t = Math.min(1f, (now - insertedAt) / insertionDuration());
            float progress = 1 - (1 - t) * (1 - t);
            paint.setColor(Color.WHITE);
            paint.setAlpha((int) (255 * progress));
            canvas.drawBitmap(insertion, snapshotX,
                    snapshotY + (1 - progress) * view.getTextSize() * .65f * insertionStrength, paint);
        }
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            float t = (now - p.time) / (240f * (.7f + .3f * p.strength));
            if (t >= 1) {
                particles.remove(i);
                continue;
            }
            float progress = 1 - (1 - t) * (1 - t);
            paint.setColor(p.color);
            paint.setAlpha((int) (Color.alpha(p.color) * (1 - t) * (1 - t)));
            float radius = AndroidUtilities.dpf2(.85f) * (.6f + .4f * p.strength) * (1 - t * .55f);
            float x = p.x + p.dx * progress, y = p.y + p.dy * progress;
            canvas.drawRect(x - radius, y - radius, x + radius, y + radius, paint);
        }
        if (insertion != null || !particles.isEmpty()) view.postInvalidateOnAnimation();
    }

    private void clearInsertion() {
        if (insertion != null) insertion.recycle();
        insertion = null;
        pending = false;
    }

    public void clear() {
        clearInsertion();
        clearReplacement();
        particles.clear();
    }

    private void clearReplacement() {
        if (replacedBitmap != null) replacedBitmap.recycle();
        replacedBitmap = null;
        replacedText = "";
        replacedRegions.clear();
    }

    private static final class GlyphRegion {
        final int start, end;
        final Region region;

        GlyphRegion(int start, int end, Region region) {
            this.start = start;
            this.end = end;
            this.region = region;
        }
    }

    private static final class Particle {
        float x, y, dx, dy, strength;
        int color;
        long time;
    }
}
