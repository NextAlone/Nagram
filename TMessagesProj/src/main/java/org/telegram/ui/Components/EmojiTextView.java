package org.telegram.ui.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.telegram.messenger.Emoji;

public class EmojiTextView extends TextView {

    public EmojiTextView(Context context) {
        super(context);
    }

    public EmojiTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmojiTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EmojiTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        text = Emoji.replaceEmoji(text, getPaint().getFontMetricsInt(), (int) getTextSize(), false);
        super.setText(text, type);
    }

}
