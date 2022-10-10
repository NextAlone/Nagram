/*

 This is the source code of exteraGram for Android.

 We do not and cannot prevent the use of our code,
 but be respectful and credit the original author.

 Copyright @immat0x1, 2022.

*/

package com.exteragram.messenger;

import android.app.Activity;
import android.content.SharedPreferences;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.TLRPC;

import java.util.Arrays;

public class ExteraConfig {

    private static final Object sync = new Object();

    // appearance
    public static boolean useSystemFonts;
    public static boolean blurForAllThemes;
    public static boolean centerTitle;
    public static boolean newSwitchStyle;
    public static boolean disableDividers;
    public static boolean transparentNavBar;
    public static boolean squareFab;
    public static int eventType;
    public static boolean changeStatus, newGroup, newSecretChat, newChannel, contacts, calls, peopleNearby, archivedChats, savedMessages, scanQr, inviteFriends, telegramFeatures;

    // general
    public static float avatarCorners = 30.0f;
    public static int downloadSpeedBoost;
    public static boolean uploadSpeedBoost;
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
    public static boolean disableVibration;
    public static boolean disableAnimatedAvatars;

    // chats
    public static float stickerSize = 14.0f;
    public static int stickerShape;
    public static boolean hideStickerTime;
    public static boolean unlimitedRecentStickers;
    public static boolean premiumAutoPlayback;
    public static boolean hidePremiumStickersTab;
    public static boolean hideFeaturedEmojisTabs;

    public static boolean hideSendAsChannel;
    public static boolean hideKeyboardOnScroll;
    public static boolean disableReactions;
    public static boolean disableGreetingSticker;
    public static boolean disableJumpToNextChannel;
    public static boolean dateOfForwardedMsg;
    public static boolean showMessageID;
    public static boolean showActionTimestamps;
    public static boolean zalgoFilter;

    public static boolean rearVideoMessages;
    public static boolean disableCamera;
    public static boolean pauseOnMinimize;
    public static boolean disablePlayback;
    public static boolean disableProximityEvents;

    // updates
    public static long lastUpdateCheckTime;
    public static long updateScheduleTimestamp;
    public static boolean checkUpdatesOnLaunch;

    // other
    private static final int[] OFFICIAL_CHANNELS = {1233768168, 1524581881, 1571726392, 1632728092, 1638754701, 1779596027, 1172503281};
    private static final int[] DEVS = {963080346, 1282540315, 1374434073, 388099852, 1972014627, 168769611};
    public static long channelToSave;

    private static boolean configLoaded;

    public static SharedPreferences.Editor editor;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
                return;
            }

            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraconfig", Activity.MODE_PRIVATE);
            editor = preferences.edit();

            useSystemFonts = preferences.getBoolean("useSystemFonts", true);
            disableVibration = preferences.getBoolean("disableVibration", false);
            blurForAllThemes = preferences.getBoolean("blurForAllThemes", false);
            centerTitle = preferences.getBoolean("centerTitle", false);
            disableDividers = preferences.getBoolean("disableDividers", false);
            newSwitchStyle = preferences.getBoolean("newSwitchStyle", true);
            transparentNavBar = preferences.getBoolean("transparentNavBar", false);
            squareFab = preferences.getBoolean("squareFab", false);

            avatarCorners = preferences.getFloat("avatarCorners", 30.0f);
            downloadSpeedBoost = preferences.getInt("downloadSpeedBoost", 0);
            uploadSpeedBoost = preferences.getBoolean("uploadSpeedBoost", false);
            disableNumberRounding = preferences.getBoolean("disableNumberRounding", false);
            formatTimeWithSeconds = preferences.getBoolean("formatTimeWithSeconds", false);
            chatsOnTitle = preferences.getBoolean("chatsOnTitle", false);
            forceTabletMode = preferences.getBoolean("forceTabletMode", false);
            disableAnimatedAvatars = preferences.getBoolean("disableAnimatedAvatars", false);
            archiveOnPull = preferences.getBoolean("archiveOnPull", false);
            disableUnarchiveSwipe = preferences.getBoolean("disableUnarchiveSwipe", true);
            forcePacmanAnimation = preferences.getBoolean("forcePacmanAnimation", false);
            hidePhoneNumber = preferences.getBoolean("hidePhoneNumber", false);
            showID = preferences.getBoolean("showID", true);
            showDC = preferences.getBoolean("showDC", false);

            stickerSize = preferences.getFloat("stickerSize", 14.0f);
            stickerShape = preferences.getInt("stickerShape", 0);
            hideStickerTime = preferences.getBoolean("hideStickerTime", false);
            unlimitedRecentStickers = preferences.getBoolean("unlimitedRecentStickers", false);
            premiumAutoPlayback = preferences.getBoolean("premiumAutoPlayback", false);
            hidePremiumStickersTab = preferences.getBoolean("hidePremiumStickersTab", false);
            hideFeaturedEmojisTabs = preferences.getBoolean("hideFeaturedEmojisTabs", false);

            hideSendAsChannel = preferences.getBoolean("hideSendAsChannel", false);
            hideKeyboardOnScroll = preferences.getBoolean("hideKeyboardOnScroll", true);
            disableReactions = preferences.getBoolean("disableReactions", false);
            disableJumpToNextChannel = preferences.getBoolean("disableJumpToNextChannel", false);
            disableGreetingSticker = preferences.getBoolean("disableGreetingSticker", false);
            dateOfForwardedMsg = preferences.getBoolean("dateOfForwardedMsg", false);
            showMessageID = preferences.getBoolean("showMessageID", false);
            showActionTimestamps = preferences.getBoolean("showActionTimestamps", true);
            zalgoFilter = preferences.getBoolean("zalgoFilter", false);

            rearVideoMessages = preferences.getBoolean("rearVideoMessages", false);
            disableCamera = preferences.getBoolean("disableCamera", false);
            disableProximityEvents = preferences.getBoolean("disableProximityEvents", false);
            pauseOnMinimize = preferences.getBoolean("pauseOnMinimize", true);
            disablePlayback = preferences.getBoolean("disablePlayback", true);

            changeStatus = preferences.getBoolean("changeStatus", true);
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

            lastUpdateCheckTime = preferences.getLong("lastUpdateCheckTime", 0);
            updateScheduleTimestamp = preferences.getLong("updateScheduleTimestamp", 0);
            checkUpdatesOnLaunch = preferences.getBoolean("checkUpdatesOnLaunch", true);

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

    public static int getAvatarCorners(float size) {
        return getAvatarCorners(size, false);
    }

    public static int getAvatarCorners(float size, boolean toPx) {
        if (avatarCorners == 0) {
            return 0;
        } else {
            return (int) (avatarCorners * (size / 56.0f) * (toPx ? 1 : AndroidUtilities.density));
        }
    }

    public static void toggleDrawerElements(int id) {
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
            case 12:
                editor.putBoolean("changeStatus", changeStatus ^= true).apply();
                break;
        }
    }

    public static void setChannelToSave(long id) {
        editor.putLong("channelToSave", channelToSave = id).apply();
    }

    public static void toggleLogging() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Activity.MODE_PRIVATE).edit();
        editor.putBoolean("logsEnabled", BuildVars.LOGS_ENABLED ^= true).apply();
        if (!BuildVars.LOGS_ENABLED) FileLog.cleanupLogs();
    }

    public static boolean getLogging() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean("logsEnabled", BuildVars.DEBUG_VERSION);
    }
}
