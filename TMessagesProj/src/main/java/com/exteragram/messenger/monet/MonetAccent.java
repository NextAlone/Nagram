/*

 This is the source code of exteraGram for Android.

 We do not and cannot prevent the use of our code,
 but be respectful and credit the original author.

 Copyright @immat0x1, 2022.

*/

package com.exteragram.messenger.monet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import com.google.android.material.color.MaterialColors;

public class MonetAccent {

    private final static int defaultColor = Color.argb(0xFF, 0x3D, 0xDC, 0x84);

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("ResourceType")
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("ResourceType")
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static int reqAttrFromDevice(Context ctx, @AttrRes int attr, int defaultColor, int minApi) {
        if (Build.VERSION.SDK_INT < minApi) {
            return defaultColor;
        } else {
            return MaterialColors.getColor(getCtx(ctx, false), attr, defaultColor);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static int reqAttrFromDeviceDark(Context ctx, @AttrRes int attr, int defaultColor, int minApi) {
        if (Build.VERSION.SDK_INT < minApi) {
            return defaultColor;
        } else {
            return MaterialColors.getColor(getCtx(ctx, true), attr, defaultColor);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static Context getCtx(Context ctx, boolean dark) {
        return new ContextThemeWrapper(ctx, dark ? android.R.style.Theme_DeviceDefault_DayNight : android.R.style.Theme_DeviceDefault_Light);
    }
}