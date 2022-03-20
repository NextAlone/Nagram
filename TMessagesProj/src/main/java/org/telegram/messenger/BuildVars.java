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

    public static boolean DEBUG_VERSION = BuildConfig.BUILD_TYPE.equals("debug");
    public static boolean LOGS_ENABLED = BuildConfig.BUILD_TYPE.equals("debug");
    public static boolean DEBUG_PRIVATE_VERSION = BuildConfig.BUILD_TYPE.equals("debug");
    public static boolean USE_CLOUD_STRINGS = true;
    public static boolean CHECK_UPDATES = false;
    public static boolean NO_SCOPED_STORAGE = Build.VERSION.SDK_INT <= 29;
    
    public static int BUILD_VERSION = 2600;
    public static String BUILD_VERSION_STRING = "8.6.2";
    public static int APP_ID = BuildConfig.APP_ID; // Obtain your own APP_ID at https://core.telegram.org/api/obtaining_api_id
    public static String APP_HASH = BuildConfig.APP_HASH; // Obtain your own APP_HASH at https://core.telegram.org/api/obtaining_api_id

    public static String SMS_HASH = isStandaloneApp() ? "w0lkcmTZkKh" : (DEBUG_VERSION ? "O2P2z+/jBpJ" : "oLeq9AcOZkT");
    public static String PLAYSTORE_APP_URL = "https://play.google.com/store/apps/details?id=org.telegram.messenger";

    static {
        if (!DEBUG_PRIVATE_VERSION && ApplicationLoader.applicationContext != null) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Context.MODE_PRIVATE);
            LOGS_ENABLED = DEBUG_VERSION = sharedPreferences.getBoolean("logsEnabled", DEBUG_VERSION);
        }
    }

    public static boolean isStandaloneApp()  {
        return true;
    }

    public static boolean isBetaApp() {
        return BuildConfig.BUILD_TYPE.equals("debug");
    }
}
