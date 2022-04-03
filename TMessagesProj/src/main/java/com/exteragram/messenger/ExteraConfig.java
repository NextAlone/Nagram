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

    public static boolean useSystemFonts;
    public static boolean disableVibration;
    public static boolean blurForAllThemes;

    public static boolean hideAllChats;
    public static boolean hideProxySponsor;
    public static boolean hidePhoneNumber;
    public static boolean showID;
    public static boolean chatsOnTitle;
    public static boolean forceTabletMode;

    public static float stickerSize = 14.0f;
    public static boolean hideStickerTime;
    public static boolean unlimitedRecentStickers;

    public static boolean hideSendAsChannel;
    public static boolean hideKeyboardOnScroll;
    public static boolean archiveOnPull;
    public static boolean dateOfForwardedMsg;

    public static boolean disableReactions;

    public static boolean rearVideoMessages;
    public static boolean disableCamera;
    public static boolean pauseOnMinimize;
    public static boolean disablePlayback;
    public static boolean disableProximityEvents;

    public static boolean newGroup;
    public static boolean newSecretChat;
    public static boolean newChannel;
    public static boolean contacts;
    public static boolean calls;
    public static boolean peopleNearby;
    public static boolean archivedChats;
    public static boolean savedMessages;
    public static boolean scanQr;
    public static boolean inviteFriends;
    public static boolean telegramFeatures;

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

            useSystemFonts = preferences.getBoolean("useSystemFonts", false);
            disableVibration = preferences.getBoolean("disableVibration", false);
            blurForAllThemes = preferences.getBoolean("blurForAllThemes", true);

            hideAllChats = preferences.getBoolean("hideAllChats", false);
            hideProxySponsor = preferences.getBoolean("hideProxySponsor", true);
            hidePhoneNumber = preferences.getBoolean("hidePhoneNumber", false);
            showID = preferences.getBoolean("showID", false);
            chatsOnTitle = preferences.getBoolean("chatsOnTitle", true);
            forceTabletMode = preferences.getBoolean("forceTabletMode", false);

            stickerSize = preferences.getFloat("stickerSize", 14.0f);
            hideStickerTime = preferences.getBoolean("hideStickerTime", false);
            unlimitedRecentStickers = preferences.getBoolean("UnlimitedRecentStickers", false);

            hideSendAsChannel = preferences.getBoolean("hideSendAsChannel", false);
            hideKeyboardOnScroll = preferences.getBoolean("hideKeyboardOnScroll", true);
            archiveOnPull = preferences.getBoolean("archiveOnPull", true);
            dateOfForwardedMsg = preferences.getBoolean("dateOfForwardedMsg", false);

            disableReactions = preferences.getBoolean("disableReactions", false);

            rearVideoMessages = preferences.getBoolean("rearVideoMessages", false);
            disableCamera = preferences.getBoolean("disableCamera", false);
            disableProximityEvents = preferences.getBoolean("disableProximityEvents", false);
            pauseOnMinimize = preferences.getBoolean("pauseOnMinimize", true);
            disablePlayback = preferences.getBoolean("disablePlayback", true);

            newGroup = preferences.getBoolean("newGroup", true);
            newSecretChat = preferences.getBoolean("newSecretChat", false);
            newChannel = preferences.getBoolean("newChannel", false);
            contacts = preferences.getBoolean("contacts", true);
            calls = preferences.getBoolean("calls", false);
            peopleNearby = preferences.getBoolean("peopleNearby", false);
            archivedChats = preferences.getBoolean("archivedChats", true);
            savedMessages = preferences.getBoolean("savedMessages", true);
            scanQr = preferences.getBoolean("scanQr", true);
            inviteFriends = preferences.getBoolean("inviteFriends", false);
            telegramFeatures = preferences.getBoolean("telegramFeatures", true);

            configLoaded = true;
        }
    }

    public static void toggleUseSystemFonts() {
        useSystemFonts = !useSystemFonts;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("useSystemFonts", useSystemFonts);
        editor.commit();
    }

    public static void toggleDisableVibration() {
        disableVibration = !disableVibration;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableVibration", disableVibration);
        editor.commit();
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

    public static void toggleUnlimitedRecentStickers() {
        unlimitedRecentStickers = !unlimitedRecentStickers;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("unlimitedRecentStickers", unlimitedRecentStickers);
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

    public static void toggleDisableReactions() {
        disableReactions = !disableReactions;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableReactions", disableReactions);
        editor.commit();
    }

    public static void toggleRearVideoMessages() {
        rearVideoMessages = !rearVideoMessages;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("rearVideoMessages", rearVideoMessages);
        editor.commit();
    }

    public static void toggleDisableCamera() {
        disableCamera = !disableCamera;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableCamera", disableCamera);
        editor.commit();
    }

    public static void toggleDisableProximityEvents() {
        disableProximityEvents = !disableProximityEvents;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableProximityEvents", disableProximityEvents);
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

    public static void toggleNewGroup() {
        newGroup = !newGroup;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("newGroup", newGroup);
        editor.commit();
    }

    public static void toggleNewSecretChat() {
        newSecretChat = !newSecretChat;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("newSecretChat", newSecretChat);
        editor.commit();
    }

    public static void toggleNewChannel() {
        newChannel = !newChannel;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("newChannel", newChannel);
        editor.commit();
    }

    public static void toggleContacts() {
        contacts = !contacts;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("contacts", contacts);
        editor.commit();
    }

    public static void toggleCalls() {
        calls = !calls;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("calls", calls);
        editor.commit();
    }

    public static void togglePeopleNearby() {
        peopleNearby = !peopleNearby;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("peopleNearby", peopleNearby);
        editor.commit();
    }

    public static void toggleArchivedChats() {
        archivedChats = !archivedChats;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("archivedChats", archivedChats);
        editor.commit();
    }

    public static void toggleSavedMessages() {
        savedMessages = !savedMessages;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("savedMessages", savedMessages);
        editor.commit();
    }

    public static void toggleScanQr() {
        scanQr = !scanQr;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("scanQr", scanQr);
        editor.commit();
    }

    public static void toggleInviteFriends() {
        inviteFriends = !inviteFriends;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("inviteFriends", inviteFriends);
        editor.commit();
    }

    public static void toggleTelegramFeatures() {
        telegramFeatures = !telegramFeatures;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("telegramFeatures", telegramFeatures);
        editor.commit();
    }
}
