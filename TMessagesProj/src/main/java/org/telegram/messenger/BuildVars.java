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
    public static int BUILD_VERSION = 2702;
    public static String BUILD_VERSION_STRING = "8.8.2";
    public static int APP_ID = 4;
    public static String APP_HASH = "014b35b6184100b085b0d0572f9b5103";

    public static String SMS_HASH = isStandaloneApp() ? "w0lkcmTZkKh" : (DEBUG_VERSION ? "O2P2z+/jBpJ" : "oLeq9AcOZkT");
    public static String PLAYSTORE_APP_URL = "https://play.google.com/store/apps/details?id=org.telegram.messenger";

    // You can use this flag to disable Google Play Billing (If you're making fork and want it to be in Google Play)
    public static boolean IS_BILLING_UNAVAILABLE = true;

    static {
        // Takes from build.config :shrug:
        BUILD_VERSION = BuildConfig.VERSION_CODE;
        BUILD_VERSION_STRING = BuildConfig.VERSION_NAME;

        APP_ID = BuildConfig.APP_ID; // Obtain your own APP_ID at https://core.telegram.org/api/obtaining_api_id
        APP_HASH = BuildConfig.APP_HASH; // Obtain your own APP_HASH at https://core.telegram.org/api/obtaining_api_id
        SMS_HASH = BuildConfig.SMS_HASH; // Obtain your own SMS_HASH with https://bit.ly/3rxcuTd

        if (ApplicationLoader.applicationContext != null) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Context.MODE_PRIVATE);
            LOGS_ENABLED = DEBUG_VERSION = sharedPreferences.getBoolean("logsEnabled", DEBUG_VERSION);
        }
    }

    public static boolean useInvoiceBilling() {
        return DEBUG_VERSION || isStandaloneApp() || isBetaApp();
    }

    public static boolean isStandaloneApp() {
        return true;
    }

    public static boolean isBetaApp() {
        return BuildConfig.BUILD_TYPE.equals("debug");
    }
}
