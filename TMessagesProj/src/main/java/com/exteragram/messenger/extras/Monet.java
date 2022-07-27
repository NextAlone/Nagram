package com.exteragram.messenger.extras;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import com.google.android.material.color.MaterialColors;
import org.telegram.messenger.R;

public class Monet {
    private final static int defaultColor = Color.argb(0xFF, 0x3D, 0xDC, 0x84);
    private static ContextThemeWrapper ctxWrapper;

    public static int getAccentColor(boolean dark, Context ctx, @ColorInt int accent) {
        if (Build.VERSION.SDK_INT >= 31) {
            return ContextCompat.getColor(ctx, accent);
        } else {
            if (dark) {
                return reqAttrFromDeviceDark(ctx, android.R.attr.colorPrimaryDark, defaultColor, 29);
            } else {
                return reqAttrFromDevice(ctx, android.R.attr.colorAccent, defaultColor, 29);
            }
        }
    }

    public static int getBackgroundColor(boolean dark, Context ctx, @ColorInt int accent) {
        if (Build.VERSION.SDK_INT >= 31) {
            return ContextCompat.getColor(ctx, accent);
        } else {
            if (dark) {
                return reqAttrFromDeviceDark(ctx, android.R.attr.colorSecondary, -1, 31);
            } else {
                return reqAttrFromDevice(ctx, android.R.attr.colorBackground, -1, 31);
            }
        }
    }

    private static int reqAttrFromDevice(Context ctx, @AttrRes int attr, int defaultColor, int minApi) {
        if (Build.VERSION.SDK_INT < minApi) {
            return defaultColor;
        } else {
            return MaterialColors.getColor(getCtx(ctx, false), attr, defaultColor);
        }
    }

    private static int reqAttrFromDeviceDark(Context ctx, @AttrRes int attr, int defaultColor, int minApi) {
        if (Build.VERSION.SDK_INT < minApi) {
            return defaultColor;
        } else {
            return MaterialColors.getColor(getCtx(ctx, true), attr, defaultColor);
        }
    }

    private static Context getCtx(Context ctx, boolean dark) {
        ctxWrapper = new ContextThemeWrapper(ctx, dark ? android.R.style.Theme_DeviceDefault_DayNight : android.R.style.Theme_DeviceDefault_Light);
        return ctxWrapper;
    }
}