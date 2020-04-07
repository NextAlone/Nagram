package tw.nekomimi.nekogram;

import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;

import java.util.LinkedList;

import tw.nekomimi.nekogram.database.NitritesKt;

public class NekoXConfig {

    public static String FAQ_URL = "https://telegra.ph/NekoX-%E5%B8%B8%E8%A6%8B%E5%95%8F%E9%A1%8C-03-31";

    protected static SharedPreferences preferences = NitritesKt.openMainSharedPreference("nekox_config");

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

    public static boolean ignoreMutedCount;

    public static boolean disableSystemAccount;
    public static boolean disableProxyWhenVpnEnabled;
    public static boolean skipOpenLinkConfirm;

    public static boolean removeTitleEmoji;
    public static boolean hidePublicProxy;

    static {

        disableChatAction = preferences.getBoolean("disable_chat_action", false);

        developerMode = preferences.getBoolean("developer_mode", false);

        disableFlagSecure = preferences.getBoolean("disable_flag_secure", false);
        disableScreenshotDetection = preferences.getBoolean("disable_screenshot_detection", false);

        showTestBackend = preferences.getBoolean("show_test_backend", false);
        showBotLogin = preferences.getBoolean("show_bot_login", false);

        sortByUnread = preferences.getBoolean("sort_by_unread", false);
        sortByUnmuted = preferences.getBoolean("sort_by_unmuted", true);
        sortByUser = preferences.getBoolean("sort_by_user", true);
        sortByContacts = preferences.getBoolean("sort_by_contacts", true);

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

        removeTitleEmoji = preferences.getBoolean("remove_title_emoji", false);
        ignoreMutedCount = preferences.getBoolean("ignore_muted_count", true);
        hidePublicProxy = preferences.getBoolean("hide_public_proxy",false);

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

    public static void toggleRemoveTitleEmoji() {

        preferences.edit().putBoolean("remove_title_emoji", removeTitleEmoji = !removeTitleEmoji).apply();

    }

    public static void toggleIgnoredMutedCount() {

        preferences.edit().putBoolean("ignore_muted_count", ignoreMutedCount = !ignoreMutedCount).apply();

    }

    public static void toggleHidePublicProxy() {

        preferences.edit().putBoolean("hide_public_proxy", hidePublicProxy = !hidePublicProxy).apply();

    }

    public static LinkedList<TLRPC.TL_dialogFilterSuggested> internalFilters = new LinkedList<>();

    static {

        mkFilter(LocaleController.getString("NotificationsUsers", R.string.FilterNameUsers),
                LocaleController.getString("FilterNameUsersDescription", R.string.FilterNameUsersDescription),
                MessagesController.DIALOG_FILTER_FLAG_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.contacts = true;
                    it.non_contacts = true;
                    it.exclude_archived = true;

                });

        mkFilter(LocaleController.getString("FilterNameContacts", R.string.FilterNameContacts),
                LocaleController.getString("FilterNameContactsDescription", R.string.FilterNameContactsDescription),
                MessagesController.DIALOG_FILTER_FLAG_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.contacts = true;
                    it.exclude_archived = true;

                });

        mkFilter(LocaleController.getString("FilterNameGroups", R.string.FilterNameGroups),
                LocaleController.getString("FilterNameContactsDescription", R.string.FilterNameGroupsDescription),
                MessagesController.DIALOG_FILTER_FLAG_GROUPS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.groups = true;
                    it.exclude_archived = true;

                });

        mkFilter(LocaleController.getString("FilterNameChannels", R.string.FilterNameChannels),
                LocaleController.getString("FilterNameChannelsDescription", R.string.FilterNameChannelsDescription),
                MessagesController.DIALOG_FILTER_FLAG_CHANNELS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.broadcasts = true;
                    it.exclude_archived = true;

                });

        mkFilter(LocaleController.getString("FilterNameBots", R.string.FilterNameBots),
                LocaleController.getString("FilterNameBotsDescription", R.string.FilterNameBotsDescription),
                MessagesController.DIALOG_FILTER_FLAG_BOTS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.bots = true;
                    it.exclude_archived = true;

                });

        mkFilter(LocaleController.getString("FilterNameUnmuted", R.string.FilterNameUnmuted),
                LocaleController.getString("FilterNameUnmutedDescription", R.string.FilterNameUnmutedDescription),
                MessagesController.DIALOG_FILTER_FLAG_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_GROUPS |
                        MessagesController.DIALOG_FILTER_FLAG_CHANNELS |
                        MessagesController.DIALOG_FILTER_FLAG_BOTS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.contacts = true;
                    it.non_contacts = true;
                    it.groups = true;
                    it.broadcasts = true;
                    it.bots = true;
                    it.exclude_muted = true;
                    it.exclude_archived = true;

                });

        mkFilter(LocaleController.getString("FilterNameUnread2", R.string.FilterNameUnread2),
                LocaleController.getString("FilterNameUnreadDescription", R.string.FilterNameUnreadDescription),
                MessagesController.DIALOG_FILTER_FLAG_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_GROUPS |
                        MessagesController.DIALOG_FILTER_FLAG_CHANNELS |
                        MessagesController.DIALOG_FILTER_FLAG_BOTS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.contacts = true;
                    it.non_contacts = true;
                    it.groups = true;
                    it.broadcasts = true;
                    it.bots = true;
                    it.exclude_read = true;
                    it.exclude_archived = true;

                });

        mkFilter(LocaleController.getString("FilterNameUnmutedAndUnread", R.string.FilterNameUnmutedAndUnread),
                LocaleController.getString("FilterNameUnmutedAndUnreadDescription", R.string.FilterNameUnmutedAndUnreadDescription),
                MessagesController.DIALOG_FILTER_FLAG_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS |
                        MessagesController.DIALOG_FILTER_FLAG_GROUPS |
                        MessagesController.DIALOG_FILTER_FLAG_CHANNELS |
                        MessagesController.DIALOG_FILTER_FLAG_BOTS |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ |
                        MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED,
                (it) -> {

                    it.contacts = true;
                    it.non_contacts = true;
                    it.groups = true;
                    it.broadcasts = true;
                    it.bots = true;
                    it.exclude_muted = true;
                    it.exclude_read = true;
                    it.exclude_archived = true;

                });

    }

    @FunctionalInterface
    interface FilterBuilder {

        void apply(TLRPC.TL_dialogFilter filter);

    }

    private static int currId = 10;

    private static void mkFilter(String name, String description, int flag, FilterBuilder builder) {

        TLRPC.TL_dialogFilterSuggested suggestedFilter = new TLRPC.TL_dialogFilterSuggested();

        suggestedFilter.description = description != null ? description : "Nya ~";

        suggestedFilter.filter = new TLRPC.TL_dialogFilter();

        suggestedFilter.filter.id = currId;

        suggestedFilter.filter.title = name;
        suggestedFilter.filter.flags = flag;

        builder.apply(suggestedFilter.filter);

        internalFilters.add(suggestedFilter);

        currId++;

    }

}