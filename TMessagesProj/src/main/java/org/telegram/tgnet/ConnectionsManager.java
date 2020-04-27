package org.telegram.tgnet;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;

import com.v2ray.ang.util.Utils;

import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BaseController;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.EmuDetector;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.KeepAliveJob;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;

import java.io.File;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.utils.DnsFactory;
import tw.nekomimi.nekogram.utils.UIUtil;

//import org.telegram.messenger.BuildConfig;

public class ConnectionsManager extends BaseController {

    public final static int ConnectionTypeGeneric = 1;
    public final static int ConnectionTypeDownload = 2;
    public final static int ConnectionTypeUpload = 4;
    public final static int ConnectionTypePush = 8;
    public final static int ConnectionTypeDownload2 = ConnectionTypeDownload | (1 << 16);

    public final static int FileTypePhoto = 0x01000000;
    public final static int FileTypeVideo = 0x02000000;
    public final static int FileTypeAudio = 0x03000000;
    public final static int FileTypeFile = 0x04000000;

    public final static int RequestFlagEnableUnauthorized = 1;
    public final static int RequestFlagFailOnServerErrors = 2;
    public final static int RequestFlagCanCompress = 4;
    public final static int RequestFlagWithoutLogin = 8;
    public final static int RequestFlagTryDifferentDc = 16;
    public final static int RequestFlagForceDownload = 32;
    public final static int RequestFlagInvokeAfter = 64;
    public final static int RequestFlagNeedQuickAck = 128;

    public final static int ConnectionStateConnecting = 1;
    public final static int ConnectionStateWaitingForNetwork = 2;
    public final static int ConnectionStateConnected = 3;
    public final static int ConnectionStateConnectingToProxy = 4;
    public final static int ConnectionStateUpdating = 5;

    private static long lastDnsRequestTime;

    public final static int DEFAULT_DATACENTER_ID = Integer.MAX_VALUE;

    private long lastPauseTime = System.currentTimeMillis();
    private boolean appPaused = true;
    private boolean isUpdating;
    private int connectionState;
    private AtomicInteger lastRequestToken = new AtomicInteger(1);
    private int appResumeCount;

    private static AsyncTask currentTask;

    private static HashMap<String, ResolveHostByNameTask> resolvingHostnameTasks = new HashMap<>();

    public static final Executor DNS_THREAD_POOL_EXECUTOR;
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>(128);
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "DnsAsyncTask #" + mCount.getAndIncrement());
        }
    };

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        DNS_THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    private static class ResolvedDomain {

        public InetAddress[] addresses;
        long ttl;

        public ResolvedDomain(InetAddress[] a, long t) {
            addresses = a;
            ttl = t;
        }

        public String getAddress() {
            if (addresses.length == 0) return "";
            return addresses[Utilities.random.nextInt(addresses.length)].getHostAddress();
        }
    }

    private static HashMap<String, ResolvedDomain> dnsCache = new HashMap<>();

    private static int lastClassGuid = 1;

    private static volatile ConnectionsManager[] Instance = new ConnectionsManager[UserConfig.MAX_ACCOUNT_COUNT];

    public static ConnectionsManager getInstance(int num) {
        ConnectionsManager localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (ConnectionsManager.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    Instance[num] = localInstance = new ConnectionsManager(num);
                }
            }
        }
        return localInstance;
    }

    public ConnectionsManager(int instance) {
        super(instance);
        connectionState = native_getConnectionState(currentAccount);
        init();
    }

    public void init() {
        String deviceModel;
        String systemLangCode;
        String langCode;
        String appVersion;
        String systemVersion;
        File config = ApplicationLoader.getFilesDirFixed();
        if (currentAccount != 0) {
            config = new File(config, "account" + currentAccount);
            config.mkdirs();
        }
        File tgnetFile = new File(config,"tgnet.dat");
        File tgnetFileNew = new File(config,"tgnet.dat.new");
        if (tgnetFileNew.isFile()) {
            tgnetFile.delete();
            tgnetFileNew.renameTo(tgnetFile);
        }
        String configPath = config.toString();
        boolean enablePushConnection = isPushConnectionEnabled();
        try {
            systemLangCode = LocaleController.getSystemLocaleStringIso639().toLowerCase();
            langCode = LocaleController.getLocaleStringIso639().toLowerCase();
            deviceModel = Build.MANUFACTURER + Build.MODEL;
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            appVersion = pInfo.versionName + " (" + pInfo.versionCode + ")";
            systemVersion = "SDK " + Build.VERSION.SDK_INT;
        } catch (Exception e) {
            systemLangCode = "en";
            langCode = "";
            deviceModel = "Android unknown";
            appVersion = "App version unknown";
            systemVersion = "SDK " + Build.VERSION.SDK_INT;
        }
        if (systemLangCode.trim().length() == 0) {
            systemLangCode = "en";
        }
        if (deviceModel.trim().length() == 0) {
            deviceModel = "Android unknown";
        }
        if (appVersion.trim().length() == 0) {
            appVersion = "App version unknown";
        }
        if (systemVersion.trim().length() == 0) {
            systemVersion = "SDK Unknown";
        }
        getUserConfig().loadConfig();
        String pushString = SharedConfig.pushString;
        if (TextUtils.isEmpty(pushString) && !TextUtils.isEmpty(SharedConfig.pushStringStatus)) {
            pushString = SharedConfig.pushStringStatus;
        }
        String fingerprint = AndroidUtilities.getCertificateSHA256Fingerprint();

        int timezoneOffset = (TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()) / 1000;

        int layer = MessagesController.getMainSettings(currentAccount).getInt("layer",TLRPC.LAYER);

        if (layer != TLRPC.LAYER) {

            FileLog.d("use custom layer " + layer);

        }

        init(BuildVars.BUILD_VERSION, layer, BuildConfig.APP_ID, deviceModel, systemVersion, appVersion, langCode, systemLangCode, configPath, FileLog.getNetworkLogPath(), pushString, fingerprint, timezoneOffset, getUserConfig().getClientUserId(), enablePushConnection);
    }

    public boolean isPushConnectionEnabled() {
        SharedPreferences preferences = MessagesController.getGlobalNotificationsSettings();
        if (preferences.contains("pushConnection")) {
            return preferences.getBoolean("pushConnection", true);
        } else {
            return MessagesController.getMainSettings(UserConfig.selectedAccount).getBoolean("backgroundConnection", false);
        }
    }

    public long getCurrentTimeMillis() {
        return native_getCurrentTimeMillis(currentAccount);
    }

    public int getCurrentTime() {
        return native_getCurrentTime(currentAccount);
    }

    public int getTimeDifference() {
        return native_getTimeDifference(currentAccount);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock) {
        return sendRequest(object, completionBlock, null, 0);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, int flags) {
        return sendRequest(object, completionBlock, null, null, flags, DEFAULT_DATACENTER_ID, ConnectionTypeGeneric, true);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, int flags, int connetionType) {
        return sendRequest(object, completionBlock, null, null, flags, DEFAULT_DATACENTER_ID, connetionType, true);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, QuickAckDelegate quickAckBlock, int flags) {
        return sendRequest(object, completionBlock, quickAckBlock, null, flags, DEFAULT_DATACENTER_ID, ConnectionTypeGeneric, true);
    }

    public int sendRequest(final TLObject object, final RequestDelegate onComplete, final QuickAckDelegate onQuickAck, final WriteToSocketDelegate onWriteToSocket, final int flags, final int datacenterId, final int connetionType, final boolean immediate) {
        final int requestToken = lastRequestToken.getAndIncrement();
        UIUtil.runOnIoDispatcher(() -> {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("send request " + object.getClass().getSimpleName() + " with token = " + requestToken);
            }
            try {
                NativeByteBuffer buffer = new NativeByteBuffer(object.getObjectSize());
                object.serializeToStream(buffer);
                object.freeResources();

                native_sendRequest(currentAccount, buffer.address, (response, errorCode, errorText, networkType) -> {
                    try {
                        TLObject resp = null;
                        TLRPC.TL_error error = null;
                        if (response != 0) {
                            NativeByteBuffer buff = NativeByteBuffer.wrap(response);
                            buff.reused = true;
                            resp = object.deserializeResponse(buff, buff.readInt32(true), true);
                        } else if (errorText != null) {
                            error = new TLRPC.TL_error();
                            error.code = errorCode;
                            error.text = errorText;
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e(object.getClass().getSimpleName() + " got error " + error.code + " " + error.text + " with token = " + requestToken);
                            }
                        }
                        if (resp != null) {
                            resp.networkType = networkType;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("java received " + resp + " error = " + (error == null ? "null" : (error.code + ": " + error.text)));
                        }
                        final TLObject finalResponse = resp;
                        final TLRPC.TL_error finalError = error;
                        Utilities.stageQueue.postRunnable(() -> {
                            onComplete.run(finalResponse, finalError);
                            if (finalResponse != null) {
                                finalResponse.freeResources();
                            }
                        });
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }, onQuickAck, onWriteToSocket, flags, datacenterId, connetionType, immediate, requestToken);
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
        return requestToken;
    }

    public void cancelRequest(int token, boolean notifyServer) {
        native_cancelRequest(currentAccount, token, notifyServer);
    }

    public void cleanup(boolean resetKeys) {
        native_cleanUp(currentAccount, resetKeys);
    }

    public void cancelRequestsForGuid(int guid) {
        native_cancelRequestsForGuid(currentAccount, guid);
    }

    public void bindRequestToGuid(int requestToken, int guid) {
        native_bindRequestToGuid(currentAccount, requestToken, guid);
    }

    public void applyDatacenterAddress(int datacenterId, String ipAddress, int port, int flag) {
        native_applyDatacenterAddress(currentAccount, datacenterId, ipAddress, port, flag);
    }

    public int getConnectionState() {
        if (connectionState == ConnectionStateConnected && isUpdating) {
            return ConnectionStateUpdating;
        }
        return connectionState;
    }

    public void setUserId(int id) {
        native_setUserId(currentAccount, id);
    }

    public void checkConnection() {
        native_setUseIpv6(currentAccount, useIpv6Address());
        native_setNetworkAvailable(currentAccount, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType(), ApplicationLoader.isConnectionSlow());
    }

    public void setPushConnectionEnabled(boolean value) {
        native_setPushConnectionEnabled(currentAccount, value);
    }

    public void init(int version, int layer, int apiId, String deviceModel, String systemVersion, String appVersion, String langCode, String systemLangCode, String configPath, String logPath, String regId, String cFingerprint, int timezoneOffset, int userId, boolean enablePushConnection) {

        if (SharedConfig.proxyEnabled && SharedConfig.currentProxy != null) {

            native_setProxySettings(currentAccount, SharedConfig.currentProxy.address, SharedConfig.currentProxy.port, SharedConfig.currentProxy.username, SharedConfig.currentProxy.password, SharedConfig.currentProxy.secret);

        }

        native_init(currentAccount, version, layer, apiId, deviceModel, systemVersion, appVersion, langCode, systemLangCode, configPath, logPath, regId, cFingerprint, timezoneOffset, userId, enablePushConnection, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType());
        checkConnection();

    }

    public static void setLangCode(String langCode) {
        langCode = langCode.replace('_', '-').toLowerCase();
        for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
            native_setLangCode(a, langCode);
        }
    }

    public static void setRegId(String regId, String status) {
        String pushString = regId;
        if (TextUtils.isEmpty(pushString) && !TextUtils.isEmpty(status)) {
            pushString = status;
        }
        for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
            native_setRegId(a, pushString);
        }
    }

    public static void setSystemLangCode(String langCode) {
        langCode = langCode.replace('_', '-').toLowerCase();
        for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
            native_setSystemLangCode(a, langCode);
        }
    }

    public void switchBackend() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        preferences.edit().remove("language_showed2").apply();
        native_switchBackend(currentAccount);
    }

    public void resumeNetworkMaybe() {
        native_resumeNetwork(currentAccount, true);
    }

    public void updateDcSettings() {
        native_updateDcSettings(currentAccount);
    }

    public long getPauseTime() {
        return lastPauseTime;
    }

    public long checkProxy(String address, int port, String username, String password, String secret, RequestTimeDelegate requestTimeDelegate) {
        if (TextUtils.isEmpty(address)) {
            return 0;
        }
        if (address == null) {
            address = "";
        }
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (secret == null) {
            secret = "";
        }
        return native_checkProxy(currentAccount, address, port, username, password, secret, requestTimeDelegate);
    }

    public void setAppPaused(final boolean value, final boolean byScreenState) {
        if (!byScreenState) {
            appPaused = value;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("app paused = " + value);
            }
            if (value) {
                appResumeCount--;
            } else {
                appResumeCount++;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("app resume count " + appResumeCount);
            }
            if (appResumeCount < 0) {
                appResumeCount = 0;
            }
        }
        if (appResumeCount == 0) {
            if (lastPauseTime == 0) {
                lastPauseTime = System.currentTimeMillis();
            }
            native_pauseNetwork(currentAccount);
        } else {
            if (appPaused) {
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reset app pause time");
            }
            if (lastPauseTime != 0 && System.currentTimeMillis() - lastPauseTime > 5000) {
                getContactsController().checkContacts();
            }
            lastPauseTime = 0;
            native_resumeNetwork(currentAccount, false);
        }
    }

    public static void onUnparsedMessageReceived(long address, final int currentAccount) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            int constructor = buff.readInt32(true);
            final TLObject message = TLClassStore.Instance().TLdeserialize(buff, constructor, true);
            if (message instanceof TLRPC.Updates) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("java received " + message);
                }
                KeepAliveJob.finishJob();
                Utilities.stageQueue.postRunnable(() -> AccountInstance.getInstance(currentAccount).getMessagesController().processUpdates((TLRPC.Updates) message, false));
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d(String.format("java received unknown constructor 0x%x", constructor));
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void onUpdate(final int currentAccount) {
        Utilities.stageQueue.postRunnable(() -> AccountInstance.getInstance(currentAccount).getMessagesController().updateTimerProc());
    }

    public static void onSessionCreated(final int currentAccount) {
        Utilities.stageQueue.postRunnable(() -> AccountInstance.getInstance(currentAccount).getMessagesController().getDifference());
    }

    public static void onConnectionStateChanged(final int state, final int currentAccount) {
        AndroidUtilities.runOnUIThread(() -> {
            getInstance(currentAccount).connectionState = state;
            AccountInstance.getInstance(currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.didUpdateConnectionState);
        });
    }

    public static void onLogout(final int currentAccount) {
        AndroidUtilities.runOnUIThread(() -> {
            AccountInstance accountInstance = AccountInstance.getInstance(currentAccount);
            if (accountInstance.getUserConfig().getClientUserId() != 0) {
                accountInstance.getUserConfig().clearConfig();
                accountInstance.getMessagesController().performLogout(0);
            }
        });
    }

    public static int getInitFlags() {
        int flags = 0;
        EmuDetector detector = EmuDetector.with(ApplicationLoader.applicationContext);
        if (detector.detect()) {
            flags |= 1024;
        }
        try {
            String installer = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
            if ("com.android.vending".equals(installer)) {
                flags |= 2048;
            }
        } catch (Throwable ignore) {

        }
        return flags;
    }

    public static void onBytesSent(int amount, int networkType, final int currentAccount) {
        try {
            AccountInstance.getInstance(currentAccount).getStatsController().incrementSentBytesCount(networkType, StatsController.TYPE_TOTAL, amount);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void onRequestNewServerIpAndPort(final int second, final int currentAccount) {
        Utilities.stageQueue.postRunnable(() -> {
            if (MessagesController.getMainSettings(currentAccount).getBoolean("custom_dc", false)) {
                return;
            }

            if (currentTask != null || second == 0 && Math.abs(lastDnsRequestTime - System.currentTimeMillis()) < 10000 || !ApplicationLoader.isNetworkOnline()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("don't start task, current task = " + currentTask + " next task = " + second + " time diff = " + Math.abs(lastDnsRequestTime - System.currentTimeMillis()) + " network = " + ApplicationLoader.isNetworkOnline());
                }
                return;
            }

            lastDnsRequestTime = System.currentTimeMillis();

            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start dns txt task");
            }
            DnsTxtLoadTask task = new DnsTxtLoadTask(currentAccount);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
            currentTask = task;

        });
    }

    public static void onProxyError() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShowAlert, 3);
    }

    public static void getHostByName(String hostName, long address) {
        AndroidUtilities.runOnUIThread(() -> {
            ResolvedDomain resolvedDomain = dnsCache.get(hostName);
            if (resolvedDomain != null && SystemClock.elapsedRealtime() - resolvedDomain.ttl < 5 * 60 * 1000) {
                String addr = resolvedDomain.getAddress();
                native_onHostNameResolved(hostName, address, addr, Utils.isIpv6Address(addr));
            } else {
                ResolveHostByNameTask task = resolvingHostnameTasks.get(hostName);
                if (task == null) {
                    task = new ResolveHostByNameTask(hostName);
                    try {
                        task.executeOnExecutor(DNS_THREAD_POOL_EXECUTOR, null, null, null);
                    } catch (Throwable e) {
                        FileLog.e(e);
                        native_onHostNameResolved(hostName, address, "", false);
                        return;
                    }
                    resolvingHostnameTasks.put(hostName, task);
                }
                task.addAddress(address);
            }
        });
    }

    public static void onBytesReceived(int amount, int networkType, final int currentAccount) {
        try {
            StatsController.getInstance(currentAccount).incrementReceivedBytesCount(networkType, StatsController.TYPE_TOTAL, amount);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void onUpdateConfig(long address, final int currentAccount) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            final TLRPC.TL_config message = TLRPC.TL_config.TLdeserialize(buff, buff.readInt32(true), true);
            if (message != null) {
                Utilities.stageQueue.postRunnable(() -> AccountInstance.getInstance(currentAccount).getMessagesController().updateConfig(message));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void onInternalPushReceived(final int currentAccount) {
        KeepAliveJob.startJob();
    }

    public static void setProxySettings(boolean enabled, String address, int port, String username, String password, String secret) {
        if (address == null) {
            address = "";
        }
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (secret == null) {
            secret = "";
        }
        for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
            if (enabled && !TextUtils.isEmpty(address)) {
                native_setProxySettings(a, address, port, username, password, secret);
            } else {
                native_setProxySettings(a, "", 1080, "", "", "");
            }
            AccountInstance accountInstance = AccountInstance.getInstance(a);
            if (accountInstance.getUserConfig().isClientActivated()) {
                accountInstance.getMessagesController().checkProxyInfo(true);
            }
        }
    }

    public static native void native_switchBackend(int currentAccount);

    public static native int native_isTestBackend(int currentAccount);

    public static native void native_pauseNetwork(int currentAccount);

    public static native void native_setUseIpv6(int currentAccount, boolean value);

    public static native void native_updateDcSettings(int currentAccount);

    public static native void native_setNetworkAvailable(int currentAccount, boolean value, int networkType, boolean slow);

    public static native void native_resumeNetwork(int currentAccount, boolean partial);

    public static native long native_getCurrentTimeMillis(int currentAccount);

    public static native int native_getCurrentTime(int currentAccount);

    public static native int native_getTimeDifference(int currentAccount);

    public static native void native_sendRequest(int currentAccount, long object, RequestDelegateInternal onComplete, QuickAckDelegate onQuickAck, WriteToSocketDelegate onWriteToSocket, int flags, int datacenterId, int connetionType, boolean immediate, int requestToken);

    public static native void native_cancelRequest(int currentAccount, int token, boolean notifyServer);

    public static native void native_cleanUp(int currentAccount, boolean resetKeys);

    public static native void native_cancelRequestsForGuid(int currentAccount, int guid);

    public static native void native_bindRequestToGuid(int currentAccount, int requestToken, int guid);

    public static native void native_applyDatacenterAddress(int currentAccount, int datacenterId, String ipAddress, int port, int flag);

    public static native int native_getConnectionState(int currentAccount);

    public static native void native_setUserId(int currentAccount, int id);

    public static native void native_init(int currentAccount, int version, int layer, int apiId, String deviceModel, String systemVersion, String appVersion, String langCode, String systemLangCode, String configPath, String logPath, String regId, String cFingerprint, int timezoneOffset, int userId, boolean enablePushConnection, boolean hasNetwork, int networkType);

    public static native void native_setProxySettings(int currentAccount, String address, int port, String username, String password, String secret);

    public static native void native_setLangCode(int currentAccount, String langCode);

    public static native void native_setRegId(int currentAccount, String regId);

    public static native void native_setSystemLangCode(int currentAccount, String langCode);

    public static native void native_seSystemLangCode(int currentAccount, String langCode);

    public static native void native_setJava(boolean useJavaByteBuffers);

    public static native void native_setPushConnectionEnabled(int currentAccount, boolean value);

    public static native void native_applyDnsConfig(int currentAccount, long address, String phone, int date);

    public static native long native_checkProxy(int currentAccount, String address, int port, String username, String password, String secret, RequestTimeDelegate requestTimeDelegate);

    public static native void native_onHostNameResolved(String host, long address, String ip, boolean ipv6);

    public static int generateClassGuid() {
        return lastClassGuid++;
    }

    public void setIsUpdating(final boolean value) {
        AndroidUtilities.runOnUIThread(() -> {
            if (isUpdating == value) {
                return;
            }
            isUpdating = value;
            if (connectionState == ConnectionStateConnected) {
                AccountInstance.getInstance(currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.didUpdateConnectionState);
            }
        });
    }

    @SuppressLint("NewApi")
    public static boolean useIpv6Address() {
        if (Build.VERSION.SDK_INT < 19) {
            return false;
        }
        if (BuildVars.LOGS_ENABLED) {
            try {
                NetworkInterface networkInterface;
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    networkInterface = networkInterfaces.nextElement();
                    if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.getInterfaceAddresses().isEmpty()) {
                        continue;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("valid interface: " + networkInterface);
                    }
                    List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                    for (int a = 0; a < interfaceAddresses.size(); a++) {
                        InterfaceAddress address = interfaceAddresses.get(a);
                        InetAddress inetAddress = address.getAddress();
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("address: " + inetAddress.getHostAddress());
                        }
                        if (inetAddress.isLinkLocalAddress() || inetAddress.isLoopbackAddress() || inetAddress.isMulticastAddress()) {
                            continue;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("address is good");
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        try {
            NetworkInterface networkInterface;
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            boolean hasIpv4 = false;
            boolean hasIpv6 = false;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }
                List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                for (int a = 0; a < interfaceAddresses.size(); a++) {
                    InterfaceAddress address = interfaceAddresses.get(a);
                    InetAddress inetAddress = address.getAddress();
                    if (inetAddress.isLinkLocalAddress() || inetAddress.isLoopbackAddress() || inetAddress.isMulticastAddress()) {
                        continue;
                    }
                    if (inetAddress instanceof Inet6Address) {
                        hasIpv6 = true;
                    } else if (inetAddress instanceof Inet4Address) {
                        String addrr = inetAddress.getHostAddress();
                        if (!addrr.startsWith("192.0.0.")) {
                            hasIpv4 = true;
                        }
                    }
                }
            }
            if (NekoConfig.useIPv6) {
                return hasIpv6;
            } else {
                return !hasIpv4 && hasIpv6;
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }

        return false;
    }


    private static class ResolveHostByNameTask extends AsyncTask<Void, Void, ResolvedDomain> {

        private ArrayList<Long> addresses = new ArrayList<>();
        private String currentHostName;

        public ResolveHostByNameTask(String hostName) {
            super();
            currentHostName = hostName;
        }

        public void addAddress(long address) {
            if (addresses.contains(address)) {
                return;
            }
            addresses.add(address);
        }

        protected ResolvedDomain doInBackground(Void... voids) {

            InetAddress[] result = DnsFactory.Companion.lookup(currentHostName).toArray(new InetAddress[0]);

            return new ResolvedDomain(result, 10 * 60 * 1000L);

        }

        @Override
        protected void onPostExecute(final ResolvedDomain result) {
            if (result != null) {
                dnsCache.put(currentHostName, result);
                for (int a = 0, N = addresses.size(); a < N; a++) {
                    String address = result.getAddress();
                    native_onHostNameResolved(currentHostName, addresses.get(a), address, Utils.isIpv6Address(address));
                }
            } else {
                for (int a = 0, N = addresses.size(); a < N; a++) {
                    native_onHostNameResolved(currentHostName, addresses.get(a), "", false);
                }
            }
            resolvingHostnameTasks.remove(currentHostName);
        }
    }

    private static class DnsTxtLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {

        private int currentAccount;
        private int responseDate;

        public DnsTxtLoadTask(int instance) {
            super();
            currentAccount = instance;
        }

        protected NativeByteBuffer doInBackground(Void... voids) {

            String domain = native_isTestBackend(currentAccount) != 0 ? "tapv3.stel.com" : AccountInstance.getInstance(currentAccount).getMessagesController().dcDomainName;
            try {
                ArrayList<String> arrayList = DnsFactory.Companion.getTxts(domain);
                Collections.sort(arrayList, (o1, o2) -> {
                    int l1 = o1.length();
                    int l2 = o2.length();
                    if (l1 > l2) {
                        return -1;
                    } else if (l1 < l2) {
                        return 1;
                    }
                    return 0;
                });
                StringBuilder builder = new StringBuilder();
                for (int a = 0; a < arrayList.size(); a++) {
                    builder.append(arrayList.get(a).replace("\"", ""));
                }
                byte[] bytes = Base64.decode(builder.toString(), Base64.DEFAULT);
                NativeByteBuffer buffer = new NativeByteBuffer(bytes.length);
                buffer.writeBytes(bytes);
                return buffer;
            } catch (Throwable e) {
                FileLog.e(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final NativeByteBuffer result) {
            Utilities.stageQueue.postRunnable(() -> {
                currentTask = null;
                if (result != null) {
                    native_applyDnsConfig(currentAccount, result.address, AccountInstance.getInstance(currentAccount).getUserConfig().getClientPhone(), responseDate);
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("failed to get dns txt result");
                        FileLog.d("restart load task");
                    }
                    DnsTxtLoadTask task = new DnsTxtLoadTask(currentAccount);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
                    currentTask = task;
                }
            });
        }
    }

}
