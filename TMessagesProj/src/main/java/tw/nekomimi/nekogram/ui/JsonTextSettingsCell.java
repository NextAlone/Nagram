package tw.nekomimi.nekogram.ui;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.EmojiTextView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.utils.code.highlight.CodeHighlighting;

public class JsonTextSettingsCell extends TextDetailSettingsCell {

    public interface FullJsonProvider {
        String getFullJson();
        void onFullJsonCopied();
    }

    private FullJsonProvider fullJsonProvider;

    private static final int MAX_POLL_RETRIES = 60;
    private static final long POLL_INTERVAL_MS = 100L;

    private final EmojiTextView jsonTextView;
    private boolean needDivider;
    private int currentRequestId;
    private String currentChunkText;
    private SpannableString currentSpannable;
    private boolean highlightApplied;
    private Runnable pendingPoll;

    public JsonTextSettingsCell(Context context) {
        super(context);

        jsonTextView = new EmojiTextView(context);
        jsonTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        jsonTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        jsonTextView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        jsonTextView.setLines(0);
        jsonTextView.setMaxLines(0);
        jsonTextView.setSingleLine(false);
        jsonTextView.setTextIsSelectable(true);
        jsonTextView.setIncludeFontPadding(false);
        jsonTextView.setPadding(0, 0, 0, AndroidUtilities.dp(4));
        jsonTextView.setTypeface(android.graphics.Typeface.MONOSPACE);
        jsonTextView.setCustomSelectionActionModeCallback(buildSelectionActionModeCallback());

        getTextView().setVisibility(GONE);
        getValueTextView().setVisibility(GONE);

        addView(jsonTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 21, 4, 21, 0));

        setClickable(false);
        setFocusable(false);
    }

    public void setJsonChunk(String rawChunk, SpannableString cached, boolean divider, java.util.Map<String, SpannableString> cacheStore) {
        needDivider = divider;
        setWillNotDraw(!divider);

        cancelPendingPoll();

        if (rawChunk == null || rawChunk.isEmpty()) {
            currentRequestId++;
            currentChunkText = null;
            currentSpannable = null;
            highlightApplied = false;
            jsonTextView.setText("");
            return;
        }

        if (cached != null) {
            currentRequestId++;
            currentChunkText = rawChunk;
            currentSpannable = cached;
            highlightApplied = true;
            jsonTextView.setText(cached);
            return;
        }

        final int requestId = ++currentRequestId;
        currentChunkText = rawChunk;
        currentSpannable = null;
        highlightApplied = false;
        jsonTextView.setText(rawChunk);

        final java.util.Map<String, SpannableString> store = cacheStore;
        Utilities.globalQueue.postRunnable(() -> {
            SpannableString lockedResult;
            try {
                lockedResult = CodeHighlighting.getHighlighted(rawChunk, "json");
            } catch (Throwable ignored) {
                lockedResult = new SpannableString(rawChunk);
            }
            final SpannableString lockedSpannable = lockedResult;
            AndroidUtilities.runOnUIThread(() -> {
                if (requestId != currentRequestId) {
                    return;
                }
                schedulePollAndApply(requestId, rawChunk, lockedSpannable, store, 0);
            });
        });
    }

    private void schedulePollAndApply(int requestId, String rawChunk, SpannableString lockedSpannable, java.util.Map<String, SpannableString> store, int attempt) {
        if (requestId != currentRequestId) {
            return;
        }
        if (hasAnyColorSpan(lockedSpannable)) {
            SpannableString stable = copyToStableSpannable(rawChunk, lockedSpannable);
            currentSpannable = stable;
            highlightApplied = true;
            jsonTextView.setText(stable);
            if (store != null) {
                store.put(rawChunk, stable);
            }
            return;
        }
        if (attempt >= MAX_POLL_RETRIES) {
            currentSpannable = lockedSpannable;
            jsonTextView.setText(lockedSpannable);
            return;
        }
        pendingPoll = () -> {
            pendingPoll = null;
            schedulePollAndApply(requestId, rawChunk, lockedSpannable, store, attempt + 1);
        };
        AndroidUtilities.runOnUIThread(pendingPoll, POLL_INTERVAL_MS);
    }

    private void cancelPendingPoll() {
        if (pendingPoll != null) {
            AndroidUtilities.cancelRunOnUIThread(pendingPoll);
            pendingPoll = null;
        }
    }

    public void setJsonChunkAsync(String rawChunk, boolean divider) {
        setJsonChunk(rawChunk, null, divider, null);
    }

    public void refreshHighlighting() {
        jsonTextView.invalidate();
    }

    public void cacheIfReady(java.util.Map<String, SpannableString> store) {
        if (store == null || currentSpannable == null || currentChunkText == null || !highlightApplied) {
            return;
        }
        store.put(currentChunkText, currentSpannable);
    }

    private static boolean hasAnyColorSpan(SpannableString s) {
        if (s == null) return false;
        try {
            Object[] spans = s.getSpans(0, s.length(), CodeHighlighting.ColorSpan.class);
            return spans != null && spans.length > 0;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static SpannableString copyToStableSpannable(String rawText, SpannableString source) {
        SpannableString dst = new SpannableString(rawText);
        try {
            CodeHighlighting.ColorSpan[] spans = source.getSpans(0, source.length(), CodeHighlighting.ColorSpan.class);
            if (spans != null) {
                for (CodeHighlighting.ColorSpan span : spans) {
                    int start = source.getSpanStart(span);
                    int end = source.getSpanEnd(span);
                    if (start < 0 || end < 0 || start >= end) continue;
                    if (end > dst.length()) end = dst.length();
                    dst.setSpan(new CodeHighlighting.ColorSpan(span.group), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        } catch (Throwable ignored) {
        }
        return dst;
    }

    public void setTitle(CharSequence title) {
        if (title == null) {
            getTextView().setVisibility(GONE);
            android.view.ViewGroup.LayoutParams params = jsonTextView.getLayoutParams();
            if (params instanceof android.widget.FrameLayout.LayoutParams) {
                android.widget.FrameLayout.LayoutParams layoutParams = (android.widget.FrameLayout.LayoutParams) params;
                layoutParams.topMargin = AndroidUtilities.dp(4);
                jsonTextView.setLayoutParams(layoutParams);
            }
            return;
        }
        getTextView().setText(title);
        getTextView().setVisibility(VISIBLE);

        android.view.ViewGroup.LayoutParams params = jsonTextView.getLayoutParams();
        if (params instanceof android.widget.FrameLayout.LayoutParams) {
            android.widget.FrameLayout.LayoutParams layoutParams = (android.widget.FrameLayout.LayoutParams) params;
            layoutParams.topMargin = AndroidUtilities.dp(35);
            jsonTextView.setLayoutParams(layoutParams);
        }
    }

    public void setChunkLayout(boolean isFirstChunk, boolean isLastChunk) {
        int topMargin;
        if (isFirstChunk) {
            topMargin = getTextView().getVisibility() == VISIBLE ? AndroidUtilities.dp(35) : AndroidUtilities.dp(4);
        } else {
            topMargin = 0;
        }
        android.view.ViewGroup.LayoutParams params = jsonTextView.getLayoutParams();
        if (params instanceof android.widget.FrameLayout.LayoutParams) {
            android.widget.FrameLayout.LayoutParams layoutParams = (android.widget.FrameLayout.LayoutParams) params;
            layoutParams.topMargin = topMargin;
            jsonTextView.setLayoutParams(layoutParams);
        }
        int bottomPadding = isLastChunk ? AndroidUtilities.dp(4) : 0;
        jsonTextView.setPadding(
                jsonTextView.getPaddingLeft(),
                jsonTextView.getPaddingTop(),
                jsonTextView.getPaddingRight(),
                bottomPadding
        );
        int cellTop = isFirstChunk ? AndroidUtilities.dp(0) : 0;
        int cellBottom = isLastChunk ? AndroidUtilities.dp(0) : 0;
        setMinimumHeight(0);
        setPadding(getPaddingLeft(), cellTop, getPaddingRight(), cellBottom);
    }

    public void setFullJsonProvider(FullJsonProvider provider) {
        this.fullJsonProvider = provider;
    }

    private ActionMode.Callback buildSelectionActionModeCallback() {
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                if (fullJsonProvider != null && menu.findItem(R_ID_COPY_ALL_JSON) == null) {
                    String label = LocaleController.getString(org.telegram.messenger.R.string.Copy)
                            + " · "
                            + LocaleController.getString(org.telegram.messenger.R.string.ExportAsJson);
                    menu.add(Menu.NONE, R_ID_COPY_ALL_JSON, Menu.NONE, label);
                }
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R_ID_COPY_ALL_JSON) {
                    if (fullJsonProvider != null) {
                        String full = fullJsonProvider.getFullJson();
                        if (full != null && !full.isEmpty()) {
                            try {
                                AndroidUtilities.addToClipboard(full);
                                fullJsonProvider.onFullJsonCopied();
                            } catch (Throwable ignored) {
                            }
                        }
                    }
                    mode.finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        };
    }

    private static final int R_ID_COPY_ALL_JSON = View.generateViewId();

    @Override
    public void invalidate() {
        super.invalidate();
        jsonTextView.invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelPendingPoll();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }
}
