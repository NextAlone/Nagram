package tw.nekomimi.nekogram;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsService;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.util.StrUtil;
import tw.nekomimi.nkmr.NekomuraConfig;

public class NekoConfig {

    public static final int TITLE_TYPE_TEXT = 0;
    public static final int TITLE_TYPE_ICON = 1;
    public static final int TITLE_TYPE_MIX = 2;

    public static String getOpenPGPAppName() {

        if (StrUtil.isNotBlank(NekomuraConfig.openPGPApp.String())) {

            try {
                PackageManager manager = ApplicationLoader.applicationContext.getPackageManager();
                ApplicationInfo info = manager.getApplicationInfo(NekomuraConfig.openPGPApp.String(), PackageManager.GET_META_DATA);
                return (String) manager.getApplicationLabel(info);
            } catch (PackageManager.NameNotFoundException e) {
                NekomuraConfig.openPGPApp.setConfigString("");
            }

        }

        return LocaleController.getString("None", R.string.None);

    }

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
//        useIPv6 = preferences.getBoolean("useIPv6", false);
//        hidePhone = preferences.getBoolean("hidePhone", true);
//        ignoreBlocked = preferences.getBoolean("ignoreBlocked", false);
//
//        boolean forceTablet = preferences.getBoolean("forceTablet", false);
//        if (forceTablet) {
//            tabletMode = 1;
//            preferences.edit()
//                    .remove("forceTablet")
//                    .putInt("tabletMode", 1)
//                    .apply();
//        } else {
//            tabletMode = preferences.getInt("tabletMode", 0);
//        }
//
//        typeface = preferences.getInt("typeface", 0);
//        nameOrder = preferences.getInt("nameOrder", 1);
//        mapPreviewProvider = preferences.getInt("mapPreviewProvider", 0);
//        transparentStatusBar = preferences.getBoolean("transparentStatusBar", false);
//        residentNotification = preferences.getBoolean("residentNotification", false);
//        hideProxySponsorChannel = preferences.getBoolean("hideProxySponsorChannel", false);
//        showAddToSavedMessages = preferences.getBoolean("showAddToSavedMessages", true);
//        showReport = preferences.getBoolean("showReport", true);
//        showViewHistory = preferences.getBoolean("showViewHistory", true);
//        showAdminActions = preferences.getBoolean("showAdminActions", true);
//        showChangePermissions = preferences.getBoolean("showChangePermissions", true);
//        showDeleteDownloadedFile = preferences.getBoolean("showDeleteDownloadedFile", true);
//        showMessageDetails = preferences.getBoolean("showMessageDetails", false);
//        showTranslate = preferences.getBoolean("showTranslate", true);
//        showRepeat = preferences.getBoolean("showRepeat", false);
//        showMessageHide = preferences.getBoolean("showMessageHide", false);
//
//        eventType = preferences.getInt("eventType", 0);
//        actionBarDecoration = preferences.getInt("actionBarDecoration", 0);
//        newYear = preferences.getBoolean("newYear", false);
//        stickerSize = preferences.getFloat("stickerSize", 14.0f);
//        unlimitedFavedStickers = preferences.getBoolean("unlimitedFavedStickers", false);
//        unlimitedPinnedDialogs = preferences.getBoolean("unlimitedPinnedDialogs", false);
//        translationProvider = preferences.getInt("translationProvider", 1);
//        disablePhotoSideAction = preferences.getBoolean("disablePhotoSideAction", true);
//        openArchiveOnPull = preferences.getBoolean("openArchiveOnPull", false);
//        //showHiddenFeature = preferences.getBoolean("showHiddenFeature", false);
//        hideKeyboardOnChatScroll = preferences.getBoolean("hideKeyboardOnChatScroll", false);
//        avatarAsDrawerBackground = preferences.getBoolean("avatarAsDrawerBackground", true);
//        avatarBackgroundBlur = preferences.getBoolean("avatarBackgroundBlur", false);
//        avatarBackgroundDarken = preferences.getBoolean("avatarBackgroundDarken", false);
//        useSystemEmoji = preferences.getBoolean("useSystemEmoji", false);
//        showTabsOnForward = preferences.getBoolean("showTabsOnForward", false);
//        rearVideoMessages = preferences.getBoolean("rearVideoMessages", false);
//        hideAllTab = preferences.getBoolean("hideAllTab", false);
//        pressTitleToOpenAllChats = preferences.getBoolean("pressTitleToOpenAllChats", false);
//
//        disableChatAction = preferences.getBoolean("disable_chat_action", false);
//        sortByUnread = preferences.getBoolean("sort_by_unread", false);
//        sortByUnmuted = preferences.getBoolean("sort_by_unmuted", true);
//        sortByUser = preferences.getBoolean("sort_by_user", true);
//        sortByContacts = preferences.getBoolean("sort_by_contacts", true);
//
//        disableUndo = preferences.getBoolean("disable_undo", false);
//
//        filterUsers = preferences.getBoolean("filter_users", true);
//        filterContacts = preferences.getBoolean("filter_contacts", true);
//        filterGroups = preferences.getBoolean("filter_groups", true);
//        filterChannels = preferences.getBoolean("filter_channels", true);
//        filterBots = preferences.getBoolean("filter_bots", true);
//        filterAdmins = preferences.getBoolean("filter_admins", true);
//        filterUnmuted = preferences.getBoolean("filter_unmuted", true);
//        filterUnread = preferences.getBoolean("filter_unread", true);
//        filterUnmutedAndUnread = preferences.getBoolean("filter_unmuted_and_unread", true);
//
//        disableSystemAccount = preferences.getBoolean("disable_system_account", false);
//        disableProxyWhenVpnEnabled = preferences.getBoolean("disable_proxy_when_vpn_enabled", false);
//        skipOpenLinkConfirm = preferences.getBoolean("skip_open_link_confirm", false);
//
//        ignoreMutedCount = preferences.getBoolean("ignore_muted_count", true);
//        useDefaultTheme = preferences.getBoolean("use_default_theme", false);
//        showIdAndDc = preferences.getBoolean("show_id_and_dc", false);
//
//        googleCloudTranslateKey = preferences.getString("google_cloud_translate_key", null);
//        cachePath = preferences.getString("cache_path", null);
//
//        translateToLang = preferences.getString("trans_to_lang", null);
//        translateInputLang = preferences.getString("trans_input_to_lang", "en");
//
//        ccToLang = preferences.getString("opencc_to_lang", null);
//        ccInputLang = preferences.getString("opencc_input_to_lang", null);
//
//        tabsTitleType = preferences.getInt("tabsTitleType", TITLE_TYPE_TEXT);
//        confirmAVMessage = preferences.getBoolean("confirmAVMessage", false);
//        askBeforeCall = preferences.getBoolean("askBeforeCall", false);
//        disableNumberRounding = preferences.getBoolean("disableNumberRounding", false);
//
//        useSystemDNS = preferences.getBoolean("useSystemDNS", false);
//        customDoH = preferences.getString("customDoH", "");
//        hideProxyByDefault = preferences.getBoolean("hide_proxy_by_default", false);
//        useProxyItem = preferences.getBoolean("use_proxy_item", true);
//
//        disableAppBarShadow = preferences.getBoolean("disableAppBarShadow", false);
//        mediaPreview = preferences.getBoolean("mediaPreview", true);
//
//        proxyAutoSwitch = preferences.getBoolean("proxy_auto_switch", false);
//
//        usePersianCalendar = preferences.getInt("persian_calendar", 0);
//        displayPersianCalendarByLatin = preferences.getBoolean("displayPersianCalendarByLatin", false);
//        openPGPApp = preferences.getString("openPGPApp", "");
//        openPGPKeyId = preferences.getLong("openPGPKeyId", 0L);
//
//        disableVibration = preferences.getBoolean("disableVibration", false);
//        autoPauseVideo = preferences.getBoolean("autoPauseVideo", false);
//        disableProximityEvents = preferences.getBoolean("disableProximityEvents", false);
//
//        ignoreContentRestrictions = preferences.getBoolean("ignoreContentRestrictions", !BuildVars.isPlay);
//        useChatAttachMediaMenu = preferences.getBoolean("useChatAttachMediaMenu", true);
//        disableLinkPreviewByDefault = preferences.getBoolean("disableLinkPreviewByDefault", false);
//        sendCommentAfterForward = preferences.getBoolean("sendCommentAfterForward", true);
//        increaseVoiceMessageQuality = preferences.getBoolean("increaseVoiceMessageQuality", true);
//        acceptSecretChat = preferences.getBoolean("acceptSecretChat", true);
//        disableTrending = preferences.getBoolean("disableTrending", true);
//        dontSendGreetingSticker = preferences.getBoolean("dontSendGreetingSticker", false);
//        hideTimeForSticker = preferences.getBoolean("hideTimeForSticker", false);
//        takeGIFasVideo = preferences.getBoolean("takeGIFasVideo", false);
//        maxRecentStickerCount = preferences.getInt("maxRecentStickerCount", 20);
//        disableSwipeToNext = preferences.getBoolean("disableSwipeToNext", true);
//        disableRemoteEmojiInteractions = preferences.getBoolean("disableRemoteEmojiInteractions", true);
//        disableChoosingSticker = preferences.getBoolean("disableChoosingSticker", false);
//
//        disableAutoDownloadingWin32Executable = preferences.getBoolean("disableAutoDownloadingWin32Executable", true);
//        disableAutoDownloadingArchive = preferences.getBoolean("disableAutoDownloadingArchive", true);
//
//        enableStickerPin = preferences.getBoolean("enableStickerPin", false);
//        useMediaStreamInVoip = preferences.getBoolean("useMediaStreamInVoip", false);
//        customAudioBitrate = (short) preferences.getInt("customAudioBitrate", 32);
//        disableGroupVoipAudioProcessing = preferences.getBoolean("disableGroupVoipAudioProcessing", false);
    }


    private static final String EMOJI_FONT_AOSP = "NotoColorEmoji.ttf";

    public static boolean loadSystemEmojiFailed = false;
    private static Typeface systemEmojiTypeface;

    public static Typeface getSystemEmojiTypeface() {
        if (!loadSystemEmojiFailed && systemEmojiTypeface == null) {
            try {
                Pattern p = Pattern.compile(">(.*emoji.*)</font>", Pattern.CASE_INSENSITIVE);
                BufferedReader br = new BufferedReader(new FileReader("/system/etc/fonts.xml"));
                String line;
                while ((line = br.readLine()) != null) {
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        systemEmojiTypeface = Typeface.createFromFile("/system/fonts/" + m.group(1));
                        FileLog.d("emoji font file fonts.xml = " + m.group(1));
                        break;
                    }
                }
                br.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (systemEmojiTypeface == null) {
                try {
                    systemEmojiTypeface = Typeface.createFromFile("/system/fonts/" + EMOJI_FONT_AOSP);
                    FileLog.d("emoji font file = " + EMOJI_FONT_AOSP);
                } catch (Exception e) {
                    FileLog.e(e);
                    loadSystemEmojiFailed = true;
                }
            }
        }
        return systemEmojiTypeface;
    }

    public static int getNotificationColor() {
        int color = 0;
        Configuration configuration = ApplicationLoader.applicationContext.getResources().getConfiguration();
        boolean isDark = (configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        if (isDark) {
            color = 0xffffffff;
        } else {
            if (Theme.getActiveTheme().hasAccentColors()) {
                color = Theme.getActiveTheme().getAccentColor(Theme.getActiveTheme().currentAccentId);
            }
            if (Theme.getActiveTheme().isDark() || color == 0) {
                color = Theme.getColor(Theme.key_actionBarDefault);
            }
            // too bright
            if (AndroidUtilities.computePerceivedBrightness(color) >= 0.721f) {
                color = Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader) | 0xff000000;
            }
        }
        return color;
    }

}