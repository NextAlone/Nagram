package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Property;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonAppend;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatActivityEnterViewAnimatedIconView extends RLottieImageView {
    private State currentState;
    private AnimatorSet emojiButtonAnimation;
//    private TransitState animatingState;

//    private Map<TransitState, RLottieDrawable> stateMap = new HashMap<TransitState, RLottieDrawable>() {
//        @Nullable
//        @Override
//        public RLottieDrawable get(@Nullable Object key) {
//            RLottieDrawable obj = super.get(key);
//            if (obj == null) {
//                TransitState state = (TransitState) key;
//                int res = state.resource;
//                return new RLottieDrawable(res, String.valueOf(res), AndroidUtilities.dp(32), AndroidUtilities.dp(32));
//            }
//            return obj;
//        }
//    };

    public ChatActivityEnterViewAnimatedIconView(Context context) {
        super(context);
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
            setImageResource(currentState.resource);
        } else {
            if (emojiButtonAnimation != null)
                emojiButtonAnimation.cancel();
            LayerDrawable mixed = new LayerDrawable(new Drawable[]{getResources().getDrawable(fromState.resource), getResources().getDrawable(currentState.resource)});
            mixed.getDrawable(0).setAlpha(255);
            mixed.getDrawable(1).setAlpha(0);
            emojiButtonAnimation = new AnimatorSet();
            emojiButtonAnimation.playTogether(
                    ObjectAnimator.ofInt(mixed, new PropertyAlpha(0), 0),
                    ObjectAnimator.ofInt(mixed, new PropertyAlpha(1), 255)
                    );
            emojiButtonAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(emojiButtonAnimation)) {
                        emojiButtonAnimation = null;
                    }
                }
            });
            emojiButtonAnimation.setDuration(200);
            setImageDrawable(mixed);
            emojiButtonAnimation.start();
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
        GIF(R.drawable.deproko_baseline_gif_24);

        final int resource;

        State(int resource) {
            this.resource = resource;
        }
    }
}
