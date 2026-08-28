package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;

import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;


import java.util.ArrayList;
import java.util.List;

public class AnimatedPhoneNumberEditText extends HintEditText {
    private final static float SPRING_MULTIPLIER = 100f;

    private HintFadeProperty hintFadeProperty = new HintFadeProperty();
    private List<Float> hintAnimationValues = new ArrayList<>();
    private List<SpringAnimation> hintAnimations = new ArrayList<>();

    private Boolean wasHintVisible;
    private String wasHint;

    private Runnable hintAnimationCallback;

    public AnimatedPhoneNumberEditText(Context context) {
        super(context);
    }

    @Override
    public void setHintText(String value) {
        if (!xyz.nextalone.nagram.NaConfig.INSTANCE.getInputTextAnimations().Bool()) {
            if (hintAnimationCallback != null) removeCallbacks(hintAnimationCallback);
            for (SpringAnimation animation : hintAnimations) animation.cancel();
            hintAnimations.clear();
            hintAnimationValues.clear();
            wasHint = value;
            wasHintVisible = !TextUtils.isEmpty(value);
            super.setHintText(value);
            return;
        }
        boolean show = !TextUtils.isEmpty(value);
        boolean runAnimation = false;

        if (wasHintVisible == null || wasHintVisible != show) {
            hintAnimationValues.clear();
            for (SpringAnimation a : hintAnimations) {
                a.cancel();
            }
            hintAnimations.clear();
            wasHintVisible = show;
            runAnimation = TextUtils.isEmpty(getText());
        }

        String str = show ? value : wasHint;
        if (str == null) str = "";
        wasHint = value;

        if (show || !runAnimation) {
            super.setHintText(value);
        }

        if (runAnimation) {
            runHintAnimation(str.length(), show, () -> {
                hintAnimationValues.clear();
                for (SpringAnimation a : hintAnimations) {
                    a.cancel();
                }

                if (!show) {
                    super.setHintText(value);
                }
            });
        }
    }

    @Override
    public String getHintText() {
        return wasHint;
    }

    private void runHintAnimation(int length, boolean show, Runnable callback) {
        if (hintAnimationCallback != null) {
            removeCallbacks(hintAnimationCallback);
        }
        for (int i = 0; i < length; i++) {
            float startValue = show ? 0 : 1, finalValue = show ? 1 : 0;

            SpringAnimation springAnimation = new SpringAnimation(i, hintFadeProperty)
                    .setSpring(new SpringForce(finalValue * SPRING_MULTIPLIER)
                            .setStiffness(500)
                            .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY)
                            .setFinalPosition(finalValue * SPRING_MULTIPLIER))
                    .setStartValue(startValue * SPRING_MULTIPLIER);
            hintAnimations.add(springAnimation);
            hintAnimationValues.add(startValue);
            postDelayed(springAnimation::start, i * 5L);
        }
        postDelayed(hintAnimationCallback = callback, length * 5L + 150L);
    }

    @Override
    protected void onPreDrawHintCharacter(int index, Canvas canvas, float pivotX, float pivotY) {
        if (index < hintAnimationValues.size()) {
            hintPaint.setAlpha((int) (hintAnimationValues.get(index) * 0xFF));
        }
    }

    private final class HintFadeProperty extends FloatPropertyCompat<Integer> {
        public HintFadeProperty() {
            super("hint_fade");
        }

        @Override
        public float getValue(Integer object) {
            return object < hintAnimationValues.size() ? hintAnimationValues.get(object) * SPRING_MULTIPLIER : 0;
        }

        @Override
        public void setValue(Integer object, float value) {
            if (object < hintAnimationValues.size()) {
                hintAnimationValues.set((int) object, value / SPRING_MULTIPLIER);
                invalidate();
            }
        }
    }
}
