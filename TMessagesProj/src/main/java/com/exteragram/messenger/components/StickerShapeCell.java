/*

 This is the source code of exteraGram for Android.

 We do not and cannot prevent the use of our code,
 but be respectful and credit the original author.

 Copyright @immat0x1, 2022.

*/

package com.exteragram.messenger.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

import com.exteragram.messenger.ExteraConfig;

public class StickerShapeCell extends LinearLayout {

    private class StickerShape extends FrameLayout {

        private RadioButton button;
        private boolean isRounded;
        private boolean isRoundedAsMsg;
        private RectF rect = new RectF();
        private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        public StickerShape(Context context, boolean rounded, boolean roundedAsMsg) {
            super(context);
            setWillNotDraw(false);

            isRounded = rounded;
            isRoundedAsMsg = roundedAsMsg;

            textPaint.setTextSize(AndroidUtilities.dp(13));

            button = new RadioButton(context) {
                @Override
                public void invalidate() {
                    super.invalidate();
                    StickerShape.this.invalidate();
                }
            };
            button.setSize(AndroidUtilities.dp(20));
            addView(button, LayoutHelper.createFrame(22, 22, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0, 0, 40));
            button.setChecked(!isRounded && !isRoundedAsMsg && ExteraConfig.stickerShape == 0 || isRounded && ExteraConfig.stickerShape == 1 || isRoundedAsMsg && ExteraConfig.stickerShape == 2, false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int color = Theme.getColor(Theme.key_switchTrack);
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);

            button.setColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_radioBackgroundChecked));

            rect.set(AndroidUtilities.dp(1), AndroidUtilities.dp(1), getMeasuredWidth() - AndroidUtilities.dp(1), AndroidUtilities.dp(100));
            Theme.chat_instantViewRectPaint.setColor(Color.argb((int) (43 * button.getProgress()), r, g, b));
            canvas.drawRoundRect(rect, AndroidUtilities.dp(6), AndroidUtilities.dp(6), Theme.chat_instantViewRectPaint);

            rect.set(0, 0, getMeasuredWidth(), AndroidUtilities.dp(100));
            Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) (31 * (1.0f - button.getProgress())), r, g, b));
            canvas.drawRoundRect(rect, AndroidUtilities.dp(6), AndroidUtilities.dp(6), Theme.dialogs_onlineCirclePaint);

            rect.set(AndroidUtilities.dp(10), AndroidUtilities.dp(7), getMeasuredWidth() - AndroidUtilities.dp(10), getMeasuredHeight() - AndroidUtilities.dp(70));

            String text;
            if (isRounded) {
                text = LocaleController.getString("StickerShapeRounded", R.string.StickerShapeRounded);
            } else if (isRoundedAsMsg) {
                text = LocaleController.getString("StickerShapeRoundedMsg", R.string.StickerShapeRoundedMsg);
            } else {
                text = LocaleController.getString("Default", R.string.Default);
            }
            int width = (int) Math.ceil(textPaint.measureText(text));

            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            canvas.drawText(text, (getMeasuredWidth() - width) / 2, AndroidUtilities.dp(120), textPaint);

            Theme.dialogs_onlineCirclePaint.setColor(Color.argb(90, r, g, b));
            if (!isRounded && !isRoundedAsMsg) {
                canvas.drawRoundRect(rect, AndroidUtilities.dp(0), AndroidUtilities.dp(0), Theme.dialogs_onlineCirclePaint);
            } else if (isRounded) {
                canvas.drawRoundRect(rect, AndroidUtilities.dp(6), AndroidUtilities.dp(6), Theme.dialogs_onlineCirclePaint);
            } else if (isRoundedAsMsg) {
                canvas.drawRoundRect(rect, AndroidUtilities.dp(SharedConfig.bubbleRadius), AndroidUtilities.dp(SharedConfig.bubbleRadius), Theme.dialogs_onlineCirclePaint);
            }
        }
    }

    private StickerShape[] stickerShape = new StickerShape[3];

    public StickerShapeCell(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setPadding(AndroidUtilities.dp(13), AndroidUtilities.dp(10), AndroidUtilities.dp(13), 0);

        for (int a = 0; a < 3; a++) {
            boolean rounded = a == 1;
            boolean roundedAsMsg = a == 2;
            stickerShape[a] = new StickerShape(context, rounded, roundedAsMsg);
            addView(stickerShape[a], LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 0.5f, 8, 0, 8, 0));
            stickerShape[a].setOnClickListener(v -> {
                for (int b = 0; b < 3; b++) {
                    stickerShape[b].button.setChecked(stickerShape[b] == v, true);
                }
                ExteraConfig.setStickerShape(rounded ? 1 : (roundedAsMsg ? 2 : 0));
                updateStickerPreview();
            });
        }
    }

    protected void updateStickerPreview() {};

    @Override
    public void invalidate() {
        super.invalidate();
        for (int a = 0; a < 3; a++) {
            stickerShape[a].invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(143), MeasureSpec.EXACTLY));
    }
}
