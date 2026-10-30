package tw.nekomimi.nekogram.settings.cell;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextPaint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.core.graphics.ColorUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ScaleStateListAnimator;

import xyz.nextalone.nagram.NaConfig;

@SuppressLint("ViewConstructor")
public class StickerShapeCell extends LinearLayout {

    private final StickerShape[] stickerShape = new StickerShape[3];
    private final Runnable onShapeChanged;

    public StickerShapeCell(Context context, Runnable onShapeChanged, Runnable longClickListener) {
        super(context);
        this.onShapeChanged = onShapeChanged;
        setOrientation(HORIZONTAL);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        setPadding(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(13.0f), 0);

        for (int i = 0; i < 3; i++) {
            final int index = i;
            boolean isRounded = i == 1;
            boolean isRoundedAsMsg = i == 2;
            stickerShape[i] = new StickerShape(context, isRounded, isRoundedAsMsg);
            ScaleStateListAnimator.apply(stickerShape[i], 0.03f, 1.5f);
            addView(stickerShape[i], LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1.0f / 3.0f, 8, 0, 8, 0));
            stickerShape[i].setOnClickListener(v -> {
                for (int j = 0; j < 3; j++) {
                    stickerShape[j].setSelected(j == index, true);
                }
                NaConfig.INSTANCE.getStickerShape().setConfigInt(index);
                if (this.onShapeChanged != null) {
                    this.onShapeChanged.run();
                }
            });
            stickerShape[i].setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.run();
                    return true;
                }
                return false;
            });
        }
    }

    public void updateSelection() {
        int current = NaConfig.INSTANCE.getStickerShape().Int();
        for (int i = 0; i < 3; i++) {
            if (stickerShape[i] != null) {
                stickerShape[i].setSelected(i == current, false);
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        for (int i = 0; i < 3; i++) {
            if (stickerShape[i] != null) {
                stickerShape[i].invalidate();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(
                View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(130.0f), View.MeasureSpec.EXACTLY)
        );
    }

    public static class StickerShape extends FrameLayout {
        private final boolean isRounded;
        private final boolean isRoundedAsMsg;
        private float progress;
        private final RectF rect = new RectF();
        private final RectF backgroundRect = new RectF();
        private final TextPaint textPaint;
        private final Paint backgroundPaint;
        private final Paint strokePaint;
        private final Paint shapePaint;

        public StickerShape(Context context, boolean isRounded, boolean isRoundedAsMsg) {
            super(context);
            this.isRounded = isRounded;
            this.isRoundedAsMsg = isRoundedAsMsg;
            setWillNotDraw(false);

            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(AndroidUtilities.dp(13.0f));

            backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeWidth(AndroidUtilities.dp(2));

            shapePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            int currentShape = NaConfig.INSTANCE.getStickerShape().Int();
            boolean selected = (!isRounded && !isRoundedAsMsg && currentShape == 0)
                    || (isRounded && currentShape == 1)
                    || (isRoundedAsMsg && currentShape == 2);
            setSelected(selected, false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int bg = Theme.getColor(Theme.key_windowBackgroundWhite);
            int textColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlackText);
            int cardColor = Theme.blendOver(bg, Theme.multAlpha(textColor, 0.06f));
            int mockColor = Theme.blendOver(cardColor, Theme.multAlpha(textColor, 0.12f));

            backgroundPaint.setColor(cardColor);
            backgroundRect.set(0, 0, getMeasuredWidth(), AndroidUtilities.dp(80.0f));
            canvas.drawRoundRect(backgroundRect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), backgroundPaint);

            if (progress > 0) {
                strokePaint.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
                strokePaint.setAlpha((int) (progress * 255));
                float strokeInset = strokePaint.getStrokeWidth() / 2f;
                backgroundRect.set(strokeInset, strokeInset, getMeasuredWidth() - strokeInset, AndroidUtilities.dp(80.0f) - strokeInset);
                canvas.drawRoundRect(backgroundRect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), strokePaint);
            }

            int stringResId;
            if (isRounded) {
                stringResId = R.string.StickerShapeRounded;
            } else if (isRoundedAsMsg) {
                stringResId = R.string.StickerShapeRoundedMsg;
            } else {
                stringResId = R.string.Default;
            }
            String string = LocaleController.getString(stringResId);
            int textWidth = (int) Math.ceil(textPaint.measureText(string));
            canvas.drawText(string, (getMeasuredWidth() - textWidth) / 2f, AndroidUtilities.dp(102.0f), textPaint);

            rect.set(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), getMeasuredWidth() - AndroidUtilities.dp(10.0f), AndroidUtilities.dp(70.0f));
            shapePaint.setColor(mockColor);

            if (!isRounded && !isRoundedAsMsg) {
                canvas.drawRoundRect(rect, 0, 0, shapePaint);
            } else if (isRounded) {
                canvas.drawRoundRect(rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), shapePaint);
            } else {
                Rect r = new Rect();
                rect.round(r);
                int bubble = AndroidUtilities.dp(SharedConfig.bubbleRadius);
                float f = bubble;
                float f2 = bubble / 3f;
                ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(new float[]{f, f, f, f, f, f, f2, f2}, null, null));
                shapeDrawable.getPaint().setColor(mockColor);
                shapeDrawable.setBounds(r);
                shapeDrawable.draw(canvas);
            }
        }

        private void setProgress(float f) {
            this.progress = f;
            int normalColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlackText);
            int selectedColor = Theme.getColor(Theme.key_windowBackgroundWhiteValueText);
            this.textPaint.setColor(ColorUtils.blendARGB(normalColor, selectedColor, f));
            this.textPaint.setTypeface(f >= 0.5f ? AndroidUtilities.bold() : null);
            invalidate();
        }

        public void setSelected(boolean selected, boolean animated) {
            float target = selected ? 1.0f : 0.0f;
            if (target == progress && animated) {
                return;
            }
            if (animated) {
                ValueAnimator animator = ValueAnimator.ofFloat(progress, target).setDuration(250L);
                animator.setInterpolator(Easings.easeInOutQuad);
                animator.addUpdateListener(animation -> {
                    setProgress((Float) animation.getAnimatedValue());
                });
                animator.start();
                return;
            }
            setProgress(target);
        }
    }
}
