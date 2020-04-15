package tw.nekomimi.nekogram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationsService;
import org.telegram.messenger.SharedConfig;

import tw.nekomimi.nekogram.database.NitritesKt;
import tw.nekomimi.nekogram.transtale.TranslateDb;
import tw.nekomimi.nekogram.utils.UIUtil;

@SuppressLint("ApplySharedPref")
public class NekoConfig {

    public static SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);

    public static boolean useIPv6 = preferences.getBoolean("useIPv6", false);
    //public static boolean showHiddenFeature = false;

    public static boolean useSystemEmoji = preferences.getBoolean("useSystemEmoji", SharedConfig.useSystemEmoji);
    public static boolean ignoreBlocked = preferences.getBoolean("ignoreBlocked", false);
    public static boolean hideProxySponsorChannel = preferences.getBoolean("hideProxySponsorChannel", false);
    public static boolean disablePhotoSideAction = preferences.getBoolean("disablePhotoSideAction", true);
    //showHiddenFeature = preferences.getBoolean("showHiddenFeature", false);
    public static boolean hideKeyboardOnChatScroll = preferences.getBoolean("hideKeyboardOnChatScroll", false);
    public static int mapPreviewProvider = preferences.getInt("mapPreviewProvider", 0);
    public static float stickerSize = preferences.getFloat("stickerSize", 14.0f);
    public static int translationProvider = preferences.getInt("translationProvider", 1);

    public static boolean showAddToSavedMessages = preferences.getBoolean("showAddToSavedMessages", true);
    public static boolean showReport = preferences.getBoolean("showReport", false);
    public static boolean showPrPr = preferences.getBoolean("showPrPr", false);
    public static boolean showViewHistory = preferences.getBoolean("showViewHistory", true);
    public static boolean showAdminActions = preferences.getBoolean("showAdminActions", true);
    public static boolean showChangePermissions = preferences.getBoolean("showChangePermissions", true);
    public static boolean showDeleteDownloadedFile = preferences.getBoolean("showDeleteDownloadedFile", true);
    public static boolean showMessageDetails = preferences.getBoolean("showMessageDetails", false);
    public static boolean showTranslate = preferences.getBoolean("showTranslate", true);
    public static boolean showRepeat = preferences.getBoolean("showRepeat", true);

    public static boolean hidePhone = preferences.getBoolean("hidePhone", true);
    public static int typeface = preferences.getInt("typeface", 0);
    public static boolean transparentStatusBar = preferences.getBoolean("transparentStatusBar", false);
    public static boolean forceTablet = preferences.getBoolean("forceTablet", false);
    public static boolean openArchiveOnPull = preferences.getBoolean("openArchiveOnPull", false);
    public static boolean avatarAsDrawerBackground = preferences.getBoolean("avatarAsDrawerBackground", true);
    public static boolean showTabsOnForward;
    public static int nameOrder = preferences.getInt("nameOrder", 1);
    public static int eventType = preferences.getInt("eventType", 0);
    public static boolean newYear = preferences.getBoolean("newYear", false);
    public static int actionBarDecoration = preferences.getInt("actionBarDecoration", 0);
    public static boolean unlimitedFavedStickers = preferences.getBoolean("unlimitedFavedStickers", false);
    public static boolean unlimitedPinnedDialogs = preferences.getBoolean("unlimitedPinnedDialogs", false);

    public static boolean residentNotification = preferences.getBoolean("residentNotification", false);

    public static boolean disableChatAction = preferences.getBoolean("disable_chat_action", false);
    public static boolean sortByUnread = preferences.getBoolean("sort_by_unread", false);
    public static boolean sortByUnmuted = preferences.getBoolean("sort_by_unmuted", true);
    public static boolean sortByUser = preferences.getBoolean("sort_by_user", true);
    public static boolean sortByContacts = preferences.getBoolean("sort_by_contacts", true);

    public static boolean disableUndo = preferences.getBoolean("disable_undo", false);

    public static boolean filterUsers = preferences.getBoolean("filter_users", true);
    public static boolean filterContacts = preferences.getBoolean("filter_contacts", true);
    public static boolean filterGroups = preferences.getBoolean("filter_groups", true);
    public static boolean filterChannels = preferences.getBoolean("filter_channels", true);
    public static boolean filterBots = preferences.getBoolean("filter_bots", true);
    public static boolean filterAdmins = preferences.getBoolean("filter_admins", true);
    public static boolean filterUnmuted = preferences.getBoolean("filter_unmuted", true);
    public static boolean filterUnread = preferences.getBoolean("filter_unread", true);
    public static boolean filterUnmutedAndUnread = preferences.getBoolean("filter_unmuted_and_unread", true);

    public static boolean ignoreMutedCount;

    public static boolean disableSystemAccount = preferences.getBoolean("disable_system_account", false);
    public static boolean disableProxyWhenVpnEnabled;
    public static boolean skipOpenLinkConfirm;

    public static boolean removeTitleEmoji;
    public static boolean hidePublicProxy;
    public static boolean useDefaultTheme;
    public static boolean showIdAndDc;

    private static volatile boolean loaded;

    public static void loadConfig() {

        synchronized (NekoConfig.class) {

            if (loaded) return;

            loaded = true;

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
            hideKeyboardOnChatScroll = preferences.getBoolean("hideKeyboardOnChatScroll", false);
            avatarAsDrawerBackground = preferences.getBoolean("avatarAsDrawerBackground", true);
            useSystemEmoji = preferences.getBoolean("useSystemEmoji", SharedConfig.useSystemEmoji);
            showTabsOnForward = preferences.getBoolean("showTabsOnForward", showTabsOnForward);

            disableChatAction = preferences.getBoolean("disable_chat_action", false);
            sortByUnread = preferences.getBoolean("sort_by_unread", false);
            sortByUnmuted = preferences.getBoolean("sort_by_unmuted", true);
            sortByUser = preferences.getBoolean("sort_by_user", true);
            sortByContacts = preferences.getBoolean("sort_by_contacts", true);

            disableUndo = preferences.getBoolean("disable_undo", false);

            filterUsers = preferences.getBoolean("filter_users", true);
            filterContacts = preferences.getBoolean("filter_contacts", true);
            filterGroups = preferences.getBoolean("filter_groups", true);
            filterChannels = preferences.getBoolean("filter_channels", true);
            filterBots = preferences.getBoolean("filter_bots", true);
            filterAdmins = preferences.getBoolean("filter_admins", true);
            filterUnmuted = preferences.getBoolean("filter_unmuted", true);
            filterUnread = preferences.getBoolean("filter_unread", true);
            filterUnmutedAndUnread = preferences.getBoolean("filter_unmuted_and_unread", true);

            disableSystemAccount = preferences.getBoolean("disable_system_account", false);
            disableProxyWhenVpnEnabled = preferences.getBoolean("disable_proxy_when_vpn_enabled", false);
            skipOpenLinkConfirm = preferences.getBoolean("skip_open_link_confirm", false);

            removeTitleEmoji = preferences.getBoolean("remove_title_emoji", false);
            ignoreMutedCount = preferences.getBoolean("ignore_muted_count", true);
            hidePublicProxy = preferences.getBoolean("hide_public_proxy", false);

            useDefaultTheme = preferences.getBoolean("use_default_theme", false);
            showIdAndDc = preferences.getBoolean("show_id_and_dc", false);

        }

    }

    static {

        loadConfig();

    }

    public static void toggleShowAddToSavedMessages() {
        showAddToSavedMessages = !showAddToSavedMessages;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showAddToSavedMessages", showAddToSavedMessages);
        editor.commit();
    }

    public static void toggleShowReport() {
        showReport = !showReport;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showReport", showReport);
        editor.commit();
    }

    public static void toggleShowViewHistory() {
        showViewHistory = !showViewHistory;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showViewHistory", showViewHistory);
        editor.commit();
    }

    public static void toggleShowPrPr() {
        showPrPr = !showPrPr;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showPrPr", showPrPr);
        editor.commit();
    }

    public static void toggleShowAdminActions() {
        showAdminActions = !showAdminActions;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showAdminActions", showAdminActions);
        editor.commit();
    }

    public static void toggleShowChangePermissions() {
        showChangePermissions = !showChangePermissions;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showChangePermissions", showChangePermissions);
        editor.commit();
    }

    public static void toggleShowDeleteDownloadedFile() {
        showDeleteDownloadedFile = !showDeleteDownloadedFile;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showDeleteDownloadedFile", showDeleteDownloadedFile);
        editor.commit();
    }

    public static void toggleShowMessageDetails() {
        showMessageDetails = !showMessageDetails;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showMessageDetails", showMessageDetails);
        editor.commit();
    }

    public static void toggleShowRepeat() {
        showRepeat = !showRepeat;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showRepeat", showRepeat);
        editor.commit();
    }

    public static void toggleIPv6() {
        useIPv6 = !useIPv6;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("useIPv6", useIPv6);
        editor.commit();
    }

    public static void toggleHidePhone() {
        hidePhone = !hidePhone;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hidePhone", hidePhone);
        editor.commit();
    }

    public static void toggleIgnoreBlocked() {
        ignoreBlocked = !ignoreBlocked;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("ignoreBlocked", ignoreBlocked);
        editor.commit();
    }

    public static void toggleForceTablet() {
        forceTablet = !forceTablet;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("forceTablet", forceTablet);
        editor.commit();
    }

    public static void toggleTypeface() {
        typeface = typeface == 0 ? 1 : 0;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("typeface", typeface);
        editor.commit();
    }

    public static void setNameOrder(int order) {
        nameOrder = order;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("nameOrder", nameOrder);
        editor.commit();

        LocaleController.getInstance().recreateFormatters();
    }

    public static void setMapPreviewProvider(int provider) {
        mapPreviewProvider = provider;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("mapPreviewProvider", mapPreviewProvider);
        editor.commit();
    }

    public static void toggleTransparentStatusBar() {
        transparentStatusBar = !transparentStatusBar;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("transparentStatusBar", transparentStatusBar);
        editor.commit();
    }

    public static void toggleResidentNotification() {
        residentNotification = !residentNotification;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("residentNotification", residentNotification);
        editor.commit();
        ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, NotificationsService.class));
        ApplicationLoader.startPushService();
    }

    public static void toggleHideProxySponsorChannel() {
        hideProxySponsorChannel = !hideProxySponsorChannel;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideProxySponsorChannel", hideProxySponsorChannel);
        editor.commit();
    }

    public static void setEventType(int type) {
        eventType = type;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("eventType", eventType);
        editor.commit();
    }

    public static void setActionBarDecoration(int decoration) {
        actionBarDecoration = decoration;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("actionBarDecoration", actionBarDecoration);
        editor.commit();
    }

    public static void toggleNewYear() {
        newYear = !newYear;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("newYear", newYear);
        editor.commit();
    }

    public static void toggleUnlimitedFavedStickers() {
        unlimitedFavedStickers = !unlimitedFavedStickers;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("unlimitedFavedStickers", unlimitedFavedStickers);
        editor.commit();
    }

    public static void toggleUnlimitedPinnedDialogs() {
        unlimitedPinnedDialogs = !unlimitedPinnedDialogs;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("unlimitedPinnedDialogs", unlimitedPinnedDialogs);
        editor.commit();
    }

    public static void toggleShowTranslate() {
        showTranslate = !showTranslate;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTranslate", showTranslate);
        editor.commit();
    }

    public static void setStickerSize(float size) {
        stickerSize = size;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("stickerSize", stickerSize);
        editor.commit();
    }

    public static void setTranslationProvider(int provider) {
        translationProvider = provider;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("translationProvider", translationProvider);
        editor.commit();
        UIUtil.runOnIoDispatcher(TranslateDb::clear);
    }

    public static void toggleDisablePhotoSideAction() {
        disablePhotoSideAction = !disablePhotoSideAction;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disablePhotoSideAction", disablePhotoSideAction);
        editor.commit();
    }

    public static void toggleOpenArchiveOnPull() {
        openArchiveOnPull = !openArchiveOnPull;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("openArchiveOnPull", openArchiveOnPull);
        editor.commit();
    }

    /*public static void toggleShowHiddenFeature() {
        showHiddenFeature = !showHiddenFeature;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showHiddenFeature", showHiddenFeature);
        editor.commit();
    } */

    public static void toggleHideKeyboardOnChatScroll() {
        hideKeyboardOnChatScroll = !hideKeyboardOnChatScroll;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideKeyboardOnChatScroll", hideKeyboardOnChatScroll);
        editor.commit();
    }

    public static void toggleAvatarAsDrawerBackground() {
        avatarAsDrawerBackground = !avatarAsDrawerBackground;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("avatarAsDrawerBackground", avatarAsDrawerBackground);
        editor.commit();
    }

    public static void toggleUseSystemEmoji() {
        preferences.edit().putBoolean("useSystemEmoji", useSystemEmoji = !useSystemEmoji).commit();
    }

    public static void toggleDisableChatAction() {
        preferences.edit().putBoolean("disable_chat_action", disableChatAction = !disableChatAction).commit();
    }

    public static void toggleSortByUnread() {

        preferences.edit().putBoolean("sort_by_unread", sortByUnread = !sortByUnread).commit();

    }

    public static void toggleShowTabsOnForward() {
        showTabsOnForward = !showTabsOnForward;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTabsOnForward", showTabsOnForward);
        editor.commit();
    }

    public static void toggleSortByUnmuted() {

        preferences.edit().putBoolean("sort_by_unmuted", sortByUnmuted = !sortByUnmuted).commit();

    }

    public static void toggleSortByUser() {

        preferences.edit().putBoolean("sort_by_user", sortByUser = !sortByUser).commit();

    }

    public static void toggleSortByContacts() {

        preferences.edit().putBoolean("sort_by_contacts", sortByContacts = !sortByContacts).commit();

    }

    public static void toggleDisableUndo() {

        preferences.edit().putBoolean("disable_undo", disableUndo = !disableUndo).commit();

    }

    public static void toggleFilterUsers() {

        preferences.edit().putBoolean("filter_users", filterUsers = !filterUsers).commit();

    }

    public static void toggleFilterContacts() {

        preferences.edit().putBoolean("filter_contacts", filterContacts = !filterContacts).commit();

    }

    public static void toggleFilterGroups() {

        preferences.edit().putBoolean("filterGroups", filterGroups = !filterGroups).commit();

    }

    public static void toggleFilterChannels() {

        preferences.edit().putBoolean("filter_channels", filterChannels = !filterChannels).commit();

    }

    public static void toggleFilterBots() {

        preferences.edit().putBoolean("filter_bots", filterBots = !filterBots).commit();

    }

    public static void toggleFilterAdmins() {

        preferences.edit().putBoolean("filter_admins", filterAdmins = !filterAdmins).commit();

    }

    public static void toggleFilterUnmuted() {

        preferences.edit().putBoolean("filter_unmuted", filterUnmuted = !filterUnmuted).commit();

    }

    public static void toggleDisableFilterUnread() {

        preferences.edit().putBoolean("filter_unread", filterUnread = !filterUnread).commit();

    }

    public static void toggleFilterUnmutedAndUnread() {

        preferences.edit().putBoolean("filter_unmuted_and_unread", filterUnmutedAndUnread = !filterUnmutedAndUnread).commit();

    }

    public static void toggleDisableSystemAccount() {

        preferences.edit().putBoolean("disable_system_account", disableSystemAccount = !disableSystemAccount).commit();

    }

    public static void toggleDisableProxyWhenVpnEnabled() {

        preferences.edit().putBoolean("disable_proxy_when_vpn_enabled", disableProxyWhenVpnEnabled = !disableProxyWhenVpnEnabled).commit();

    }

    public static void toggleSkipOpenLinkConfirm() {

        preferences.edit().putBoolean("skip_open_link_confirm", skipOpenLinkConfirm = !skipOpenLinkConfirm).commit();

    }

    public static void toggleRemoveTitleEmoji() {

        preferences.edit().putBoolean("remove_title_emoji", removeTitleEmoji = !removeTitleEmoji).commit();

    }

    public static void toggleIgnoredMutedCount() {

        preferences.edit().putBoolean("ignore_muted_count", ignoreMutedCount = !ignoreMutedCount).commit();

    }

    public static void toggleHidePublicProxy() {

        preferences.edit().putBoolean("hide_public_proxy", hidePublicProxy = !hidePublicProxy).commit();

    }

    public static void toggleUseDefaultTheme() {

        preferences.edit().putBoolean("use_default_theme", useDefaultTheme = !useDefaultTheme).commit();

    }

    public static void toggleShowIdAndDc() {

        preferences.edit().putBoolean("show_id_and_dc", showIdAndDc = !showIdAndDc).commit();

    }

}