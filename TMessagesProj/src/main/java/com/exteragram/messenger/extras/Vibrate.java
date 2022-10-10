/*

 This is the source code of exteraGram for Android.

 We do not and cannot prevent the use of our code,
 but be respectful and credit the original author.

 Copyright @immat0x1, 2022.

*/

package com.exteragram.messenger.extras;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.View;
import android.view.ViewGroup;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

import com.exteragram.messenger.ExteraConfig;

public class Vibrate {

    private final static long time = 200L;

    public static void disableHapticFeedback(View view) {
        if (view == null) {
            return;
        }
        view.setHapticFeedbackEnabled(false);
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View child = ((ViewGroup) view).getChildAt(i);
                child.setHapticFeedbackEnabled(false);
            }
        }
    }

    public static void vibrate() {

        if (ExteraConfig.disableVibration) {
            return;
        }

        Vibrator vibrator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) ApplicationLoader.applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
        } else {
            vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService(Context.VIBRATOR_SERVICE);
        }

        if (!vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    vibrator.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE));
                } catch (Exception e) {
                    FileLog.e("Failed to vibrate");
                }
            } else {
                try {
                    vibrator.vibrate(time);
                } catch (Exception e) {
                    FileLog.e("Failed to vibrate");
                }
            }
        }
    }
}