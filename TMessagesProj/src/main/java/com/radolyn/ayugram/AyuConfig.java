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
import org.telegram.messenger.*;


public class AyuConfig {
    private static final Object sync = new Object();

    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    public static boolean sendReadMessagePackets;
    public static boolean sendOnlinePackets;
    public static boolean sendOfflinePacketAfterOnline;
    public static boolean sendUploadProgress;
    public static boolean sendReadStotyPackets;
    public static boolean useScheduledMessages;
    public static boolean markReadAfterSend;
    public static boolean showGhostToggleInDrawer;
    public static boolean openStotyWarning;

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
            sendReadMessagePackets = preferences.getBoolean("sendReadMessagePackets", true);
            sendOnlinePackets = preferences.getBoolean("sendOnlinePackets", true);
            sendUploadProgress = preferences.getBoolean("sendUploadProgress", true);
            sendReadStotyPackets = preferences.getBoolean("sendReadStotyPackets", true);
            sendOfflinePacketAfterOnline = preferences.getBoolean("sendOfflinePacketAfterOnline", false);
            markReadAfterSend = preferences.getBoolean("markReadAfterSend", true);
            // ~ Ghost other options
            openStotyWarning = preferences.getBoolean("openStotyWarning", false);
            showGhostToggleInDrawer = preferences.getBoolean("showGhostToggleInDrawer", true);
            useScheduledMessages = preferences.getBoolean("useScheduledMessages", false);

            configLoaded = true;
        }
    }

    public static boolean isGhostModeActive() {
        return !sendReadMessagePackets && !sendOnlinePackets && !sendReadStotyPackets && !sendUploadProgress && sendOfflinePacketAfterOnline;
    }

    public static void setGhostMode(boolean enabled) {
        sendReadMessagePackets = !enabled;
        sendOnlinePackets = !enabled;
        sendUploadProgress = !enabled;
        sendReadStotyPackets = !enabled;
        sendOfflinePacketAfterOnline = enabled;

        AyuConfig.editor.putBoolean("sendReadMessagePackets", AyuConfig.sendReadMessagePackets).apply();
        AyuConfig.editor.putBoolean("sendOnlinePackets", AyuConfig.sendOnlinePackets).apply();
        AyuConfig.editor.putBoolean("sendUploadProgress", AyuConfig.sendUploadProgress).apply();
        AyuConfig.editor.putBoolean("sendReadStotyPackets", AyuConfig.sendReadStotyPackets).apply();
        AyuConfig.editor.putBoolean("sendOfflinePacketAfterOnline", AyuConfig.sendOfflinePacketAfterOnline).apply();
    }

    public static void toggleGhostMode() {
        // giga move
        setGhostMode(!isGhostModeActive());
    }

}
