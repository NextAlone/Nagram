/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.messenger;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;

import androidx.annotation.Nullable;
import androidx.core.content.pm.ShortcutManagerCompat;

import com.v2ray.ang.V2RayConfig;
import com.v2ray.ang.dto.AngConfig;
import com.v2ray.ang.util.Utils;

import org.apache.commons.lang3.StringUtils;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.tgnet.ConnectionsManager;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
import okhttp3.HttpUrl;
import tw.nekomimi.nekogram.ProxyManager;
import tw.nekomimi.nekogram.ShadowsocksLoader;
import tw.nekomimi.nekogram.ShadowsocksRLoader;
import tw.nekomimi.nekogram.VmessLoader;
import tw.nekomimi.nekogram.sub.SubInfo;
import tw.nekomimi.nekogram.sub.SubManager;
import tw.nekomimi.nekogram.utils.AlertUtil;
import tw.nekomimi.nekogram.utils.EnvUtil;
import tw.nekomimi.nekogram.utils.FileUtil;
import tw.nekomimi.nekogram.utils.UIUtil;

import static com.v2ray.ang.V2RayConfig.SSR_PROTOCOL;
import static com.v2ray.ang.V2RayConfig.SS_PROTOCOL;

public class SharedConfig {

    public static String pushString = "";
    public static String pushStringStatus = "";
    public static byte[] pushAuthKey;
    public static byte[] pushAuthKeyId;

    public static String directShareHash;

    public static boolean saveIncomingPhotos;
    public static String passcodeHash = "";
    public static long passcodeRetryInMs;
    public static long lastUptimeMillis;
    public static int badPasscodeTries;
    public static byte[] passcodeSalt = new byte[0];
    public static boolean appLocked;
    public static int passcodeType;
    public static int autoLockIn = 60 * 60;
    public static boolean allowScreenCapture;
    public static int lastPauseTime;
    public static boolean isWaitingForPasscodeEnter;
    public static boolean useFingerprint = true;
    public static String lastUpdateVersion;
    public static int suggestStickers;
    public static boolean loopStickers;
    public static int keepMedia = 2;
    public static int lastKeepMediaCheckTime;
    public static int searchMessagesAsListHintShows;
    public static int textSelectionHintShows;
    public static int scheduledOrNoSoundHintShows;
    public static int lockRecordAudioVideoHint;
    public static boolean searchMessagesAsListUsed;
    public static boolean stickersReorderingHintUsed;
    public static boolean disableVoiceAudioEffects;
    private static int lastLocalId = -210000;

    public static String storageCacheDir;

    private static String passportConfigJson = "";
    private static HashMap<String, String> passportConfigMap;
    public static int passportConfigHash;

    private static boolean configLoaded;
    private static final Object sync = new Object();
    private static final Object localIdSync = new Object();

    public static boolean saveToGallery;
    public static int mapPreviewType = 2;
    public static boolean chatBubbles = Build.VERSION.SDK_INT >= 30;
    public static boolean autoplayGifs = true;
    public static boolean autoplayVideo = true;
    public static boolean raiseToSpeak = true;
    public static boolean customTabs = true;
    public static boolean directShare = true;
    public static boolean inappCamera = true;
    public static boolean roundCamera16to9 = true;
    public static boolean noSoundHintShowed = false;
    public static boolean streamMedia = true;
    public static boolean streamAllVideo = false;
    public static boolean streamMkv = false;
    public static boolean saveStreamMedia = true;
    public static boolean smoothKeyboard = true;
    public static boolean pauseMusicOnRecord = true;
    public static boolean noStatusBar;
    public static boolean sortContactsByName;
    public static boolean sortFilesByName;
    public static boolean shuffleMusic;
    public static boolean playOrderReversed;
    public static boolean hasCameraCache;
    public static boolean showNotificationsForAllAccounts = true;
    public static int repeatMode;
    public static boolean allowBigEmoji;
    public static boolean useSystemEmoji;
    public static int fontSize = 12;
    public static int bubbleRadius = 3;
    public static int ivFontSize = 12;
    private static int devicePerformanceClass;

    public static boolean drawDialogIcons;
    public static boolean useThreeLinesLayout;
    public static boolean archiveHidden;

    public static int distanceSystemType;

    public static ConcurrentHashSet<Integer> activeAccounts;
    public static int loginingAccount = -1;

    static {
        loadConfig();
    }

    public static class ProxyInfo implements Comparable<ProxyInfo> {

        public int group;

        public String address;
        public int port;
        public String username;
        public String password;
        public String secret;

        public long proxyCheckPingId;
        public long ping;
        public boolean checking;
        public boolean available;
        public long availableCheckTime;

        @Override
        public int compareTo(ProxyInfo info) {

            if (available && !info.available) {
                return -1;
            } else if (!available && info.available) {
                return 1;
            } else if (available && info.available) {
                return (int) (ping - info.ping);
            } else {
                return hashCode() + "".compareTo(info.hashCode() + "");
            }

        }

        public long subId;

        public ProxyInfo() {
            address = "";
            password = "";
            username = "";
            secret = "";
        }

        public ProxyInfo(String a, int p, String u, String pw, String s) {
            address = a;
            port = p;
            username = u;
            password = pw;
            secret = s;
            if (address == null) {
                address = "";
            }
            if (password == null) {
                password = "";
            }
            if (username == null) {
                username = "";
            }
            if (secret == null) {
                secret = "";
            }
        }

        public String getAddress() {

            return address + ":" + port;

        }

        public String getType() {

            if (!StrUtil.isBlank(secret)) {

                return "MTProto";

            } else {

                return "Socks5";

            }

        }

        public String getTitle() {

            StringBuilder builder = new StringBuilder();

            builder.append("[ ");

            if (subId != 0L) {

                try {

                    builder.append(SubManager.getSubList().find(ObjectFilters.eq("id", subId)).firstOrDefault().displayName());

                } catch (Exception e) {

                    builder.append("Unknown");

                }

            } else {

                builder.append(getType());

            }

            builder.append(" ] ");

            if (StrUtil.isBlank(getRemarks())) {

                builder.append(getAddress());

            } else {

                builder.append(getRemarks());

            }

            return builder.toString();

        }

        private String remarks;

        public String getRemarks() {

            return remarks;

        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
            if (StrUtil.isBlank(remarks)) {
                this.remarks = null;
            }
        }

        public String toUrl() {

            HttpUrl.Builder builder = HttpUrl.parse(StrUtil.isBlank(secret) ?
                    "https://t.me/socks" : "https://t.me/proxy").newBuilder()
                    .addQueryParameter("server", address)
                    .addQueryParameter("port", port + "");

            if (!StrUtil.isBlank(secret)) {

                builder.addQueryParameter("secret", secret);

            } else {

                builder.addQueryParameter("user", username)
                        .addQueryParameter("pass", password);

            }

            if (!StrUtil.isBlank(remarks)) {

                builder.fragment(Utils.INSTANCE.urlEncode(remarks));

            }

            return builder.toString();

        }

        public static ProxyInfo fromUrl(String url) {

            Uri lnk = Uri.parse(url);

            if (lnk == null) throw new IllegalArgumentException(url);

            ProxyInfo info = new ProxyInfo(lnk.getQueryParameter("server"),
                    Utilities.parseInt(lnk.getQueryParameter("port")),
                    lnk.getQueryParameter("user"),
                    lnk.getQueryParameter("pass"),
                    lnk.getQueryParameter("secret"));

            if (StrUtil.isNotBlank(lnk.getFragment())) {

                info.setRemarks(lnk.getFragment());

            }

            return info;

        }

        public JSONObject toJson() throws JSONException {

            JSONObject object = toJsonInternal();

            return object;

        }

        public JSONObject toJsonInternal() throws JSONException {

            JSONObject obj = new JSONObject();

            if (!StrUtil.isBlank(remarks)) {
                obj.put("remarks", remarks);
            }

            if (group != 0) {
                obj.put("group", group);
            }

            obj.put("address", address);
            obj.put("port", port);
            if (StrUtil.isBlank(secret)) {
                obj.put("type", "socks5");
                if (!username.isEmpty()) {
                    obj.put("username", username);
                }
                if (!password.isEmpty()) {
                    obj.put("password", password);
                }
            } else {
                obj.put("type", "mtproto");
                obj.put("secret", secret);
            }

            return obj;

        }

        public static ProxyInfo fromJson(JSONObject obj) {

            ProxyInfo info;

            switch (obj.optString("type", "null")) {

                case "socks5": {

                    info = new ProxyInfo();

                    info.group = obj.optInt("group", 0);
                    info.address = obj.optString("address", "");
                    info.port = obj.optInt("port", 443);
                    info.username = obj.optString("username", "");
                    info.password = obj.optString("password", "");

                    info.remarks = obj.optString("remarks");

                    if (StrUtil.isBlank(info.remarks)) info.remarks = null;

                    info.group = obj.optInt("group", 0);

                    break;

                }

                case "mtproto": {

                    info = new ProxyInfo();

                    info.address = obj.optString("address", "");
                    info.port = obj.optInt("port", 443);
                    info.secret = obj.optString("secret", "");

                    info.remarks = obj.optString("remarks");

                    if (StrUtil.isBlank(info.remarks)) info.remarks = null;

                    info.group = obj.optInt("group", 0);

                    break;

                }

                case "vmess": {

                    info = new VmessProxy(obj.optString("link"));

                    break;

                }

                case "shadowsocks": {

                    info = new ShadowsocksProxy(obj.optString("link"));

                    break;

                }

                case "shadowsocksr": {

                    info = new ShadowsocksRProxy(obj.optString("link"));

                    break;

                }

                default: {

                    throw new IllegalStateException("invalid proxy type " + obj.optString("type", "null"));

                }

            }

            return info;

        }

        @Override
        public int hashCode() {

            return (address + port + username + password + secret).hashCode();

        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return super.equals(obj) || (obj instanceof ProxyInfo && hashCode() == obj.hashCode());
        }
    }

    public abstract static class ExternalSocks5Proxy extends ProxyInfo {

        public ExternalSocks5Proxy() {

            address = "127.0.0.1";
            username = "";
            password = "";
            secret = "";

        }

        public abstract boolean isStarted();

        public abstract void start();

        public abstract void stop();

        @Override
        public abstract String getAddress();

        @Override
        public abstract String toUrl();

        @Override
        public abstract String getRemarks();

        @Override
        public abstract void setRemarks(String remarks);

        @Override
        public abstract String getType();

        @Override
        public abstract JSONObject toJsonInternal() throws JSONException;

    }

    public static class VmessProxy extends ExternalSocks5Proxy {

        public AngConfig.VmessBean bean;
        public VmessLoader loader;

        {

            if (BuildVars.isMini) {

                throw new RuntimeException(LocaleController.getString("MiniVersionAlert", R.string.MiniVersionAlert));

            }

        }

        public VmessProxy(String vmessLink) {

            this(VmessLoader.parseVmessLink(vmessLink));

        }

        public VmessProxy(AngConfig.VmessBean bean) {

            this.bean = bean;

        }

        @Override
        public String getAddress() {
            return bean.getAddress() + ":" + bean.getPort();
        }

        @Override
        public boolean isStarted() {

            return loader != null;

        }

        @Override
        public void start() {

            if (loader != null) return;

            VmessLoader loader = new VmessLoader();

            try {

                loader.initConfig(bean);

                port = loader.start();

                this.loader = loader;

                if (SharedConfig.proxyEnabled && SharedConfig.currentProxy == this) {

                    ConnectionsManager.setProxySettings(true, address, port, username, password, secret);

                }

            } catch (Exception e) {

                FileLog.e(e);

                AlertUtil.showToast(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());

            }

        }

        @Override
        public void stop() {

            if (loader != null) {

                VmessLoader loader = this.loader;

                loader.stop();

                this.loader = null;

            }

        }

        @Override
        public String toUrl() {
            return bean.toString();
        }

        @Override
        public String getRemarks() {
            return bean.getRemarks();
        }

        @Override
        public void setRemarks(String remarks) {
            bean.setRemarks(remarks);
        }

        @Override
        public String getType() {

            if (bean.getConfigType() == V2RayConfig.EConfigType.Trojan) {

                return "Trojan";

            } else {

                return "Vmess";

            }

        }

        @Override
        public JSONObject toJsonInternal() throws JSONException {

            JSONObject obj = new JSONObject();
            obj.put("type", "vmess");
            obj.put("link", toUrl());
            return obj;

        }

        @Override
        public int hashCode() {
            return (bean.getAddress() + bean.getPort() + bean.getId() + bean.getNetwork() + bean.getPath()).hashCode();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return super.equals(obj) || (obj instanceof VmessProxy && bean.equals(((VmessProxy) obj).bean));
        }

    }

    public static class ShadowsocksProxy extends ExternalSocks5Proxy {

        public ShadowsocksLoader.Bean bean;
        public ShadowsocksLoader loader;

        public ShadowsocksProxy(String ssLink) {

            this(ShadowsocksLoader.Bean.Companion.parse(ssLink));

        }

        public ShadowsocksProxy(ShadowsocksLoader.Bean bean) {

            this.bean = bean;

            if (BuildVars.isMini) {

                throw new RuntimeException(LocaleController.getString("MiniVersionAlert", R.string.MiniVersionAlert));

            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                throw new RuntimeException(LocaleController.getString("MinApi21Required", R.string.MinApi21Required));

            }

        }

        @Override
        public String getAddress() {
            return bean.getHost() + ":" + bean.getRemotePort();
        }

        @Override
        public boolean isStarted() {

            return loader != null;

        }

        @Override
        public void start() {

            if (loader != null) return;

            port = ProxyManager.mkPort();
            ShadowsocksLoader loader = new ShadowsocksLoader();
            loader.initConfig(bean, port);

            loader.start();

            this.loader = loader;

            if (SharedConfig.proxyEnabled && SharedConfig.currentProxy == this) {

                ConnectionsManager.setProxySettings(true, address, port, username, password, secret);

            }

        }

        @Override
        public void stop() {

            if (loader != null) {

                FileLog.d(getTitle() + " stopped");

                ShadowsocksLoader loader = this.loader;

                loader.stop();

                this.loader = null;

            }

        }

        @Override
        public String toUrl() {
            return bean.toString();
        }


        @Override
        public String getRemarks() {
            return bean.getRemarks();
        }

        @Override
        public void setRemarks(String remarks) {
            bean.setRemarks(remarks);
        }

        @Override
        public String getType() {
            return "SS";
        }

        @Override
        public JSONObject toJsonInternal() throws JSONException {

            JSONObject obj = new JSONObject();
            obj.put("type", "shadowsocks");
            obj.put("link", toUrl());
            return obj;

        }

        @Override
        public int hashCode() {

            return (bean.getHost() + bean.getRemotePort() + bean.getMethod()).hashCode();

        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return super.equals(obj) || (obj instanceof ShadowsocksProxy && bean.equals(((ShadowsocksProxy) obj).bean));
        }

    }

    public static class ShadowsocksRProxy extends ExternalSocks5Proxy {

        public ShadowsocksRLoader.Bean bean;
        public ShadowsocksRLoader loader;

        public ShadowsocksRProxy(String ssLink) {

            this(ShadowsocksRLoader.Bean.Companion.parse(ssLink));

        }

        public ShadowsocksRProxy(ShadowsocksRLoader.Bean bean) {

            this.bean = bean;

            if (BuildVars.isMini) {

                throw new RuntimeException(LocaleController.getString("MiniVersionAlert", R.string.MiniVersionAlert));

            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                throw new RuntimeException(LocaleController.getString("MinApi21Required", R.string.MinApi21Required));

            }

        }

        @Override
        public String getAddress() {
            return bean.getHost() + ":" + bean.getRemotePort();
        }

        @Override
        public boolean isStarted() {

            return loader != null;

        }

        @Override
        public void start() {

            if (loader != null) return;

            port = ProxyManager.mkPort();
            ShadowsocksRLoader loader = new ShadowsocksRLoader();
            loader.initConfig(bean, port);

            loader.start();

            this.loader = loader;

            if (SharedConfig.proxyEnabled && SharedConfig.currentProxy == this) {

                ConnectionsManager.setProxySettings(true, address, port, username, password, secret);

            }

        }

        @Override
        public void stop() {

            if (loader != null) {

                ShadowsocksRLoader loader = this.loader;

                this.loader = null;

                loader.stop();

            }

        }

        @Override
        public String toUrl() {
            return bean.toString();
        }

        @Override
        public String getRemarks() {
            return bean.getRemarks();
        }

        @Override
        public void setRemarks(String remarks) {
            bean.setRemarks(remarks);
        }

        @Override
        public String getType() {
            return "SSR";
        }

        @Override
        public JSONObject toJsonInternal() throws JSONException {

            JSONObject obj = new JSONObject();
            obj.put("type", "shadowsocksr");
            obj.put("link", toUrl());
            return obj;

        }

        @Override
        public int hashCode() {

            return (bean.getHost() + bean.getRemotePort() + bean.getMethod() + bean.getProtocol() + bean.getProtocol_param() + bean.getObfs() + bean.getObfs_param()).hashCode();

        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return super.equals(obj) || (obj instanceof ShadowsocksRProxy && bean.equals(((ShadowsocksRProxy) obj).bean));
        }

    }

    public static LinkedList<ProxyInfo> proxyList = new LinkedList<>();

    public static LinkedList<ProxyInfo> getProxyList() {

        while (true) {

            try {

                return new LinkedList<>(proxyList);

            } catch (Exception ignored) {
            }

        }

    }

    private static boolean proxyListLoaded;
    public static ProxyInfo currentProxy;

    public static void saveConfig() {
        synchronized (sync) {
            try {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("saveIncomingPhotos", saveIncomingPhotos);
                editor.putString("passcodeHash1", passcodeHash);
                editor.putString("passcodeSalt", passcodeSalt.length > 0 ? Base64.encodeToString(passcodeSalt, Base64.DEFAULT) : "");
                editor.putBoolean("appLocked", appLocked);
                editor.putInt("passcodeType", passcodeType);
                editor.putLong("passcodeRetryInMs", passcodeRetryInMs);
                editor.putLong("lastUptimeMillis", lastUptimeMillis);
                editor.putInt("badPasscodeTries", badPasscodeTries);
                editor.putInt("autoLockIn", autoLockIn);
                editor.putInt("lastPauseTime", lastPauseTime);
                editor.putString("lastUpdateVersion2", lastUpdateVersion);
                editor.putBoolean("useFingerprint", useFingerprint);
                editor.putBoolean("allowScreenCapture", allowScreenCapture);
                editor.putString("pushString2", pushString);
                editor.putString("pushAuthKey", pushAuthKey != null ? Base64.encodeToString(pushAuthKey, Base64.DEFAULT) : "");
                editor.putInt("lastLocalId", lastLocalId);
                editor.putString("passportConfigJson", passportConfigJson);
                editor.putInt("passportConfigHash", passportConfigHash);
                editor.putBoolean("sortContactsByName", sortContactsByName);
                editor.putBoolean("sortFilesByName", sortFilesByName);
                editor.putInt("textSelectionHintShows", textSelectionHintShows);
                editor.putInt("scheduledOrNoSoundHintShows", scheduledOrNoSoundHintShows);
                editor.putInt("lockRecordAudioVideoHint", lockRecordAudioVideoHint);
                editor.putBoolean("disableVoiceAudioEffects", disableVoiceAudioEffects);
                editor.putString("storageCacheDir", !TextUtils.isEmpty(storageCacheDir) ? storageCacheDir : "");
                editor.apply();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static int getLastLocalId() {
        int value;
        synchronized (localIdSync) {
            value = lastLocalId--;
        }
        return value;
    }

    public static void saveAccounts() {
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE).edit()
                .putString("active_accounts", StringUtils.join(activeAccounts, ","))
                .apply();
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded || ApplicationLoader.applicationContext == null) {
                return;
            }

            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", Context.MODE_PRIVATE);
            saveIncomingPhotos = preferences.getBoolean("saveIncomingPhotos", false);
            passcodeHash = preferences.getString("passcodeHash1", "");
            appLocked = preferences.getBoolean("appLocked", false);
            passcodeType = preferences.getInt("passcodeType", 0);
            passcodeRetryInMs = preferences.getLong("passcodeRetryInMs", 0);
            lastUptimeMillis = preferences.getLong("lastUptimeMillis", 0);
            badPasscodeTries = preferences.getInt("badPasscodeTries", 0);
            autoLockIn = preferences.getInt("autoLockIn", 60 * 60);
            lastPauseTime = preferences.getInt("lastPauseTime", 0);
            useFingerprint = preferences.getBoolean("useFingerprint", true);
            lastUpdateVersion = preferences.getString("lastUpdateVersion2", "3.5");
            allowScreenCapture = preferences.getBoolean("allowScreenCapture", false);
            lastLocalId = preferences.getInt("lastLocalId", -210000);
            pushString = preferences.getString("pushString2", "");
            passportConfigJson = preferences.getString("passportConfigJson", "");
            passportConfigHash = preferences.getInt("passportConfigHash", 0);
            storageCacheDir = preferences.getString("storageCacheDir", null);
            String authKeyString = preferences.getString("pushAuthKey", null);
            if (!TextUtils.isEmpty(authKeyString)) {
                pushAuthKey = Base64.decode(authKeyString, Base64.DEFAULT);
            }

            if (passcodeHash.length() > 0 && lastPauseTime == 0) {
                lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000 - 60 * 10);
            }

            String passcodeSaltString = preferences.getString("passcodeSalt", "");
            if (passcodeSaltString.length() > 0) {
                passcodeSalt = Base64.decode(passcodeSaltString, Base64.DEFAULT);
            } else {
                passcodeSalt = new byte[0];
            }

            preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            saveToGallery = preferences.getBoolean("save_gallery", false);
            autoplayGifs = preferences.getBoolean("autoplay_gif", true);
            autoplayVideo = preferences.getBoolean("autoplay_video", true);
            mapPreviewType = preferences.getInt("mapPreviewType", 2);
            raiseToSpeak = preferences.getBoolean("raise_to_speak", true);
            customTabs = preferences.getBoolean("custom_tabs", true);
            directShare = preferences.getBoolean("direct_share", true);
            shuffleMusic = preferences.getBoolean("shuffleMusic", false);
            playOrderReversed = !shuffleMusic && preferences.getBoolean("playOrderReversed", false);
            inappCamera = preferences.getBoolean("inappCamera", true);
            hasCameraCache = preferences.contains("cameraCache");
            roundCamera16to9 = true;//preferences.getBoolean("roundCamera16to9", false);
            repeatMode = preferences.getInt("repeatMode", 0);
            fontSize = preferences.getInt("fons_size", AndroidUtilities.isTablet() ? 14 : 12);
            bubbleRadius = preferences.getInt("bubbleRadius", 3);
            ivFontSize = preferences.getInt("iv_font_size", fontSize);
            allowBigEmoji = preferences.getBoolean("allowBigEmoji", true);
            useSystemEmoji = preferences.getBoolean("useSystemEmoji", false);
            streamMedia = preferences.getBoolean("streamMedia", true);
            saveStreamMedia = preferences.getBoolean("saveStreamMedia", true);
            smoothKeyboard = preferences.getBoolean("smoothKeyboard2", true);
            pauseMusicOnRecord = preferences.getBoolean("pauseMusicOnRecord", true);
            streamAllVideo = preferences.getBoolean("streamAllVideo", BuildVars.DEBUG_VERSION);
            streamMkv = preferences.getBoolean("streamMkv", false);
            suggestStickers = preferences.getInt("suggestStickers", 0);
            sortContactsByName = preferences.getBoolean("sortContactsByName", false);
            sortFilesByName = preferences.getBoolean("sortFilesByName", false);
            noSoundHintShowed = preferences.getBoolean("noSoundHintShowed", false);
            directShareHash = preferences.getString("directShareHash2", null);
            useThreeLinesLayout = preferences.getBoolean("useThreeLinesLayout", false);
            archiveHidden = preferences.getBoolean("archiveHidden", false);
            distanceSystemType = preferences.getInt("distanceSystemType", 0);
            devicePerformanceClass = preferences.getInt("devicePerformanceClass", -1);
            loopStickers = preferences.getBoolean("loopStickers", true);
            keepMedia = preferences.getInt("keep_media", 2);
            noStatusBar = preferences.getBoolean("noStatusBar", false);
            lastKeepMediaCheckTime = preferences.getInt("lastKeepMediaCheckTime", 0);
            searchMessagesAsListHintShows = preferences.getInt("searchMessagesAsListHintShows", 0);
            searchMessagesAsListUsed = preferences.getBoolean("searchMessagesAsListUsed", false);
            stickersReorderingHintUsed = preferences.getBoolean("stickersReorderingHintUsed", false);
            textSelectionHintShows = preferences.getInt("textSelectionHintShows", 0);
            scheduledOrNoSoundHintShows = preferences.getInt("scheduledOrNoSoundHintShows", 0);
            lockRecordAudioVideoHint = preferences.getInt("lockRecordAudioVideoHint", 0);
            disableVoiceAudioEffects = preferences.getBoolean("disableVoiceAudioEffects", false);
            preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE);
            showNotificationsForAllAccounts = preferences.getBoolean("AllAccounts", true);
            activeAccounts = Arrays.stream(preferences.getString("active_accounts", "").split(",")).filter(StrUtil::isNotBlank).map(Integer::parseInt).collect(Collectors.toCollection(ConcurrentHashSet::new));

            if (!preferences.contains("accounts_loaded")) {
                int maxAccounts;

                File sharedPrefs = new File(ApplicationLoader.applicationContext.getFilesDir().getParentFile(), "shared_prefs");
                if (new File(sharedPrefs, "userconfig31.xml").isFile()) {
                    maxAccounts = 32;
                } else if (new File(sharedPrefs, "userconfig15.xml").isFile()) {
                    maxAccounts = 16;
                } else {
                    maxAccounts = -1;
                }

                for (int i = 0; i < maxAccounts; i++) {
                    SharedPreferences perf;
                    if (i == 0) {
                        perf = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", Context.MODE_PRIVATE);
                    } else {
                        perf = ApplicationLoader.applicationContext.getSharedPreferences("userconfig" + i, Context.MODE_PRIVATE);
                    }
                    if (StrUtil.isNotBlank(perf.getString("user", null))) {
                        activeAccounts.add(i);
                    }
                }

                if (!SharedConfig.activeAccounts.isEmpty()) {
                    preferences.edit().putString("active_accounts", StringUtils.join(activeAccounts, ",")).apply();
                }

                preferences.edit().putBoolean("accounts_loaded", true).apply();
            }

            configLoaded = true;

        }

    }

    public static void increaseBadPasscodeTries() {
        SharedConfig.badPasscodeTries++;
        if (badPasscodeTries >= 3) {
            switch (SharedConfig.badPasscodeTries) {
                case 3:
                    passcodeRetryInMs = 5000;
                    break;
                case 4:
                    passcodeRetryInMs = 10000;
                    break;
                case 5:
                    passcodeRetryInMs = 15000;
                    break;
                case 6:
                    passcodeRetryInMs = 20000;
                    break;
                case 7:
                    passcodeRetryInMs = 25000;
                    break;
                default:
                    passcodeRetryInMs = 30000;
                    break;
            }
            SharedConfig.lastUptimeMillis = SystemClock.elapsedRealtime();
        }
        saveConfig();
    }

    public static boolean isPassportConfigLoaded() {
        return passportConfigMap != null;
    }

    public static void setPassportConfig(String json, int hash) {
        passportConfigMap = null;
        passportConfigJson = json;
        passportConfigHash = hash;
        saveConfig();
        getCountryLangs();
    }

    public static HashMap<String, String> getCountryLangs() {
        if (passportConfigMap == null) {
            passportConfigMap = new HashMap<>();
            try {
                JSONObject object = new JSONObject(passportConfigJson);
                Iterator<String> iter = object.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    passportConfigMap.put(key.toUpperCase(), object.getString(key).toUpperCase());
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        return passportConfigMap;
    }

    public static boolean checkPasscode(String passcode) {
        if (passcodeSalt.length == 0) {
            boolean result = Utilities.MD5(passcode).equals(passcodeHash);
            if (result) {
                try {
                    passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(passcodeSalt);
                    byte[] passcodeBytes = passcode.getBytes("UTF-8");
                    byte[] bytes = new byte[32 + passcodeBytes.length];
                    System.arraycopy(passcodeSalt, 0, bytes, 0, 16);
                    System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                    System.arraycopy(passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                    passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, bytes.length));
                    saveConfig();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            return result;
        } else {
            try {
                byte[] passcodeBytes = passcode.getBytes("UTF-8");
                byte[] bytes = new byte[32 + passcodeBytes.length];
                System.arraycopy(passcodeSalt, 0, bytes, 0, 16);
                System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                System.arraycopy(passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                String hash = Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, bytes.length));
                return passcodeHash.equals(hash);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return false;
    }

    public static void clearConfig() {
        saveIncomingPhotos = false;
        appLocked = false;
        passcodeType = 0;
        passcodeRetryInMs = 0;
        lastUptimeMillis = 0;
        badPasscodeTries = 0;
        passcodeHash = "";
        passcodeSalt = new byte[0];
        autoLockIn = 60 * 60;
        lastPauseTime = 0;
        useFingerprint = true;
        isWaitingForPasscodeEnter = false;
        allowScreenCapture = false;
        lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
        textSelectionHintShows = 0;
        scheduledOrNoSoundHintShows = 0;
        lockRecordAudioVideoHint = 0;
        saveConfig();
    }

    public static void setSuggestStickers(int type) {
        suggestStickers = type;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("suggestStickers", suggestStickers);
        editor.commit();
    }

    public static void setSearchMessagesAsListUsed(boolean searchMessagesAsListUsed) {
        SharedConfig.searchMessagesAsListUsed = searchMessagesAsListUsed;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("searchMessagesAsListUsed", searchMessagesAsListUsed);
        editor.commit();
    }

    public static void setStickersReorderingHintUsed(boolean stickersReorderingHintUsed) {
        SharedConfig.stickersReorderingHintUsed = stickersReorderingHintUsed;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("stickersReorderingHintUsed", stickersReorderingHintUsed);
        editor.commit();
    }

    public static void increaseTextSelectionHintShowed() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("textSelectionHintShows", ++textSelectionHintShows);
        editor.commit();
    }

    public static void removeTextSelectionHint() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("textSelectionHintShows", 3);
        editor.commit();
    }

    public static void increaseScheduledOrNoSuoundHintShowed() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("scheduledOrNoSoundHintShows", ++scheduledOrNoSoundHintShows);
        editor.commit();
    }

    public static void removeScheduledOrNoSuoundHint() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("scheduledOrNoSoundHintShows", 3);
        editor.commit();
    }

    public static void increaseLockRecordAudioVideoHintShowed() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("lockRecordAudioVideoHint", ++lockRecordAudioVideoHint);
        editor.commit();
    }

    public static void removeLockRecordAudioVideoHint() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("lockRecordAudioVideoHint", 3);
        editor.commit();
    }

    public static void increaseSearchAsListHintShows() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("searchMessagesAsListHintShows", ++searchMessagesAsListHintShows);
        editor.commit();
    }

    public static void setKeepMedia(int value) {
        keepMedia = value;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("keep_media", keepMedia);
        editor.commit();
    }

    public static void checkKeepMedia() {
        int time = (int) (System.currentTimeMillis() / 1000);
        if (Math.abs(time - lastKeepMediaCheckTime) < 60 * 60) {
            return;
        }
        lastKeepMediaCheckTime = time;
        File cacheDir = FileLoader.checkDirectory(FileLoader.MEDIA_DIR_CACHE);
        Utilities.globalQueue.postRunnable(() -> {
            if (keepMedia != 2) {
                int days;
                if (keepMedia == 0) {
                    days = 7;
                } else if (keepMedia == 1) {
                    days = 30;
                } else if (keepMedia == 4) {
                    days = 1;
                } else {
                    days = 3;
                }
                long currentTime = time - 60 * 60 * 24 * days;
                final SparseArray<File> paths = ImageLoader.getInstance().createMediaPaths();
                for (int a = 0; a < paths.size(); a++) {
                    if (paths.keyAt(a) == FileLoader.MEDIA_DIR_CACHE) {
                        continue;
                    }
                    try {
                        Utilities.clearDir(paths.valueAt(a).getAbsolutePath(), 0, currentTime, false);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            }
            File stickersPath = new File(cacheDir, "acache");
            if (stickersPath.exists()) {
                long currentTime = time - 60 * 60 * 24;
                try {
                    Utilities.clearDir(stickersPath.getAbsolutePath(), 0, currentTime, false);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("lastKeepMediaCheckTime", lastKeepMediaCheckTime);
            editor.commit();
        });
    }

    public static void toggleDisableVoiceAudioEffects() {
        disableVoiceAudioEffects = !disableVoiceAudioEffects;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableVoiceAudioEffects", disableVoiceAudioEffects);
        editor.commit();
    }

    public static void toggleNoStatusBar() {
        noStatusBar = !noStatusBar;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("noStatusBar", noStatusBar);
        editor.commit();
    }

    public static void toggleLoopStickers() {
        loopStickers = !loopStickers;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("loopStickers", loopStickers);
        editor.commit();
    }

    public static void toggleBigEmoji() {
        allowBigEmoji = !allowBigEmoji;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("allowBigEmoji", allowBigEmoji);
        editor.commit();
    }

    public static void setPlaybackOrderType(int type) {
        if (type == 2) {
            shuffleMusic = true;
            playOrderReversed = false;
        } else if (type == 1) {
            playOrderReversed = true;
            shuffleMusic = false;
        } else {
            playOrderReversed = false;
            shuffleMusic = false;
        }
        MediaController.getInstance().checkIsNextMediaFileDownloaded();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("shuffleMusic", shuffleMusic);
        editor.putBoolean("playOrderReversed", playOrderReversed);
        editor.commit();
    }

    public static void setRepeatMode(int mode) {
        repeatMode = mode;
        if (repeatMode < 0 || repeatMode > 2) {
            repeatMode = 0;
        }
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("repeatMode", repeatMode);
        editor.commit();
    }

    public static void toggleSaveToGallery() {
        saveToGallery = !saveToGallery;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("save_gallery", saveToGallery);
        editor.commit();
        checkSaveToGalleryFiles();
    }

    public static void toggleAutoplayGifs() {
        autoplayGifs = !autoplayGifs;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("autoplay_gif", autoplayGifs);
        editor.commit();
    }

    public static void setUseThreeLinesLayout(boolean value) {
        useThreeLinesLayout = value;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("useThreeLinesLayout", useThreeLinesLayout);
        editor.commit();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.dialogsNeedReload, true);
    }

    public static void toggleArchiveHidden() {
        archiveHidden = !archiveHidden;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("archiveHidden", archiveHidden);
        editor.commit();
    }

    public static void toggleAutoplayVideo() {
        autoplayVideo = !autoplayVideo;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("autoplay_video", autoplayVideo);
        editor.commit();
    }

    public static boolean isSecretMapPreviewSet() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        return preferences.contains("mapPreviewType");
    }

    public static void setSecretMapPreviewType(int value) {
        mapPreviewType = value;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("mapPreviewType", mapPreviewType);
        editor.commit();
    }

    public static void setNoSoundHintShowed(boolean value) {
        if (noSoundHintShowed == value) {
            return;
        }
        noSoundHintShowed = value;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("noSoundHintShowed", noSoundHintShowed);
        editor.commit();
    }

    public static void toogleRaiseToSpeak() {
        raiseToSpeak = !raiseToSpeak;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("raise_to_speak", raiseToSpeak);
        editor.commit();
    }

    public static void toggleCustomTabs() {
        customTabs = !customTabs;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("custom_tabs", customTabs);
        editor.commit();
    }

    public static void toggleDirectShare() {
        directShare = !directShare;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("direct_share", directShare);
        editor.commit();
        ShortcutManagerCompat.removeAllDynamicShortcuts(ApplicationLoader.applicationContext);
        MediaDataController.getInstance(UserConfig.selectedAccount).buildShortcuts();
    }

    public static void toggleStreamMedia() {
        streamMedia = !streamMedia;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("streamMedia", streamMedia);
        editor.commit();
    }

    public static void toggleSortContactsByName() {
        sortContactsByName = !sortContactsByName;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("sortContactsByName", sortContactsByName);
        editor.commit();
    }

    public static void toggleSortFilesByName() {
        sortFilesByName = !sortFilesByName;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("sortFilesByName", sortFilesByName);
        editor.commit();
    }

    public static void toggleStreamAllVideo() {
        streamAllVideo = !streamAllVideo;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("streamAllVideo", streamAllVideo);
        editor.commit();
    }

    public static void toggleStreamMkv() {
        streamMkv = !streamMkv;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("streamMkv", streamMkv);
        editor.commit();
    }

    public static void toggleSaveStreamMedia() {
        saveStreamMedia = !saveStreamMedia;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("saveStreamMedia", saveStreamMedia);
        editor.commit();
    }

    public static void toggleSmoothKeyboard() {
        smoothKeyboard = !smoothKeyboard;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("smoothKeyboard2", smoothKeyboard);
        editor.commit();
    }

    public static void togglePauseMusicOnRecord() {
        pauseMusicOnRecord = !pauseMusicOnRecord;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("pauseMusicOnRecord", pauseMusicOnRecord);
        editor.commit();
    }

    public static void toggleInappCamera() {
        inappCamera = !inappCamera;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("inappCamera", inappCamera);
        editor.commit();
    }

    public static void toggleRoundCamera16to9() {
        roundCamera16to9 = !roundCamera16to9;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("roundCamera16to9", roundCamera16to9);
        editor.commit();
    }

    public static void setDistanceSystemType(int type) {
        distanceSystemType = type;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("distanceSystemType", distanceSystemType);
        editor.commit();
        LocaleController.resetImperialSystemType();
    }

    public static boolean proxyEnabled;

    public static void setProxyEnable(boolean enable) {

        proxyEnabled = enable;

        SharedPreferences preferences = MessagesController.getGlobalMainSettings();

        preferences.edit().putBoolean("proxy_enabled", enable).commit();

        ProxyInfo info = currentProxy;

        if (info == null) {

            info = new ProxyInfo();

        }

        ProxyInfo finalInfo = info;

        UIUtil.runOnIoDispatcher(() -> {

            try {

                if (enable && finalInfo instanceof ExternalSocks5Proxy) {

                    ((ExternalSocks5Proxy) finalInfo).start();

                } else if (!enable && finalInfo instanceof ExternalSocks5Proxy) {

                    ((ExternalSocks5Proxy) finalInfo).stop();

                }

            } catch (Exception e) {

                FileLog.e(e);
                AlertUtil.showToast(e);

                return;

            }

            ConnectionsManager.setProxySettings(enable, finalInfo.address, finalInfo.port, finalInfo.username, finalInfo.password, finalInfo.secret);

            UIUtil.runOnUIThread(() -> NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged));

        });

    }

    public static void setCurrentProxy(@Nullable ProxyInfo info) {

        currentProxy = info;

        MessagesController.getGlobalMainSettings().edit()
                .putInt("current_proxy", info == null ? 0 : info.hashCode())
                .commit();

        setProxyEnable(info != null);

    }

    public static void reloadProxyList() {
        proxyListLoaded = false;
        loadProxyList();

        if (proxyEnabled && currentProxy == null) {
            setProxyEnable(false);
        }

    }

    public static void loadProxyList() {
        if (proxyListLoaded) {
            return;
        }

        if (!proxyList.isEmpty()) {
            for (ProxyInfo proxyInfo : getProxyList()) {
                if (proxyInfo instanceof ExternalSocks5Proxy) {
                    ((ExternalSocks5Proxy) proxyInfo).stop();
                }
            }
        }

        proxyListLoaded = true;
        proxyList.clear();
        currentProxy = null;

        int current = MessagesController.getGlobalMainSettings().getInt("current_proxy", 0);

        for (SubInfo subInfo : SubManager.getSubList().find()) {

            if (!subInfo.enable) continue;

//            if (subInfo.id == 1L) {
//
//                try {
//                    RelayBatonProxy publicProxy = (RelayBatonProxy) parseProxyInfo(RelayBatonLoader.publicServer);
//                    publicProxy.setRemarks(LocaleController.getString("NekoXProxy",R.string.NekoXProxy));
//                    publicProxy.subId = subInfo.id;
//                    proxyList.add(publicProxy);
//                    if (publicProxy.hashCode() == current) {
//                        currentProxy = publicProxy;
//                        UIUtil.runOnIoDispatcher(publicProxy::start);
//                    }
//                } catch (InvalidProxyException e) {
//                    e.printStackTrace();
//                }
//
//            }

            for (String proxy : subInfo.proxies) {

                try {

                    ProxyInfo info = parseProxyInfo(proxy);

                    info.subId = subInfo.id;

                    if (info.hashCode() == current) {

                        currentProxy = info;

                        if (info instanceof ExternalSocks5Proxy) {

                            UIUtil.runOnIoDispatcher(() -> {

                                try {

                                    ((ExternalSocks5Proxy) info).start();

                                } catch (Exception e) {

                                    FileLog.e(e);
                                    AlertUtil.showToast(e);

                                }

                            });

                        }

                    }

                    proxyList.add(info);

                } catch (Exception e) {

                    FileLog.d("load sub proxy failed: " + e);

                }

            }

        }

        File proxyListFile = new File(ApplicationLoader.applicationContext.getFilesDir().getParentFile(), "nekox/proxy_list.json");

        boolean error = false;

        if (proxyListFile.isFile()) {

            try {

                JSONArray proxyArray = new JSONArray(FileUtil.readUtf8String(proxyListFile));

                for (int a = 0; a < proxyArray.length(); a++) {

                    JSONObject proxyObj = proxyArray.getJSONObject(a);

                    ProxyInfo info;

                    try {

                        info = ProxyInfo.fromJson(proxyObj);

                    } catch (Exception ex) {

                        FileLog.d("load proxy failed: " + ex);

                        error = true;

                        continue;

                    }

                    proxyList.add(info);

                    if (info.hashCode() == current) {

                        currentProxy = info;

                        if (info instanceof ExternalSocks5Proxy) {

                            if (info instanceof ExternalSocks5Proxy) {

                                UIUtil.runOnIoDispatcher(() -> {

                                    try {

                                        ((ExternalSocks5Proxy) info).start();

                                    } catch (Exception e) {

                                        FileLog.e(e);
                                        AlertUtil.showToast(e);

                                    }

                                });

                            }
                        }

                    }

                }

            } catch (Exception ex) {

                FileLog.d("invalid proxy list json format" + ex);

            }

        }

        if (error) saveProxyList();

        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);

        boolean proxyEnabledValue = preferences.getBoolean("proxy_enabled", false);

        if (proxyEnabledValue && currentProxy == null) proxyEnabledValue = false;

        proxyEnabled = proxyEnabledValue;

    }

    public static ProxyInfo parseProxyInfo(String url) throws InvalidProxyException {

        if (url.startsWith(V2RayConfig.VMESS_PROTOCOL) || url.startsWith(V2RayConfig.VMESS1_PROTOCOL) || url.startsWith(V2RayConfig.TROJAN_PROTOCOL)) {

            try {

                return new VmessProxy(url);

            } catch (Exception ex) {

                throw new InvalidProxyException(ex);

            }

        } else if (url.startsWith(SS_PROTOCOL)) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                throw new InvalidProxyException("shadowsocks requires min api 21");

            }

            try {

                return new ShadowsocksProxy(url);

            } catch (Exception ex) {

                throw new InvalidProxyException(ex);

            }

        } else if (url.startsWith(SSR_PROTOCOL)) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                throw new InvalidProxyException("shadowsocksR requires min api 21");

            }

            try {

                return new ShadowsocksRProxy(url);

            } catch (Exception ex) {

                throw new InvalidProxyException(ex);

            }

        }/* else if (url.startsWith(RB_PROTOCOL)) {

            try {

                return new RelayBatonProxy(url);

            } catch (Exception ex) {

                throw new InvalidProxyException(ex);

            }

        } */

        if (url.startsWith("tg:proxy") ||
                url.startsWith("tg://proxy") ||
                url.startsWith("tg:socks") ||
                url.startsWith("tg://socks") ||
                url.startsWith("https://t.me/proxy") ||
                url.startsWith("https://t.me/socks")) {
            return ProxyInfo.fromUrl(url);
        }

        throw new InvalidProxyException();

    }

    public static class InvalidProxyException extends Exception {

        public InvalidProxyException() {
        }

        public InvalidProxyException(String messsage) {
            super(messsage);
        }

        public InvalidProxyException(Throwable cause) {

            super(cause);

        }

    }

    public static void saveProxyList() {
        UIUtil.runOnIoDispatcher(() -> {

            JSONArray proxyArray = new JSONArray();

            for (ProxyInfo info : getProxyList()) {
                try {
                    JSONObject obj = info.toJsonInternal();
                    if (info.subId != 0L) {
                        continue;
                    }
                    proxyArray.put(obj);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            File proxyListFile = new File(ApplicationLoader.applicationContext.getFilesDir().getParentFile(), "nekox/proxy_list.json");

            try {
                FileUtil.writeUtf8String(proxyArray.toString(), proxyListFile);
            } catch (Exception e) {
                FileLog.e(e);
            }

        });
    }

    public static ProxyInfo addProxy(ProxyInfo proxyInfo) {
        synchronized (sync) {
            int count = proxyList.size();
            for (int a = 0; a < count; a++) {
                ProxyInfo info = proxyList.get(a);
                if (info.equals(proxyInfo)) {
                    return info;
                }
            }
            proxyList.add(proxyInfo);
        }
        saveProxyList();
        return proxyInfo;
    }

    public static void deleteProxy(ProxyInfo proxyInfo) {

        if (currentProxy == proxyInfo) {
            currentProxy = null;
            if (proxyEnabled) {
                setProxyEnable(false);
            }
        }
        proxyList.remove(proxyInfo);
        if (proxyInfo.subId != 0) {
            SubInfo sub = SubManager.getSubList().find(ObjectFilters.eq("id", proxyInfo.subId)).firstOrDefault();
            try {
                if (sub.proxies.remove(proxyInfo.toUrl())) {
                    SubManager.getSubList().update(sub);
                }
            } catch (UnsupportedOperationException ignored) {
            }
        } else {
            saveProxyList();
        }
    }

    public static void deleteAllProxy() {

        setCurrentProxy(null);

        proxyListLoaded = false;

        proxyList.clear();

        saveProxyList();

        loadProxyList();

    }

    public static void checkSaveToGalleryFiles() {
        try {
            File telegramPath = EnvUtil.getTelegramPath();
            File imagePath = new File(telegramPath, "images");
            imagePath.mkdirs();
            File videoPath = new File(telegramPath, "videos");
            videoPath.mkdirs();

            if (saveToGallery) {
                if (imagePath.isDirectory()) {
                    new File(imagePath, ".nomedia").delete();
                }
                if (videoPath.isDirectory()) {
                    new File(videoPath, ".nomedia").delete();
                }
            } else {
                if (imagePath.isDirectory()) {
                    AndroidUtilities.createEmptyFile(new File(imagePath, ".nomedia"));
                }
                if (videoPath.isDirectory()) {
                    AndroidUtilities.createEmptyFile(new File(videoPath, ".nomedia"));
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public final static int PERFORMANCE_CLASS_LOW = 0;
    public final static int PERFORMANCE_CLASS_AVERAGE = 1;
    public final static int PERFORMANCE_CLASS_HIGH = 2;

    public static int getDevicePerformanceClass() {
        if (devicePerformanceClass == -1) {
            int maxCpuFreq = -1;
            try {
                RandomAccessFile reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
                String line = reader.readLine();
                if (line != null) {
                    maxCpuFreq = Utilities.parseInt(line) / 1000;
                }
                reader.close();
            } catch (Throwable ignore) {

            }
            int androidVersion = Build.VERSION.SDK_INT;
            int cpuCount = ConnectionsManager.CPU_COUNT;
            int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
            if (androidVersion < 21 || cpuCount <= 2 || memoryClass <= 100 || cpuCount <= 4 && maxCpuFreq != -1 && maxCpuFreq <= 1250 || cpuCount <= 4 && maxCpuFreq <= 1600 && memoryClass <= 128 && androidVersion <= 21 || cpuCount <= 4 && maxCpuFreq <= 1300 && memoryClass <= 128 && androidVersion <= 24) {
                devicePerformanceClass = PERFORMANCE_CLASS_LOW;
            } else if (cpuCount < 8 || memoryClass <= 160 || maxCpuFreq != -1 && maxCpuFreq <= 1650 || maxCpuFreq == -1 && cpuCount == 8 && androidVersion <= 23) {
                devicePerformanceClass = PERFORMANCE_CLASS_AVERAGE;
            } else {
                devicePerformanceClass = PERFORMANCE_CLASS_HIGH;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("device performance info (cpu_count = " + cpuCount + ", freq = " + maxCpuFreq + ", memoryClass = " + memoryClass + ", android version " + androidVersion + ")");
            }
        }

        return devicePerformanceClass;
    }
}
