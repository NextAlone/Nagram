package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.media.Image;
import android.os.Build;
import android.util.Property;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonAppend;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;

import java.util.HashMap;
import java.util.Map;

public class ChatActivityEnterViewAnimatedIconView extends FrameLayout {
    private State currentState;
    private AnimatorSet buttonsAnimation;
    private final ImageView[] buttonViews = new ImageView[2];

    public ChatActivityEnterViewAnimatedIconView(Context context, ChatActivityEnterView parentActivity) {
        super(context);
        for (int a = 0; a < 2; a++) {
            buttonViews[a] = new ImageView(context);
            buttonViews[a].setColorFilter(new PorterDuffColorFilter(parentActivity.getThemedColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
            buttonViews[a].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            if (Build.VERSION.SDK_INT >= 21) {
                buttonViews[a].setBackground(Theme.createSelectorDrawable(parentActivity.getThemedColor(Theme.key_listSelector)));
            }
            addView(buttonViews[a], LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.BOTTOM | Gravity.LEFT, 0, 0, 0, 0));
        }
        buttonViews[0].setVisibility(VISIBLE);
        buttonViews[1].setVisibility(GONE);
    }

    public void setColorFilter(ColorFilter cf) {
        buttonViews[0].setColorFilter(cf);
        buttonViews[1].setColorFilter(cf);
    }

    private static class PropertyAlpha extends Property<LayerDrawable, Integer> {
        private final int index;

        public PropertyAlpha(int index) {
            super(Integer.class, "alpha");
            this.index = index;
        }

        @Override
        public void set(LayerDrawable object, Integer value) {
            object.getDrawable(index).setAlpha(value);
        }

        @Override
        public Integer get(LayerDrawable object) {
            return object.getDrawable(index).getAlpha();
        }
    }

    public void setState(State state, boolean animate) {
        if (animate && state == currentState) {
            return;
        }
        State fromState = currentState;
        currentState = state;
        if (!animate || fromState == null) {
            buttonViews[0].setImageResource(currentState.resource);
        } else {
            if (buttonsAnimation != null)
                buttonsAnimation.cancel();
            buttonViews[1].setVisibility(VISIBLE);
            buttonViews[1].setImageResource(currentState.resource);
            buttonViews[0].setAlpha(1.0f);
            buttonViews[1].setAlpha(0.0f);
            buttonsAnimation = new AnimatorSet();
            buttonsAnimation.playTogether(
                    ObjectAnimator.ofFloat(buttonViews[0], View.SCALE_X, 0.1f),
                    ObjectAnimator.ofFloat(buttonViews[0], View.SCALE_Y, 0.1f),
                    ObjectAnimator.ofFloat(buttonViews[0], View.ALPHA, 0.0f),
                    ObjectAnimator.ofFloat(buttonViews[1], View.SCALE_X, 1.0f),
                    ObjectAnimator.ofFloat(buttonViews[1], View.SCALE_Y, 1.0f),
                    ObjectAnimator.ofFloat(buttonViews[1], View.ALPHA, 1.0f));
            buttonsAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(buttonsAnimation)) {
                        buttonsAnimation = null;
                        ImageView temp = buttonViews[1];
                        buttonViews[1] = buttonViews[0];
                        buttonViews[0] = temp;
                        buttonViews[1].setVisibility(INVISIBLE);
                        buttonViews[1].setAlpha(0.0f);
                        buttonViews[1].setScaleX(0.1f);
                        buttonViews[1].setScaleY(0.1f);
                    }
                }
            });
            buttonsAnimation.setDuration(200);
            buttonsAnimation.start();
        }

        switch (state) {
            case VOICE:
                setContentDescription(LocaleController.getString("AccDescrVoiceMessage", R.string.AccDescrVoiceMessage));
                break;
            case VIDEO:
                setContentDescription(LocaleController.getString("AccDescrVideoMessage", R.string.AccDescrVideoMessage));
                break;
        }
    }

//    private TransitState getAnyState(State from) {
//        for (TransitState transitState : TransitState.values()) {
//            if (transitState.firstState == from) {
//                return transitState;
//            }
//        }
//        return null;
//    }
//
//    private TransitState getState(State from, State to) {
//        for (TransitState transitState : TransitState.values()) {
//            if (transitState.firstState == from && transitState.secondState == to) {
//                return transitState;
//            }
//        }
//        return null;
//    }
//
//    private enum TransitState {
//        VOICE_TO_VIDEO(State.VOICE, State.VIDEO, R.raw.voice_to_video),
//        STICKER_TO_KEYBOARD(State.STICKER, State.KEYBOARD, R.raw.sticker_to_keyboard),
//        SMILE_TO_KEYBOARD(State.SMILE, State.KEYBOARD, R.raw.smile_to_keyboard),
//        VIDEO_TO_VOICE(State.VIDEO, State.VOICE, R.raw.video_to_voice),
//        KEYBOARD_TO_STICKER(State.KEYBOARD, State.STICKER, R.raw.keyboard_to_sticker),
//        KEYBOARD_TO_GIF(State.KEYBOARD, State.GIF, R.raw.keyboard_to_gif),
//        KEYBOARD_TO_SMILE(State.KEYBOARD, State.SMILE, R.raw.keyboard_to_smile),
//        GIF_TO_KEYBOARD(State.GIF, State.KEYBOARD, R.raw.gif_to_keyboard),
//        GIF_TO_SMILE(State.GIF, State.SMILE, R.raw.gif_to_smile),
//        SMILE_TO_GIF(State.SMILE, State.GIF, R.raw.smile_to_gif),
//        SMILE_TO_STICKER(State.SMILE, State.STICKER, R.raw.smile_to_sticker),
//        STICKER_TO_SMILE(State.STICKER, State.SMILE, R.raw.sticker_to_smile);
//
//        final State firstState, secondState;
//        final int resource;
//
//        TransitState(State firstState, State secondState, int resource) {
//            this.firstState = firstState;
//            this.secondState = secondState;
//            this.resource = resource;
//        }
//    }

    public enum State {
        VOICE(R.drawable.baseline_mic_24),
        VIDEO(R.drawable.baseline_camera_alt_24),
        STICKER(R.drawable.deproko_baseline_stickers_24),
        KEYBOARD(R.drawable.baseline_keyboard_24),
        SMILE(R.drawable.baseline_emoticon_outline_24),
        GIF(R.drawable.deproko_baseline_gif_24),
        MENU(R.drawable.ic_ab_other);

        final int resource;

        State(int resource) {
            this.resource = resource;
        }
    }
}
