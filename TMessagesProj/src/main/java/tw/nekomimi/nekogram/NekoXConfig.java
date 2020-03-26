package tw.nekomimi.nekogram;

import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;

public class NekoXConfig {

    public static int[] DEVELOPER_IDS = {896711046, 1121722278, 899300686, 339984997};

    private static SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekox_config", Context.MODE_PRIVATE);

    public static boolean disableChatAction;

    public static boolean developerModeEntrance;
    public static boolean developerMode;

    public static boolean disableFlagSecure;
    public static boolean disableScreenshotDetection;

    public static boolean showTestBackend;
    public static boolean showBotLogin;

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

    public static boolean disableSystemAccount;
    public static boolean disableProxyWhenVpnEnabled;
    public static boolean skipOpenLinkConfirm;


    static {

        disableChatAction = preferences.getBoolean("disable_chat_action", false);

        developerMode = preferences.getBoolean("developer_mode", false);

        disableFlagSecure = preferences.getBoolean("disable_flag_secure", false);
        disableScreenshotDetection = preferences.getBoolean("disable_screenshot_detection", false);

        showTestBackend = preferences.getBoolean("show_test_backend", false);
        showBotLogin = preferences.getBoolean("show_bot_login", false);

        sortByUnread = preferences.getBoolean("sort_by_unread", true);
        sortByUnmuted = preferences.getBoolean("sort_by_unmuted", false);
        sortByUser = preferences.getBoolean("sort_by_user", false);
        sortByContacts = preferences.getBoolean("sort_by_contacts", false);

        disableUndo = preferences.getBoolean("disable_undo", true);

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

    }

    public static void toggleDisableChatAction() {

        preferences.edit().putBoolean("disable_chat_action", disableChatAction = !disableChatAction).apply();

    }

    public static void toggleDeveloperMode() {

        preferences.edit().putBoolean("developer_mode", developerMode = !developerMode).apply();

    }

    public static void toggleDisableFlagSecure() {

        preferences.edit().putBoolean("disable_flag_secure", disableFlagSecure = !disableFlagSecure).apply();

    }

    public static void toggleDisableScreenshotDetection() {

        preferences.edit().putBoolean("disable_screenshot_detection", disableScreenshotDetection = !disableScreenshotDetection).apply();

    }

    public static void toggleShowTestBackend() {

        preferences.edit().putBoolean("show_test_backend", showTestBackend = !showTestBackend).apply();

    }

    public static void toggleShowBotLogin() {

        preferences.edit().putBoolean("show_bot_login", showBotLogin = !showBotLogin).apply();

    }

    public static void toggleSortByUnread() {

        preferences.edit().putBoolean("sort_by_unread", sortByUnread = !sortByUnread).apply();

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
}