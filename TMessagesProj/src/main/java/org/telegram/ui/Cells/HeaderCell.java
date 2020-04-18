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
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

public class HeaderCell extends LinearLayout {

    private TextView textView;
    private TextView textView2;
    private int height = 40;

    public HeaderCell(Context context) {
        this(context, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, false);
    }

    public HeaderCell(Context context, int padding) {
        this(context, Theme.key_windowBackgroundWhiteBlueHeader, padding, 15, false);
    }

    public HeaderCell(Context context, String textColorKey, int padding, int topMargin, boolean text2) {
        super(context);

        setOrientation(LinearLayout.VERTICAL);
        setPadding(AndroidUtilities.dp(padding), AndroidUtilities.dp(topMargin), AndroidUtilities.dp(padding), 0);

        textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        textView.setMinHeight(AndroidUtilities.dp(height - topMargin));
        textView.setTextColor(Theme.getColor(textColorKey));
        textView.setTag(textColorKey);
        addView(textView, LayoutHelper.createLinear(-1, -2));

        textView2 = new TextView(getContext());
        textView2.setTextSize(13);
        textView2.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        addView(textView2, LayoutHelper.createLinear(-2, -2, 0, 4, 0, 0));

        if (!text2) textView2.setVisibility(View.GONE);

    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        params.width = -1;
        super.setLayoutParams(params);
    }

    public void setHeight(int value) {
        textView.setMinHeight(AndroidUtilities.dp(height = value) - ((LayoutParams) textView.getLayoutParams()).topMargin);
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        if (animators != null) {
            animators.add(ObjectAnimator.ofFloat(textView, "alpha", value ? 1.0f : 0.5f));
        } else {
            textView.setAlpha(value ? 1.0f : 0.5f);
        }
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setText2(String text) {
        if (textView2.getVisibility() != View.VISIBLE) {
            textView2.setVisibility(View.VISIBLE);
        }
        textView2.setText(text);
    }

    public TextView getTextView2() {
        return textView2;
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AccessibilityNodeInfo.CollectionItemInfo collection = info.getCollectionItemInfo();
            if (collection != null) {
                info.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(collection.getRowIndex(), collection.getRowSpan(), collection.getColumnIndex(), collection.getColumnSpan(), true));
            }
        }
    }
}
