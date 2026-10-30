package tw.nekomimi.nekogram.settings.cell;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.MessageDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.LayoutHelper;

import xyz.nextalone.nagram.NaConfig;
import xyz.nextalone.nagram.helper.DoubleTap;

public class DoubleTapCell extends LinearLayout {

    private static final int ICON_WIDTH = AndroidUtilities.dp(12.0f);
    private final int[] actionIcon = new int[2];
    private final ValueAnimator[] animator = new ValueAnimator[2];
    private final ValueAnimator[] circleAnimator = new ValueAnimator[4];
    private final ValueAnimator[] circleSizeAnimator = new ValueAnimator[4];
    private final float[] circleProgress = new float[4];
    private final float[] circleSizeProgress = new float[4];
    private final float[] iconChangingProgress = new float[2];
    private final MessageDrawable[] messages;
    private final Paint bubblePaint;
    private final Paint outlinePaint;
    private final Paint[] circleOutlinePaint = new Paint[2];
    private final FrameLayout preview;
    private final RectF rect = new RectF();

    public DoubleTapCell(final Context context) {
        super(context);
        setWillNotDraw(false);
        setOrientation(VERTICAL);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        setPadding(AndroidUtilities.dp(13.0f), 0, AndroidUtilities.dp(13.0f), AndroidUtilities.dp(10.0f));

        bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bubblePaint.setStyle(Paint.Style.FILL);

        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(AndroidUtilities.dp(1.0f) / 2.0f);

        circleOutlinePaint[0] = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleOutlinePaint[0].setStyle(Paint.Style.STROKE);
        circleOutlinePaint[1] = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleOutlinePaint[1].setStyle(Paint.Style.STROKE);

        messages = new MessageDrawable[]{new MessageDrawable(0, false, false), new MessageDrawable(0, true, false)};

        FrameLayout frameLayout = new FrameLayout(context) {
            private final Rect roundRect = new Rect();

            @Override
            public void onDraw(Canvas canvas) {
                int bg = Theme.getColor(Theme.key_windowBackgroundWhite);
                int textColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlackText);
                int bubbleColor = Theme.blendOver(bg, Theme.multAlpha(textColor, 0.06f));
                int outlineColor = Theme.blendOver(bg, Theme.multAlpha(textColor, 0.12f));
                int rippleColor = Theme.getColor(Theme.key_windowBackgroundWhiteValueText);
                int iconColor = Theme.getColor(Theme.key_chats_menuItemIcon);

                bubblePaint.setColor(bubbleColor);
                outlinePaint.setColor(outlineColor);

                float strokeWidth = outlinePaint.getStrokeWidth() / 2.0f;
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();

                for (int i = 0; i < 2; i++) {
                    canvas.save();
                    if (i == 0) {
                        rect.set(AndroidUtilities.dp(8.0f) + strokeWidth, AndroidUtilities.dp(10.0f) + strokeWidth, (width / 2.0f) - AndroidUtilities.dp(8.0f) - strokeWidth, AndroidUtilities.dp(75.0f) - strokeWidth);
                    } else {
                        canvas.translate(0.0f, AndroidUtilities.dp(80.0f));
                        rect.set((width / 2.0f) + strokeWidth + AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f) + strokeWidth, width - AndroidUtilities.dp(8.0f) - strokeWidth, AndroidUtilities.dp(70.0f) - strokeWidth);
                    }
                    rect.round(roundRect);
                    messages[i].setBounds(roundRect);
                    messages[i].draw(canvas, bubblePaint);
                    messages[i].draw(canvas, outlinePaint);

                    for (int i2 = 0; i2 < 2; i2++) {
                        int i3 = i + (i2 * 2);
                        if (circleProgress[i3] > 0.01f) {
                            circleOutlinePaint[i2].setColor(ColorUtils.blendARGB(0, rippleColor, circleProgress[i3]));
                            circleOutlinePaint[i2].setStrokeWidth(AndroidUtilities.dp(1.5f) * circleProgress[i3] * circleProgress[i3]);
                            float measuredWidth = ((i == 0 ? 1 : 3) * width) / 4.0f;
                            float measuredHeight = height / 4.0f;
                            float f = (i == 0 ? 3.0f : -2.0f);
                            canvas.drawCircle(measuredWidth, measuredHeight + AndroidUtilities.dpf2(f), AndroidUtilities.dp(25 - (i2 * 6)) * circleSizeProgress[i3], circleOutlinePaint[i2]);
                        }
                    }

                    if (actionIcon[i] != 0) {
                        Drawable drawable = ContextCompat.getDrawable(context, actionIcon[i]);
                        if (drawable != null) {
                            int cx = ((i == 0 ? 1 : 3) * width) / 4;
                            int cy = (int) ((height / 4.0f) + AndroidUtilities.dpf2(i == 0 ? 3.0f : -2.0f));
                            drawable.setBounds(cx - ICON_WIDTH, cy - ICON_WIDTH, cx + ICON_WIDTH, cy + ICON_WIDTH);

                            float inset = AndroidUtilities.dp(4.0f - (iconChangingProgress[i] * 4.0f));
                            drawable.setBounds(
                                    (int) (drawable.getBounds().left - inset),
                                    (int) (drawable.getBounds().top - inset),
                                    (int) (drawable.getBounds().right + inset),
                                    (int) (drawable.getBounds().bottom + inset)
                            );
                            int alpha = (int) (iconChangingProgress[i] * 255);
                            drawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(iconColor, alpha), PorterDuff.Mode.MULTIPLY));
                            drawable.draw(canvas);
                        }
                    }
                    canvas.restore();
                }
            }
        };

        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                triggerTap(e.getX());
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                triggerTap(e.getX());
                return true;
            }

            private void triggerTap(float x) {
                if (x < frameLayout.getMeasuredWidth() / 2.0f) {
                    updateIcons(1, true);
                } else {
                    updateIcons(2, true);
                }
            }
        });

        frameLayout.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });

        this.preview = frameLayout;
        frameLayout.setWillNotDraw(false);
        addView(frameLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        updateIcons(0, false);
    }

    public static int getActionIcon(int action) {
        switch (action) {
            case DoubleTap.DOUBLE_TAP_ACTION_SEND_REACTIONS:
            case DoubleTap.DOUBLE_TAP_ACTION_SHOW_REACTIONS:
                return R.drawable.msg_reactions;
            case DoubleTap.DOUBLE_TAP_ACTION_TRANSLATE:
                return R.drawable.msg_translate;
            case DoubleTap.DOUBLE_TAP_ACTION_REPLY:
                return R.drawable.menu_reply;
            case DoubleTap.DOUBLE_TAP_ACTION_SAVE:
                return R.drawable.msg_saved;
            case DoubleTap.DOUBLE_TAP_ACTION_REPEAT:
            case DoubleTap.DOUBLE_TAP_ACTION_REPEAT_AS_COPY:
                return R.drawable.msg_repeat;
            case DoubleTap.DOUBLE_TAP_ACTION_EDIT:
                return R.drawable.msg_edit;
            case DoubleTap.DOUBLE_TAP_ACTION_NONE:
            default:
                return R.drawable.msg_block2;
        }
    }

    @SuppressLint("Recycle")
    public void updateIcons(int target, boolean animated) {
        for (int i2 = 0; i2 < 2; i2++) {
            if (target == 1 && i2 != 0) continue;
            if (target == 2 && i2 != 1) continue;
            final int bubbleIndex = i2;
            if (animated) {
                for (int i3 = 0; i3 < 2; i3++) {
                    final int circleIndex = i3;
                    final int slot = (circleIndex * 2) + bubbleIndex;

                    if (circleSizeAnimator[slot] != null) {
                        circleSizeAnimator[slot].cancel();
                        circleSizeAnimator[slot] = null;
                    }
                    ValueAnimator sizeAnim = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(1300L);
                    sizeAnim.setStartDelay(60L * circleIndex);
                    sizeAnim.setInterpolator(Easings.easeInOutQuad);
                    sizeAnim.addUpdateListener(animation -> {
                        circleSizeProgress[slot] = (Float) animation.getAnimatedValue();
                        invalidate();
                    });
                    sizeAnim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            circleSizeProgress[slot] = 0.0f;
                            invalidate();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            circleSizeProgress[slot] = 0.0f;
                            invalidate();
                        }
                    });
                    circleSizeAnimator[slot] = sizeAnim;

                    if (circleAnimator[slot] != null) {
                        circleAnimator[slot].cancel();
                        circleAnimator[slot] = null;
                    }
                    ValueAnimator fadeAnim = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(1200L);
                    fadeAnim.setStartDelay((circleIndex * 80L) + 150L);
                    fadeAnim.setInterpolator(Easings.easeInOutQuad);
                    fadeAnim.addUpdateListener(animation -> {
                        float val = (Float) animation.getAnimatedValue();
                        float alpha = val < 0.35f ? (val / 0.35f) : Math.max(0.0f, (1.0f - val) / 0.65f);
                        circleProgress[slot] = alpha;
                        invalidate();
                    });
                    fadeAnim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            circleProgress[slot] = 0.0f;
                            invalidate();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            circleProgress[slot] = 0.0f;
                            invalidate();
                        }
                    });
                    circleAnimator[slot] = fadeAnim;

                    sizeAnim.start();
                    fadeAnim.start();
                }

                if (animator[bubbleIndex] != null) {
                    animator[bubbleIndex].cancel();
                    animator[bubbleIndex] = null;
                }
                ValueAnimator iconAnim = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(250L);
                iconAnim.setInterpolator(Easings.easeInOutQuad);
                iconAnim.addUpdateListener(animation -> {
                    iconChangingProgress[bubbleIndex] = (Float) animation.getAnimatedValue();
                    invalidate();
                });
                iconAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        actionIcon[bubbleIndex] = getActionIcon(bubbleIndex == 0
                                ? NaConfig.INSTANCE.getDoubleTapActionIncoming().Int()
                                : NaConfig.INSTANCE.getDoubleTapActionOutgoing().Int());
                        ValueAnimator popIn = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(250L);
                        popIn.setInterpolator(Easings.easeInOutQuad);
                        popIn.addUpdateListener(anim -> {
                            iconChangingProgress[bubbleIndex] = (Float) anim.getAnimatedValue();
                            invalidate();
                        });
                        popIn.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation2) {
                                iconChangingProgress[bubbleIndex] = 1.0f;
                                performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                                invalidate();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation2) {
                                iconChangingProgress[bubbleIndex] = 1.0f;
                            }
                        });
                        animator[bubbleIndex] = popIn;
                        popIn.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        iconChangingProgress[bubbleIndex] = 1.0f;
                    }
                });
                animator[bubbleIndex] = iconAnim;
                iconAnim.start();
            } else {
                for (int c = 0; c < 2; c++) {
                    int slot = (c * 2) + bubbleIndex;
                    if (circleAnimator[slot] != null) {
                        circleAnimator[slot].cancel();
                        circleAnimator[slot] = null;
                    }
                    if (circleSizeAnimator[slot] != null) {
                        circleSizeAnimator[slot].cancel();
                        circleSizeAnimator[slot] = null;
                    }
                    circleSizeProgress[slot] = 0.0f;
                    circleProgress[slot] = 0.0f;
                }
                if (animator[bubbleIndex] != null) {
                    animator[bubbleIndex].cancel();
                    animator[bubbleIndex] = null;
                }
                iconChangingProgress[bubbleIndex] = 1.0f;
                actionIcon[bubbleIndex] = getActionIcon(bubbleIndex == 0
                        ? NaConfig.INSTANCE.getDoubleTapActionIncoming().Int()
                        : NaConfig.INSTANCE.getDoubleTapActionOutgoing().Int());
                invalidate();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int i = 0; i < 4; i++) {
            if (circleAnimator[i] != null) {
                circleAnimator[i].cancel();
                circleAnimator[i] = null;
            }
            if (circleSizeAnimator[i] != null) {
                circleSizeAnimator[i].cancel();
                circleSizeAnimator[i] = null;
            }
            circleProgress[i] = 0.0f;
            circleSizeProgress[i] = 0.0f;
        }
        for (int i = 0; i < 2; i++) {
            if (animator[i] != null) {
                animator[i].cancel();
                animator[i] = null;
            }
            iconChangingProgress[i] = 1.0f;
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (this.preview != null) {
            this.preview.invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(
                View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(170.0f), View.MeasureSpec.EXACTLY)
        );
    }
}
