package tw.nekomimi.nekogram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationsService;
import org.telegram.messenger.SharedConfig;

import tw.nekomimi.nekogram.database.NitritesKt;

@SuppressLint("ApplySharedPref")
public class NekoConfig {

    private static final Object sync = new Object();
    public static boolean useIPv6 = false;
    //public static boolean showHiddenFeature = false;

    public static boolean useSystemEmoji = SharedConfig.useSystemEmoji;
    public static boolean ignoreBlocked = false;
    public static boolean hideProxySponsorChannel = false;
    public static boolean disablePhotoSideAction = true;
    public static boolean hideKeyboardOnChatScroll = false;
    public static int mapPreviewProvider = 0;
    public static float stickerSize = 14.0f;
    public static int translationProvider = 1;

    public static boolean showAddToSavedMessages = true;
    public static boolean showReport = false;
    public static boolean showPrPr = false;
    public static boolean showViewHistory = true;
    public static boolean showAdminActions = true;
    public static boolean showChangePermissions = true;
    public static boolean showDeleteDownloadedFile = true;
    public static boolean showMessageDetails = false;
    public static boolean showTranslate = true;
    public static boolean showRepeat = true;

    public static boolean hidePhone = true;
    public static int typeface = 0;
    public static boolean transparentStatusBar = false;
    public static boolean forceTablet = false;
    public static boolean openArchiveOnPull = false;
    public static boolean avatarAsDrawerBackground = true;
    public static int nameOrder = 1;
    public static int eventType = 0;
    public static boolean newYear = false;
    public static int actionBarDecoration = 0;
    public static boolean unlimitedFavedStickers = false;
    public static boolean unlimitedPinnedDialogs = false;

    public static boolean residentNotification = false;

    private static boolean configLoaded;

    static {
        loadConfig();
    }

    public static void saveConfig() {
        synchronized (sync) {
            try {
                SharedPreferences preferences = NitritesKt.openMainSharedPreference("nekoconfing");
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("useIPv6", useIPv6);
                editor.putBoolean("hidePhone", hidePhone);
                editor.putBoolean("ignoreBlocked", ignoreBlocked);
                editor.putBoolean("forceTablet", forceTablet);
                editor.putBoolean("transparentStatusBar", transparentStatusBar);
                editor.putBoolean("residentNotification", residentNotification);
                editor.putBoolean("hideProxySponsorChannel", hideProxySponsorChannel);
                editor.putBoolean("showAddToSavedMessages", showAddToSavedMessages);
                editor.putBoolean("showReport", showReport);
                editor.putBoolean("showPrPr", showPrPr);
                editor.putBoolean("showViewHistory", showViewHistory);
                editor.putBoolean("showAdminActions", showAdminActions);
                editor.putBoolean("showChangePermissions", showChangePermissions);
                editor.putBoolean("showDeleteDownloadedFile", showDeleteDownloadedFile);
                editor.putBoolean("showMessageDetails", showMessageDetails);
                editor.putBoolean("showTranslate", showTranslate);
                editor.putBoolean("showRepeat", showRepeat);
                editor.putBoolean("newYear", newYear);
                editor.putBoolean("unlimitedFavedStickers", unlimitedFavedStickers);
                editor.putBoolean("unlimitedPinnedDialogs", unlimitedPinnedDialogs);
                editor.putBoolean("disablePhotoSideAction", disablePhotoSideAction);
                editor.putBoolean("hideKeyboardOnChatScroll", hideKeyboardOnChatScroll);
                editor.putBoolean("openArchiveOnPull", openArchiveOnPull);
                // editor.putBoolean("showHiddenFeature", showHiddenFeature);
                editor.putBoolean("avatarAsDrawerBackground", avatarAsDrawerBackground);
                editor.putBoolean("useSystemEmoji", useSystemEmoji);
                editor.putFloat("stickerSize", stickerSize);
                editor.putInt("typeface", typeface);
                editor.putInt("nameOrder", nameOrder);
                editor.putInt("mapPreviewProvider", mapPreviewProvider);
                editor.putInt("translationProvider", translationProvider);
                editor.putInt("eventType", eventType);
                editor.putInt("actionBarDecoration", actionBarDecoration);
                editor.apply();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
                return;
            }

            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
            useIPv6 = preferences.getBoolean("useIPv6", false);
            hidePhone = preferences.getBoolean("hidePhone", true);
            ignoreBlocked = preferences.getBoolean("ignoreBlocked", false);
            forceTablet = preferences.getBoolean("forceTablet", false);
            typeface = preferences.getInt("typeface", 0);
            nameOrder = preferences.getInt("nameOrder", 1);
            mapPreviewProvider = preferences.getInt("mapPreviewProvider", 0);
            transparentStatusBar = preferences.getBoolean("transparentStatusBar", false);
            residentNotification = preferences.getBoolean("residentNotification", false);
            hideProxySponsorChannel = preferences.getBoolean("hideProxySponsorChannel", false);
            showAddToSavedMessages = preferences.getBoolean("showAddToSavedMessages", true);
            showReport = preferences.getBoolean("showReport", false);
            showPrPr = preferences.getBoolean("showPrPr", false);
            showViewHistory = preferences.getBoolean("showViewHistory", true);
            showAdminActions = preferences.getBoolean("showAdminActions", true);
            showChangePermissions = preferences.getBoolean("showChangePermissions", true);
            showDeleteDownloadedFile = preferences.getBoolean("showDeleteDownloadedFile", true);
            showMessageDetails = preferences.getBoolean("showMessageDetails", false);
            showTranslate = preferences.getBoolean("showTranslate", true);
            showRepeat = preferences.getBoolean("showRepeat", true);
            eventType = preferences.getInt("eventType", 0);
            actionBarDecoration = preferences.getInt("actionBarDecoration", 0);
            newYear = preferences.getBoolean("newYear", false);
            stickerSize = preferences.getFloat("stickerSize", 14.0f);
            unlimitedFavedStickers = preferences.getBoolean("unlimitedFavedStickers", false);
            unlimitedPinnedDialogs = preferences.getBoolean("unlimitedPinnedDialogs", false);
            translationProvider = preferences.getInt("translationProvider", 1);
            disablePhotoSideAction = preferences.getBoolean("disablePhotoSideAction", true);
            openArchiveOnPull = preferences.getBoolean("openArchiveOnPull", false);
            //showHiddenFeature = preferences.getBoolean("showHiddenFeature", false);
            hideKeyboardOnChatScroll = preferences.getBoolean("hideKeyboardOnChatScroll", false);
            avatarAsDrawerBackground = preferences.getBoolean("avatarAsDrawerBackground", true);
            useSystemEmoji = preferences.getBoolean("useSystemEmoji", SharedConfig.useSystemEmoji);
            configLoaded = true;
        }
    }

    public static void toggleShowAddToSavedMessages() {
        showAddToSavedMessages = !showAddToSavedMessages;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showAddToSavedMessages", showAddToSavedMessages);
        editor.apply();
    }

    public static void toggleShowReport() {
        showReport = !showReport;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showReport", showReport);
        editor.apply();
    }

    public static void toggleShowViewHistory() {
        showViewHistory = !showViewHistory;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showViewHistory", showViewHistory);
        editor.apply();
    }

    public static void toggleShowPrPr() {
        showPrPr = !showPrPr;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showPrPr", showPrPr);
        editor.apply();
    }

    public static void toggleShowAdminActions() {
        showAdminActions = !showAdminActions;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showAdminActions", showAdminActions);
        editor.apply();
    }

    public static void toggleShowChangePermissions() {
        showChangePermissions = !showChangePermissions;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showChangePermissions", showChangePermissions);
        editor.apply();
    }

    public static void toggleShowDeleteDownloadedFile() {
        showDeleteDownloadedFile = !showDeleteDownloadedFile;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showDeleteDownloadedFile", showDeleteDownloadedFile);
        editor.apply();
    }

    public static void toggleShowMessageDetails() {
        showMessageDetails = !showMessageDetails;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showMessageDetails", showMessageDetails);
        editor.apply();
    }

    public static void toggleShowRepeat() {
        showRepeat = !showRepeat;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showRepeat", showRepeat);
        editor.apply();
    }

    public static void toggleIPv6() {
        useIPv6 = !useIPv6;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("useIPv6", useIPv6);
        editor.apply();
    }

    public static void toggleHidePhone() {
        hidePhone = !hidePhone;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hidePhone", hidePhone);
        editor.apply();
    }

    public static void toggleIgnoreBlocked() {
        ignoreBlocked = !ignoreBlocked;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("ignoreBlocked", ignoreBlocked);
        editor.apply();
    }

    public static void toggleForceTablet() {
        forceTablet = !forceTablet;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("forceTablet", forceTablet);
        editor.apply();
    }

    public static void toggleTypeface() {
        typeface = typeface == 0 ? 1 : 0;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("typeface", typeface);
        editor.apply();
    }

    public static void setNameOrder(int order) {
        nameOrder = order;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("nameOrder", nameOrder);
        editor.apply();

        LocaleController.getInstance().recreateFormatters();
    }

    public static void setMapPreviewProvider(int provider) {
        mapPreviewProvider = provider;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("mapPreviewProvider", mapPreviewProvider);
        editor.apply();
    }

    public static void toggleTransparentStatusBar() {
        transparentStatusBar = !transparentStatusBar;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("transparentStatusBar", transparentStatusBar);
        editor.apply();
    }

    public static void toggleResidentNotification() {
        residentNotification = !residentNotification;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("residentNotification", residentNotification);
        editor.apply();
        ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, NotificationsService.class));
        ApplicationLoader.startPushService();
    }

    public static void toggleHideProxySponsorChannel() {
        hideProxySponsorChannel = !hideProxySponsorChannel;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideProxySponsorChannel", hideProxySponsorChannel);
        editor.apply();
    }

    public static void setEventType(int type) {
        eventType = type;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("eventType", eventType);
        editor.apply();
    }

    public static void setActionBarDecoration(int decoration) {
        actionBarDecoration = decoration;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("actionBarDecoration", actionBarDecoration);
        editor.apply();
    }

    public static void toggleNewYear() {
        newYear = !newYear;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("newYear", newYear);
        editor.apply();
    }

    public static void toggleUnlimitedFavedStickers() {
        unlimitedFavedStickers = !unlimitedFavedStickers;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("unlimitedFavedStickers", unlimitedFavedStickers);
        editor.apply();
    }

    public static void toggleUnlimitedPinnedDialogs() {
        unlimitedPinnedDialogs = !unlimitedPinnedDialogs;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("unlimitedPinnedDialogs", unlimitedPinnedDialogs);
        editor.apply();
    }

    public static void toggleShowTranslate() {
        showTranslate = !showTranslate;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTranslate", showTranslate);
        editor.apply();
    }

    public static void setStickerSize(float size) {
        stickerSize = size;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("stickerSize", stickerSize);
        editor.apply();
    }

    public static void setTranslationProvider(int provider) {
        translationProvider = provider;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("translationProvider", translationProvider);
        editor.apply();
    }

    public static void toggleDisablePhotoSideAction() {
        disablePhotoSideAction = !disablePhotoSideAction;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disablePhotoSideAction", disablePhotoSideAction);
        editor.apply();
    }

    public static void toggleOpenArchiveOnPull() {
        openArchiveOnPull = !openArchiveOnPull;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("openArchiveOnPull", openArchiveOnPull);
        editor.apply();
    }

    /*public static void toggleShowHiddenFeature() {
        showHiddenFeature = !showHiddenFeature;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showHiddenFeature", showHiddenFeature);
        editor.apply();
    } */

    public static void toggleHideKeyboardOnChatScroll() {
        hideKeyboardOnChatScroll = !hideKeyboardOnChatScroll;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideKeyboardOnChatScroll", hideKeyboardOnChatScroll);
        editor.apply();
    }

    public static void toggleAvatarAsDrawerBackground() {
        avatarAsDrawerBackground = !avatarAsDrawerBackground;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("avatarAsDrawerBackground", avatarAsDrawerBackground);
        editor.apply();
    }

    public static void toggleUseSystemEmoji() {
        useSystemEmoji = !useSystemEmoji;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("useSystemEmoji", useSystemEmoji);
        editor.commit();
    }
}