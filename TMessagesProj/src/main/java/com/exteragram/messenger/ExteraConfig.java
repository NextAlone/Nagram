package com.exteragram.messenger;

import android.app.Activity;
import android.content.SharedPreferences;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;

public class ExteraConfig {

    private static final Object sync = new Object();

    public static boolean blurForAllThemes;

    public static boolean hideAllChats;
    public static boolean hideProxySponsor;
    public static boolean hidePhoneNumber;
    public static boolean showID;
    public static boolean chatsOnTitle;
    public static boolean forceTabletMode;

    public static float stickerSize = 14.0f;
    public static boolean hideStickerTime;

    public static boolean hideSendAsChannel;
    public static boolean hideKeyboardOnScroll;
    public static boolean archiveOnPull;
    public static boolean dateOfForwardedMsg;

    public static boolean rearVideoMessages;
    public static boolean pauseOnMinimize;
    public static boolean disablePlayback;

    private static boolean configLoaded;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
                return;
            }

            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);

            blurForAllThemes = preferences.getBoolean("blurForAllThemes", true);

            hideAllChats = preferences.getBoolean("hideAllChats", false);
            hideProxySponsor = preferences.getBoolean("hideProxySponsor", true);
            hidePhoneNumber = preferences.getBoolean("hidePhoneNumber", false);
            showID = preferences.getBoolean("showID", false);
            chatsOnTitle = preferences.getBoolean("chatsOnTitle", true);
            forceTabletMode = preferences.getBoolean("forceTabletMode", false);

            stickerSize = preferences.getFloat("stickerSize", 14.0f);
            hideStickerTime = preferences.getBoolean("hideStickerTime", false);

            hideSendAsChannel = preferences.getBoolean("hideSendAsChannel", false);
            hideKeyboardOnScroll = preferences.getBoolean("hideKeyboardOnScroll", true);
            archiveOnPull = preferences.getBoolean("archiveOnPull", true);
            dateOfForwardedMsg = preferences.getBoolean("dateOfForwardedMsg", false);

            rearVideoMessages = preferences.getBoolean("rearVideoMessages", false);
            pauseOnMinimize = preferences.getBoolean("pauseOnMinimize", true);
            disablePlayback = preferences.getBoolean("disablePlayback", true);

            configLoaded = true;
        }
    }

    public static void toggleBlurForAllThemes() {
        blurForAllThemes = !blurForAllThemes;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("blurForAllThemes", blurForAllThemes);
        editor.commit();
    }

    public static void toggleHideAllChats() {
        hideAllChats = !hideAllChats;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideAllChats", hideAllChats);
        editor.commit();
    }

    public static void toggleHideProxySponsor() {
        hideProxySponsor = !hideProxySponsor;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideProxySponsor", hideProxySponsor);
        editor.commit();
    }

    public static void toggleHidePhoneNumber() {
        hidePhoneNumber = !hidePhoneNumber;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hidePhoneNumber", hidePhoneNumber);
        editor.commit();
    }

    public static void toggleShowID() {
        showID = !showID;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showID", showID);
        editor.commit();
    }

    public static void toggleChatsOnTitle() {
        chatsOnTitle = !chatsOnTitle;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("chatsOnTitle", chatsOnTitle);
        editor.commit();
    }

    public static void toggleForceTabletMode() {
        forceTabletMode = !forceTabletMode;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("forceTabletMode", forceTabletMode);
        editor.commit();
    }

    public static void setStickerSize(float size) {
        stickerSize = size;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("stickerSize", stickerSize);
        editor.commit();
    }

    public static void toggleHideStickerTime() {
        hideStickerTime = !hideStickerTime;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideStickerTime", hideStickerTime);
        editor.commit();
    }

    public static void toggleHideSendAsChannel() {
        hideSendAsChannel = !hideSendAsChannel;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideSendAsChannel", hideSendAsChannel);
        editor.commit();
    }

    public static void toggleHideKeyboardOnScroll() {
        hideKeyboardOnScroll = !hideKeyboardOnScroll;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideKeyboardOnScroll", hideKeyboardOnScroll);
        editor.commit();
    }

    public static void toggleArchiveOnPull() {
        archiveOnPull = !archiveOnPull;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("archiveOnPull", archiveOnPull);
        editor.commit();
    }

    public static void toggleDateOfForwardedMsg() {
        dateOfForwardedMsg = !dateOfForwardedMsg;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("dateOfForwardedMsg", dateOfForwardedMsg);
        editor.commit();
    }

    public static void toggleRearVideoMessages() {
        rearVideoMessages = !rearVideoMessages;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("rearVideoMessages", rearVideoMessages);
        editor.commit();
    }

    public static void togglePauseOnMinimize() {
        pauseOnMinimize = !pauseOnMinimize;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("pauseOnMinimize", pauseOnMinimize);
        editor.commit();
    }
    public static void toggleDisablePlayback() {
        disablePlayback = !disablePlayback;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disablePlayback", disablePlayback);
        editor.commit();
    }
}
