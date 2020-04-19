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
    public static boolean LOGS_ENABLED = false;
    public static boolean DEBUG_PRIVATE_VERSION = false;
    public static boolean USE_CLOUD_STRINGS = true;
    public static boolean CHECK_UPDATES = true;
    public static boolean NO_SCOPED_STORAGE = Build.VERSION.SDK_INT <= 29;
    public static int BUILD_VERSION = 2538;
    public static String BUILD_VERSION_STRING = "8.4.4";
    public static int APP_ID = 4;
    public static String APP_HASH = "014b35b6184100b085b0d0572f9b5103";
  
    public static String SMS_HASH = isStandaloneApp() ? "w0lkcmTZkKh" : (DEBUG_VERSION ? "O2P2z+/jBpJ" : "oLeq9AcOZkT");
    public static String PLAYSTORE_APP_URL = "https://play.google.com/store/apps/details?id=org.telegram.messenger";

    static {
        if (ApplicationLoader.applicationContext != null) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Context.MODE_PRIVATE);
            LOGS_ENABLED = DEBUG_VERSION || sharedPreferences.getBoolean("logsEnabled", DEBUG_VERSION);
            LOGS_ENABLED = sharedPreferences.getBoolean("logsEnabled", false);
        }

        BUILD_VERSION_STRING = BuildConfig.VERSION_NAME;
        APP_ID = BuildConfig.APP_ID;
        APP_HASH = BuildConfig.APP_HASH;
        PLAYSTORE_APP_URL = "";
        SMS_HASH = "";
        DEBUG_VERSION = true;
        CHECK_UPDATES = (BuildConfig.CHECK_UPDATES != 0);
        BUILD_VERSION *= 10;
        BUILD_VERSION += BuildConfig.ADDITIONAL_BUILD_NUMBER;
    }
    public static int USER_ID_OWNER = BuildConfig.USER_ID_OWNER;
    public static String USER_REPO = BuildConfig.USER_REPO;

    private static Boolean standaloneApp;
    public static boolean isStandaloneApp() {
        return true;
    }

    private static Boolean betaApp;
    public static boolean isBetaApp() {
        if (betaApp == null) {
            betaApp = ApplicationLoader.applicationContext != null && "org.telegram.messenger.beta".equals(ApplicationLoader.applicationContext.getPackageName());
        }
        return betaApp;
    }
}
