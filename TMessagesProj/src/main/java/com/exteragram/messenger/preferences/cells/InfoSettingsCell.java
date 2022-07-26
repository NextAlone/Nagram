/*
 * This is the source code of exteraGram for Android (8.6.2)
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright @immat0x1, 2022
 */

package com.exteragram.messenger.preferences.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

import com.exteragram.messenger.ExteraUtils;

public class InfoSettingsCell extends FrameLayout {

    private TextView textView;
    private TextView valueTextView;
    private ImageView imageView;

    public InfoSettingsCell(Context context) {
        super(context);

        textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rregular.ttf"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        textView.setText(ExteraUtils.getAppName() + " | v" + BuildVars.BUILD_VERSION_STRING);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setPadding(0, 0, 0, 0);
        textView.setGravity(Gravity.CENTER);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER | Gravity.TOP, 50, 148, 50, 0));

        valueTextView = new TextView(context);
        valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rregular.ttf"));
        valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        valueTextView.setText(LocaleController.getString("AboutExteraDescription", R.string.AboutExteraDescription));
        valueTextView.setGravity(Gravity.CENTER);
        valueTextView.setLines(0);
        valueTextView.setMaxLines(0);
        valueTextView.setSingleLine(false);
        valueTextView.setPadding(0, 0, 0, 0);
        addView(valueTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER | Gravity.TOP, 60, 178, 60, 20));

        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(R.drawable.ic_logo_foreground);
        addView(imageView, LayoutHelper.createFrame(108, 108, Gravity.CENTER | Gravity.TOP, 0, 20, 0, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    @Override
    public void invalidate() {
        super.invalidate();
        textView.invalidate();
    }
}
