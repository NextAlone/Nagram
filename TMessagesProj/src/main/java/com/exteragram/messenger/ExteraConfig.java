package com.exteragram.messenger;

import android.app.Activity;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.tgnet.TLRPC;
import org.telegram.messenger.UserConfig;

import java.util.Arrays;

public class ExteraConfig {

    private static final Object sync = new Object();

    private static final int[] OFFICIAL_CHANNELS = {1233768168, 1524581881, 1571726392, 1632728092, 1638754701, 1779596027};
    private static final int[] DEVS = {963080346, 1282540315, 2116073546, 1374434073, 388099852};

    public static boolean scrollablePreview;

    public static boolean useSystemFonts;
    public static boolean disableVibration;
    public static boolean blurForAllThemes;

    public static boolean hideAllChats;
    public static boolean hidePhoneNumber;
    public static boolean showID;
    public static boolean chatsOnTitle;
    public static boolean forceTabletMode;

    public static float stickerSize = 14.0f;
    public static boolean hideStickerTime;
    public static boolean unlimitedRecentStickers;

    public static boolean hideSendAsChannel;
    public static boolean hideKeyboardOnScroll;
    public static boolean disableReactions;
    public static boolean disableGreetingSticker;
    public static boolean disableJumpToNextChannel;
    public static boolean archiveOnPull;
    public static boolean dateOfForwardedMsg;

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
    public static int eventType;
    
    public static long channelToSave = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();

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

            scrollablePreview = preferences.getBoolean("scrollablePreview", true);

            useSystemFonts = preferences.getBoolean("useSystemFonts", false);
            disableVibration = preferences.getBoolean("disableVibration", false);
            blurForAllThemes = preferences.getBoolean("blurForAllThemes", true);

            hideAllChats = preferences.getBoolean("hideAllChats", false);
            hidePhoneNumber = preferences.getBoolean("hidePhoneNumber", false);
            showID = preferences.getBoolean("showID", false);
            chatsOnTitle = preferences.getBoolean("chatsOnTitle", true);
            forceTabletMode = preferences.getBoolean("forceTabletMode", false);

            stickerSize = preferences.getFloat("stickerSize", 14.0f);
            hideStickerTime = preferences.getBoolean("hideStickerTime", false);
            unlimitedRecentStickers = preferences.getBoolean("unlimitedRecentStickers", false);

            hideSendAsChannel = preferences.getBoolean("hideSendAsChannel", false);
            hideKeyboardOnScroll = preferences.getBoolean("hideKeyboardOnScroll", true);
            disableReactions = preferences.getBoolean("disableReactions", false);
            disableJumpToNextChannel = preferences.getBoolean("disableJumpToNextChannel", false);
            disableGreetingSticker = preferences.getBoolean("disableGreetingSticker", false);
            archiveOnPull = preferences.getBoolean("archiveOnPull", true);
            dateOfForwardedMsg = preferences.getBoolean("dateOfForwardedMsg", false);

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
            eventType = preferences.getInt("eventType", 0);

            channelToSave = preferences.getLong("channelToSave", UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId());
            
            configLoaded = true;
        }
    }

    public static boolean isExtera(TLRPC.Chat chat) {
        return Arrays.stream(OFFICIAL_CHANNELS).anyMatch(id -> id == chat.id);
    }
    
    public static boolean isExteraDev(TLRPC.User user) {
        return Arrays.stream(DEVS).anyMatch(id -> id == user.id);
    }

    public static void toggleUseSystemFonts() {
        useSystemFonts = !useSystemFonts;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("useSystemFonts", useSystemFonts);
        editor.apply();
    }

    public static void toggleDisableVibration() {
        disableVibration = !disableVibration;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableVibration", disableVibration);
        editor.apply();
    }

    public static void toggleBlurForAllThemes() {
        blurForAllThemes = !blurForAllThemes;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("blurForAllThemes", blurForAllThemes);
        editor.apply();
    }

    public static void toggleHideAllChats() {
        hideAllChats = !hideAllChats;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideAllChats", hideAllChats);
        editor.apply();
    }

    public static void toggleHidePhoneNumber() {
        hidePhoneNumber = !hidePhoneNumber;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hidePhoneNumber", hidePhoneNumber);
        editor.apply();
    }

    public static void toggleShowID() {
        showID = !showID;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showID", showID);
        editor.apply();
    }

    public static void toggleChatsOnTitle() {
        chatsOnTitle = !chatsOnTitle;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("chatsOnTitle", chatsOnTitle);
        editor.apply();
    }

    public static void toggleForceTabletMode() {
        forceTabletMode = !forceTabletMode;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("forceTabletMode", forceTabletMode);
        editor.apply();
    }

    public static void setStickerSize(float size) {
        stickerSize = size;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("stickerSize", stickerSize);
        editor.apply();
    }

    public static void toggleHideStickerTime() {
        hideStickerTime = !hideStickerTime;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideStickerTime", hideStickerTime);
        editor.apply();
    }

    public static void toggleUnlimitedRecentStickers() {
        unlimitedRecentStickers = !unlimitedRecentStickers;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("unlimitedRecentStickers", unlimitedRecentStickers);
        editor.apply();
    }

    public static void toggleHideSendAsChannel() {
        hideSendAsChannel = !hideSendAsChannel;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideSendAsChannel", hideSendAsChannel);
        editor.apply();
    }

    public static void toggleHideKeyboardOnScroll() {
        hideKeyboardOnScroll = !hideKeyboardOnScroll;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideKeyboardOnScroll", hideKeyboardOnScroll);
        editor.apply();
    }

    public static void toggleDisableReactions() {
        disableReactions = !disableReactions;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableReactions", disableReactions);
        editor.apply();
    }

    public static void toggleDisableGreetingSticker() {
        disableGreetingSticker = !disableGreetingSticker;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableGreetingSticker", disableGreetingSticker);
        editor.apply();
    }

    public static void toggleDisableJumpToNextChannel() {
        disableJumpToNextChannel = !disableJumpToNextChannel;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableJumpToNextChannel", disableJumpToNextChannel);
        editor.apply();
    }

    public static void toggleArchiveOnPull() {
        archiveOnPull = !archiveOnPull;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("archiveOnPull", archiveOnPull);
        editor.apply();
    }

    public static void toggleDateOfForwardedMsg() {
        dateOfForwardedMsg = !dateOfForwardedMsg;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("dateOfForwardedMsg", dateOfForwardedMsg);
        editor.apply();
    }

    public static void toggleRearVideoMessages() {
        rearVideoMessages = !rearVideoMessages;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("rearVideoMessages", rearVideoMessages);
        editor.apply();
    }

    public static void toggleDisableCamera() {
        disableCamera = !disableCamera;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableCamera", disableCamera);
        editor.apply();
    }

    public static void toggleDisableProximityEvents() {
        disableProximityEvents = !disableProximityEvents;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableProximityEvents", disableProximityEvents);
        editor.apply();
    }

    public static void togglePauseOnMinimize() {
        pauseOnMinimize = !pauseOnMinimize;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("pauseOnMinimize", pauseOnMinimize);
        editor.apply();
    }
    public static void toggleDisablePlayback() {
        disablePlayback = !disablePlayback;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disablePlayback", disablePlayback);
        editor.apply();
    }

    public static void toggleNewGroup() {
        newGroup = !newGroup;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("newGroup", newGroup);
        editor.apply();
    }

    public static void toggleNewSecretChat() {
        newSecretChat = !newSecretChat;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("newSecretChat", newSecretChat);
        editor.apply();
    }

    public static void toggleNewChannel() {
        newChannel = !newChannel;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("newChannel", newChannel);
        editor.apply();
    }

    public static void toggleContacts() {
        contacts = !contacts;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("contacts", contacts);
        editor.apply();
    }

    public static void toggleCalls() {
        calls = !calls;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("calls", calls);
        editor.apply();
    }

    public static void togglePeopleNearby() {
        peopleNearby = !peopleNearby;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("peopleNearby", peopleNearby);
        editor.apply();
    }

    public static void toggleArchivedChats() {
        archivedChats = !archivedChats;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("archivedChats", archivedChats);
        editor.apply();
    }

    public static void toggleSavedMessages() {
        savedMessages = !savedMessages;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("savedMessages", savedMessages);
        editor.apply();
    }

    public static void toggleScanQr() {
        scanQr = !scanQr;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("scanQr", scanQr);
        editor.apply();
    }

    public static void toggleInviteFriends() {
        inviteFriends = !inviteFriends;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("inviteFriends", inviteFriends);
        editor.apply();
    }

    public static void toggleTelegramFeatures() {
        telegramFeatures = !telegramFeatures;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("telegramFeatures", telegramFeatures);
        editor.apply();
    }
    
    public static void changeChannelToSave(long id) {
        channelToSave = id;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("channelToSave", channelToSave);
        editor.apply();
    }

    public static void setEventType(int event) {
        eventType = event;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("eventType", eventType);
        editor.apply();
    }
}
