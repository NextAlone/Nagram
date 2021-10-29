package tw.nekomimi.nkmr;

import static tw.nekomimi.nekogram.NekoConfig.TITLE_TYPE_TEXT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import android.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SuppressLint("ApplySharedPref")
public class NekomuraConfig {
    private static final int configTypeBool = 0;
    private static final int configTypeInt = 1;
    private static final int configTypeString = 2;
    private static final int configTypeSetInt = 3;
    private static final int configTypeMapIntInt = 4;
    private static final int configTypeLong = 5;
    private static final int configTypeFloat = 6;

    public static class ConfigItem {
        private String key;
        private int type;

        private Object defaultValue;
        private Object value;
        private boolean forceDefault;//针对某些人的垃圾选项，应该不允许改变默认值

        private ConfigItem(String k, int t, Object d) {
            key = k;
            type = t;
            defaultValue = d;
        }

        public String getKey() {
            return key;
        }

        // 读配置

        public boolean Bool() {
            return (boolean) value;
        }

        public int Int() {
            return (int) value;
        }

        public Long Long() {
            return (Long) value;
        }

        public Float Float() {
            return (Float) value;
        }

        public String String() {
            return value.toString();
        }

        public HashSet<Integer> SetInt() {
            return (HashSet<Integer>) value;
        }

        public HashMap<Integer, Integer> MapIntInt() {
            return (HashMap<Integer, Integer>) value;
        }

        public boolean SetIntContains(Integer v) {
            return ((HashSet<Integer>) value).contains(v);
        }


        public void changed(Object o) {
            value = o;
        }

        //写配置
        //这里没有检查类型哦

        public boolean toggleConfigBool() {
            value = !this.Bool();
            if (forceDefault) {
                value = defaultValue;
            }
            saveConfig(this);
            return this.Bool();//返回toggle之后的
        }

        public void setConfigBool(boolean v) {
            value = v;
            if (forceDefault) {
                value = defaultValue;
            }
            saveConfig(this);
        }

        public void setConfigInt(int v) {
            if (forceDefault) {
                value = defaultValue;
            }
            value = v;
            saveConfig(this);
        }

        public void setConfigLong(Long v) {
            if (forceDefault) {
                value = defaultValue;
            }
            value = v;
            saveConfig(this);
        }

        public void setConfigFloat(Float v) {
            if (forceDefault) {
                value = defaultValue;
            }
            value = v;
            saveConfig(this);
        }

        public void setConfigString(String v) {
            value = v;
            if (forceDefault) {
                value = defaultValue;
            }
            saveConfig(this);
        }

        public void setConfigSetInt(HashSet<Integer> v) {
            value = v;
            if (forceDefault) {
                value = defaultValue;
            }
            saveConfig(this);
        }

        public void setConfigMapInt(HashMap<Integer, Integer> v) {
            value = v;
            saveConfig(this);
        }
    }

    private static boolean configLoaded;
    private static final Object sync = new Object();
    private static final ArrayList<ConfigItem> configs = new ArrayList<>();

    // Configs
    public static ConfigItem migrate = addConfig("NekoConfigMigrate", configTypeBool, false);
    public static ConfigItem largeAvatarInDrawer = addConfig("largeAvatarInDrawer", configTypeInt, 0); // 0:TG Default 1:NekoX Default 2:Large Avatar
    public static ConfigItem unreadBadgeOnBackButton = addConfig("unreadBadgeOnBackButton", configTypeBool, false);

    //From NekoConfig
    public static ConfigItem useIPv6 = addConfig("useIPv6", configTypeBool, false);
    public static ConfigItem hidePhone = addConfig("hidePhone", configTypeBool, true);
    public static ConfigItem ignoreBlocked = addConfig("ignoreBlocked", configTypeBool, false);
    public static ConfigItem forceTablet = addConfig("forceTablet", configTypeInt, 0);
    public static ConfigItem inappCamera = addConfig("inappCamera", configTypeBool, false); // fake
    public static ConfigItem smoothKeyboard = addConfig("smoothKeyboard", configTypeBool, false);// fake

    public static ConfigItem typeface = addConfig("typeface_b", configTypeBool, false);
    public static ConfigItem nameOrder = addConfig("nameOrder", configTypeInt, 1);
    public static ConfigItem mapPreviewProvider = addConfig("mapPreviewProvider", configTypeInt, 0);
    public static ConfigItem transparentStatusBar = addConfig("transparentStatusBar", configTypeBool, false);
    public static ConfigItem residentNotification = addConfig("residentNotification", configTypeBool, false);
    public static ConfigItem hideProxySponsorChannel = addConfig("hideProxySponsorChannel", configTypeBool, false);
    public static ConfigItem showAddToSavedMessages = addConfig("showAddToSavedMessages", configTypeBool, true);
    public static ConfigItem showReport = addConfig("showReport", configTypeBool, true);
    public static ConfigItem showViewHistory = addConfig("showViewHistory", configTypeBool, true);
    public static ConfigItem showAdminActions = addConfig("showAdminActions", configTypeBool, true);
    public static ConfigItem showChangePermissions = addConfig("showChangePermissions", configTypeBool, true);
    public static ConfigItem showDeleteDownloadedFile = addConfig("showDeleteDownloadedFile", configTypeBool, true);
    public static ConfigItem showMessageDetails = addConfig("showMessageDetails", configTypeBool, false);
    public static ConfigItem showTranslate = addConfig("showTranslate", configTypeBool, true);
    public static ConfigItem showRepeat = addConfig("showRepeat", configTypeBool, false);
    public static ConfigItem showMessageHide = addConfig("showMessageHide", configTypeBool, false);

    public static ConfigItem eventType = addConfig("eventType", configTypeInt, 0);
    public static ConfigItem actionBarDecoration = addConfig("actionBarDecoration", configTypeInt, 0);
    public static ConfigItem newYear = addConfig("newYear", configTypeBool, false);
    public static ConfigItem stickerSize = addConfig("stickerSize", configTypeFloat, 14.0f);
    public static ConfigItem unlimitedFavedStickers = addConfig("unlimitedFavedStickers", configTypeBool, false);
    public static ConfigItem unlimitedPinnedDialogs = addConfig("unlimitedPinnedDialogs", configTypeBool, false);
    public static ConfigItem translationProvider = addConfig("translationProvider", configTypeInt, 1);
    public static ConfigItem disablePhotoSideAction = addConfig("disablePhotoSideAction", configTypeBool, true);
    public static ConfigItem openArchiveOnPull = addConfig("openArchiveOnPull", configTypeBool, false);
    public static ConfigItem hideKeyboardOnChatScroll = addConfig("hideKeyboardOnChatScroll", configTypeBool, false);
    public static ConfigItem avatarBackgroundBlur = addConfig("avatarBackgroundBlur", configTypeBool, false);
    public static ConfigItem avatarBackgroundDarken = addConfig("avatarBackgroundDarken", configTypeBool, false);
    public static ConfigItem useSystemEmoji = addConfig("useSystemEmoji", configTypeBool, false);
    public static ConfigItem showTabsOnForward = addConfig("showTabsOnForward", configTypeBool, false);
    public static ConfigItem rearVideoMessages = addConfig("rearVideoMessages", configTypeBool, false);
    public static ConfigItem hideAllTab = addConfig("hideAllTab", configTypeBool, false);
    public static ConfigItem pressTitleToOpenAllChats = addConfig("pressTitleToOpenAllChats", configTypeBool, false);

    public static ConfigItem disableChatAction = addConfig("disable_chat_action", configTypeBool, false);
    public static ConfigItem sortByUnread = addConfig("sort_by_unread", configTypeBool, false);
    public static ConfigItem sortByUnmuted = addConfig("sort_by_unmuted", configTypeBool, true);
    public static ConfigItem sortByUser = addConfig("sort_by_user", configTypeBool, true);
    public static ConfigItem sortByContacts = addConfig("sort_by_contacts", configTypeBool, true);

    public static ConfigItem disableUndo = addConfig("disable_undo", configTypeBool, false);

    public static ConfigItem filterUsers = addConfig("filter_users", configTypeBool, true);
    public static ConfigItem filterContacts = addConfig("filter_contacts", configTypeBool, true);
    public static ConfigItem filterGroups = addConfig("filter_groups", configTypeBool, true);
    public static ConfigItem filterChannels = addConfig("filter_channels", configTypeBool, true);
    public static ConfigItem filterBots = addConfig("filter_bots", configTypeBool, true);
    public static ConfigItem filterAdmins = addConfig("filter_admins", configTypeBool, true);
    public static ConfigItem filterUnmuted = addConfig("filter_unmuted", configTypeBool, true);
    public static ConfigItem filterUnread = addConfig("filter_unread", configTypeBool, true);
    public static ConfigItem filterUnmutedAndUnread = addConfig("filter_unmuted_and_unread", configTypeBool, true);

    public static ConfigItem disableSystemAccount = addConfig("disable_system_account", configTypeBool, false);
    public static ConfigItem disableProxyWhenVpnEnabled = addConfig("disable_proxy_when_vpn_enabled", configTypeBool, false);
    public static ConfigItem skipOpenLinkConfirm = addConfig("skip_open_link_confirm", configTypeBool, false);

    public static ConfigItem ignoreMutedCount = addConfig("ignore_muted_count", configTypeBool, true);
    public static ConfigItem useDefaultTheme = addConfig("use_default_theme", configTypeBool, false);
    public static ConfigItem showIdAndDc = addConfig("show_id_and_dc", configTypeBool, false);

    public static ConfigItem googleCloudTranslateKey = addConfig("google_cloud_translate_key", configTypeString, "");
    public static ConfigItem cachePath = addConfig("cache_path", configTypeString, "");

    public static ConfigItem translateToLang = addConfig("trans_to_lang", configTypeString, "");
    public static ConfigItem translateInputLang = addConfig("trans_input_to_lang", configTypeString, "en");

    public static ConfigItem ccToLang = addConfig("opencc_to_lang", configTypeString, "");
    public static ConfigItem ccInputLang = addConfig("opencc_input_to_lang", configTypeString, "");

    public static ConfigItem tabsTitleType = addConfig("tabsTitleType", configTypeInt, TITLE_TYPE_TEXT);
    public static ConfigItem confirmAVMessage = addConfig("confirmAVMessage", configTypeBool, false);
    public static ConfigItem askBeforeCall = addConfig("askBeforeCall", configTypeBool, false);
    public static ConfigItem disableNumberRounding = addConfig("disableNumberRounding", configTypeBool, false);

    public static ConfigItem useSystemDNS = addConfig("useSystemDNS", configTypeBool, false);
    public static ConfigItem customDoH = addConfig("customDoH", configTypeString, "");
    public static ConfigItem hideProxyByDefault = addConfig("hide_proxy_by_default", configTypeBool, false);
    public static ConfigItem useProxyItem = addConfig("use_proxy_item", configTypeBool, true);

    public static ConfigItem disableAppBarShadow = addConfig("disableAppBarShadow", configTypeBool, false);
    public static ConfigItem mediaPreview = addConfig("mediaPreview", configTypeBool, true);

    public static ConfigItem proxyAutoSwitch = addConfig("proxy_auto_switch", configTypeBool, false);

    public static ConfigItem usePersianCalendar = addConfig("persian_calendar", configTypeBool, false);
    public static ConfigItem displayPersianCalendarByLatin = addConfig("displayPersianCalendarByLatin", configTypeBool, false);
    public static ConfigItem openPGPApp = addConfig("openPGPApp", configTypeString, "");
    public static ConfigItem openPGPKeyId = addConfig("openPGPKeyId", configTypeLong, 0L);

    public static ConfigItem disableVibration = addConfig("disableVibration", configTypeBool, false);
    public static ConfigItem autoPauseVideo = addConfig("autoPauseVideo", configTypeBool, false);
    public static ConfigItem disableProximityEvents = addConfig("disableProximityEvents", configTypeBool, false);

    public static ConfigItem ignoreContentRestrictions = addConfig("ignoreContentRestrictions", configTypeBool, !BuildVars.isPlay);
    public static ConfigItem useChatAttachMediaMenu = addConfig("useChatAttachMediaMenu", configTypeBool, true);
    public static ConfigItem disableLinkPreviewByDefault = addConfig("disableLinkPreviewByDefault", configTypeBool, false);
    public static ConfigItem sendCommentAfterForward = addConfig("sendCommentAfterForward", configTypeBool, true);
    public static ConfigItem increaseVoiceMessageQuality = addConfig("increaseVoiceMessageQuality", configTypeBool, true);
    public static ConfigItem acceptSecretChat = addConfig("acceptSecretChat", configTypeBool, true);
    public static ConfigItem disableTrending = addConfig("disableTrending", configTypeBool, true);
    public static ConfigItem dontSendGreetingSticker = addConfig("dontSendGreetingSticker", configTypeBool, false);
    public static ConfigItem hideTimeForSticker = addConfig("hideTimeForSticker", configTypeBool, false);
    public static ConfigItem takeGIFasVideo = addConfig("takeGIFasVideo", configTypeBool, false);
    public static ConfigItem maxRecentStickerCount = addConfig("maxRecentStickerCount", configTypeInt, 20);
    public static ConfigItem disableSwipeToNext = addConfig("disableSwipeToNext", configTypeBool, true);
    public static ConfigItem disableRemoteEmojiInteractions = addConfig("disableRemoteEmojiInteractions", configTypeBool, true);
    public static ConfigItem disableChoosingSticker = addConfig("disableChoosingSticker", configTypeBool, false);

    public static ConfigItem disableAutoDownloadingWin32Executable = addConfig("disableAutoDownloadingWin32Executable", configTypeBool, true);
    public static ConfigItem disableAutoDownloadingArchive = addConfig("disableAutoDownloadingArchive", configTypeBool, true);

    public static ConfigItem enableStickerPin = addConfig("enableStickerPin", configTypeBool, false);
    public static ConfigItem useMediaStreamInVoip = addConfig("useMediaStreamInVoip", configTypeBool, false);
    public static ConfigItem customAudioBitrate = addConfig("customAudioBitrate", configTypeInt, 32);
    public static ConfigItem disableGroupVoipAudioProcessing = addConfig("disableGroupVoipAudioProcessing", configTypeBool, false);


    static {
        loadConfig(false);
    }

    public static ConfigItem addConfig(String k, int t, Object d) {
        ConfigItem a = new ConfigItem(k, t, d);
        configs.add(a);
        return a;
    }

    public static ConfigItem findOne(String key) {
        for (int i = 0; i < configs.size(); i++) {
            ConfigItem o = configs.get(i);
            if (key.equals(o.key)) {
                return o;
            }
        }
        return null;
    }

    public static void loadConfig(boolean force) {
        synchronized (sync) {
            if (configLoaded && !force) {
                return;
            }
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nkmrcfg", Activity.MODE_PRIVATE);
            for (int i = 0; i < configs.size(); i++) {
                ConfigItem o = configs.get(i);

                if (o.forceDefault) {
                    o.value = o.defaultValue;
                    continue;
                }

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

    public static void saveConfig(ConfigItem item) {
        synchronized (sync) {
            try {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nkmrcfg", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                for (int i = 0; i < configs.size(); i++) {
                    ConfigItem o = configs.get(i);
                    if (item != null && !item.getKey().equals(o.getKey())) continue;

                    if (o.type == configTypeBool) {
                        editor.putBoolean(o.key, (boolean) o.value);
                    }
                    if (o.type == configTypeInt) {
                        editor.putInt(o.key, (int) o.value);
                    }
                    if (o.type == configTypeLong) {
                        editor.putLong(o.key, (Long) o.value);
                    }
                    if (o.type == configTypeFloat) {
                        editor.putFloat(o.key, (Float) o.value);
                    }
                    if (o.type == configTypeString) {
                        editor.putString(o.key, o.value.toString());
                    }
                    if (o.type == configTypeSetInt) {
                        HashSet<String> ss = new HashSet<>();
                        for (Integer n : (Set<Integer>) o.value) {
                            ss.add(Integer.toString(n));
                        }
                        editor.putStringSet(o.key, ss);
                    }
                    if (o.type == configTypeMapIntInt) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(o.value);
                        oos.close();
                        editor.putString(o.key, Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
                    }
                }
                editor.commit();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }
}