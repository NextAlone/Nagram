/*
 * This is the source code of Telegram for Android v. 7.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2020.
 */

package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class BuildVars {

    public static boolean DEBUG_VERSION = false;
    public static boolean DEBUG_PRIVATE_VERSION = false;
    public static boolean CHECK_UPDATES = true;
    public static boolean LOGS_ENABLED = true;
    public static boolean USE_CLOUD_STRINGS = true;
    public static boolean NO_SCOPED_STORAGE = Build.VERSION.SDK_INT <= 29;
    public static int BUILD_VERSION = 2594;
    public static String BUILD_VERSION_STRING = "8.6.1";
    public static int APP_ID = 19797609;
    public static String APP_HASH = "e8f1567dbbf38944a1391c4d23c34b60";
    public static String APPCENTER_HASH = "e07b49da-11a5-46db-a780-f5cd7b9a1a5a";

    public static String SMS_HASH = isStandaloneApp() ? "w0lkcmTZkKh" : (DEBUG_VERSION ? "O2P2z+/jBpJ" : "oLeq9AcOZkT");
    public static String PLAYSTORE_APP_URL = "https://play.google.com/store/apps/details?id=top.qwq2333.nullgram";

    static {
        if (ApplicationLoader.applicationContext != null) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Context.MODE_PRIVATE);
            LOGS_ENABLED = DEBUG_VERSION || sharedPreferences.getBoolean("logsEnabled", DEBUG_VERSION);
        }
    }

    private static Boolean standaloneApp;
    public static boolean isStandaloneApp() {
        if (standaloneApp == null) {
            standaloneApp = ApplicationLoader.applicationContext != null && "org.telegram.messenger.web".equals(ApplicationLoader.applicationContext.getPackageName());
        }
        return standaloneApp;
    }

    private static Boolean betaApp;
    public static boolean isBetaApp() {
        if (betaApp == null) {
            betaApp = ApplicationLoader.applicationContext != null && "top.qwq233.nullgram.beta".equals(ApplicationLoader.applicationContext.getPackageName());
        }
        return betaApp;
    }
}
