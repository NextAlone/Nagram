/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram;

import android.app.Activity;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import org.telegram.messenger.*;

import java.util.ArrayList;
import java.util.Arrays;

public class AyuConfig {
    private static final Object sync = new Object();

    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    public static boolean sendReadPackets;
    public static boolean sendOnlinePackets;
    public static boolean sendOfflinePacketAfterOnline;
    public static boolean sendUploadProgress;
    public static boolean useScheduledMessages;
    public static boolean markReadAfterSend;

    public static boolean showGhostToggleInDrawer;
    public static boolean WALMode;

    private static boolean configLoaded;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
                return;
            }

            preferences = ApplicationLoader.applicationContext.getSharedPreferences("ayuconfig", Activity.MODE_PRIVATE);
            editor = preferences.edit();

            // ~ Ghost essentials
            sendReadPackets = preferences.getBoolean("sendReadPackets", true);
            sendOnlinePackets = preferences.getBoolean("sendOnlinePackets", true);
            sendUploadProgress = preferences.getBoolean("sendUploadProgress", true);
            sendOfflinePacketAfterOnline = preferences.getBoolean("sendOfflinePacketAfterOnline", false);
            markReadAfterSend = preferences.getBoolean("markReadAfterSend", true);

            useScheduledMessages = preferences.getBoolean("useScheduledMessages", false);


            configLoaded = true;
        }
    }

    public static boolean isGhostModeActive() {
        return !sendReadPackets && !sendOnlinePackets && !sendUploadProgress && sendOfflinePacketAfterOnline;
    }

    public static void setGhostMode(boolean enabled) {
        sendReadPackets = !enabled;
        sendOnlinePackets = !enabled;
        sendUploadProgress = !enabled;
        sendOfflinePacketAfterOnline = enabled;

        AyuConfig.editor.putBoolean("sendReadPackets", AyuConfig.sendReadPackets).apply();
        AyuConfig.editor.putBoolean("sendOnlinePackets", AyuConfig.sendOnlinePackets).apply();
        AyuConfig.editor.putBoolean("sendUploadProgress", AyuConfig.sendUploadProgress).apply();
        AyuConfig.editor.putBoolean("sendOfflinePacketAfterOnline", AyuConfig.sendOfflinePacketAfterOnline).apply();
    }

    public static void toggleGhostMode() {
        // giga move
        setGhostMode(!isGhostModeActive());
    }

}
