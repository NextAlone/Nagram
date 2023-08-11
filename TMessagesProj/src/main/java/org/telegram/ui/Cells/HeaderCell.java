/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

public class HeaderCell extends LinearLayout {

    private TextView textView;
    private TextView textView2;
    private int height = 40;
    private final Theme.ResourcesProvider resourcesProvider;

    public HeaderCell(Context context) {
        this(context, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, false, null);
    }

    public HeaderCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, false, resourcesProvider);
    }

    public HeaderCell(Context context, int padding) {
        this(context, Theme.key_windowBackgroundWhiteBlueHeader, padding, 15, false, null);
    }

    public HeaderCell(Context context, int padding, Theme.ResourcesProvider resourcesProvider) {
        this(context, Theme.key_windowBackgroundWhiteBlueHeader, padding, 15, false, resourcesProvider);
    }

    public HeaderCell(Context context, int textColorKey, int padding, int topMargin, boolean text2) {
        this(context, textColorKey, padding, topMargin, text2, null);
    }

    public HeaderCell(Context context, int textColorKey, int padding, int topMargin, boolean text2, Theme.ResourcesProvider resourcesProvider) {
        this(context, textColorKey, padding, topMargin, 0, text2, resourcesProvider);
    }

    public HeaderCell(Context context, int textColorKey, int padding, int topMargin, int bottomMargin, boolean text2, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;

        setOrientation(LinearLayout.VERTICAL);
        setPadding(AndroidUtilities.dp(padding), AndroidUtilities.dp(topMargin), AndroidUtilities.dp(padding), 0);

        textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        textView.setTextColor(getThemedColor(textColorKey));
        textView.setTag(textColorKey);
//        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, padding, topMargin, padding, text2 ? 0 : bottomMargin));
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        textView2 = new TextView(getContext());
        textView2.setTextSize(13);
        textView2.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        textView2.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        addView(textView2, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 4, 0, bottomMargin));

        if (!text2) textView2.setVisibility(View.GONE);

        ViewCompat.setAccessibilityHeading(this, true);
    }

    // NekoX: BottomSheet BigTitle, move big title from constructor to here
    public HeaderCell setBigTitle(boolean enabled) {
        if (enabled) {
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/mw_bold.ttf"));
        } else {
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        }
        return this;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        params.width = -1;
        super.setLayoutParams(params);
    }

    public void setHeight(int value) {
        textView.setMinHeight(AndroidUtilities.dp(value) - ((LayoutParams) textView.getLayoutParams()).topMargin);
    }

    public void setTopMargin(int topMargin) {
        ((LayoutParams) textView.getLayoutParams()).topMargin = AndroidUtilities.dp(topMargin);
        setHeight(height);
    }

    public void setBottomMargin(int bottomMargin) {
        ((LayoutParams) textView.getLayoutParams()).bottomMargin = AndroidUtilities.dp(bottomMargin);
        if (textView2 != null) {
            ((LayoutParams) textView2.getLayoutParams()).bottomMargin = AndroidUtilities.dp(bottomMargin);
        }
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        if (animators != null) {
            animators.add(ObjectAnimator.ofFloat(textView, View.ALPHA, value ? 1.0f : 0.5f));
        } else {
            textView.setAlpha(value ? 1.0f : 0.5f);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    public void setTextSize(float dip) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dip);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setText(CharSequence text) {
        textView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        textView.setText(text);
    }

    public void setText2(CharSequence text) {
        if (textView2.getVisibility() != View.VISIBLE) {
            textView2.setVisibility(View.VISIBLE);
        }
        textView2.setText(text);
    }

    public TextView getTextView() {
        return textView;
    }

    public TextView getTextView2() {
        return textView2;
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.setHeading(true);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AccessibilityNodeInfo.CollectionItemInfo collection = info.getCollectionItemInfo();
            if (collection != null) {
                info.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(collection.getRowIndex(), collection.getRowSpan(), collection.getColumnIndex(), collection.getColumnSpan(), true));
            }
        }
        info.setEnabled(true);
    }

    private int getThemedColor(int key) {
        return Theme.getColor(key, resourcesProvider);
    }
}
