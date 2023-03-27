/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import androidx.multidex.MultiDex;

import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.LauncherIconController;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;

import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.NekoXConfig;
import tw.nekomimi.nekogram.parts.SignturesKt;
import tw.nekomimi.nekogram.utils.FileUtil;

import static android.os.Build.VERSION.SDK_INT;

public class ApplicationLoader extends Application {
    private static PendingIntent pendingIntent;

    @SuppressLint("StaticFieldLeak")
    public static volatile Context applicationContext;

    public static volatile NetworkInfo currentNetworkInfo;
    public static volatile Handler applicationHandler;

    private static ConnectivityManager connectivityManager;
    private static volatile boolean applicationInited = false;
    private static volatile  ConnectivityManager.NetworkCallback networkCallback;
    private static long lastNetworkCheckTypeTime;
    private static int lastKnownNetworkType = -1;

    public static long startTime;

    public static volatile boolean isScreenOn = false;
    public static volatile boolean mainInterfacePaused = true;
    public static volatile boolean mainInterfaceStopped = true;
    public static volatile boolean externalInterfacePaused = true;
    public static volatile boolean mainInterfacePausedStageQueue = true;
    public static boolean canDrawOverlays;
    public static volatile long mainInterfacePausedStageQueueTime;

    private static PushListenerController.IPushListenerServiceProvider pushProvider;
    private static IMapsProvider mapsProvider;
    private static ILocationServiceProvider locationServiceProvider;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        try {
            applicationContext = getApplicationContext();
        } catch (Throwable ignore) {
        }
        Thread.currentThread().setUncaughtExceptionHandler((thread, error) -> {
            Log.e("nekox", "from " + thread.toString(), error);
        });
    }

    public static ILocationServiceProvider getLocationServiceProvider() {
        if (locationServiceProvider == null) {
            if (BuildVars.isGServicesCompiled) {
                try {
                    locationServiceProvider = (ILocationServiceProvider) Class.forName("org.telegram.messenger.GoogleLocationProvider").newInstance();
                    locationServiceProvider.init(applicationContext);
                } catch (Exception e) {
                    FileLog.e("Failed to load GoogleLocationService Provider from gservices", e);
                    locationServiceProvider = new ILocationServiceProvider.DummyLocationServiceProvider();
                }
            } else {
                locationServiceProvider = new ILocationServiceProvider.DummyLocationServiceProvider();
            }
        }
        return locationServiceProvider;
    }

    public static IMapsProvider getMapsProvider() {
        if (mapsProvider == null) {
            if (NekoConfig.useOSMDroidMap.Bool())
                mapsProvider = new OSMDroidMapsProvider();
            else {
                if (BuildVars.isGServicesCompiled) {
                    try {
                        mapsProvider = (IMapsProvider) Class.forName("org.telegram.messenger.GoogleMapsProvider").newInstance();
                    } catch (Exception e) {
                        FileLog.e("Failed to load Google Maps Provider from gservices", e);
                        mapsProvider = new OSMDroidMapsProvider();
                    }
                } else {
                    mapsProvider = new OSMDroidMapsProvider();
                }
            }
        }
        return mapsProvider;
    }

    public static PushListenerController.IPushListenerServiceProvider getPushProvider() {
        if (pushProvider == null) {
            pushProvider = PushListenerController.getProvider();
        }
        return pushProvider;
    }

    public static String getApplicationId() {
        return BuildConfig.APPLICATION_ID;
    }

    @SuppressLint("SdCardPath")
    public static File getDataDirFixed() {
        try {
            File path = applicationContext.getFilesDir();
            if (path != null) {
                return path.getParentFile();
            }
        } catch (Exception ignored) {
        }
        try {
            ApplicationInfo info = applicationContext.getApplicationInfo();
            return new File(info.dataDir);
        } catch (Exception ignored) {
        }
        return new File("/data/data/" + BuildConfig.APPLICATION_ID + "/");
    }

    public static File getFilesDirFixed() {
        File filesDir = new File(getDataDirFixed(), "files");
        FileUtil.initDir(filesDir);
        return filesDir;
    }

    public static File getCacheDirFixed() {
        File filesDir = new File(getDataDirFixed(), "cache");
        FileUtil.initDir(filesDir);
        return filesDir;
    }

    public static void postInitApplication() {
        if (applicationInited || applicationContext == null) {
            return;
        }
        applicationInited = true;

        SharedConfig.loadConfig();
        LocaleController.getInstance();
        SharedPrefsHelper.init(applicationContext);
        UserConfig.getInstance(0).loadConfig();

        try {
            connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    } catch (Throwable ignore) {

                    }

                    boolean isSlow = isConnectionSlow();
                    for (int a : SharedConfig.activeAccounts) {
                        ConnectionsManager.getInstance(a).checkConnection();
                        FileLoader.getInstance(a).onNetworkChanged(isSlow);
                    }

                    if (SharedConfig.loginingAccount != -1) {
                        ConnectionsManager.getInstance(SharedConfig.loginingAccount).checkConnection();
                        FileLoader.getInstance(SharedConfig.loginingAccount).onNetworkChanged(isSlow);
                    }
                }
            };
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            ApplicationLoader.applicationContext.registerReceiver(networkStateReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            final BroadcastReceiver mReceiver = new ScreenReceiver();
            applicationContext.registerReceiver(mReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            PowerManager pm = (PowerManager) ApplicationLoader.applicationContext.getSystemService(Context.POWER_SERVICE);
            isScreenOn = pm.isScreenOn();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("screen state = " + isScreenOn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LinkedList<Runnable> postRun = new LinkedList<>();
        for (int a : SharedConfig.activeAccounts) {
            final int finalA = a;
            Runnable initRunnable = () -> loadAccount(finalA);
            if (finalA == UserConfig.selectedAccount) {
                initRunnable.run();
                ChatThemeController.init();
            }
            else postRun.add(initRunnable);
        }
        for (Runnable runnable : postRun) {
            Utilities.stageQueue.postRunnable(runnable);
        }
        // init fcm
        initPushServices();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("app initied");
        }
    }

    public static void loadAccount(int account) {
        UserConfig inst = UserConfig.getInstance(account);
        inst.loadConfig();
        if (!inst.isClientActivated()) {
            if (SharedConfig.activeAccounts.remove(account)) {
                SharedConfig.saveAccounts();
            }
        }

        MessagesController.getInstance(account);
        if ("".equals(SharedConfig.pushStringStatus)) {
            SharedConfig.pushStringStatus = "__FIREBASE_GENERATING_SINCE_" + ConnectionsManager.getInstance(account).getCurrentTime() + "__";
        } else {
            ConnectionsManager.getInstance(account);
        }
        TLRPC.User user = UserConfig.getInstance(account).getCurrentUser();
        if (user != null) {
            MessagesController.getInstance(account).putUser(user, true);
        }
        Utilities.stageQueue.postRunnable(() -> {
            SendMessagesHelper.getInstance(account).checkUnsentMessages();
            ContactsController.getInstance(account).checkAppAccount();
            DownloadController.getInstance(account);
        });
    }

    public ApplicationLoader() {
        super();
    }

    @Override
    public void onCreate() {
        try {
            applicationContext = getApplicationContext();
        } catch (Throwable ignore) {
        }

        super.onCreate();

        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("app start time = " + (startTime = SystemClock.elapsedRealtime()));
            FileLog.d("buildVersion = " + BuildVars.BUILD_VERSION);
        }
        if (applicationContext == null) {
            applicationContext = getApplicationContext();
        }

        Utilities.stageQueue.postRunnable(() -> SignturesKt.checkMT(this));

        NativeLoader.initNativeLibs(ApplicationLoader.applicationContext);
        ConnectionsManager.native_setJava(false);
        new ForegroundDetector(this) {
            @Override
            public void onActivityStarted(Activity activity) {
                boolean wasInBackground = isBackground();
                super.onActivityStarted(activity);
                if (wasInBackground) {
                    ensureCurrentNetworkGet(true);
                }
            }
        };
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load libs time = " + (SystemClock.elapsedRealtime() - startTime));
        }

        applicationHandler = new Handler(applicationContext.getMainLooper());

        org.osmdroid.config.Configuration.getInstance().setUserAgentValue("Telegram-FOSS ( NekoX ) " + BuildConfig.VERSION_NAME);
        org.osmdroid.config.Configuration.getInstance().setOsmdroidBasePath(new File(ApplicationLoader.applicationContext.getCacheDir(), "osmdroid"));

        LauncherIconController.tryFixLauncherIconIfNeeded();
        ProxyRotationController.init();
    }

    // Local Push Service, TFoss implementation
    public static void startPushService() {
        Utilities.stageQueue.postRunnable(ApplicationLoader::startPushServiceInternal);
    }

    private static void startPushServiceInternal() {
        if (PushListenerController.getProvider().hasServices()) {
            return;
        }
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        boolean enabled;
        if (preferences.contains("pushService")) {
            enabled = preferences.getBoolean("pushService", false);
        } else {
            enabled = MessagesController.getMainSettings(UserConfig.selectedAccount).getBoolean("keepAliveService", false);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("pushService", enabled);
            editor.putBoolean("pushConnection", enabled);
            editor.apply();
            ConnectionsManager.getInstance(UserConfig.selectedAccount).setPushConnectionEnabled(enabled);
        }
        if (enabled) {
            AndroidUtilities.runOnUIThread(() -> {
                try {
                    Log.d("TFOSS", "Trying to start push service every 10 minutes");
                    // Telegram-FOSS: unconditionally enable push service
                    AlarmManager am = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
                    Intent i = new Intent(applicationContext, NotificationsService.class);
                    pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, i, PendingIntent.FLAG_IMMUTABLE);

                    am.cancel(pendingIntent);
                    am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 60 * 1000, pendingIntent);

                    Log.d("TFOSS", "Starting push service...");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        applicationContext.startForegroundService(new Intent(applicationContext, NotificationsService.class));
                    } else {
                        applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
                    }
                } catch (Throwable e) {
                    Log.d("TFOSS", "Failed to start push service");
                }
            });

        } else AndroidUtilities.runOnUIThread(() -> {
            applicationContext.stopService(new Intent(applicationContext, NotificationsService.class));

            PendingIntent pintent = PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationsService.class), PendingIntent.FLAG_MUTABLE);
            AlarmManager alarm = (AlarmManager)applicationContext.getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pintent);
            if (pendingIntent != null) {
                alarm.cancel(pendingIntent);
            }
        });
    }

    private static void initPushServices() {
        AndroidUtilities.runOnUIThread(() -> {
            if (getPushProvider().hasServices()) {
                getPushProvider().onRequestPushToken();
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("No valid " + getPushProvider().getLogTitle() + " APK found.");
                }
                SharedConfig.pushStringStatus = "__NO_GOOGLE_PLAY_SERVICES__";
                PushListenerController.sendRegistrationToServer(getPushProvider().getPushType(), null);
                startPushService();
            }
        }, 1000);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            LocaleController.getInstance().onDeviceConfigurationChange(newConfig);
            AndroidUtilities.checkDisplaySize(applicationContext, newConfig);
            VideoCapturerDevice.checkScreenCapturerSize();
            AndroidUtilities.resetTabletFlag();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ensureCurrentNetworkGet(boolean force) {
        if (force || currentNetworkInfo == null) {
            try {
                if (connectivityManager == null) {
                    connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                }
                currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (networkCallback == null) {
                        networkCallback = new ConnectivityManager.NetworkCallback() {
                            @Override
                            public void onAvailable(@NonNull Network network) {
                                lastKnownNetworkType = -1;
                            }

                            @Override
                            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                                lastKnownNetworkType = -1;
                            }
                        };
                        connectivityManager.registerDefaultNetworkCallback(networkCallback);
                    }
                }
            } catch (Throwable ignore) {

            }
        }
    }

    public static boolean isRoaming() {
        try {
            ensureCurrentNetworkGet(false);
            return currentNetworkInfo != null && currentNetworkInfo.isRoaming();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return false;
    }

    public static boolean isConnectedOrConnectingToWiFi() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo != null && (currentNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI || currentNetworkInfo.getType() == ConnectivityManager.TYPE_ETHERNET)) {
                NetworkInfo.State state = currentNetworkInfo.getState();
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING || state == NetworkInfo.State.SUSPENDED) {
                    return true;
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return false;
    }

    public static boolean isConnectedToWiFi() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo != null && (currentNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI || currentNetworkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) && currentNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return false;
    }

    public static boolean isConnectionSlow() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo != null && currentNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (currentNetworkInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return true;
                }
            }
        } catch (Throwable ignore) {

        }
        return false;
    }

    public static int getAutodownloadNetworkType() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo == null) {
                return StatsController.TYPE_MOBILE;
            }
            if (currentNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI || currentNetworkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && (lastKnownNetworkType == StatsController.TYPE_MOBILE || lastKnownNetworkType == StatsController.TYPE_WIFI) && System.currentTimeMillis() - lastNetworkCheckTypeTime < 5000) {
                    return lastKnownNetworkType;
                }
                if (connectivityManager.isActiveNetworkMetered()) {
                    lastKnownNetworkType = StatsController.TYPE_MOBILE;
                } else {
                    lastKnownNetworkType = StatsController.TYPE_WIFI;
                }
                lastNetworkCheckTypeTime = System.currentTimeMillis();
                return lastKnownNetworkType;
            }
            if (currentNetworkInfo.isRoaming()) {
                return StatsController.TYPE_ROAMING;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return StatsController.TYPE_MOBILE;
    }

    public static int getCurrentNetworkType() {
        if (isConnectedOrConnectingToWiFi()) {
            return StatsController.TYPE_WIFI;
        } else if (isRoaming()) {
            return StatsController.TYPE_ROAMING;
        } else {
            return StatsController.TYPE_MOBILE;
        }
    }

    public static boolean isNetworkOnlineFast() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo == null) {
                return true;
            }
            if (currentNetworkInfo.isConnectedOrConnecting() || currentNetworkInfo.isAvailable()) {
                return true;
            }

            NetworkInfo netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            } else {
                netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    return true;
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
            return true;
        }
        return false;
    }

    public static boolean isNetworkOnlineRealtime() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && (netInfo.isConnectedOrConnecting() || netInfo.isAvailable())) {
                return true;
            }

            netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            } else {
                netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    return true;
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
            return true;
        }
        return false;
    }

    public static boolean isNetworkOnline() {
        boolean result = isNetworkOnlineRealtime();
        if (BuildVars.DEBUG_PRIVATE_VERSION) {
            boolean result2 = isNetworkOnlineFast();
            if (result != result2) {
                FileLog.d("network online mismatch");
            }
        }
        return result;
    }

//    public static void startAppCenter(Activity context) {
//        applicationLoaderInstance.startAppCenterInternal(context);
//    }
//
//    public static void checkForUpdates() {
//        applicationLoaderInstance.checkForUpdatesInternal();
//    }
//
//    public static void appCenterLog(Throwable e) {
//        applicationLoaderInstance.appCenterLogInternal(e);
//    }

    protected void appCenterLogInternal(Throwable e) {

    }

    protected void checkForUpdatesInternal() {

    }

    protected void startAppCenterInternal(Activity context) {

    }

}
