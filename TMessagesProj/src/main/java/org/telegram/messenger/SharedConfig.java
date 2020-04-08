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
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;

import androidx.annotation.Nullable;

import com.v2ray.ang.V2RayConfig;
import com.v2ray.ang.dto.AngConfig;
import com.v2ray.ang.util.Utils;

import org.checkerframework.checker.index.qual.PolyUpperBound;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ProxyListActivity;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import kotlin.text.StringsKt;
import okhttp3.HttpUrl;
import tw.nekomimi.nekogram.NekoXConfig;
import tw.nekomimi.nekogram.ShadowsocksRLoader;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.ProxyManager;
import tw.nekomimi.nekogram.ShadowsocksLoader;
import tw.nekomimi.nekogram.VmessLoader;
import tw.nekomimi.nekogram.utils.FileUtil;
import tw.nekomimi.nekogram.utils.ProxyUtil;
import tw.nekomimi.nekogram.utils.StrUtil;
import tw.nekomimi.nekogram.utils.UIUtil;

import static com.v2ray.ang.V2RayConfig.SSR_PROTOCOL;
import static com.v2ray.ang.V2RayConfig.SS_PROTOCOL;

public class SharedConfig {

    public static String pushString = "";
    public static String pushStringStatus = "";
    public static byte[] pushAuthKey;
    public static byte[] pushAuthKeyId;

    public static long directShareHash;

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
    private static int lastLocalId = -210000;

    private static String passportConfigJson = "";
    private static HashMap<String, String> passportConfigMap;
    public static int passportConfigHash;

    private static boolean configLoaded;
    private static final Object sync = new Object();
    private static final Object localIdSync = new Object();

    public static boolean saveToGallery;
    public static int mapPreviewType = 2;
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
    public static boolean smoothKeyboard = false;
    public static boolean pauseMusicOnRecord = true;
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

        public boolean isPublic;

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


        public String getTitle() {

            if (StrUtil.isBlank(remarks)) {

                return (isPublic ? LocaleController.getString("PublicPrefix", R.string.PublicPrefix) : "[MTProto]") + " " + address + ":" + port;

            } else {

                return (isPublic ? LocaleController.getString("PublicPrefix", R.string.PublicPrefix) : "[MTProto]") + " " + remarks;

            }

        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
            if (StrUtil.isBlank(remarks)) {
                remarks = null;
            }
        }

        private String remarks;

        public String toUrl() {

            HttpUrl.Builder builder = HttpUrl.parse(StrUtil.isBlank(secret) ?
                    "https://t.me/socks" : "https://t.me/proxy").newBuilder()
                    .addQueryParameter("address", address)
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

            HttpUrl lnk = HttpUrl.parse(url);

            return new ProxyInfo(lnk.queryParameter("address"),
                    Utilities.parseInt(lnk.queryParameter("port")),
                    lnk.queryParameter("user"),
                    lnk.queryParameter("pass"),
                    lnk.queryParameter("secret"));

        }

        public JSONObject toJson() throws JSONException {

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
        public abstract String getTitle();

        @Override
        public abstract String toUrl();

        @Override
        public abstract String getRemarks();

        @Override
        public abstract void setRemarks(String remarks);

        @Override
        public abstract JSONObject toJson() throws JSONException;

    }

    public static class VmessProxy extends ExternalSocks5Proxy {

        public AngConfig.VmessBean bean;
        public VmessLoader loader;

        public VmessProxy(String vmessLink) {

            this(VmessLoader.parseVmessLink(vmessLink));

        }

        public VmessProxy(AngConfig.VmessBean bean) {

            this.bean = bean;

        }

        @Override
        public String getTitle() {

            if (StrUtil.isBlank(getRemarks())) {

                return (isPublic ? LocaleController.getString("PublicPrefix", R.string.PublicPrefix) : "[Vmess]") + " " + bean.getAddress() + ":" + bean.getPort();

            } else {

                return (isPublic ? LocaleController.getString("PublicPrefix", R.string.PublicPrefix) : "[Vmess]") + " " + getRemarks();

            }

        }

        @Override
        public boolean isStarted() {

            return loader != null;

        }

        @Override
        public void start() {

            stop();

            port = ProxyManager.getPortForBean(bean);

            VmessLoader loader = new VmessLoader();
            loader.initConfig(bean, port);
            loader.start();

            this.loader = loader;

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
        public JSONObject toJson() throws JSONException {

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

        }

        @Override
        public String getTitle() {

            if (StrUtil.isBlank(getRemarks())) {

                return (isPublic ? LocaleController.getString("PublicPrefix", R.string.PublicPrefix) : "[SS]") + " " + bean.getHost() + ":" + bean.getRemotePort();

            } else {

                return (isPublic ? LocaleController.getString("PublicPrefix", R.string.PublicPrefix) : "[SS]") + " " + getRemarks();

            }

        }

        @Override
        public boolean isStarted() {

            return loader != null;

        }

        @Override
        public void start() {

            stop();

            port = ProxyManager.getPortForBean(bean);

            ShadowsocksLoader loader = new ShadowsocksLoader();
            loader.initConfig(bean, port);
            loader.start();

            this.loader = loader;

        }

        @Override
        public void stop() {

            if (loader != null) {

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
        public JSONObject toJson() throws JSONException {

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

        }

        @Override
        public String getTitle() {

            if (StrUtil.isBlank(getRemarks())) {

                return (isPublic ? LocaleController.getString("PublicPrefix", R.string.PublicPrefix) : "[SSR]") + " " + bean.getHost() + ":" + bean.getRemotePort();

            } else {

                return (isPublic ? LocaleController.getString("PublicPrefix", R.string.PublicPrefix) : "[SSR]") + " " + getRemarks();

            }

        }

        @Override
        public boolean isStarted() {

            return loader != null;

        }

        @Override
        public void start() {

            stop();

            port = ProxyManager.getPortForBean(bean);

            ShadowsocksRLoader loader = new ShadowsocksRLoader();
            loader.initConfig(bean, port);
            loader.start();

            this.loader = loader;

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
        public JSONObject toJson() throws JSONException {

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

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
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
            playOrderReversed = preferences.getBoolean("playOrderReversed", false);
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
            smoothKeyboard = preferences.getBoolean("smoothKeyboard", false);
            pauseMusicOnRecord = preferences.getBoolean("pauseMusicOnRecord", true);
            streamAllVideo = preferences.getBoolean("streamAllVideo", BuildVars.DEBUG_VERSION);
            streamMkv = preferences.getBoolean("streamMkv", false);
            suggestStickers = preferences.getInt("suggestStickers", 0);
            sortContactsByName = preferences.getBoolean("sortContactsByName", false);
            sortFilesByName = preferences.getBoolean("sortFilesByName", false);
            noSoundHintShowed = preferences.getBoolean("noSoundHintShowed", false);
            directShareHash = preferences.getLong("directShareHash", 0);
            useThreeLinesLayout = preferences.getBoolean("useThreeLinesLayout", false);
            archiveHidden = preferences.getBoolean("archiveHidden", false);
            distanceSystemType = preferences.getInt("distanceSystemType", 0);
            devicePerformanceClass = preferences.getInt("devicePerformanceClass", -1);
            loopStickers = preferences.getBoolean("loopStickers", true);
            keepMedia = preferences.getInt("keep_media", 2);
            lastKeepMediaCheckTime = preferences.getInt("lastKeepMediaCheckTime", 0);
            searchMessagesAsListHintShows = preferences.getInt("searchMessagesAsListHintShows", 0);
            searchMessagesAsListUsed = preferences.getBoolean("searchMessagesAsListUsed", false);
            stickersReorderingHintUsed = preferences.getBoolean("stickersReorderingHintUsed", false);
            textSelectionHintShows = preferences.getInt("textSelectionHintShows", 0);
            scheduledOrNoSoundHintShows = preferences.getInt("scheduledOrNoSoundHintShows", 0);
            lockRecordAudioVideoHint = preferences.getInt("lockRecordAudioVideoHint", 0);
            preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE);
            showNotificationsForAllAccounts = preferences.getBoolean("AllAccounts", true);

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
        editor.apply();
    }

    public static void setSearchMessagesAsListUsed(boolean searchMessagesAsListUsed) {
        SharedConfig.searchMessagesAsListUsed = searchMessagesAsListUsed;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("searchMessagesAsListUsed", searchMessagesAsListUsed);
        editor.apply();
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
        editor.apply();
    }

    public static void removeTextSelectionHint() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("textSelectionHintShows", 3);
        editor.apply();
    }

    public static void increaseScheduledOrNoSuoundHintShowed() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("scheduledOrNoSoundHintShows", ++scheduledOrNoSoundHintShows);
        editor.apply();
    }

    public static void removeScheduledOrNoSuoundHint() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("scheduledOrNoSoundHintShows", 3);
        editor.apply();
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
        editor.apply();
    }

    public static void setKeepMedia(int value) {
        keepMedia = value;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("keep_media", keepMedia);
        editor.apply();
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
            editor.apply();
        });
    }

    public static void toggleLoopStickers() {
        loopStickers = !loopStickers;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("loopStickers", loopStickers);
        editor.apply();
    }

    public static void toggleBigEmoji() {
        allowBigEmoji = !allowBigEmoji;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("allowBigEmoji", allowBigEmoji);
        editor.apply();
    }

    public static void toggleShuffleMusic(int type) {
        if (type == 2) {
            shuffleMusic = !shuffleMusic;
        } else {
            playOrderReversed = !playOrderReversed;
        }
        MediaController.getInstance().checkIsNextMediaFileDownloaded();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("shuffleMusic", shuffleMusic);
        editor.putBoolean("playOrderReversed", playOrderReversed);
        editor.apply();
    }

    public static void toggleRepeatMode() {
        repeatMode++;
        if (repeatMode > 2) {
            repeatMode = 0;
        }
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("repeatMode", repeatMode);
        editor.apply();
    }

    public static void toggleSaveToGallery() {
        saveToGallery = !saveToGallery;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("save_gallery", saveToGallery);
        editor.apply();
        checkSaveToGalleryFiles();
    }

    public static void toggleAutoplayGifs() {
        autoplayGifs = !autoplayGifs;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("autoplay_gif", autoplayGifs);
        editor.apply();
    }

    public static void setUseThreeLinesLayout(boolean value) {
        useThreeLinesLayout = value;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("useThreeLinesLayout", useThreeLinesLayout);
        editor.apply();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.dialogsNeedReload, true);
    }

    public static void toggleArchiveHidden() {
        archiveHidden = !archiveHidden;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("archiveHidden", archiveHidden);
        editor.apply();
    }

    public static void toggleAutoplayVideo() {
        autoplayVideo = !autoplayVideo;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("autoplay_video", autoplayVideo);
        editor.apply();
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
        editor.apply();
    }

    public static void setNoSoundHintShowed(boolean value) {
        if (noSoundHintShowed == value) {
            return;
        }
        noSoundHintShowed = value;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("noSoundHintShowed", noSoundHintShowed);
        editor.apply();
    }

    public static void toogleRaiseToSpeak() {
        raiseToSpeak = !raiseToSpeak;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("raise_to_speak", raiseToSpeak);
        editor.apply();
    }

    public static void toggleCustomTabs() {
        customTabs = !customTabs;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("custom_tabs", customTabs);
        editor.apply();
    }

    public static void toggleDirectShare() {
        directShare = !directShare;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("direct_share", directShare);
        editor.apply();
    }

    public static void toggleStreamMedia() {
        streamMedia = !streamMedia;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("streamMedia", streamMedia);
        editor.apply();
    }

    public static void toggleSortContactsByName() {
        sortContactsByName = !sortContactsByName;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("sortContactsByName", sortContactsByName);
        editor.apply();
    }

    public static void toggleSortFilesByName() {
        sortFilesByName = !sortFilesByName;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("sortFilesByName", sortFilesByName);
        editor.apply();
    }

    public static void toggleStreamAllVideo() {
        streamAllVideo = !streamAllVideo;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("streamAllVideo", streamAllVideo);
        editor.apply();
    }

    public static void toggleStreamMkv() {
        streamMkv = !streamMkv;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("streamMkv", streamMkv);
        editor.apply();
    }

    public static void toggleSaveStreamMedia() {
        saveStreamMedia = !saveStreamMedia;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("saveStreamMedia", saveStreamMedia);
        editor.apply();
    }

    public static void toggleSmoothKeyboard() {
        smoothKeyboard = !smoothKeyboard;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("smoothKeyboard", smoothKeyboard);
        editor.apply();
    }

    public static void togglePauseMusicOnRecord() {
        pauseMusicOnRecord = !pauseMusicOnRecord;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("pauseMusicOnRecord", pauseMusicOnRecord);
        editor.apply();
    }

    public static void toggleInappCamera() {
        inappCamera = !inappCamera;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("inappCamera", inappCamera);
        editor.apply();
    }

    public static void toggleRoundCamera16to9() {
        roundCamera16to9 = !roundCamera16to9;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("roundCamera16to9", roundCamera16to9);
        editor.apply();
    }

    public static void setDistanceSystemType(int type) {
        distanceSystemType = type;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("distanceSystemType", distanceSystemType);
        editor.apply();
        LocaleController.resetImperialSystemType();
    }

    public static boolean proxyEnabled;

    static {

        loadProxyList();

        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);

        boolean proxyEnabledValue = preferences.getBoolean("proxy_enabled", false);

        if (proxyEnabledValue && currentProxy == null) proxyEnabledValue = false;

        proxyEnabled = proxyEnabledValue;

    }

    public static void setProxyEnable(boolean enable) {

        UIUtil.runOnIoDispatcher(() -> {

            proxyEnabled = enable;

            SharedPreferences preferences = MessagesController.getGlobalMainSettings();

            preferences.edit().putBoolean("proxy_enabled", enable).apply();

            ProxyInfo info = currentProxy;

            if (info == null) {

                info = new ProxyInfo();

            }

            if (enable && info instanceof ExternalSocks5Proxy) {

                ((ExternalSocks5Proxy) info).start();

            } else if (!enable && info instanceof ExternalSocks5Proxy) {

                ((ExternalSocks5Proxy) info).stop();

            }

            ConnectionsManager.setProxySettings(enable, info.address, info.port, info.username, info.password, info.secret);

        });

    }

    public static void setCurrentProxy(@Nullable ProxyInfo info) {

        currentProxy = info;

        MessagesController.getGlobalMainSettings().edit()
                .putInt("current_proxy", info == null ? 0 : info.hashCode())
                .apply();

        saveProxyList();

        setProxyEnable(true);

    }

    public static void reloadProxyList() {
        proxyListLoaded = false;
        loadProxyList();
    }

    public static void loadProxyList() {
        if (proxyListLoaded) {
            return;
        }

        proxyListLoaded = true;
        proxyList.clear();
        currentProxy = null;

        int current = MessagesController.getGlobalMainSettings().getInt("current_proxy", 0);

        try {

            if (!NekoXConfig.hidePublicProxy) {

                VmessProxy publicProxy = new VmessProxy(VmessLoader.getPublic());
                publicProxy.isPublic = true;
                proxyList.add(publicProxy);

                if (publicProxy.hashCode() == current) {

                    currentProxy = publicProxy;

                    publicProxy.start();

                }

            }

        } catch (Exception e) {
            FileLog.e(e);
        }


        File remoteProxyListFile = ProxyUtil.cacheFile;

        if (remoteProxyListFile.isFile() && !NekoXConfig.hidePublicProxy) {

            try {

                JSONArray proxyArray = new JSONArray(FileUtil.readUtf8String(remoteProxyListFile));

                for (int a = 0; a < proxyArray.length(); a++) {

                    JSONObject proxyObj = proxyArray.getJSONObject(a);

                    ProxyInfo info;

                    try {

                        if (!proxyObj.isNull("proxy")) {

                            // old remote protocol

                            info = parseProxyInfo(proxyObj.getString("proxy"));

                        } else {

                            info = ProxyInfo.fromJson(proxyObj);

                        }

                    } catch (Exception ex) {

                        FileLog.e("load proxy failed", ex);

                        continue;

                    }

                    info.isPublic = true;

                    proxyList.add(info);

                    if (info.hashCode() == current) {

                        currentProxy = info;

                        if (info instanceof ExternalSocks5Proxy) {

                            ((ExternalSocks5Proxy) info).start();

                        }

                    }

                }

            } catch (Exception ex) {

                FileLog.e("invalid proxy list json format", ex);

            }

        }

        File proxyListFile = new File(ApplicationLoader.applicationContext.getFilesDir().getParentFile(), "nekox/proxy_list.json");

        if (proxyListFile.isFile()) {

            try {

                JSONArray proxyArray = new JSONArray(FileUtil.readUtf8String(proxyListFile));

                for (int a = 0; a < proxyArray.length(); a++) {

                    JSONObject proxyObj = proxyArray.getJSONObject(a);

                    ProxyInfo info;

                    try {

                        info = ProxyInfo.fromJson(proxyObj);

                    } catch (Exception ex) {

                        FileLog.e("load proxy failed", ex);

                        continue;

                    }

                    if (!proxyObj.isNull("internal")) continue;
                    if (info.getTitle().toLowerCase().contains("nekox.me")) continue;

                    proxyList.add(info);

                    if (info.hashCode() == current) {

                        currentProxy = info;

                        if (info instanceof ExternalSocks5Proxy) {

                            ((ExternalSocks5Proxy) info).start();

                        }

                    }

                }

            } catch (Exception ex) {

                FileLog.e("invalid proxy list json format", ex);

            }

        }

    }

    public static ProxyInfo parseProxyInfo(String url) throws InvalidProxyException {

        if (url.startsWith(V2RayConfig.VMESS_PROTOCOL) || url.startsWith(V2RayConfig.VMESS1_PROTOCOL)) {

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

        }

        if (url.startsWith("tg:proxy") ||
                url.startsWith("tg://proxy") ||
                url.startsWith("tg:socks") ||
                url.startsWith("tg://socks") ||
                url.startsWith("https://t.me/proxy") ||
                url.startsWith("https://t.me/socks")) {
            url = url
                    .replace("tg:proxy", "tg://telegram.org")
                    .replace("tg://proxy", "tg://telegram.org")
                    .replace("tg://socks", "tg://telegram.org")
                    .replace("tg:socks", "tg://telegram.org");
            Uri data = Uri.parse(url);
            return new ProxyInfo(data.getQueryParameter("server"),
                    Utilities.parseInt(data.getQueryParameter("port")),
                    data.getQueryParameter("user"),
                    data.getQueryParameter("pass"),
                    data.getQueryParameter("secret"));
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

            synchronized (sync) {
                for (ProxyInfo info : new LinkedList<>(proxyList)) {
                    try {
                        JSONObject obj = info.toJson();
                        if (info.isPublic) {
                            continue;
                        }
                        proxyArray.put(obj);
                    } catch (JSONException e) {
                        FileLog.e(e);
                    }
                }
            }

            File proxyListFile = new File(ApplicationLoader.applicationContext.getFilesDir().getParentFile(), "nekox/proxy_list.json");

            try {
                FileUtil.writeUtf8String(proxyArray.toString(4), proxyListFile);
            } catch (JSONException e) {
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
        }
        proxyList.add(proxyInfo);
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
        saveProxyList();
    }

    public static void deleteAllProxy() {

        setProxyEnable(false);

        proxyListLoaded = false;

        proxyList.clear();

        saveProxyList();

        loadProxyList();

    }

    public static void checkSaveToGalleryFiles() {
        try {
            File telegramPath = ApplicationLoader.applicationContext.getExternalFilesDir( "Telegram").getParentFile();
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

    public static int getDevicePerfomanceClass() {
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
            if (BuildVars.DEBUG_VERSION) {
                FileLog.d("device performance info (cpu_count = " + cpuCount + ", freq = " + maxCpuFreq + ", memoryClass = " + memoryClass + ", android version " + androidVersion + ")");
            }
        }

        return devicePerformanceClass;
    }
}
