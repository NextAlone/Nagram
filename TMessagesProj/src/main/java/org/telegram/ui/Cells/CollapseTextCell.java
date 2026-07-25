package org.telegram.ui.Cells;

import static org.telegram.messenger.AndroidUtilities.dp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;

@SuppressLint("ViewConstructor")
public class CollapseTextCell extends FrameLayout {

    public final AnimatedTextView textView;
    private View collapsedArrow;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean centered;
    private Boolean collapsed;

    @SuppressLint("UseCompatLoadingForDrawables")
    public CollapseTextCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;

        textView = new AnimatedTextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        textView.setTextSize(dp(14));
        textView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        textView.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        textView.setOnWidthUpdatedListener(this::updateCollapseArrowTranslation);
        addView(textView, LayoutHelper.createFrameRelatively(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL, 21, 0, 38, 3));

        collapsedArrow = new View(context);
        Drawable drawable = getContext().getResources().getDrawable(R.drawable.arrow_more).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider), PorterDuff.Mode.MULTIPLY));
        collapsedArrow.setBackground(drawable);
        addView(collapsedArrow, LayoutHelper.createFrameRelatively(14, 14, Gravity.START | Gravity.CENTER_VERTICAL, 21, 1, 0, 3));
    }

    public void set(CharSequence text, boolean collapsed) {
        textView.setText(text);
        collapsedArrow.animate().cancel();
        final float rotation = collapsed ? 0 : 180;
        if (this.collapsed != null && this.collapsed != collapsed && isLaidOut() && isAttachedToWindow()) {
            collapsedArrow.animate().rotation(rotation).setDuration(340).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
        } else {
            collapsedArrow.setRotation(rotation);
        }
        this.collapsed = collapsed;
    }

    public void setColor(int colorKey) {
        int color = Theme.getColor(colorKey, resourcesProvider);
        textView.setTextColor(color);
        collapsedArrow.getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
    }

    public void setCentered(boolean centered) {
        if (this.centered == centered) {
            return;
        }
        this.centered = centered;
        if (centered) {
            textView.setLayoutParams(LayoutHelper.createFrame(
                    LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                    Gravity.CENTER, 0, 0, 0, 3));
            collapsedArrow.setLayoutParams(LayoutHelper.createFrame(
                    14, 14, Gravity.CENTER, 0, 1, 0, 3));
        } else {
            textView.setLayoutParams(LayoutHelper.createFrameRelatively(
                    LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                    Gravity.START | Gravity.CENTER_VERTICAL, 21, 0, 38, 3));
            collapsedArrow.setLayoutParams(LayoutHelper.createFrameRelatively(
                    14, 14, Gravity.START | Gravity.CENTER_VERTICAL, 21, 1, 0, 3));
        }
        updateCollapseArrowTranslation();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(dp(46), MeasureSpec.EXACTLY));
        updateCollapseArrowTranslation();
    }

    private void updateCollapseArrowTranslation() {
        float textWidth = textView.getDrawable().getCurrentWidth();

        if (centered) {
            float direction = LocaleController.isRTL ? -1 : 1;
            textView.setTranslationX(-direction * (dp(14) + dp(1)) / 2f);
            collapsedArrow.setTranslationX(direction * (textWidth + dp(1)) / 2f);
            return;
        }
        textView.setTranslationX(0);
        float translateX = textWidth + dp(1);
        if (LocaleController.isRTL) {
            collapsedArrow.setTranslationX(-translateX);
        } else {
            collapsedArrow.setTranslationX(translateX);
        }
    }
}
