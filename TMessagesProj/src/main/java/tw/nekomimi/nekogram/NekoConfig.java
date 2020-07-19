package tw.nekomimi.nekogram;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsService;
import org.telegram.messenger.R;

import java.util.Locale;

import cn.hutool.core.util.StrUtil;

public class NekoConfig {

    public static final int TITLE_TYPE_TEXT = 0;
    public static final int TITLE_TYPE_ICON = 1;
    public static final int TITLE_TYPE_MIX = 2;

    private static SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);

    public static boolean useIPv6;

    public static boolean useSystemEmoji;
    public static boolean ignoreBlocked;
    public static boolean hideProxySponsorChannel;
    public static boolean disablePhotoSideAction;
    public static boolean hideKeyboardOnChatScroll;
    public static boolean rearVideoMessages;
    public static boolean hideAllTab;
    public static boolean confirmAVMessage;
    public static boolean askBeforeCall;
    public static boolean disableNumberRounding;
    public static int mapPreviewProvider;
    public static float stickerSize;
    public static int translationProvider;
    public static int tabsTitleType;

    public static boolean showAddToSavedMessages;
    public static boolean showReport;
    public static boolean showViewHistory;
    public static boolean showAdminActions;
    public static boolean showChangePermissions;
    public static boolean showDeleteDownloadedFile;
    public static boolean showMessageDetails;
    public static boolean showTranslate;
    public static boolean showRepeat;

    public static boolean hidePhone;
    public static int typeface;
    public static boolean transparentStatusBar;
    public static boolean forceTablet;
    public static boolean openArchiveOnPull;
    public static boolean avatarAsDrawerBackground;
    public static boolean showTabsOnForward;
    public static int nameOrder;
    public static int eventType;
    public static boolean newYear;
    public static int actionBarDecoration;
    public static boolean unlimitedFavedStickers;
    public static boolean unlimitedPinnedDialogs;
    public static boolean residentNotification;

    public static boolean disableChatAction;
    public static boolean sortByUnread;
    public static boolean sortByUnmuted;
    public static boolean sortByUser;
    public static boolean sortByContacts;

    public static boolean disableUndo;

    public static boolean filterUsers;
    public static boolean filterContacts;
    public static boolean filterGroups;
    public static boolean filterChannels;
    public static boolean filterBots;
    public static boolean filterAdmins;
    public static boolean filterUnmuted;
    public static boolean filterUnread;
    public static boolean filterUnmutedAndUnread;

    public static boolean ignoreMutedCount;

    public static boolean disableSystemAccount;
    public static boolean disableProxyWhenVpnEnabled;
    public static boolean skipOpenLinkConfirm;

    public static boolean removeTitleEmoji;
    public static boolean useDefaultTheme;
    public static boolean showIdAndDc;

    public static String googleCloudTranslateKey;

    public static String cachePath;

    public static String translateToLang;
    public static String translateInputLang;

    public static boolean hideProxyByDefault;
    public static boolean useProxyItem;

    public static boolean disableAppBarShadow;
    public static boolean mediaPreview;

    public static boolean proxyAutoSwitch;
    public static boolean usePersianCalender;

    public static String formatLang(String name) {

        if (name == null) {

            return LocaleController.getString("Default", R.string.Default);

        } else {

            if (name.contains("-")) {

                return new Locale(StrUtil.subBefore(name, "-", false), StrUtil.subAfter(name, "-", false)).getDisplayName(LocaleController.getInstance().currentLocale);

            } else {

                return new Locale(name).getDisplayName(LocaleController.getInstance().currentLocale);

            }

        }

    }

    static {

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
        useSystemEmoji = preferences.getBoolean("useSystemEmoji", false);
        showTabsOnForward = preferences.getBoolean("showTabsOnForward", false);
        rearVideoMessages = preferences.getBoolean("rearVideoMessages", false);
        hideAllTab = preferences.getBoolean("hideAllTab", false);

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

        removeTitleEmoji = preferences.getBoolean("remove_title_emoji", true);
        ignoreMutedCount = preferences.getBoolean("ignore_muted_count", true);
        useDefaultTheme = preferences.getBoolean("use_default_theme", false);
        showIdAndDc = preferences.getBoolean("show_id_and_dc", false);

        googleCloudTranslateKey = preferences.getString("google_cloud_translate_key", null);
        cachePath = preferences.getString("cache_path", null);

        translateToLang = preferences.getString("trans_to_lang", null);
        translateInputLang = preferences.getString("trans_input_to_lang", "en");

        tabsTitleType = preferences.getInt("tabsTitleType", TITLE_TYPE_TEXT);
        confirmAVMessage = preferences.getBoolean("confirmAVMessage", false);
        askBeforeCall = preferences.getBoolean("askBeforeCall", false);
        disableNumberRounding = preferences.getBoolean("disableNumberRounding", false);

        hideProxyByDefault = preferences.getBoolean("hide_proxy_by_default", false);
        useProxyItem = preferences.getBoolean("use_proxy_item", true);

        disableAppBarShadow = preferences.getBoolean("disableAppBarShadow", false);
        mediaPreview = preferences.getBoolean("mediaPreview", false);

        proxyAutoSwitch = preferences.getBoolean("proxy_auto_switch", false);
        usePersianCalender = preferences.getBoolean("usePersianCalender", false);

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
        editor.commit();
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
        preferences.edit().putBoolean("useSystemEmoji", useSystemEmoji = !useSystemEmoji).apply();
    }

    public static void toggleDisableChatAction() {
        preferences.edit().putBoolean("disable_chat_action", disableChatAction = !disableChatAction).apply();
    }

    public static void toggleSortByUnread() {

        preferences.edit().putBoolean("sort_by_unread", sortByUnread = !sortByUnread).apply();

    }

    public static void toggleShowTabsOnForward() {
        showTabsOnForward = !showTabsOnForward;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTabsOnForward", showTabsOnForward);
        editor.apply();
    }

    public static void toggleRearVideoMessages() {
        rearVideoMessages = !rearVideoMessages;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("rearVideoMessages", rearVideoMessages);
        editor.apply();
    }

    public static void toggleHideAllTab() {
        hideAllTab = !hideAllTab;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideAllTab", hideAllTab);
        editor.apply();
    }

    public static void toggleSortByUnmuted() {

        preferences.edit().putBoolean("sort_by_unmuted", sortByUnmuted = !sortByUnmuted).apply();

    }

    public static void toggleSortByUser() {

        preferences.edit().putBoolean("sort_by_user", sortByUser = !sortByUser).apply();

    }

    public static void toggleSortByContacts() {

        preferences.edit().putBoolean("sort_by_contacts", sortByContacts = !sortByContacts).apply();

    }

    public static void toggleDisableUndo() {

        preferences.edit().putBoolean("disable_undo", disableUndo = !disableUndo).apply();

    }

    public static void toggleFilterUsers() {

        preferences.edit().putBoolean("filter_users", filterUsers = !filterUsers).apply();

    }

    public static void toggleFilterContacts() {

        preferences.edit().putBoolean("filter_contacts", filterContacts = !filterContacts).apply();

    }

    public static void toggleFilterGroups() {

        preferences.edit().putBoolean("filterGroups", filterGroups = !filterGroups).apply();

    }

    public static void toggleFilterChannels() {

        preferences.edit().putBoolean("filter_channels", filterChannels = !filterChannels).apply();

    }

    public static void toggleFilterBots() {

        preferences.edit().putBoolean("filter_bots", filterBots = !filterBots).apply();

    }

    public static void toggleFilterAdmins() {

        preferences.edit().putBoolean("filter_admins", filterAdmins = !filterAdmins).apply();

    }

    public static void toggleFilterUnmuted() {

        preferences.edit().putBoolean("filter_unmuted", filterUnmuted = !filterUnmuted).apply();

    }

    public static void toggleDisableFilterUnread() {

        preferences.edit().putBoolean("filter_unread", filterUnread = !filterUnread).apply();

    }

    public static void toggleFilterUnmutedAndUnread() {

        preferences.edit().putBoolean("filter_unmuted_and_unread", filterUnmutedAndUnread = !filterUnmutedAndUnread).apply();

    }

    public static void toggleDisableSystemAccount() {

        preferences.edit().putBoolean("disable_system_account", disableSystemAccount = !disableSystemAccount).apply();

    }

    public static void toggleDisableProxyWhenVpnEnabled() {

        preferences.edit().putBoolean("disable_proxy_when_vpn_enabled", disableProxyWhenVpnEnabled = !disableProxyWhenVpnEnabled).apply();

    }

    public static void toggleSkipOpenLinkConfirm() {

        preferences.edit().putBoolean("skip_open_link_confirm", skipOpenLinkConfirm = !skipOpenLinkConfirm).apply();

    }

    public static void toggleRemoveTitleEmoji() {

        preferences.edit().putBoolean("remove_title_emoji", removeTitleEmoji = !removeTitleEmoji).apply();

    }

    public static void toggleIgnoredMutedCount() {

        preferences.edit().putBoolean("ignore_muted_count", ignoreMutedCount = !ignoreMutedCount).apply();

    }

    public static void toggleUseDefaultTheme() {

        preferences.edit().putBoolean("use_default_theme", useDefaultTheme = !useDefaultTheme).apply();

    }

    public static void toggleShowIdAndDc() {

        preferences.edit().putBoolean("show_id_and_dc", showIdAndDc = !showIdAndDc).apply();

    }

    public static void setGoogleTranslateKey(String key) {

        preferences.edit().putString("google_cloud_translate_key", googleCloudTranslateKey = key).apply();

    }

    public static void setCachePath(String cachePath) {

        preferences.edit().putString("cache_path", NekoConfig.cachePath = cachePath).apply();

    }

    public static void setTranslateToLang(String toLang) {

        preferences.edit().putString("trans_to_lang", translateToLang = toLang).apply();

    }

    public static void setTranslateInputToLang(String toLang) {

        preferences.edit().putString("trans_input_to_lang", translateInputLang = toLang).apply();

    }

    public static void setTabsTitleType(int type) {

        preferences.edit().putInt("tabsTitleType", tabsTitleType = type).apply();

    }

    public static void toggleConfirmAVMessage() {

        preferences.edit().putBoolean("confirmAVMessage", confirmAVMessage = !confirmAVMessage).apply();

    }

    public static void toggleAskBeforeCall() {

        preferences.edit().putBoolean("askBeforeCall", askBeforeCall = !askBeforeCall).apply();

    }

    public static void toggleHideProxyByDefault() {

        preferences.edit().putBoolean("hide_proxy_by_default", hideProxyByDefault = !hideProxyByDefault).apply();

        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged);

    }

    public static void toggleUseProxyItem() {

        preferences.edit().putBoolean("use_proxy_item",useProxyItem = !useProxyItem).apply();

        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged);

    }

    public static void toggleDisableNumberRounding() {

        preferences.edit().putBoolean("disableNumberRounding",disableNumberRounding = !disableNumberRounding).apply();

    }

    public static void toggleDisableAppBarShadow() {

        preferences.edit().putBoolean("disableAppBarShadow",disableAppBarShadow = !disableAppBarShadow).apply();

    }

    public static void toggleMediaPreview() {

        preferences.edit().putBoolean("mediaPreview",mediaPreview = !mediaPreview).apply();

    }

    public static void toggleProxyAutoSwitch() {

        preferences.edit().putBoolean("proxy_auto_switch",proxyAutoSwitch = !proxyAutoSwitch).apply();

    }

    public static void toggleUsePersianCalender() {

        preferences.edit().putBoolean("usePersianCalender",usePersianCalender = !usePersianCalender).apply();

    }


}