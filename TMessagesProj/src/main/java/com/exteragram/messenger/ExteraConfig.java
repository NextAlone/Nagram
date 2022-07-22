package com.exteragram.messenger;

import android.app.Activity;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.tgnet.TLRPC;
import org.telegram.messenger.UserConfig;

import com.exteragram.messenger.ExteraUtils;

import java.util.Arrays;

public class ExteraConfig {

    private static final Object sync = new Object();

    private static final int[] OFFICIAL_CHANNELS = {1233768168, 1524581881, 1571726392, 1632728092, 1638754701, 1779596027, 1172503281};
    private static final int[] DEVS = {963080346, 1282540315, 1374434073, 388099852, 1972014627};

    public static boolean useSystemFonts;
    public static boolean disableVibration;
    public static boolean blurForAllThemes;
    public static boolean centerTitle;
    public static boolean newSwitchStyle;
    public static boolean transparentNavBar;
    public static boolean squareFab;

    public static boolean disableNumberRounding;
    public static boolean formatTimeWithSeconds;
    public static boolean chatsOnTitle;
    public static boolean forceTabletMode;
    public static boolean archiveOnPull;
    public static boolean disableUnarchiveSwipe;
    public static boolean forcePacmanAnimation;
    public static boolean hidePhoneNumber;
    public static boolean showID;
    public static boolean showDC;

    public static float stickerSize = 14.0f;
    public static int stickerForm;
    public static boolean hideStickerTime;
    public static boolean unlimitedRecentStickers;
    public static boolean sendMessageBeforeSendSticker;
    public static boolean premiumAutoPlayback;

    public static boolean hideSendAsChannel;
    public static boolean hideKeyboardOnScroll;
    public static boolean disableReactions;
    public static boolean disableGreetingSticker;
    public static boolean disableJumpToNextChannel;
    public static boolean dateOfForwardedMsg;
    public static boolean showMessageID;
    public static boolean zalgoFilter;

    public static boolean rearVideoMessages;
    public static boolean disableCamera;
    public static boolean pauseOnMinimize;
    public static boolean disablePlayback;
    public static boolean disableProximityEvents;

    public static boolean newGroup, newSecretChat, newChannel, contacts, calls, peopleNearby, archivedChats, savedMessages, scanQr, inviteFriends, telegramFeatures;

    public static int eventType;

    public static long channelToSave;

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

            useSystemFonts = preferences.getBoolean("useSystemFonts", true);
            disableVibration = preferences.getBoolean("disableVibration", false);
            blurForAllThemes = preferences.getBoolean("blurForAllThemes", false);
            centerTitle = preferences.getBoolean("centerTitle", false);
            newSwitchStyle = preferences.getBoolean("newSwitchStyle", false);
            transparentNavBar = preferences.getBoolean("transparentNavBar", false);
            squareFab = preferences.getBoolean("squareFab", false);

            disableNumberRounding = preferences.getBoolean("disableNumberRounding", false);
            formatTimeWithSeconds = preferences.getBoolean("formatTimeWithSeconds", false);
            chatsOnTitle = preferences.getBoolean("chatsOnTitle", false);
            forceTabletMode = preferences.getBoolean("forceTabletMode", false);
            archiveOnPull = preferences.getBoolean("archiveOnPull", false);
            disableUnarchiveSwipe = preferences.getBoolean("disableUnarchiveSwipe", true);
            forcePacmanAnimation = preferences.getBoolean("forcePacmanAnimation", false);
            hidePhoneNumber = preferences.getBoolean("hidePhoneNumber", false);
            showID = preferences.getBoolean("showID", true);
            showDC = preferences.getBoolean("showDC", false);

            stickerSize = preferences.getFloat("stickerSize", 14.0f);
            stickerForm = preferences.getInt("stickerForm", 0);
            hideStickerTime = preferences.getBoolean("hideStickerTime", false);
            unlimitedRecentStickers = preferences.getBoolean("unlimitedRecentStickers", false);
            sendMessageBeforeSendSticker = preferences.getBoolean("sendMessageBeforeSendSticker", false);
            premiumAutoPlayback = preferences.getBoolean("premiumAutoPlayback", false);

            hideSendAsChannel = preferences.getBoolean("hideSendAsChannel", false);
            hideKeyboardOnScroll = preferences.getBoolean("hideKeyboardOnScroll", true);
            disableReactions = preferences.getBoolean("disableReactions", false);
            disableJumpToNextChannel = preferences.getBoolean("disableJumpToNextChannel", false);
            disableGreetingSticker = preferences.getBoolean("disableGreetingSticker", false);
            dateOfForwardedMsg = preferences.getBoolean("dateOfForwardedMsg", false);
            showMessageID = preferences.getBoolean("showMessageID", false);
            zalgoFilter = preferences.getBoolean("zalgoFilter", true);

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

            channelToSave = preferences.getLong("channelToSave", 0);

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
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("useSystemFonts", useSystemFonts ^= true).apply();
    }

    public static void toggleDisableVibration() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableVibration", disableVibration ^= true).apply();
    }

    public static void toggleBlurForAllThemes() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("blurForAllThemes", blurForAllThemes ^= true).apply();
    }

    public static void toggleHidePhoneNumber() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("hidePhoneNumber", hidePhoneNumber ^= true).apply();
    }

    public static void toggleShowID() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("showID", showID ^= true).apply();
    }

    public static void toggleShowDC() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("showDC", showDC ^= true).apply();
    }

    public static void toggleChatsOnTitle() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("chatsOnTitle", chatsOnTitle ^= true).apply();
    }

    public static void toggleForceTabletMode() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("forceTabletMode", forceTabletMode ^= true).apply();
    }

    public static void setStickerSize(float size) {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putFloat("stickerSize", stickerSize = size).apply();
    }

    public static void toggleHideStickerTime() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("hideStickerTime", hideStickerTime ^= true).apply();
    }

    public static void toggleUnlimitedRecentStickers() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("unlimitedRecentStickers", unlimitedRecentStickers ^= true).apply();
    }

    public static void toggleSendMessageBeforeSendSticker() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("sendMessageBeforeSendSticker", sendMessageBeforeSendSticker ^= true).apply();
    }

    public static void toggleHideSendAsChannel() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("hideSendAsChannel", hideSendAsChannel ^= true).apply();
    }

    public static void toggleHideKeyboardOnScroll() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("hideKeyboardOnScroll", hideKeyboardOnScroll ^= true).apply();
    }

    public static void toggleDisableReactions() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableReactions", disableReactions ^= true).apply();
    }

    public static void toggleDisableGreetingSticker() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableGreetingSticker", disableGreetingSticker ^= true).apply();
    }

    public static void toggleDisableJumpToNextChannel() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableJumpToNextChannel", disableJumpToNextChannel ^= true).apply();
    }

    public static void toggleArchiveOnPull() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("archiveOnPull", archiveOnPull ^= true).apply();
    }

    public static void toggleDateOfForwardedMsg() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("dateOfForwardedMsg", dateOfForwardedMsg ^= true).apply();
    }

    public static void toggleShowMessageID() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("showMessageID", showMessageID ^= true).apply();
    }

    public static void toggleRearVideoMessages() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("rearVideoMessages", rearVideoMessages ^= true).apply();
    }

    public static void toggleDisableCamera() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableCamera", disableCamera ^= true).apply();
    }

    public static void toggleDisableProximityEvents() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableProximityEvents", disableProximityEvents ^= true).apply();
    }

    public static void togglePauseOnMinimize() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("pauseOnMinimize", pauseOnMinimize ^= true).apply();
    }

    public static void toggleDisablePlayback() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disablePlayback", disablePlayback ^= true).apply();
    }

    public static void toggleDrawerElements(int id) {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        switch (id) {
            case 1:
                editor.putBoolean("newGroup", newGroup ^= true).apply();
                break;
            case 2:
                editor.putBoolean("newSecretChat", newSecretChat ^= true).apply();
                break;
            case 3:
                editor.putBoolean("newChannel", newChannel ^= true).apply();
                break;
            case 4:
                editor.putBoolean("contacts", contacts ^= true).apply();
                break;
            case 5:
                editor.putBoolean("calls", calls ^= true).apply();
                break;
            case 6:
                editor.putBoolean("peopleNearby", peopleNearby ^= true).apply();
                break;
            case 7:
                editor.putBoolean("archivedChats", archivedChats ^= true).apply();
                break;
            case 8:
                editor.putBoolean("savedMessages", savedMessages ^= true).apply();
                break;
            case 9:
                editor.putBoolean("scanQr", scanQr ^= true).apply();
                break;
            case 10:
                editor.putBoolean("inviteFriends", inviteFriends ^= true).apply();
                break;
            case 11:
                editor.putBoolean("telegramFeatures", telegramFeatures ^= true).apply();
                break;
        }
    }

    public static void changeChannelToSave(long id) {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putLong("channelToSave", channelToSave = id).apply();
    }

    public static void setEventType(int id) {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putInt("eventType", eventType = id).apply();
    }

    public static void toggleCenterTitle() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("centerTitle", centerTitle ^= true).apply();
    }

    public static void toggleNewSwitchStyle() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("newSwitchStyle", newSwitchStyle ^= true).apply();
    }

    public static void toggleTransparentNavBar() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("transparentNavBar", transparentNavBar ^= true).apply();
    }

    public static void toggleSquareFab() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("squareFab", squareFab ^= true).apply();
    }

    public static void toggleDisableUnarchiveSwipe() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableUnarchiveSwipe", disableUnarchiveSwipe ^= true).apply();
    }

    public static void toggleForcePacmanAnimation() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("forcePacmanAnimation", forcePacmanAnimation ^= true).apply();
    }

    public static void toggleDisableNumberRounding() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableNumberRounding", disableNumberRounding ^= true).apply();
    }

    public static void toggleFormatTimeWithSeconds() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("formatTimeWithSeconds", formatTimeWithSeconds ^= true).apply();
    }

    public static void toggleZalgoFilter() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("zalgoFilter", zalgoFilter ^= true).apply();
    }

    public static void togglePremiumAutoPlayback() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("premiumAutoPlayback", premiumAutoPlayback ^= true).apply();
    }

    public static void setStickerForm(int form) {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putInt("stickerForm", stickerForm = form).apply();
    }
}
