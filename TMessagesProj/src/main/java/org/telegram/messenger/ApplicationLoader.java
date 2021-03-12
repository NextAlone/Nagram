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
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;
import androidx.multidex.MultiDex;

import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ForegroundDetector;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import tw.nekomimi.nekogram.ExternalGcm;
import tw.nekomimi.nekogram.utils.FileUtil;
import tw.nekomimi.nekogram.utils.UIUtil;

import static android.os.Build.VERSION.SDK_INT;

public class ApplicationLoader extends Application {

    @SuppressLint("StaticFieldLeak")
    public static volatile Context applicationContext;

    public static volatile NetworkInfo currentNetworkInfo;
    public static volatile Handler applicationHandler;

    private static ConnectivityManager connectivityManager;
    private static volatile boolean applicationInited = false;

    public static long startTime;

    public static volatile boolean isScreenOn = false;
    public static volatile boolean mainInterfacePaused = true;
    public static volatile boolean mainInterfaceStopped = true;
    public static volatile boolean externalInterfacePaused = true;
    public static volatile boolean mainInterfacePausedStageQueue = true;
    public static boolean canDrawOverlays;
    public static volatile long mainInterfacePausedStageQueueTime;

    public static boolean hasPlayServices;


    @Override
    protected void attachBaseContext(Context base) {
        if (SDK_INT >= Build.VERSION_CODES.P) {
            Reflection.unseal(base);
        }
        super.attachBaseContext(base);
        try {
            applicationContext = getApplicationContext();
        } catch (Throwable ignore) {
        }
        if (SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MultiDex.install(this);
        }
        Thread.currentThread().setUncaughtExceptionHandler((thread, error) -> {
            Log.e("nekox", "from " + thread.toString(), error);
        });
    }

    /**
     * @author weishu
     * @date 2018/6/7.
     */
    public static class Reflection {
        private static final String TAG = "Reflection";

        private static Object sVmRuntime;
        private static Method setHiddenApiExemptions;

        static {
            if (SDK_INT >= Build.VERSION_CODES.P) {
                try {
                    Method forName = Class.class.getDeclaredMethod("forName", String.class);
                    Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);

                    Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
                    Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
                    setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
                    sVmRuntime = getRuntime.invoke(null);
                } catch (Throwable e) {
                    FileLog.e("reflect bootstrap failed:", e);
                }
            }

        }

        private static int UNKNOWN = -9999;

        private static final int ERROR_SET_APPLICATION_FAILED = -20;

        private static final int ERROR_EXEMPT_FAILED = -21;

        private static int unsealed = UNKNOWN;

        public static int unseal(Context context) {
            if (SDK_INT < 28) {
                // Below Android P, ignore
                return 0;
            }

            // try exempt API first.
            if (exemptAll()) {
                return 0;
            } else {
                return ERROR_EXEMPT_FAILED;
            }
        }

        /**
         * make the method exempted from hidden API check.
         *
         * @param method the method signature prefix.
         * @return true if success.
         */
        public static boolean exempt(String method) {
            return exempt(new String[]{method});
        }

        /**
         * make specific methods exempted from hidden API check.
         *
         * @param methods the method signature prefix, such as "Ldalvik/system", "Landroid" or even "L"
         * @return true if success
         */
        public static boolean exempt(String... methods) {
            if (sVmRuntime == null || setHiddenApiExemptions == null) {
                return false;
            }

            try {
                setHiddenApiExemptions.invoke(sVmRuntime, new Object[]{methods});
                return true;
            } catch (Throwable e) {
                return false;
            }
        }

        /**
         * Make all hidden API exempted.
         *
         * @return true if success.
         */
        public static boolean exemptAll() {
            return exempt(new String[]{"L"});
        }
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
        if (applicationInited) {
            return;
        }
        applicationInited = true;

        SharedConfig.loadConfig();
        UserConfig.getInstance(0).loadConfig();

        LinkedList<Runnable> postRun = new LinkedList<>();

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
            FileLog.e(e);
        }

        for (int a : SharedConfig.activeAccounts) {
            final int finalA = a;
            Runnable initRunnable = () -> loadAccount(finalA);
            if (finalA == UserConfig.selectedAccount) initRunnable.run();
            else postRun.add(initRunnable);
        }

        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("app initied");
        }
        for (Runnable runnable : postRun) {
            Utilities.stageQueue.postRunnable(runnable);
        }
        Utilities.stageQueue.postRunnable(ExternalGcm::initPlayServices);
    }

    private static final HashSet<Integer> loadedAccounts = new HashSet<>();

    public static void loadAccount(int account) {
        if (!loadedAccounts.add(account)) return;
        UserConfig inst = UserConfig.getInstance(account);
        inst.loadConfig();
        if (!inst.isClientActivated()) {
            if (SharedConfig.activeAccounts.remove(account)) {
                SharedConfig.saveAccounts();
            }
        }

        MessagesController.getInstance(account);
        if (account == 0) {
            SharedConfig.pushStringStatus = "__FIREBASE_GENERATING_SINCE_" + ConnectionsManager.getInstance(account).getCurrentTime() + "__";
        } else {
            ConnectionsManager.getInstance(account);
        }
        TLRPC.User user = UserConfig.getInstance(account).getCurrentUser();
        if (user != null) {
            MessagesController.getInstance(account).putUser(user, true);
        }

        MediaController.getInstance().init(account);

        Utilities.stageQueue.postRunnable(() -> {
            Theme.init(account);
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
        }
        if (applicationContext == null) {
            applicationContext = getApplicationContext();
        }

        try {
            Class.forName("org.robolectric.android.internal.AndroidTestEnvironment");
            return;
        } catch (ClassNotFoundException e) {
        }

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

        startPushService();

    }

    public static void startPushService() {
        Utilities.stageQueue.postRunnable(ApplicationLoader::startPushServiceInternal);
    }

    private static void startPushServiceInternal() {
        if (ExternalGcm.checkPlayServices() || (SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && isNotificationListenerEnabled())) {
            return;
        }
        SharedPreferences preferences = MessagesController.getGlobalNotificationsSettings();
        boolean enabled;
        if (preferences.contains("pushService")) {
            enabled = preferences.getBoolean("pushService", true);
            if (SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (!preferences.getBoolean("pushConnection", true)) return;
            }
        } else {
            enabled = MessagesController.getMainSettings(UserConfig.selectedAccount).getBoolean("keepAliveService", true);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("pushService", enabled);
            editor.putBoolean("pushConnection", enabled);
            editor.apply();
            SharedPreferences preferencesCA = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
            SharedPreferences.Editor editorCA = preferencesCA.edit();
            editorCA.putBoolean("pushConnection", enabled);
            editorCA.putBoolean("pushService", enabled);
            editorCA.apply();
            ConnectionsManager.getInstance(UserConfig.selectedAccount).setPushConnectionEnabled(true);
        }
        if (enabled) {
            try {
                UIUtil.runOnUIThread(() -> {
                    Log.d("TFOSS", "Starting push service...");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        applicationContext.startForegroundService(new Intent(applicationContext, NotificationsService.class));
                    } else {
                        applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
                    }
                });
            } catch (Throwable e) {
                Log.d("TFOSS", "Failed to start push service");
            }
        } else UIUtil.runOnUIThread(() -> {
            applicationContext.stopService(new Intent(applicationContext, NotificationsService.class));
            PendingIntent pintent = PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationsService.class), 0);
            AlarmManager alarm = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pintent);
        });
    }

    public static boolean isNotificationListenerEnabled() {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(applicationContext);
        if (packageNames.contains(applicationContext.getPackageName())) {
            return true;
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            LocaleController.getInstance().onDeviceConfigurationChange(newConfig);
            AndroidUtilities.checkDisplaySize(applicationContext, newConfig);
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
                if (connectivityManager.isActiveNetworkMetered()) {
                    return StatsController.TYPE_MOBILE;
                } else {
                    return StatsController.TYPE_WIFI;
                }
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
}
