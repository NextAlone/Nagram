package tw.nekomimi.nekogram.helpers;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;

import java.util.Objects;

public class TimeStringHelper {
    public static Drawable forwardsDrawable;

    public static Drawable getForwardsDrawable() {
        if (forwardsDrawable == null) {
            forwardsDrawable = Objects.requireNonNull(ContextCompat.getDrawable(ApplicationLoader.applicationContext, R.drawable.forwards_solar)).mutate();
        }
        return forwardsDrawable;
    }
}
