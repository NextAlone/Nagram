/*
 * This is the source code of Nagram.
 * It is licensed under GNU GPL v. 2 or later.
 *
 * Lyrics view for the in-app audio player.
 *
 * Renders the lyrics parsed by LyricsHelper (timed LRC or plain text) and:
 * - highlights the line matching the current playback position,
 * - auto-scrolls to keep the current line centered,
 * - supports touch drag to browse and tap to expand/collapse.
 */

package xyz.nextalone.nagram.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import org.telegram.messenger.AndroidUtilities;

import java.util.ArrayList;
import java.util.List;

import xyz.nextalone.nagram.helper.LyricsHelper;

public class LyricsView extends View {

    /** Line height used both for drawing and for computing the adaptive container height. */
    public static final float LINE_HEIGHT_DP = 30;

    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint highlightPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint.FontMetrics fontMetrics = new Paint.FontMetrics();

    private final Typeface normalTypeface;
    private final Typeface boldTypeface;

    private final List<LyricsHelper.LyricsLine> lines = new ArrayList<>();
    private boolean isSynced;
    private boolean sortedLines;

    private int currentIndex = -1;
    private float scrollOffset;
    private float targetOffset;
    private ValueAnimator scrollAnimator;

    private final float lineHeight;
    private final float textSize;
    private int textColor = 0xFF8A8A8A;
    private int highlightColor = 0xFF000000;

    private Runnable onLyricsClickListener;

    private float lastTouchX;
    private float lastTouchY;
    private long lastTouchTime;
    private boolean dragging;
    private float dragStartOffset;

    public LyricsView(Context context) {
        super(context);
        lineHeight = AndroidUtilities.dp(LINE_HEIGHT_DP);
        textSize = AndroidUtilities.dp(14);
        normalTypeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        boldTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(normalTypeface);
        highlightPaint.setTextSize(textSize);
        highlightPaint.setTypeface(boldTypeface);
        setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    public void setColors(int textColor, int highlightColor) {
        this.textColor = textColor;
        this.highlightColor = highlightColor;
        textPaint.setColor(textColor);
        highlightPaint.setColor(highlightColor);
        invalidate();
    }

    public void setOnLyricsClickListener(Runnable listener) {
        this.onLyricsClickListener = listener;
    }

    public void setLyrics(LyricsHelper.LyricsData data) {
        lines.clear();
        isSynced = false;
        sortedLines = true;
        currentIndex = -1;
        scrollOffset = 0;
        targetOffset = 0;
        dragging = false;
        if (scrollAnimator != null) {
            scrollAnimator.cancel();
            scrollAnimator = null;
        }
        if (data != null && data.lines != null) {
            lines.addAll(data.lines);
            isSynced = data.isSynced;
            long prev = Long.MIN_VALUE;
            for (LyricsHelper.LyricsLine line : lines) {
                if (line.timeMs < prev) {
                    sortedLines = false;
                    break;
                }
                prev = line.timeMs;
            }
        }
        invalidate();
    }

    public int getLineCount() {
        return lines.size();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (scrollAnimator != null) {
            scrollAnimator.cancel();
            scrollAnimator = null;
        }
    }

    public void setProgress(float progressSec) {
        if (!isSynced || lines.isEmpty()) {
            return;
        }
        long timeMs = (long) (progressSec * 1000);
        int index;
        if (sortedLines) {
            // Lines are sorted by timeMs (unsynced lines with timeMs < 0 first);
            // binary search for the last line whose timeMs is <= the current position.
            int start = 0;
            while (start < lines.size() && lines.get(start).timeMs < 0) {
                start++;
            }
            int lo = start;
            int hi = lines.size() - 1;
            index = -1;
            while (lo <= hi) {
                int mid = (lo + hi) >>> 1;
                if (lines.get(mid).timeMs <= timeMs) {
                    index = mid;
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
        } else {
            // Unsorted input (e.g. hand-built LyricsData): fall back to a linear scan.
            index = -1;
            for (int i = 0; i < lines.size(); i++) {
                LyricsHelper.LyricsLine line = lines.get(i);
                if (line.timeMs >= 0 && line.timeMs <= timeMs) {
                    index = i;
                }
            }
        }
        if (index != currentIndex) {
            currentIndex = index;
            if (dragging) {
                // User is browsing manually: update the highlight only, do not force scroll back
                invalidate();
                return;
            }
            int h = getMeasuredHeight();
            if (h > 0) {
                targetOffset = currentIndex * lineHeight - (h - lineHeight) / 2f;
                if (targetOffset < 0) {
                    targetOffset = 0;
                }
                animateScrollTo(targetOffset);
            }
            invalidate();
        }
    }

    private float getMaxScrollOffset() {
        return Math.max(0, lines.size() * lineHeight - getMeasuredHeight() + lineHeight);
    }

    private void animateScrollTo(float target) {
        if (scrollAnimator != null) {
            scrollAnimator.cancel();
            scrollAnimator = null;
        }
        final float clamped = Math.max(0, Math.min(target, getMaxScrollOffset()));
        final float from = scrollOffset;
        if (Math.abs(clamped - from) < 1f) {
            scrollOffset = clamped;
            return;
        }
        scrollAnimator = ValueAnimator.ofFloat(0f, 1f);
        scrollAnimator.setDuration(220);
        scrollAnimator.setInterpolator(new DecelerateInterpolator());
        scrollAnimator.addUpdateListener(a -> {
            float t = (float) a.getAnimatedValue();
            scrollOffset = from + (clamped - from) * t;
            invalidate();
        });
        scrollAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (lines.isEmpty()) {
            return;
        }
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        float offset = Math.max(0, Math.min(scrollOffset, getMaxScrollOffset()));
        int startLine = (int) Math.max(0, Math.floor(offset / lineHeight) - 1);
        int endLine = (int) Math.min(lines.size() - 1, Math.ceil((offset + h) / lineHeight) + 1);
        // The text area is the view width minus its padding; the container reserves the right
        // padding for the expand button, so this stays in sync with the actual layout instead
        // of duplicating layout-specific numbers here.
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int contentWidth = w - paddingLeft - paddingRight;
        for (int i = startLine; i <= endLine; i++) {
            LyricsHelper.LyricsLine line = lines.get(i);
            if (TextUtils.isEmpty(line.text)) {
                continue;
            }
            boolean isCurrent = isSynced && i == currentIndex;
            TextPaint paint = isCurrent ? highlightPaint : textPaint;
            String text = ellipsize(line.text, paint, contentWidth);
            if (text.isEmpty()) {
                continue;
            }
            float lineCenterY = i * lineHeight - offset + lineHeight / 2f;
            paint.getFontMetrics(fontMetrics);
            float baseline = lineCenterY - (fontMetrics.ascent + fontMetrics.descent) / 2f;
            float textWidth = paint.measureText(text);
            // Center within the padded content area.
            float centerX = paddingLeft + (contentWidth - textWidth) / 2f;
            canvas.drawText(text, centerX, baseline, paint);
        }
    }

    private static String ellipsize(String text, TextPaint paint, int maxWidth) {
        if (maxWidth <= 0) {
            return "";
        }
        if (paint.measureText(text) <= maxWidth) {
            return text;
        }
        CharSequence ellipsized = TextUtils.ellipsize(text, paint, maxWidth, TextUtils.TruncateAt.END);
        return ellipsized.toString();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                lastTouchTime = System.currentTimeMillis();
                dragging = false;
                dragStartOffset = scrollOffset;
                return true;
            case MotionEvent.ACTION_MOVE: {
                float dy = event.getY() - lastTouchY;
                if (!dragging && Math.abs(dy) > AndroidUtilities.dp(4)) {
                    dragging = true;
                    // Grab the parent's gesture only once an actual drag starts, so taps on the
                    // lyrics area (expand/collapse) still pass through as clicks.
                    getParent().requestDisallowInterceptTouchEvent(true);
                    if (scrollAnimator != null) {
                        scrollAnimator.cancel();
                        scrollAnimator = null;
                    }
                }
                if (dragging) {
                    float maxOffset = getMaxScrollOffset();
                    scrollOffset = Math.max(0, Math.min(maxOffset, dragStartOffset - (event.getY() - lastTouchY)));
                    invalidate();
                    return true;
                }
                return true;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                boolean wasDragging = dragging;
                if (dragging) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                dragging = false;
                boolean isClick = !wasDragging
                        && System.currentTimeMillis() - lastTouchTime < 400
                        && Math.abs(event.getX() - lastTouchX) < AndroidUtilities.dp(12)
                        && Math.abs(event.getY() - lastTouchY) < AndroidUtilities.dp(12);
                if (isClick) {
                    if (onLyricsClickListener != null) {
                        onLyricsClickListener.run();
                    }
                    return true;
                }
                if (isSynced && currentIndex >= 0) {
                    int h = getMeasuredHeight();
                    targetOffset = currentIndex * lineHeight - (h - lineHeight) / 2f;
                    if (targetOffset < 0) {
                        targetOffset = 0;
                    }
                    animateScrollTo(targetOffset);
                }
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}
