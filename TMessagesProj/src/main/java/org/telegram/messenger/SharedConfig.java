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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.core.content.pm.ShortcutManagerCompat;

import com.v2ray.ang.V2RayConfig;
import com.v2ray.ang.dto.AngConfig;
import com.v2ray.ang.util.Utils;

import org.apache.commons.lang3.StringUtils;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.json.JSONArray;
import org.json.JSONException;
import androidx.annotation.IntDef;

import org.json.JSONObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.Components.SwipeGestureSettingsView;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LaunchActivity;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import okhttp3.HttpUrl;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.proxy.ProxyManager;
import tw.nekomimi.nekogram.proxy.ShadowsocksLoader;
import tw.nekomimi.nekogram.proxy.ShadowsocksRLoader;
import tw.nekomimi.nekogram.proxy.VmessLoader;
import tw.nekomimi.nekogram.proxy.tcp2ws.WsLoader;
import tw.nekomimi.nekogram.proxy.SubInfo;
import tw.nekomimi.nekogram.proxy.SubManager;
import tw.nekomimi.nekogram.utils.AlertUtil;
import tw.nekomimi.nekogram.utils.EnvUtil;
import tw.nekomimi.nekogram.utils.FileUtil;
import tw.nekomimi.nekogram.utils.UIUtil;

import static com.v2ray.ang.V2RayConfig.SSR_PROTOCOL;
import static com.v2ray.ang.V2RayConfig.SS_PROTOCOL;
import static com.v2ray.ang.V2RayConfig.WSS_PROTOCOL;
import static com.v2ray.ang.V2RayConfig.WS_PROTOCOL;
import java.util.List;
import java.util.Locale;

public class SharedConfig {
    /**
     * V2: Ping and check time serialized
     */
    private final static int PROXY_SCHEMA_V2 = 2;
    private final static int PROXY_CURRENT_SCHEMA_VERSION = PROXY_SCHEMA_V2;

    public final static int PASSCODE_TYPE_PIN = 0,
            PASSCODE_TYPE_PASSWORD = 1;
    private static int legacyDevicePerformanceClass = -1;

    public static boolean loopStickers() {
        return LiteMode.isEnabled(LiteMode.FLAG_ANIMATED_STICKERS_CHAT);
    }

    public static boolean readOnlyStorageDirAlertShowed;

    public static void checkSdCard(File file) {
        if (file == null || SharedConfig.storageCacheDir == null || readOnlyStorageDirAlertShowed) {
            return;
        }
        if (file.getPath().startsWith(SharedConfig.storageCacheDir)) {
            AndroidUtilities.runOnUIThread(() -> {
                if (readOnlyStorageDirAlertShowed) {
                    return;
                }
                BaseFragment fragment = LaunchActivity.getLastFragment();
                if (fragment != null && fragment.getParentActivity() != null) {
                    SharedConfig.storageCacheDir = null;
                    SharedConfig.saveConfig();
                    ImageLoader.getInstance().checkMediaPaths(() -> {

                    });

                    readOnlyStorageDirAlertShowed = true;
                    AlertDialog.Builder dialog = new AlertDialog.Builder(fragment.getParentActivity());
                    dialog.setTitle(LocaleController.getString("SdCardError", R.string.SdCardError));
                    dialog.setSubtitle(LocaleController.getString("SdCardErrorDescription", R.string.SdCardErrorDescription));
                    dialog.setPositiveButton(LocaleController.getString("DoNotUseSDCard", R.string.DoNotUseSDCard), (dialog1, which) -> {

                    });
                    Dialog dialogFinal = dialog.create();
                    dialogFinal.setCanceledOnTouchOutside(false);
                    dialogFinal.show();
                }
            });
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            PASSCODE_TYPE_PIN,
            PASSCODE_TYPE_PASSWORD
    })
    public @interface PasscodeType {}

    public final static int SAVE_TO_GALLERY_FLAG_PEER = 1;
    public final static int SAVE_TO_GALLERY_FLAG_GROUP = 2;
    public final static int SAVE_TO_GALLERY_FLAG_CHANNELS = 4;

    @PushListenerController.PushType
    public static int pushType = PushListenerController.PUSH_TYPE_FIREBASE;
    public static String pushString = "";
    public static String pushStringStatus = "";
    public static long pushStringGetTimeStart;
    public static long pushStringGetTimeEnd;
    public static boolean pushStatSent;
    public static byte[] pushAuthKey;
    public static byte[] pushAuthKeyId;

    public static String directShareHash;

    @PasscodeType
    public static int passcodeType;
    public static String passcodeHash = "";
    public static long passcodeRetryInMs;
    public static long lastUptimeMillis;
    public static int badPasscodeTries;
    public static byte[] passcodeSalt = new byte[0];
    public static boolean appLocked;
    public static int autoLockIn = 60 * 60;

    public static boolean saveIncomingPhotos;
    public static boolean allowScreenCapture;
    public static int lastPauseTime;
    public static boolean isWaitingForPasscodeEnter;
    public static boolean useFingerprint = true;
    public static String lastUpdateVersion;
    public static int suggestStickers;
    public static boolean suggestAnimatedEmoji;
    public static int keepMedia = CacheByChatsController.KEEP_MEDIA_ONE_MONTH; //deprecated
    public static int lastKeepMediaCheckTime;
    public static int lastLogsCheckTime;
    public static int searchMessagesAsListHintShows;
    public static int textSelectionHintShows;
    public static int scheduledOrNoSoundHintShows;
    public static int lockRecordAudioVideoHint;
    public static boolean forwardingOptionsHintShown;
    public static boolean searchMessagesAsListUsed;
    public static boolean stickersReorderingHintUsed;
    public static boolean disableVoiceAudioEffects;
    public static boolean forceDisableTabletMode;
    public static boolean updateStickersOrderOnSend = true;
    public static boolean bigCameraForRound;
    private static int lastLocalId = -210000;

    public static String storageCacheDir;

    private static String passportConfigJson = "";
    private static HashMap<String, String> passportConfigMap;
    public static int passportConfigHash;

    private static boolean configLoaded;
    private static final Object sync = new Object();
    private static final Object localIdSync = new Object();

//    public static int saveToGalleryFlags;
    public static int mapPreviewType = 2;
    public static boolean chatBubbles = Build.VERSION.SDK_INT >= 30;
    public static boolean raiseToSpeak = false;
    public static boolean raiseToListen = true;
    public static boolean recordViaSco = false;
    public static boolean customTabs = true;
    public static boolean directShare = true;
    public static boolean inappCamera = true;
    public static boolean roundCamera16to9 = true;
    public static boolean noSoundHintShowed = false;
    public static boolean streamMedia = true;
    public static boolean streamAllVideo = false;
    public static boolean streamMkv = false;
    public static boolean saveStreamMedia = true;
    public static boolean pauseMusicOnRecord = false;
    public static boolean pauseMusicOnMedia = true;
    public static boolean noiseSupression;
    public static final boolean noStatusBar = true;
    public static boolean debugWebView;
    public static boolean sortContactsByName;
    public static boolean sortFilesByName;
    public static boolean shuffleMusic;
    public static boolean playOrderReversed;
    public static boolean hasCameraCache;
    public static boolean showNotificationsForAllAccounts = true;
    public static int repeatMode;
    public static boolean allowBigEmoji;
    public static int fontSize = 12;
    public static boolean fontSizeIsDefault;
    public static int bubbleRadius = 3;
    public static int ivFontSize = 12;
    public static boolean proxyRotationEnabled;
    public static int proxyRotationTimeout;
    public static int messageSeenHintCount;
    public static int emojiInteractionsHintCount;
    public static int dayNightThemeSwitchHintCount;

    public static TLRPC.TL_help_appUpdate pendingAppUpdate;
    public static int pendingAppUpdateBuildVersion;
    public static long lastUpdateCheckTime;

    public static boolean hasEmailLogin;

    @PerformanceClass
    private static int devicePerformanceClass;
    @PerformanceClass
    private static int overrideDevicePerformanceClass;

    public static boolean drawDialogIcons;
    public static boolean useThreeLinesLayout;
    public static boolean archiveHidden;

    private static int chatSwipeAction;

    public static int distanceSystemType;
    public static int mediaColumnsCount = 3;
    public static int fastScrollHintCount = 3;
    public static boolean dontAskManageStorage;

    public static boolean translateChats = true;

    public static CopyOnWriteArraySet<Integer> activeAccounts;
    public static int loginingAccount = -1;

    public static boolean isFloatingDebugActive;
    public static LiteMode liteMode;

    private static final int[] LOW_SOC = {
            -1775228513, // EXYNOS 850
            802464304,  // EXYNOS 7872
            802464333,  // EXYNOS 7880
            802464302,  // EXYNOS 7870
            2067362118, // MSM8953
            2067362060, // MSM8937
            2067362084, // MSM8940
            2067362241, // MSM8992
            2067362117, // MSM8952
            2067361998, // MSM8917
            -1853602818 // SDM439
    };

    private static final int[] LOW_DEVICES = {
            1903542002, // XIAOMI NIKEL (Redmi Note 4)
            1904553494, // XIAOMI OLIVE (Redmi 8)
            1616144535, // OPPO CPH2273 (Oppo A54s)
            -713271737, // OPPO OP4F2F (Oppo A54)
            -1394191140, // SAMSUNG A12 (Galaxy A12)
            -270252297, // SAMSUNG A12S (Galaxy A12)
            -270251367, // SAMSUNG A21S (Galaxy A21s)
            -270252359  // SAMSUNG A10S (Galaxy A10s)
    };

    private static final int[] AVERAGE_DEVICES = {
            812981419, // XIAOMI ANGELICA (Redmi 9C)
            -993913431 // XIAOMI DANDELION (Redmi 9A)
    };

    private static final int[] HIGH_DEVICES = {
            1908570923, // XIAOMI SWEET (Redmi Note 10 Pro)
            -980514379, // XIAOMI SECRET (Redmi Note 10S)
            577463889, // XIAOMI JOYEUSE (Redmi Note 9 Pro)
            1764745014, // XIAOMI BEGONIA (Redmi Note 8 Pro)
            1908524435, // XIAOMI SURYA (Poco X3 NFC)
            -215787089, // XIAOMI KAMA (Poco X3)
            -215458996, // XIAOMI VAYU (Poco X3 Pro)
            -1394179578, // SAMSUNG M21
            220599115, // SAMSUNG J6LTE
            1737652784 // SAMSUNG J6PRIMELTE
    };

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

        public ProxyInfo(String address, int port, String username, String password, String secret) {
            this.address = address;
            this.port = port;
            this.username = username;
            this.password = password;
            this.secret = secret;
            if (this.address == null) {
                this.address = "";
            }
            if (this.password == null) {
                this.password = "";
            }
            if (this.username == null) {
                this.username = "";
            }
            if (this.secret == null) {
                this.secret = "";
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

                case "ws": {

                    info = new WsProxy(obj.optString("link"));

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

    public static class WsProxy extends ExternalSocks5Proxy {

        public WsLoader.Bean bean;
        public WsLoader loader;

        public WsProxy(String url) {
            this(WsLoader.Companion.parse(url));
        }

        public WsProxy(WsLoader.Bean bean) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                throw new RuntimeException(LocaleController.getString("MinApi21Required", R.string.MinApi21Required));
            }

            this.bean = bean;
        }

        @Override
        public boolean isStarted() {
            return loader != null;
        }

        @Override
        public void start() {
            if (loader != null) return;
            synchronized (this) {
                loader = new WsLoader();
                port = ProxyManager.mkPort();
                loader.init(bean, port);
                loader.start();
                if (SharedConfig.proxyEnabled && SharedConfig.currentProxy == this) {
                    ConnectionsManager.setProxySettings(true, address, port, username, password, secret);
                }
            }
        }

        @Override
        public void stop() {
            if (loader == null) return;
            ConnectionsManager.setProxySettings(false, address, port, username, password, secret);
            UIUtil.runOnIoDispatcher(() -> {
                synchronized (this) {
                    if (loader == null)
                        return;
                    loader.stop();
                    loader = null;
                }
            });
        }

        @Override
        public String getAddress() {
            return bean.getServer();
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
            return "WS";
        }

        @Override
        public int hashCode() {
            return bean.hashCode();
        }

        @Override
        public JSONObject toJsonInternal() throws JSONException {
            JSONObject obj = new JSONObject();
            obj.put("type", "ws");
            obj.put("link", toUrl());
            return obj;
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

    public static Proxy getActiveSocks5Proxy() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return null;
        // https://stackoverflow.com/questions/36205896/how-to-use-httpurlconnection-over-socks-proxy-on-android
        // Android did not support socks proxy natively(using HURL) on devices previous than Marshmallow
        // Hutool use HttpURLConnection too
        if (!(currentProxy instanceof ExternalSocks5Proxy) || currentProxy instanceof WsProxy)
            return null;
        final ExternalSocks5Proxy proxy = (ExternalSocks5Proxy) currentProxy;
        if (!proxy.isStarted())
            return null;
        FileLog.w("Return socks5 proxy: " + currentProxy.toString() + " port:" + currentProxy.port);
        return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(currentProxy.address, currentProxy.port));
    }

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
                editor.putInt("pushType", pushType);
                editor.putBoolean("pushStatSent", pushStatSent);
                editor.putString("pushAuthKey", pushAuthKey != null ? Base64.encodeToString(pushAuthKey, Base64.DEFAULT) : "");
                editor.putInt("lastLocalId", lastLocalId);
                editor.putString("passportConfigJson", passportConfigJson);
                editor.putInt("passportConfigHash", passportConfigHash);
                editor.putBoolean("sortContactsByName", sortContactsByName);
                editor.putBoolean("sortFilesByName", sortFilesByName);
                editor.putInt("textSelectionHintShows", textSelectionHintShows);
                editor.putInt("scheduledOrNoSoundHintShows", scheduledOrNoSoundHintShows);
                editor.putBoolean("forwardingOptionsHintShown", forwardingOptionsHintShown);
                editor.putInt("lockRecordAudioVideoHint", lockRecordAudioVideoHint);
                editor.putString("storageCacheDir", !TextUtils.isEmpty(storageCacheDir) ? storageCacheDir : "");
                editor.putBoolean("proxyRotationEnabled", proxyRotationEnabled);
                editor.putInt("proxyRotationTimeout", proxyRotationTimeout);

                if (pendingAppUpdate != null) {
                    try {
                        SerializedData data = new SerializedData(pendingAppUpdate.getObjectSize());
                        pendingAppUpdate.serializeToStream(data);
                        String str = Base64.encodeToString(data.toByteArray(), Base64.DEFAULT);
                        editor.putString("appUpdate", str);
                        editor.putInt("appUpdateBuild", pendingAppUpdateBuildVersion);
                        data.cleanup();
                    } catch (Exception ignore) {

                    }
                } else {
                    editor.remove("appUpdate");
                }
                editor.putLong("appUpdateCheckTime", lastUpdateCheckTime);

                editor.apply();

                editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Context.MODE_PRIVATE).edit();
                editor.putBoolean("hasEmailLogin", hasEmailLogin);
                editor.putBoolean("floatingDebugActive", isFloatingDebugActive);
                editor.putBoolean("record_via_sco", recordViaSco);
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
        FileLog.e("Save accounts: " + activeAccounts, new Exception());
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE).edit()
                .putString("active_accounts", StringUtils.join(activeAccounts, ","))
                .apply();
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded || ApplicationLoader.applicationContext == null) {
                return;
            }

            BackgroundActivityPrefs.prefs = ApplicationLoader.applicationContext.getSharedPreferences("background_activity", Context.MODE_PRIVATE);

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
            pushType = preferences.getInt("pushType", PushListenerController.PUSH_TYPE_FIREBASE);
            pushStatSent = preferences.getBoolean("pushStatSent", false);
            passportConfigJson = preferences.getString("passportConfigJson", "");
            passportConfigHash = preferences.getInt("passportConfigHash", 0);
            storageCacheDir = preferences.getString("storageCacheDir", null);
            proxyRotationEnabled = preferences.getBoolean("proxyRotationEnabled", false);
            proxyRotationTimeout = preferences.getInt("proxyRotationTimeout", ProxyRotationController.DEFAULT_TIMEOUT_INDEX);
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
            lastUpdateCheckTime = preferences.getLong("appUpdateCheckTime", System.currentTimeMillis());
            try {
                String update = preferences.getString("appUpdate", null);
                if (update != null) {
                    pendingAppUpdateBuildVersion = preferences.getInt("appUpdateBuild", BuildVars.BUILD_VERSION);
                    byte[] arr = Base64.decode(update, Base64.DEFAULT);
                    if (arr != null) {
                        SerializedData data = new SerializedData(arr);
                        pendingAppUpdate = (TLRPC.TL_help_appUpdate) TLRPC.help_AppUpdate.TLdeserialize(data, data.readInt32(false), false);
                        data.cleanup();
                    }
                }
                if (pendingAppUpdate != null) {
                    long updateTime = 0;
                    int updateVersion = 0;
                    String updateVersionString = null;
                    try {
                        PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        updateVersion = packageInfo.versionCode;
                        updateVersionString = packageInfo.versionName;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    if (updateVersion == 0) {
                        updateVersion = BuildVars.BUILD_VERSION;
                    }
                    if (updateVersionString == null) {
                        updateVersionString = BuildVars.BUILD_VERSION_STRING;
                    }
                    if (pendingAppUpdateBuildVersion != updateVersion || pendingAppUpdate.version == null || updateVersionString.compareTo(pendingAppUpdate.version) >= 0 || BuildVars.DEBUG_PRIVATE_VERSION) {
                        pendingAppUpdate = null;
                        AndroidUtilities.runOnUIThread(SharedConfig::saveConfig);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }

            preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            SaveToGallerySettingsHelper.load(preferences);
            mapPreviewType = preferences.getInt("mapPreviewType", 2);
            raiseToListen = preferences.getBoolean("raise_to_listen", true);
            raiseToSpeak = preferences.getBoolean("raise_to_speak", false);
            recordViaSco = preferences.getBoolean("record_via_sco", false);
            customTabs = preferences.getBoolean("custom_tabs", true);
            directShare = preferences.getBoolean("direct_share", true);
            shuffleMusic = preferences.getBoolean("shuffleMusic", false);
            playOrderReversed = !shuffleMusic && preferences.getBoolean("playOrderReversed", false);
            inappCamera = preferences.getBoolean("inappCamera", true);
            hasCameraCache = preferences.contains("cameraCache");
            roundCamera16to9 = true;
            repeatMode = preferences.getInt("repeatMode", 0);
            fontSize = preferences.getInt("fons_size", AndroidUtilities.isTablet() ? 14 : 12);
            fontSizeIsDefault = !preferences.contains("fons_size");
            bubbleRadius = preferences.getInt("bubbleRadius", 3);
            ivFontSize = preferences.getInt("iv_font_size", fontSize);
            allowBigEmoji = preferences.getBoolean("allowBigEmoji", true);
            streamMedia = preferences.getBoolean("streamMedia", true);
            saveStreamMedia = preferences.getBoolean("saveStreamMedia", true);
            pauseMusicOnRecord = preferences.getBoolean("pauseMusicOnRecord", false);
            pauseMusicOnMedia = preferences.getBoolean("pauseMusicOnMedia", true);
            forceDisableTabletMode = preferences.getBoolean("forceDisableTabletMode", false);
            streamAllVideo = preferences.getBoolean("streamAllVideo", BuildVars.DEBUG_VERSION);
            streamMkv = preferences.getBoolean("streamMkv", false);
            suggestStickers = preferences.getInt("suggestStickers", 0);
            suggestAnimatedEmoji = preferences.getBoolean("suggestAnimatedEmoji", true);
            overrideDevicePerformanceClass = preferences.getInt("overrideDevicePerformanceClass", -1);
            devicePerformanceClass = preferences.getInt("devicePerformanceClass", -1);
            sortContactsByName = preferences.getBoolean("sortContactsByName", false);
            sortFilesByName = preferences.getBoolean("sortFilesByName", false);
            noSoundHintShowed = preferences.getBoolean("noSoundHintShowed", false);
            directShareHash = preferences.getString("directShareHash2", null);
            useThreeLinesLayout = preferences.getBoolean("useThreeLinesLayout", false);
            archiveHidden = preferences.getBoolean("archiveHidden", false);
            distanceSystemType = preferences.getInt("distanceSystemType", 0);
            keepMedia = preferences.getInt("keep_media", CacheByChatsController.KEEP_MEDIA_ONE_MONTH);
            debugWebView = preferences.getBoolean("debugWebView", false);
            lastKeepMediaCheckTime = preferences.getInt("lastKeepMediaCheckTime", 0);
            lastLogsCheckTime = preferences.getInt("lastLogsCheckTime", 0);
            searchMessagesAsListHintShows = preferences.getInt("searchMessagesAsListHintShows", 0);
            searchMessagesAsListUsed = preferences.getBoolean("searchMessagesAsListUsed", false);
            stickersReorderingHintUsed = preferences.getBoolean("stickersReorderingHintUsed", false);
            textSelectionHintShows = preferences.getInt("textSelectionHintShows", 0);
            scheduledOrNoSoundHintShows = preferences.getInt("scheduledOrNoSoundHintShows", 0);
            forwardingOptionsHintShown = preferences.getBoolean("forwardingOptionsHintShown", false);
            lockRecordAudioVideoHint = preferences.getInt("lockRecordAudioVideoHint", 0);
            disableVoiceAudioEffects = preferences.getBoolean("disableVoiceAudioEffects", false);
            noiseSupression = preferences.getBoolean("noiseSupression", false);
            chatSwipeAction = preferences.getInt("ChatSwipeAction", -1);
            messageSeenHintCount = preferences.getInt("messageSeenCount", 3);
            emojiInteractionsHintCount = preferences.getInt("emojiInteractionsHintCount", 3);
            dayNightThemeSwitchHintCount = preferences.getInt("dayNightThemeSwitchHintCount", 3);
            activeAccounts = Arrays.stream(preferences.getString("active_accounts", "").split(",")).filter(StrUtil::isNotBlank).map(Integer::parseInt).collect(Collectors.toCollection(CopyOnWriteArraySet::new));

            if (!preferences.contains("activeAccountsLoaded")) {
                int maxAccounts;

                File filesDir = ApplicationLoader.applicationContext.getFilesDir();
                if (new File(filesDir, "account31").isDirectory()) {
                    maxAccounts = 32;
                } else if (new File(filesDir, "account15").isDirectory()) {
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

                preferences.edit().putBoolean("activeAccountsLoaded", true).apply();
            }
            mediaColumnsCount = preferences.getInt("mediaColumnsCount", 3);
            fastScrollHintCount = preferences.getInt("fastScrollHintCount", 3);
            dontAskManageStorage = preferences.getBoolean("dontAskManageStorage", false);
            hasEmailLogin = preferences.getBoolean("hasEmailLogin", false);
            isFloatingDebugActive = preferences.getBoolean("floatingDebugActive", false);
            updateStickersOrderOnSend = preferences.getBoolean("updateStickersOrderOnSend", true);
            bigCameraForRound = preferences.getBoolean("bigCameraForRound", false);

            preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE);
            showNotificationsForAllAccounts = preferences.getBoolean("AllAccounts", true);

            configLoaded = true;

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && debugWebView) {
                    WebView.setWebContentsDebuggingEnabled(true);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

    }

    public static void updateTabletConfig() {
        if (fontSizeIsDefault) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            fontSize = preferences.getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);
            ivFontSize = preferences.getInt("iv_font_size", fontSize);
        }
    }

    public static void increaseBadPasscodeTries() {
        badPasscodeTries++;
        if (badPasscodeTries >= 3) {
            switch (badPasscodeTries) {
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
            lastUptimeMillis = SystemClock.elapsedRealtime();
        }
        saveConfig();
    }

    public static boolean isAutoplayVideo() {
        return LiteMode.isEnabled(LiteMode.FLAG_AUTOPLAY_VIDEOS);
    }

    public static boolean isAutoplayGifs() {
        return LiteMode.isEnabled(LiteMode.FLAG_AUTOPLAY_GIFS);
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

    public static boolean isAppUpdateAvailable() {
        if (pendingAppUpdate == null || pendingAppUpdate.document == null) {
            return false;
        }
        return pendingAppUpdateBuildVersion == BuildVars.BUILD_VERSION;
    }

    public static boolean setNewAppVersionAvailable(TLRPC.TL_help_appUpdate update) {
        if (update == null) {
            pendingAppUpdate = null;
            pendingAppUpdateBuildVersion = 0;
            saveConfig();
            return false;
        }
        pendingAppUpdate = update;
        pendingAppUpdateBuildVersion = BuildConfig.VERSION_CODE;
        saveConfig();
        return true;
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
        passcodeType = PASSCODE_TYPE_PIN;
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
        forwardingOptionsHintShown = false;
        messageSeenHintCount = 3;
        emojiInteractionsHintCount = 3;
        dayNightThemeSwitchHintCount = 3;
        saveConfig();
    }

    public static void setSuggestStickers(int type) {
        suggestStickers = type;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("suggestStickers", suggestStickers);
        editor.commit();
    }

    public static void setSearchMessagesAsListUsed(boolean value) {
        searchMessagesAsListUsed = value;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("searchMessagesAsListUsed", searchMessagesAsListUsed);
        editor.commit();
    }

    public static void setStickersReorderingHintUsed(boolean value) {
        stickersReorderingHintUsed = value;
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

    public static void forwardingOptionsHintHintShowed() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        forwardingOptionsHintShown = true;
        editor.putBoolean("forwardingOptionsHintShown", forwardingOptionsHintShown);
        editor.commit();
    }

    public static void removeScheduledOrNoSoundHint() {
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

    public static void toggleUpdateStickersOrderOnSend() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("updateStickersOrderOnSend", updateStickersOrderOnSend = !updateStickersOrderOnSend);
        editor.commit();
    }

    public static void checkLogsToDelete() {
        if (!BuildVars.LOGS_ENABLED) {
            return;
        }
        int time = (int) (System.currentTimeMillis() / 1000);
        if (Math.abs(time - lastLogsCheckTime) < 60 * 60) {
            return;
        }
        lastLogsCheckTime = time;
        Utilities.cacheClearQueue.postRunnable(() -> {
            long currentTime = time - 60 * 60 * 24 * 10;
            try {
                File dir = AndroidUtilities.getLogsDir();
                if (dir == null) {
                    return;
                }
                Utilities.clearDir(dir.getAbsolutePath(), 0, currentTime, false);
            } catch (Throwable e) {
                FileLog.e(e);
            }
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("lastLogsCheckTime", lastLogsCheckTime);
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

    public static void toggleNoiseSupression() {
        noiseSupression = !noiseSupression;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("noiseSupression", noiseSupression);
        editor.commit();
    }

    public static void toggleDebugWebView() {
        debugWebView = !debugWebView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(debugWebView);
        }
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("debugWebView", debugWebView);
        editor.apply();
    }

    public static void toggleLoopStickers() {
        LiteMode.toggleFlag(LiteMode.FLAG_ANIMATED_STICKERS_CHAT);
    }

    public static void toggleBigEmoji() {
        allowBigEmoji = !allowBigEmoji;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("allowBigEmoji", allowBigEmoji);
        editor.commit();
    }

    public static void toggleSuggestAnimatedEmoji() {
        suggestAnimatedEmoji = !suggestAnimatedEmoji;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("suggestAnimatedEmoji", suggestAnimatedEmoji);
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

    public static void overrideDevicePerformanceClass(int performanceClass) {
        MessagesController.getGlobalMainSettings().edit().putInt("overrideDevicePerformanceClass", overrideDevicePerformanceClass = performanceClass).remove("lite_mode").commit();
        if (liteMode != null) {
            liteMode.loadPreference();
        }
    }

    public static void toggleAutoplayGifs() {
        LiteMode.toggleFlag(LiteMode.FLAG_AUTOPLAY_GIFS);
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
        LiteMode.toggleFlag(LiteMode.FLAG_AUTOPLAY_VIDEOS);
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

    public static void toggleRaiseToSpeak() {
        raiseToSpeak = !raiseToSpeak;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("raise_to_speak", raiseToSpeak);
        editor.commit();
    }

    public static void toggleRaiseToListen() {
        raiseToListen = !raiseToListen;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("raise_to_listen", raiseToListen);
        editor.commit();
    }

    public static boolean enabledRaiseTo(boolean speak) {
        return raiseToListen && (!speak || raiseToSpeak);
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

    public static void setSmoothKeyboard(boolean smoothKeyboard) {
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

    public static void togglePauseMusicOnMedia() {
        pauseMusicOnMedia = !pauseMusicOnMedia;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("pauseMusicOnMedia", pauseMusicOnMedia);
        editor.commit();
    }

    public static void toggleChatBlur() {
        LiteMode.toggleFlag(LiteMode.FLAG_CHAT_BLUR);
    }

    public static void toggleForceDisableTabletMode() {
        forceDisableTabletMode = !forceDisableTabletMode;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("forceDisableTabletMode", forceDisableTabletMode);
        editor.commit();
    }

    public static void toggleInappCamera() {
        inappCamera = !inappCamera;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("inappCamera", inappCamera);
        editor.commit();
    }

    public static void setInappCamera(boolean inappCamera) {
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

        if (currentProxy instanceof ExternalSocks5Proxy && !currentProxy.equals(info)) {
            ((ExternalSocks5Proxy) currentProxy).stop();
        }

        currentProxy = info;

        MessagesController.getGlobalMainSettings().edit()
                .putInt("current_proxy", info == null ? 0 : info.hashCode())
                .apply();

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
        } else if (url.startsWith(WS_PROTOCOL) || url.startsWith(WSS_PROTOCOL)) {
            try {
                return new WsProxy(url);
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

    public static boolean isProxyEnabled() {
        return MessagesController.getGlobalMainSettings().getBoolean("proxy_enabled", false) && currentProxy != null;
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
        Utilities.globalQueue.postRunnable(() -> {
            try {
                File telegramPath = EnvUtil.getTelegramPath();
                File imagePath = new File(telegramPath, "images");
                imagePath.mkdirs();
                File videoPath = new File(telegramPath, "videos");
                videoPath.mkdirs();

                if (!BuildVars.NO_SCOPED_STORAGE) {
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
        });
    }

    public static int getChatSwipeAction(int currentAccount) {
        if (chatSwipeAction >= 0) {
            if (chatSwipeAction == SwipeGestureSettingsView.SWIPE_GESTURE_FOLDERS && MessagesController.getInstance(currentAccount).dialogFilters.isEmpty()) {
                return SwipeGestureSettingsView.SWIPE_GESTURE_ARCHIVE;
            }
            return chatSwipeAction;
        } else if (!MessagesController.getInstance(currentAccount).dialogFilters.isEmpty()) {
            return SwipeGestureSettingsView.SWIPE_GESTURE_FOLDERS;

        }
        return SwipeGestureSettingsView.SWIPE_GESTURE_ARCHIVE;
    }

    public static void updateChatListSwipeSetting(int newAction) {
        chatSwipeAction = newAction;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        preferences.edit().putInt("ChatSwipeAction", chatSwipeAction).apply();
    }

    public static void updateMessageSeenHintCount(int count) {
        messageSeenHintCount = count;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        preferences.edit().putInt("messageSeenCount", messageSeenHintCount).apply();
    }

    public static void updateEmojiInteractionsHintCount(int count) {
        emojiInteractionsHintCount = count;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        preferences.edit().putInt("emojiInteractionsHintCount", emojiInteractionsHintCount).apply();
    }


    public static void updateDayNightThemeSwitchHintCount(int count) {
        dayNightThemeSwitchHintCount = count;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        preferences.edit().putInt("dayNightThemeSwitchHintCount", dayNightThemeSwitchHintCount).apply();
    }

    public final static int PERFORMANCE_CLASS_LOW = 0;
    public final static int PERFORMANCE_CLASS_AVERAGE = 1;
    public final static int PERFORMANCE_CLASS_HIGH = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            PERFORMANCE_CLASS_LOW,
            PERFORMANCE_CLASS_AVERAGE,
            PERFORMANCE_CLASS_HIGH
    })
    public @interface PerformanceClass {}

    @PerformanceClass
    public static int getDevicePerformanceClass() {
        if (overrideDevicePerformanceClass != -1) {
            return overrideDevicePerformanceClass;
        }
        if (devicePerformanceClass == -1) {
            devicePerformanceClass = measureDevicePerformanceClass();
        }
        return devicePerformanceClass;
    }

    public static int measureDevicePerformanceClass() {
        int androidVersion = Build.VERSION.SDK_INT;
        int cpuCount = ConnectionsManager.CPU_COUNT;
        int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

        if (Build.DEVICE != null && Build.MANUFACTURER != null) {
            int hash = (Build.MANUFACTURER + " " + Build.DEVICE).toUpperCase().hashCode();
            for (int i = 0; i < LOW_DEVICES.length; ++i) {
                if (LOW_DEVICES[i] == hash) {
                    return PERFORMANCE_CLASS_LOW;
                }
            }
            for (int i = 0; i < AVERAGE_DEVICES.length; ++i) {
                if (AVERAGE_DEVICES[i] == hash) {
                    return PERFORMANCE_CLASS_AVERAGE;
                }
            }
            for (int i = 0; i < HIGH_DEVICES.length; ++i) {
                if (HIGH_DEVICES[i] == hash) {
                    return PERFORMANCE_CLASS_HIGH;
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && Build.SOC_MODEL != null) {
            int hash = Build.SOC_MODEL.toUpperCase().hashCode();
            for (int i = 0; i < LOW_SOC.length; ++i) {
                if (LOW_SOC[i] == hash) {
                    return PERFORMANCE_CLASS_LOW;
                }
            }
        }

        int totalCpuFreq = 0;
        int freqResolved = 0;
        for (int i = 0; i < cpuCount; i++) {
            try {
                RandomAccessFile reader = new RandomAccessFile(String.format(Locale.ENGLISH, "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq", i), "r");
                String line = reader.readLine();
                if (line != null) {
                    totalCpuFreq += Utilities.parseInt(line) / 1000;
                    freqResolved++;
                }
                reader.close();
            } catch (Throwable ignore) {}
        }
        int maxCpuFreq = freqResolved == 0 ? -1 : (int) Math.ceil(totalCpuFreq / (float) freqResolved);

        long ram = -1;
        try {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            ((ActivityManager) ApplicationLoader.applicationContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(memoryInfo);
            ram = memoryInfo.totalMem;
        } catch (Exception ignore) {}

        int performanceClass;
        if (
            androidVersion < 21 ||
            cpuCount <= 2 ||
            memoryClass <= 100 ||
            cpuCount <= 4 && maxCpuFreq != -1 && maxCpuFreq <= 1250 ||
            cpuCount <= 4 && maxCpuFreq <= 1600 && memoryClass <= 128 && androidVersion <= 21 ||
            cpuCount <= 4 && maxCpuFreq <= 1300 && memoryClass <= 128 && androidVersion <= 24 ||
            ram != -1 && ram < 2L * 1024L * 1024L * 1024L
        ) {
            performanceClass = PERFORMANCE_CLASS_LOW;
        } else if (
            cpuCount < 8 ||
            memoryClass <= 160 ||
            maxCpuFreq != -1 && maxCpuFreq <= 2055 ||
            maxCpuFreq == -1 && cpuCount == 8 && androidVersion <= 23
        ) {
            performanceClass = PERFORMANCE_CLASS_AVERAGE;
        } else {
            performanceClass = PERFORMANCE_CLASS_HIGH;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("device performance info selected_class = " + performanceClass + " (cpu_count = " + cpuCount + ", freq = " + maxCpuFreq + ", memoryClass = " + memoryClass + ", android version " + androidVersion + ", manufacture " + Build.MANUFACTURER + ", screenRefreshRate=" + AndroidUtilities.screenRefreshRate + ")");
        }

        return performanceClass;
    }

    public static String performanceClassName(int perfClass) {
        switch (perfClass) {
            case PERFORMANCE_CLASS_HIGH: return "HIGH";
            case PERFORMANCE_CLASS_AVERAGE: return "AVERAGE";
            case PERFORMANCE_CLASS_LOW: return "LOW";
            default: return "UNKNOWN";
        }
    }

    public static void setMediaColumnsCount(int count) {
        if (mediaColumnsCount != count) {
            mediaColumnsCount = count;
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE).edit().putInt("mediaColumnsCount", mediaColumnsCount).apply();
        }
    }

    public static void setFastScrollHintCount(int count) {
        if (fastScrollHintCount != count) {
            fastScrollHintCount = count;
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE).edit().putInt("fastScrollHintCount", fastScrollHintCount).apply();
        }
    }

    public static void setDontAskManageStorage(boolean b) {
        dontAskManageStorage = b;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE).edit().putBoolean("dontAskManageStorage", dontAskManageStorage).apply();
    }

    public static boolean canBlurChat() {
        return getDevicePerformanceClass() == PERFORMANCE_CLASS_HIGH || NekoConfig.forceBlurInChat.Bool();
    }

    public static boolean chatBlurEnabled() {
        return (canBlurChat() && LiteMode.isEnabled(LiteMode.FLAG_CHAT_BLUR)) || NekoConfig.forceBlurInChat.Bool();
    }

    public static class BackgroundActivityPrefs {
        private static SharedPreferences prefs;

        public static long getLastCheckedBackgroundActivity() {
            return prefs.getLong("last_checked", 0);
        }

        public static void setLastCheckedBackgroundActivity(long l) {
            prefs.edit().putLong("last_checked", l).apply();
        }

        public static int getDismissedCount() {
            return prefs.getInt("dismissed_count", 0);
        }

        public static void increaseDismissedCount() {
            prefs.edit().putInt("dismissed_count", getDismissedCount() + 1).apply();
        }
    }

    private static Boolean animationsEnabled;

    public static void setAnimationsEnabled(boolean b) {
        animationsEnabled = b;
    }

    public static boolean animationsEnabled() {
        if (animationsEnabled == null) {
            animationsEnabled = MessagesController.getGlobalMainSettings().getBoolean("view_animations", true);
        }
        return animationsEnabled;
    }

    public static SharedPreferences getPreferences() {
        return ApplicationLoader.applicationContext.getSharedPreferences("userconfing", Context.MODE_PRIVATE);
    }

    public static boolean deviceIsLow() {
        return getDevicePerformanceClass() == PERFORMANCE_CLASS_LOW;
    }

    public static boolean deviceIsAboveAverage() {
        return getDevicePerformanceClass() >= PERFORMANCE_CLASS_AVERAGE;
    }

    public static boolean deviceIsHigh() {
        return getDevicePerformanceClass() >= PERFORMANCE_CLASS_HIGH;
    }

    public static boolean deviceIsAverage() {
        return getDevicePerformanceClass() <= PERFORMANCE_CLASS_AVERAGE;
    }

    public static void toggleRoundCamera() {
        bigCameraForRound = !bigCameraForRound;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE)
                .edit()
                .putBoolean("bigCameraForRound", bigCameraForRound)
                .apply();
    }


    @Deprecated
    public static int getLegacyDevicePerformanceClass() {
        if (legacyDevicePerformanceClass == -1) {
            int androidVersion = Build.VERSION.SDK_INT;
            int cpuCount = ConnectionsManager.CPU_COUNT;
            int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
            int totalCpuFreq = 0;
            int freqResolved = 0;
            for (int i = 0; i < cpuCount; i++) {
                try {
                    RandomAccessFile reader = new RandomAccessFile(String.format(Locale.ENGLISH, "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq", i), "r");
                    String line = reader.readLine();
                    if (line != null) {
                        totalCpuFreq += Utilities.parseInt(line) / 1000;
                        freqResolved++;
                    }
                    reader.close();
                } catch (Throwable ignore) {}
            }
            int maxCpuFreq = freqResolved == 0 ? -1 : (int) Math.ceil(totalCpuFreq / (float) freqResolved);

            if (androidVersion < 21 || cpuCount <= 2 || memoryClass <= 100 || cpuCount <= 4 && maxCpuFreq != -1 && maxCpuFreq <= 1250 || cpuCount <= 4 && maxCpuFreq <= 1600 && memoryClass <= 128 && androidVersion <= 21 || cpuCount <= 4 && maxCpuFreq <= 1300 && memoryClass <= 128 && androidVersion <= 24) {
                legacyDevicePerformanceClass = PERFORMANCE_CLASS_LOW;
            } else if (cpuCount < 8 || memoryClass <= 160 || maxCpuFreq != -1 && maxCpuFreq <= 2050 || maxCpuFreq == -1 && cpuCount == 8 && androidVersion <= 23) {
                legacyDevicePerformanceClass = PERFORMANCE_CLASS_AVERAGE;
            } else {
                legacyDevicePerformanceClass = PERFORMANCE_CLASS_HIGH;
            }
        }
        return legacyDevicePerformanceClass;
    }
}
