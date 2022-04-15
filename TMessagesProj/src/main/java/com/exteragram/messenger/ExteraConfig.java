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
    private static final int[] DEVS = {963080346, 1282540315, 1374434073, 388099852, 1999113390, 1566664501};

    public static boolean useSystemFonts;
    public static boolean disableVibration;
    public static boolean blurForAllThemes;
    public static boolean centerTitle;
    public static boolean newSwitchStyle;
    public static boolean transparentNavBar;
    public static boolean squareFab;

    public static boolean disableNumberRounding;
    public static boolean formatTimeWithSeconds;
    public static boolean hideAllChats;
    public static boolean chatsOnTitle;
    public static boolean forceTabletMode;
    public static boolean archiveOnPull;
    public static boolean disableUnarchiveSwipe;
    public static boolean forcePacmanAnimation;
    public static boolean hidePhoneNumber;
    public static boolean showID;
    public static boolean showDC;

    public static float stickerSize = 14.0f;
    public static boolean hideStickerTime;
    public static boolean unlimitedRecentStickers;
    public static boolean sendMessageBeforeSendSticker;

    public static boolean hideSendAsChannel;
    public static boolean hideKeyboardOnScroll;
    public static boolean disableReactions;
    public static boolean disableGreetingSticker;
    public static boolean disableJumpToNextChannel;
    public static boolean dateOfForwardedMsg;
    public static boolean showMessageID;

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
            blurForAllThemes = preferences.getBoolean("blurForAllThemes", true);
            centerTitle = preferences.getBoolean("centerTitle", false);
            newSwitchStyle = preferences.getBoolean("newSwitchStyle", false);
            transparentNavBar = preferences.getBoolean("transparentNavBar", false);
            squareFab = preferences.getBoolean("squareFab", false);

            disableNumberRounding = preferences.getBoolean("disableNumberRounding", false);
            formatTimeWithSeconds = preferences.getBoolean("formatTimeWithSeconds", false);
            hideAllChats = preferences.getBoolean("hideAllChats", false);
            chatsOnTitle = preferences.getBoolean("chatsOnTitle", false);
            forceTabletMode = preferences.getBoolean("forceTabletMode", false);
            archiveOnPull = preferences.getBoolean("archiveOnPull", false);
            disableUnarchiveSwipe = preferences.getBoolean("disableUnarchiveSwipe", false);
            forcePacmanAnimation = preferences.getBoolean("forcePacmanAnimation", false);
            hidePhoneNumber = preferences.getBoolean("hidePhoneNumber", false);
            showID = preferences.getBoolean("showID", true);
            showDC = preferences.getBoolean("showDC", false);

            stickerSize = preferences.getFloat("stickerSize", 14.0f);
            hideStickerTime = preferences.getBoolean("hideStickerTime", false);
            unlimitedRecentStickers = preferences.getBoolean("unlimitedRecentStickers", false);
            sendMessageBeforeSendSticker = preferences.getBoolean("sendMessageBeforeSendSticker", false);

            hideSendAsChannel = preferences.getBoolean("hideSendAsChannel", false);
            hideKeyboardOnScroll = preferences.getBoolean("hideKeyboardOnScroll", true);
            disableReactions = preferences.getBoolean("disableReactions", false);
            disableJumpToNextChannel = preferences.getBoolean("disableJumpToNextChannel", false);
            disableGreetingSticker = preferences.getBoolean("disableGreetingSticker", false);
            dateOfForwardedMsg = preferences.getBoolean("dateOfForwardedMsg", false);
            showMessageID = preferences.getBoolean("showMessageID", false);

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
        useSystemFonts ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("useSystemFonts", useSystemFonts).apply();
    }

    public static void toggleDisableVibration() {
        disableVibration ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableVibration", disableVibration).apply();
    }

    public static void toggleBlurForAllThemes() {
        blurForAllThemes ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("blurForAllThemes", blurForAllThemes).apply();
    }

    public static void toggleHideAllChats() {
        hideAllChats ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("hideAllChats", hideAllChats).apply();
    }

    public static void toggleHidePhoneNumber() {
        hidePhoneNumber ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("hidePhoneNumber", hidePhoneNumber).apply();
    }

    public static void toggleShowID() {
        showID ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("showID", showID).apply();
    }

    public static void toggleShowDC() {
        showDC ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("showDC", showDC).apply();
    }

    public static void toggleChatsOnTitle() {
        chatsOnTitle ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("chatsOnTitle", chatsOnTitle).apply();
    }

    public static void toggleForceTabletMode() {
        forceTabletMode ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("forceTabletMode", forceTabletMode).apply();
    }

    public static void setStickerSize(float size) {
        stickerSize = size;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putFloat("stickerSize", stickerSize).apply();
    }

    public static void toggleHideStickerTime() {
        hideStickerTime ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("hideStickerTime", hideStickerTime).apply();
    }

    public static void toggleUnlimitedRecentStickers() {
        unlimitedRecentStickers ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("unlimitedRecentStickers", unlimitedRecentStickers).apply();
    }

    public static void toggleSendMessageBeforeSendSticker() {
        sendMessageBeforeSendSticker ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("sendMessageBeforeSendSticker", sendMessageBeforeSendSticker).apply();
    }

    public static void toggleHideSendAsChannel() {
        hideSendAsChannel ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("hideSendAsChannel", hideSendAsChannel).apply();
    }

    public static void toggleHideKeyboardOnScroll() {
        hideKeyboardOnScroll ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("hideKeyboardOnScroll", hideKeyboardOnScroll).apply();
    }

    public static void toggleDisableReactions() {
        disableReactions ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableReactions", disableReactions).apply();
    }

    public static void toggleDisableGreetingSticker() {
        disableGreetingSticker ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableGreetingSticker", disableGreetingSticker).apply();
    }

    public static void toggleDisableJumpToNextChannel() {
        disableJumpToNextChannel ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableJumpToNextChannel", disableJumpToNextChannel).apply();
    }

    public static void toggleArchiveOnPull() {
        archiveOnPull ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("archiveOnPull", archiveOnPull).apply();
    }

    public static void toggleDateOfForwardedMsg() {
        dateOfForwardedMsg ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("dateOfForwardedMsg", dateOfForwardedMsg).apply();
    }

    public static void toggleShowMessageID() {
        showMessageID ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("showMessageID", showMessageID).apply();
    }

    public static void toggleRearVideoMessages() {
        rearVideoMessages ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("rearVideoMessages", rearVideoMessages).apply();
    }

    public static void toggleDisableCamera() {
        disableCamera ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableCamera", disableCamera).apply();
    }

    public static void toggleDisableProximityEvents() {
        disableProximityEvents ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableProximityEvents", disableProximityEvents).apply();
    }

    public static void togglePauseOnMinimize() {
        pauseOnMinimize ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("pauseOnMinimize", pauseOnMinimize).apply();
    }

    public static void toggleDisablePlayback() {
        disablePlayback ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disablePlayback", disablePlayback).apply();
    }

    public static void toggleDrawerElements(int id) {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        switch (id) {
            case 1:
                newGroup ^= true;
                editor.putBoolean("newGroup", newGroup).apply();
                break;
            case 2:
                newSecretChat ^= true;
                editor.putBoolean("newSecretChat", newSecretChat).apply();
                break;
            case 3:
                newChannel ^= true;
                editor.putBoolean("newChannel", newChannel).apply();
                break;
            case 4:
                contacts ^= true;
                editor.putBoolean("contacts", contacts).apply();
                break;
            case 5:
                calls ^= true;
                editor.putBoolean("calls", calls).apply();
                break;
            case 6:
                peopleNearby ^= true;
                editor.putBoolean("peopleNearby", peopleNearby).apply();
                break;
            case 7:
                archivedChats ^= true;
                editor.putBoolean("archivedChats", archivedChats).apply();
                break;
            case 8:
                savedMessages ^= true;
                editor.putBoolean("savedMessages", savedMessages).apply();
                break;
            case 9:
                scanQr ^= true;
                editor.putBoolean("scanQr", scanQr).apply();
                break;
            case 10:
                inviteFriends ^= true;
                editor.putBoolean("inviteFriends", inviteFriends).apply();
                break;
            case 11:
                telegramFeatures ^= true;
                editor.putBoolean("telegramFeatures", telegramFeatures).apply();
                break;
        }
    }

    public static void changeChannelToSave(long id) {
        channelToSave = id;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putLong("channelToSave", channelToSave).apply();
    }

    public static void setEventType(int id) {
        eventType = id;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putInt("eventType", eventType).apply();
    }

    public static void toggleCenterTitle() {
        centerTitle ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("centerTitle", centerTitle).apply();
    }

    public static void toggleNewSwitchStyle() {
        newSwitchStyle ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("newSwitchStyle", newSwitchStyle).apply();
    }

    public static void toggleTransparentNavBar() {
        transparentNavBar ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("transparentNavBar", transparentNavBar).apply();
    }

    public static void toggleSquareFab() {
        squareFab ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("squareFab", squareFab).apply();
    }

    public static void toggleDisableUnarchiveSwipe() {
        disableUnarchiveSwipe ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableUnarchiveSwipe", disableUnarchiveSwipe).apply();
    }

    public static void toggleForcePacmanAnimation() {
        forcePacmanAnimation ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("forcePacmanAnimation", forcePacmanAnimation).apply();
    }

    public static void toggleDisableNumberRounding() {
        disableNumberRounding ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("disableNumberRounding", disableNumberRounding).apply();
    }

    public static void toggleFormatTimeWithSeconds() {
        formatTimeWithSeconds ^= true;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("formatTimeWithSeconds", formatTimeWithSeconds).apply();
    }
}
