package tw.nekomimi.nekogram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import android.util.Base64;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import tw.nekomimi.nekogram.config.ConfigItem;

import static tw.nekomimi.nekogram.config.ConfigItem.*;

@SuppressLint("ApplySharedPref")
public class NekoConfig {

    public static final SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nkmrcfg", Context.MODE_PRIVATE);
    public static final Object sync = new Object();
    public static final String channelAliasPrefix = "channelAliasPrefix_";

    private static boolean configLoaded = false;
    private static final ArrayList<ConfigItem> configs = new ArrayList<>();

    // Configs
    public static ConfigItem migrate = addConfig("NekoConfigMigrate", configTypeBool, false);
    public static ConfigItem largeAvatarInDrawer = addConfig("AvatarAsBackground", configTypeInt, 0); // 0:TG Default 1:NekoX Default 2:Large Avatar
    public static ConfigItem unreadBadgeOnBackButton = addConfig("unreadBadgeOnBackButton", configTypeBool, false);
    public static ConfigItem customPublicProxyIP = addConfig("customPublicProxyIP", configTypeString, "");
    public static ConfigItem update_download_soucre = addConfig("update_download_soucre", configTypeInt, 0); // 0: Github 1: Channel 2:CDNDrive, removed
    public static ConfigItem useCustomEmoji = addConfig("useCustomEmoji", configTypeBool, false);
    public static ConfigItem repeatConfirm = addConfig("repeatConfirm", configTypeBool, false);
    public static ConfigItem disableInstantCamera = addConfig("DisableInstantCamera", configTypeBool, false);
    public static ConfigItem showSeconds = addConfig("showSeconds", configTypeBool, false);

    public static ConfigItem enablePublicProxy = addConfig("enablePublicProxy", configTypeBool, true);
    public static ConfigItem autoUpdateSubInfo = addConfig("autoUpdateSubInfo", configTypeBool, true);

    // From NekoConfig
    public static ConfigItem useIPv6 = addConfig("IPv6", configTypeBool, false);
    public static ConfigItem hidePhone = addConfig("HidePhone", configTypeBool, true);
    public static ConfigItem ignoreBlocked = addConfig("IgnoreBlocked", configTypeBool, false);
    public static ConfigItem tabletMode = addConfig("TabletMode", configTypeInt, 0);
    public static ConfigItem inappCamera = addConfig("DebugMenuEnableCamera", configTypeBool, true); // fake
    public static ConfigItem smoothKeyboard = addConfig("DebugMenuEnableSmoothKeyboard", configTypeBool, false);// fake

    public static ConfigItem typeface = addConfig("TypefaceUseDefault", configTypeBool, false);
    public static ConfigItem nameOrder = addConfig("NameOrder", configTypeInt, 1);
    public static ConfigItem mapPreviewProvider = addConfig("MapPreviewProvider", configTypeInt, 0);
    public static ConfigItem transparentStatusBar = addConfig("TransparentStatusBar", configTypeBool, false);
    public static ConfigItem hideProxySponsorChannel = addConfig("HideProxySponsorChannel", configTypeBool, false);
    public static ConfigItem showAddToSavedMessages = addConfig("showAddToSavedMessages", configTypeBool, true);
    public static ConfigItem showReport = addConfig("showReport", configTypeBool, true);
    public static ConfigItem showViewHistory = addConfig("showViewHistory", configTypeBool, true);
    public static ConfigItem showAdminActions = addConfig("showAdminActions", configTypeBool, true);
    public static ConfigItem showChangePermissions = addConfig("showChangePermissions", configTypeBool, true);
    public static ConfigItem showDeleteDownloadedFile = addConfig("showDeleteDownloadedFile", configTypeBool, true);
    public static ConfigItem showMessageDetails = addConfig("showMessageDetails", configTypeBool, false);
    public static ConfigItem showTranslate = addConfig("showTranslate", configTypeBool, true);
    public static ConfigItem showRepeat = addConfig("showRepeat", configTypeBool, false);
    public static ConfigItem showShareMessages = addConfig("showShareMessages", configTypeBool, false);
    public static ConfigItem showMessageHide = addConfig("showMessageHide", configTypeBool, false);

    public static ConfigItem eventType = addConfig("eventType", configTypeInt, 0);
    public static ConfigItem actionBarDecoration = addConfig("ActionBarDecoration", configTypeInt, 0);
    public static ConfigItem newYear = addConfig("ChristmasHat", configTypeBool, false);
    public static ConfigItem stickerSize = addConfig("stickerSize", configTypeFloat, 14.0f);
    public static ConfigItem unlimitedFavedStickers = addConfig("UnlimitedFavoredStickers", configTypeBool, false);
    public static ConfigItem unlimitedPinnedDialogs = addConfig("UnlimitedPinnedDialogs", configTypeBool, false);
    public static ConfigItem translationProvider = addConfig("translationProvider", configTypeInt, 1);
    public static ConfigItem disablePhotoSideAction = addConfig("DisablePhotoViewerSideAction", configTypeBool, true);
    public static ConfigItem openArchiveOnPull = addConfig("OpenArchiveOnPull", configTypeBool, false);
    public static ConfigItem hideKeyboardOnChatScroll = addConfig("HideKeyboardOnChatScroll", configTypeBool, false);
    public static ConfigItem avatarBackgroundBlur = addConfig("BlurAvatarBackground", configTypeBool, false);
    public static ConfigItem avatarBackgroundDarken = addConfig("DarkenAvatarBackground", configTypeBool, false);
    public static ConfigItem useSystemEmoji = addConfig("EmojiUseDefault", configTypeBool, false);
    public static ConfigItem showTabsOnForward = addConfig("ShowTabsOnForward", configTypeBool, false);
    public static ConfigItem rearVideoMessages = addConfig("RearVideoMessages", configTypeBool, false);
    public static ConfigItem hideAllTab = addConfig("HideAllTab", configTypeBool, false);
    public static ConfigItem pressTitleToOpenAllChats = addConfig("pressTitleToOpenAllChats", configTypeBool, false);

    public static ConfigItem disableChatAction = addConfig("DisableChatAction", configTypeBool, false);
    public static ConfigItem sortByUnread = addConfig("sort_by_unread", configTypeBool, false);
    public static ConfigItem sortByUnmuted = addConfig("sort_by_unmuted", configTypeBool, true);
    public static ConfigItem sortByUser = addConfig("sort_by_user", configTypeBool, true);
    public static ConfigItem sortByContacts = addConfig("sort_by_contacts", configTypeBool, true);

    public static ConfigItem disableUndo = addConfig("DisableUndo", configTypeBool, false);

    public static ConfigItem filterUsers = addConfig("filter_users", configTypeBool, true);
    public static ConfigItem filterContacts = addConfig("filter_contacts", configTypeBool, true);
    public static ConfigItem filterGroups = addConfig("filter_groups", configTypeBool, true);
    public static ConfigItem filterChannels = addConfig("filter_channels", configTypeBool, true);
    public static ConfigItem filterBots = addConfig("filter_bots", configTypeBool, true);
    public static ConfigItem filterAdmins = addConfig("filter_admins", configTypeBool, true);
    public static ConfigItem filterUnmuted = addConfig("filter_unmuted", configTypeBool, true);
    public static ConfigItem filterUnread = addConfig("filter_unread", configTypeBool, true);
    public static ConfigItem filterUnmutedAndUnread = addConfig("filter_unmuted_and_unread", configTypeBool, true);

    public static ConfigItem disableSystemAccount = addConfig("DisableSystemAccount", configTypeBool, false);
//    public static ConfigItem disableProxyWhenVpnEnabled = addConfig("DisableProxyWhenVpnEnabled", configTypeBool, false);
    public static ConfigItem skipOpenLinkConfirm = addConfig("SkipOpenLinkConfirm", configTypeBool, false);

    public static ConfigItem ignoreMutedCount = addConfig("IgnoreMutedCount", configTypeBool, true);
    public static ConfigItem useDefaultTheme = addConfig("UseDefaultTheme", configTypeBool, false);
    public static ConfigItem showIdAndDc = addConfig("ShowIdAndDc", configTypeBool, false);

    public static ConfigItem googleCloudTranslateKey = addConfig("GoogleCloudTransKey", configTypeString, "");
    public static ConfigItem cachePath = addConfig("cache_path", configTypeString, "");
    public static ConfigItem customSavePath = addConfig("customSavePath", configTypeString, "NekoX");

    public static ConfigItem translateToLang = addConfig("TransToLang", configTypeString, ""); // "" -> translate to current language (MessageTrans.kt & Translator.kt)
    public static ConfigItem translateInputLang = addConfig("TransInputToLang", configTypeString, "en");

    public static ConfigItem disableNotificationBubbles = addConfig("disableNotificationBubbles", configTypeBool, false);

    public static ConfigItem ccToLang = addConfig("opencc_to_lang", configTypeString, "");
    public static ConfigItem ccInputLang = addConfig("opencc_input_to_lang", configTypeString, "");

    public static ConfigItem tabsTitleType = addConfig("TabTitleType", configTypeInt, NekoXConfig.TITLE_TYPE_TEXT);
    public static ConfigItem confirmAVMessage = addConfig("ConfirmAVMessage", configTypeBool, false);
    public static ConfigItem askBeforeCall = addConfig("AskBeforeCalling", configTypeBool, false);
    public static ConfigItem disableNumberRounding = addConfig("DisableNumberRounding", configTypeBool, false);

    public static ConfigItem useSystemDNS = addConfig("useSystemDNS", configTypeBool, false);
    public static ConfigItem customDoH = addConfig("customDoH", configTypeString, "");
    public static ConfigItem hideProxyByDefault = addConfig("HideProxyByDefault", configTypeBool, false);
    public static ConfigItem useProxyItem = addConfig("UseProxyItem", configTypeBool, true);

    public static ConfigItem disableAppBarShadow = addConfig("DisableAppBarShadow", configTypeBool, false);
    public static ConfigItem mediaPreview = addConfig("MediaPreview", configTypeBool, true);

    public static ConfigItem proxyAutoSwitch = addConfig("ProxyAutoSwitch", configTypeBool, false);

    public static ConfigItem usePersianCalendar = addConfig("UsePersiancalendar", configTypeBool, false);
    public static ConfigItem displayPersianCalendarByLatin = addConfig("DisplayPersianCalendarByLatin", configTypeBool, false);
    public static ConfigItem openPGPApp = addConfig("OpenPGPApp", configTypeString, "");
    public static ConfigItem openPGPKeyId = addConfig("OpenPGPKey", configTypeLong, 0L);

    public static ConfigItem disableVibration = addConfig("DisableVibration", configTypeBool, false);
    public static ConfigItem autoPauseVideo = addConfig("AutoPauseVideo", configTypeBool, false);
    public static ConfigItem disableProximityEvents = addConfig("DisableProximityEvents", configTypeBool, false);

    public static ConfigItem ignoreContentRestrictions = addConfig("ignoreContentRestrictions", configTypeBool, !BuildVars.isPlay);
    public static ConfigItem useChatAttachMediaMenu = addConfig("UseChatAttachEnterMenu", configTypeBool, true);
    public static ConfigItem disableLinkPreviewByDefault = addConfig("DisableLinkPreviewByDefault", configTypeBool, false);
    public static ConfigItem sendCommentAfterForward = addConfig("SendCommentAfterForward", configTypeBool, true);
    public static ConfigItem increaseVoiceMessageQuality = addConfig("IncreaseVoiceMessageQuality", configTypeBool, true);
    public static ConfigItem disableTrending = addConfig("DisableTrending", configTypeBool, true);
    public static ConfigItem dontSendGreetingSticker = addConfig("DontSendGreetingSticker", configTypeBool, false);
    public static ConfigItem hideTimeForSticker = addConfig("HideTimeForSticker", configTypeBool, false);
    public static ConfigItem takeGIFasVideo = addConfig("TakeGIFasVideo", configTypeBool, false);
    public static ConfigItem maxRecentStickerCount = addConfig("maxRecentStickerCount", configTypeInt, 20);
    public static ConfigItem disableSwipeToNext = addConfig("disableSwipeToNextChannel", configTypeBool, true);
    public static ConfigItem disableRemoteEmojiInteractions = addConfig("disableRemoteEmojiInteractions", configTypeBool, true);
    public static ConfigItem disableChoosingSticker = addConfig("disableChoosingSticker", configTypeBool, false);
    public static ConfigItem hideGroupSticker = addConfig("hideGroupSticker", configTypeBool, false);
    public static ConfigItem hideSponsoredMessage = addConfig("hideSponsoredMessage", configTypeBool, false);
    public static ConfigItem rememberAllBackMessages = addConfig("rememberAllBackMessages", configTypeBool, false);
    public static ConfigItem hideSendAsChannel = addConfig("hideSendAsChannel", configTypeBool, false);
    public static ConfigItem showSpoilersDirectly = addConfig("showSpoilersDirectly", configTypeBool, false);
    public static ConfigItem reactions = addConfig("reactions", configTypeInt, 0);
    public static ConfigItem showBottomActionsWhenSelecting = addConfig("showBottomActionsWhenSelecting", configTypeBool, false);

    public static ConfigItem labelChannelUser = addConfig("labelChannelUser", configTypeBool, false);
    public static ConfigItem channelAlias = addConfig("channelAlias", configTypeBool, false);

    public static ConfigItem disableAutoDownloadingWin32Executable = addConfig("Win32ExecutableFiles", configTypeBool, true);
    public static ConfigItem disableAutoDownloadingArchive = addConfig("ArchiveFiles", configTypeBool, true);

    public static ConfigItem enableStickerPin = addConfig("EnableStickerPin", configTypeBool, false);
    public static ConfigItem useMediaStreamInVoip = addConfig("UseMediaStreamInVoip", configTypeBool, false);
    public static ConfigItem customAudioBitrate = addConfig("customAudioBitrate", configTypeInt, 32);
    public static ConfigItem disableGroupVoipAudioProcessing = addConfig("disableGroupVoipAudioProcessing", configTypeBool, false);

    static {
        loadConfig(false);
        checkMigrate(false);
    }

    public static ConfigItem addConfig(String k, int t, Object d) {
        ConfigItem a = new ConfigItem(k, t, d);
        configs.add(a);
        return a;
    }

    public static void loadConfig(boolean force) {
        synchronized (sync) {
            if (configLoaded && !force) {
                return;
            }
            for (int i = 0; i < configs.size(); i++) {
                ConfigItem o = configs.get(i);

                if (o.type == configTypeBool) {
                    o.value = preferences.getBoolean(o.key, (boolean) o.defaultValue);
                }
                if (o.type == configTypeInt) {
                    o.value = preferences.getInt(o.key, (int) o.defaultValue);
                }
                if (o.type == configTypeLong) {
                    o.value = preferences.getLong(o.key, (Long) o.defaultValue);
                }
                if (o.type == configTypeFloat) {
                    o.value = preferences.getFloat(o.key, (Float) o.defaultValue);
                }
                if (o.type == configTypeString) {
                    o.value = preferences.getString(o.key, (String) o.defaultValue);
                }
                if (o.type == configTypeSetInt) {
                    Set<String> ss = preferences.getStringSet(o.key, new HashSet<>());
                    HashSet<Integer> si = new HashSet<>();
                    for (String s : ss) {
                        si.add(Integer.parseInt(s));
                    }
                    o.value = si;
                }
                if (o.type == configTypeMapIntInt) {
                    String cv = preferences.getString(o.key, "");
                    // Log.e("NC", String.format("Getting pref %s val %s", o.key, cv));
                    if (cv.length() == 0) {
                        o.value = new HashMap<Integer, Integer>();
                    } else {
                        try {
                            byte[] data = Base64.decode(cv, Base64.DEFAULT);
                            ObjectInputStream ois = new ObjectInputStream(
                                    new ByteArrayInputStream(data));
                            o.value = (HashMap<Integer, Integer>) ois.readObject();
                            if (o.value == null) {
                                o.value = new HashMap<Integer, Integer>();
                            }
                            ois.close();
                        } catch (Exception e) {
                            o.value = new HashMap<Integer, Integer>();
                        }
                    }
                }
            }
            configLoaded = true;
        }
    }

    public static void checkMigrate(boolean force) {
        // TODO remove this after some versions.
        if (migrate.Bool() || force)
            return;

        migrate.setConfigBool(true);

        // NekoConfig.java read & migrate
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);

        if (preferences.contains("typeface"))
            typeface.setConfigBool(preferences.getInt("typeface", 0) != 0);
        if (preferences.contains("nameOrder"))
            nameOrder.setConfigInt(preferences.getInt("nameOrder", 1));
        if (preferences.contains("mapPreviewProvider"))
            mapPreviewProvider.setConfigInt(preferences.getInt("mapPreviewProvider", 0));
        if (preferences.contains("transparentStatusBar"))
            transparentStatusBar.setConfigBool(preferences.getBoolean("transparentStatusBar", false));
        if (preferences.contains("hideProxySponsorChannel"))
            hideProxySponsorChannel.setConfigBool(preferences.getBoolean("hideProxySponsorChannel", false));
        if (preferences.contains("showAddToSavedMessages"))
            showAddToSavedMessages.setConfigBool(preferences.getBoolean("showAddToSavedMessages", true));
        if (preferences.contains("showReport"))
            showReport.setConfigBool(preferences.getBoolean("showReport", true));
        if (preferences.contains("showViewHistory"))
            showViewHistory.setConfigBool(preferences.getBoolean("showViewHistory", true));
        if (preferences.contains("showAdminActions"))
            showAdminActions.setConfigBool(preferences.getBoolean("showAdminActions", true));
        if (preferences.contains("showChangePermissions"))
            showChangePermissions.setConfigBool(preferences.getBoolean("showChangePermissions", true));
        if (preferences.contains("showDeleteDownloadedFile"))
            showDeleteDownloadedFile.setConfigBool(preferences.getBoolean("showDeleteDownloadedFile", true));
        if (preferences.contains("showMessageDetails"))
            showMessageDetails.setConfigBool(preferences.getBoolean("showMessageDetails", false));
        if (preferences.contains("showTranslate"))
            showTranslate.setConfigBool(preferences.getBoolean("showTranslate", true));
        if (preferences.contains("showRepeat"))
            showRepeat.setConfigBool(preferences.getBoolean("showRepeat", false));
        if (preferences.contains("showShareMessages"))
            showShareMessages.setConfigBool(preferences.getBoolean("showShareMessages", false));
        if (preferences.contains("showMessageHide"))
            showMessageHide.setConfigBool(preferences.getBoolean("showMessageHide", false));

        if (preferences.contains("eventType"))
            eventType.setConfigInt(preferences.getInt("eventType", 0));
        if (preferences.contains("actionBarDecoration"))
            actionBarDecoration.setConfigInt(preferences.getInt("actionBarDecoration", 0));
        if (preferences.contains("newYear"))
            newYear.setConfigBool(preferences.getBoolean("newYear", false));
        if (preferences.contains("stickerSize"))
            stickerSize.setConfigFloat(preferences.getFloat("stickerSize", 14.0f));
        if (preferences.contains("unlimitedFavedStickers"))
            unlimitedFavedStickers.setConfigBool(preferences.getBoolean("unlimitedFavedStickers", false));
        if (preferences.contains("unlimitedPinnedDialogs"))
            unlimitedPinnedDialogs.setConfigBool(preferences.getBoolean("unlimitedPinnedDialogs", false));
        if (preferences.contains("translationProvider"))
            translationProvider.setConfigInt(preferences.getInt("translationProvider", 1));
        if (preferences.contains("disablePhotoSideAction"))
            disablePhotoSideAction.setConfigBool(preferences.getBoolean("disablePhotoSideAction", true));
        if (preferences.contains("openArchiveOnPull"))
            openArchiveOnPull.setConfigBool(preferences.getBoolean("openArchiveOnPull", false));
        if (preferences.contains("showHiddenFeature"))             //showHiddenFeature.setConfigBool(preferences.getBoolean("showHiddenFeature", false));
            if (preferences.contains("hideKeyboardOnChatScroll"))
                hideKeyboardOnChatScroll.setConfigBool(preferences.getBoolean("hideKeyboardOnChatScroll", false));
        if (preferences.contains("avatarBackgroundBlur"))
            avatarBackgroundBlur.setConfigBool(preferences.getBoolean("avatarBackgroundBlur", false));
        if (preferences.contains("avatarBackgroundDarken"))
            avatarBackgroundDarken.setConfigBool(preferences.getBoolean("avatarBackgroundDarken", false));
        if (preferences.contains("useSystemEmoji"))
            useSystemEmoji.setConfigBool(preferences.getBoolean("useSystemEmoji", false));
        if (preferences.contains("showTabsOnForward"))
            showTabsOnForward.setConfigBool(preferences.getBoolean("showTabsOnForward", false));
        if (preferences.contains("rearVideoMessages"))
            rearVideoMessages.setConfigBool(preferences.getBoolean("rearVideoMessages", false));
        if (preferences.contains("hideAllTab"))
            hideAllTab.setConfigBool(preferences.getBoolean("hideAllTab", false));
        if (preferences.contains("pressTitleToOpenAllChats"))
            pressTitleToOpenAllChats.setConfigBool(preferences.getBoolean("pressTitleToOpenAllChats", false));

        if (preferences.contains("disable_chat_action"))
            disableChatAction.setConfigBool(preferences.getBoolean("disable_chat_action", false));
        if (preferences.contains("sort_by_unread"))
            sortByUnread.setConfigBool(preferences.getBoolean("sort_by_unread", false));
        if (preferences.contains("sort_by_unmuted"))
            sortByUnmuted.setConfigBool(preferences.getBoolean("sort_by_unmuted", true));
        if (preferences.contains("sort_by_user"))
            sortByUser.setConfigBool(preferences.getBoolean("sort_by_user", true));
        if (preferences.contains("sort_by_contacts"))
            sortByContacts.setConfigBool(preferences.getBoolean("sort_by_contacts", true));

        if (preferences.contains("disable_undo"))
            disableUndo.setConfigBool(preferences.getBoolean("disable_undo", false));

        if (preferences.contains("filter_users"))
            filterUsers.setConfigBool(preferences.getBoolean("filter_users", true));
        if (preferences.contains("filter_contacts"))
            filterContacts.setConfigBool(preferences.getBoolean("filter_contacts", true));
        if (preferences.contains("filter_groups"))
            filterGroups.setConfigBool(preferences.getBoolean("filter_groups", true));
        if (preferences.contains("filter_channels"))
            filterChannels.setConfigBool(preferences.getBoolean("filter_channels", true));
        if (preferences.contains("filter_bots"))
            filterBots.setConfigBool(preferences.getBoolean("filter_bots", true));
        if (preferences.contains("filter_admins"))
            filterAdmins.setConfigBool(preferences.getBoolean("filter_admins", true));
        if (preferences.contains("filter_unmuted"))
            filterUnmuted.setConfigBool(preferences.getBoolean("filter_unmuted", true));
        if (preferences.contains("filter_unread"))
            filterUnread.setConfigBool(preferences.getBoolean("filter_unread", true));
        if (preferences.contains("filter_unmuted_and_unread"))
            filterUnmutedAndUnread.setConfigBool(preferences.getBoolean("filter_unmuted_and_unread", true));

        if (preferences.contains("disable_system_account"))
            disableSystemAccount.setConfigBool(preferences.getBoolean("disable_system_account", false));
        if (preferences.contains("skip_open_link_confirm"))
            skipOpenLinkConfirm.setConfigBool(preferences.getBoolean("skip_open_link_confirm", false));

        if (preferences.contains("ignore_muted_count"))
            ignoreMutedCount.setConfigBool(preferences.getBoolean("ignore_muted_count", true));
        if (preferences.contains("use_default_theme"))
            useDefaultTheme.setConfigBool(preferences.getBoolean("use_default_theme", false));
        if (preferences.contains("show_id_and_dc"))
            showIdAndDc.setConfigBool(preferences.getBoolean("show_id_and_dc", false));

        if (preferences.contains("google_cloud_translate_key"))
            googleCloudTranslateKey.setConfigString(preferences.getString("google_cloud_translate_key", null));
        if (preferences.contains("cache_path"))
            cachePath.setConfigString(preferences.getString("cache_path", null));

        if (preferences.contains("trans_to_lang"))
            translateToLang.setConfigString(preferences.getString("trans_to_lang", ""));
        if (preferences.contains("trans_input_to_lang"))
            translateInputLang.setConfigString(preferences.getString("trans_input_to_lang", "en"));

        if (preferences.contains("opencc_to_lang"))
            ccToLang.setConfigString(preferences.getString("opencc_to_lang", null));
        if (preferences.contains("opencc_input_to_lang"))
            ccInputLang.setConfigString(preferences.getString("opencc_input_to_lang", null));

        if (preferences.contains("tabsTitleType"))
            tabsTitleType.setConfigInt(preferences.getInt("tabsTitleType", NekoXConfig.TITLE_TYPE_TEXT));
        if (preferences.contains("confirmAVMessage"))
            confirmAVMessage.setConfigBool(preferences.getBoolean("confirmAVMessage", false));
        if (preferences.contains("askBeforeCall"))
            askBeforeCall.setConfigBool(preferences.getBoolean("askBeforeCall", false));
        if (preferences.contains("disableNumberRounding"))
            disableNumberRounding.setConfigBool(preferences.getBoolean("disableNumberRounding", false));

        if (preferences.contains("useSystemDNS"))
            useSystemDNS.setConfigBool(preferences.getBoolean("useSystemDNS", false));
        if (preferences.contains("customDoH"))
            customDoH.setConfigString(preferences.getString("customDoH", ""));
        if (preferences.contains("hide_proxy_by_default"))
            hideProxyByDefault.setConfigBool(preferences.getBoolean("hide_proxy_by_default", false));
        if (preferences.contains("use_proxy_item"))
            useProxyItem.setConfigBool(preferences.getBoolean("use_proxy_item", true));

        if (preferences.contains("disableAppBarShadow"))
            disableAppBarShadow.setConfigBool(preferences.getBoolean("disableAppBarShadow", false));
        if (preferences.contains("mediaPreview"))
            mediaPreview.setConfigBool(preferences.getBoolean("mediaPreview", true));

        if (preferences.contains("proxy_auto_switch"))
            proxyAutoSwitch.setConfigBool(preferences.getBoolean("proxy_auto_switch", false));

        if (preferences.contains("openPGPApp"))
            openPGPApp.setConfigString(preferences.getString("openPGPApp", ""));
        if (preferences.contains("openPGPKeyId"))
            openPGPKeyId.setConfigLong(preferences.getLong("openPGPKeyId", 0L));

        if (preferences.contains("disableVibration"))
            disableVibration.setConfigBool(preferences.getBoolean("disableVibration", false));
        if (preferences.contains("autoPauseVideo"))
            autoPauseVideo.setConfigBool(preferences.getBoolean("autoPauseVideo", false));
        if (preferences.contains("disableProximityEvents"))
            disableProximityEvents.setConfigBool(preferences.getBoolean("disableProximityEvents", false));

        if (preferences.contains("ignoreContentRestrictions"))
            ignoreContentRestrictions.setConfigBool(preferences.getBoolean("ignoreContentRestrictions", !BuildVars.isPlay));
        if (preferences.contains("useChatAttachMediaMenu"))
            useChatAttachMediaMenu.setConfigBool(preferences.getBoolean("useChatAttachMediaMenu", true));
        if (preferences.contains("disableLinkPreviewByDefault"))
            disableLinkPreviewByDefault.setConfigBool(preferences.getBoolean("disableLinkPreviewByDefault", false));
        if (preferences.contains("sendCommentAfterForward"))
            sendCommentAfterForward.setConfigBool(preferences.getBoolean("sendCommentAfterForward", true));
        if (preferences.contains("increaseVoiceMessageQuality"))
            increaseVoiceMessageQuality.setConfigBool(preferences.getBoolean("increaseVoiceMessageQuality", true));
        if (preferences.contains("disableTrending"))
            disableTrending.setConfigBool(preferences.getBoolean("disableTrending", true));
        if (preferences.contains("dontSendGreetingSticker"))
            dontSendGreetingSticker.setConfigBool(preferences.getBoolean("dontSendGreetingSticker", false));
        if (preferences.contains("hideTimeForSticker"))
            hideTimeForSticker.setConfigBool(preferences.getBoolean("hideTimeForSticker", false));
        if (preferences.contains("takeGIFasVideo"))
            takeGIFasVideo.setConfigBool(preferences.getBoolean("takeGIFasVideo", false));
        if (preferences.contains("maxRecentStickerCount"))
            maxRecentStickerCount.setConfigInt(preferences.getInt("maxRecentStickerCount", 20));
        if (preferences.contains("disableSwipeToNext"))
            disableSwipeToNext.setConfigBool(preferences.getBoolean("disableSwipeToNext", true));
        if (preferences.contains("disableRemoteEmojiInteractions"))
            disableRemoteEmojiInteractions.setConfigBool(preferences.getBoolean("disableRemoteEmojiInteractions", true));
        if (preferences.contains("disableChoosingSticker"))
            disableChoosingSticker.setConfigBool(preferences.getBoolean("disableChoosingSticker", false));

        if (preferences.contains("disableAutoDownloadingWin32Executable"))
            disableAutoDownloadingWin32Executable.setConfigBool(preferences.getBoolean("disableAutoDownloadingWin32Executable", true));
        if (preferences.contains("disableAutoDownloadingArchive"))
            disableAutoDownloadingArchive.setConfigBool(preferences.getBoolean("disableAutoDownloadingArchive", true));

        if (preferences.contains("enableStickerPin"))
            enableStickerPin.setConfigBool(preferences.getBoolean("enableStickerPin", false));
        if (preferences.contains("useMediaStreamInVoip"))
            useMediaStreamInVoip.setConfigBool(preferences.getBoolean("useMediaStreamInVoip", false));
        if (preferences.contains("customAudioBitrate"))
            customAudioBitrate.setConfigInt(preferences.getInt("customAudioBitrate", 32));
        if (preferences.contains("disableGroupVoipAudioProcessing"))
            disableGroupVoipAudioProcessing.setConfigBool(preferences.getBoolean("disableGroupVoipAudioProcessing", false));
    }

}