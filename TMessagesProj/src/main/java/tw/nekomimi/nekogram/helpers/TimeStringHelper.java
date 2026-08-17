package tw.nekomimi.nekogram.helpers;

import static org.telegram.messenger.AndroidUtilities.dp;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.ColoredImageSpan;

import java.util.Objects;

public class TimeStringHelper {
    public static SpannableStringBuilder forwardsSpan;
    public static Drawable forwardsDrawable;

    public static CharSequence getColoredAdminString(View parent, TextPaint namePaint, SpannableStringBuilder sb) {
        SpannableString spannableString = new SpannableString("\u200B");
        spannableString.setSpan(new adminStringSpan(parent, namePaint, sb), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static class adminStringSpan extends ReplacementSpan {

        private final AnimatedTextView.AnimatedTextDrawable adminString;
        private final TextPaint namePaint;

        public adminStringSpan(View parent, TextPaint namePaint, SpannableStringBuilder sb) {
            this.namePaint = namePaint;
            adminString = new AnimatedTextView.AnimatedTextDrawable(false, false, true);
            adminString.setCallback(parent);
            float smallerDp = (2 * SharedConfig.fontSize + 10) / 3f;
            adminString.setTextSize(dp(smallerDp - 1));
            adminString.setText("");
            adminString.setGravity(Gravity.CENTER);
            setText(sb, false);

            if (forwardsDrawable == null) {
                forwardsDrawable = Objects.requireNonNull(ContextCompat.getDrawable(ApplicationLoader.applicationContext, R.drawable.forwards_solar)).mutate();
            }
            if (forwardsSpan == null) {
                forwardsSpan = new SpannableStringBuilder("\u200B");
                forwardsSpan.setSpan(new ColoredImageSpan(forwardsDrawable, ColoredImageSpan.ALIGN_CENTER), 0, 1, 0);
            }
        }

        public void setText(SpannableStringBuilder sb, boolean animated) {
            adminString.setText(sb.toString(), animated);
        }

        public void setColor(int color) {
            adminString.setTextColor(color);
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            return getWidth();
        }

        public int getWidth() {
            return (int) adminString.getWidth();
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float _x, int top, int _y, int bottom, @NonNull Paint paint) {
            if (this.namePaint.getColor() != adminString.getTextColor()) {
                adminString.setTextColor(this.namePaint.getColor());
            }
            canvas.save();
            canvas.translate(_x, -dp(2.0f));
            AndroidUtilities.rectTmp2.set(0, 0, (int) adminString.getCurrentWidth(), (int) adminString.getHeight());
            adminString.setBounds(AndroidUtilities.rectTmp2);
            adminString.draw(canvas);
            canvas.restore();
        }
    }
}
