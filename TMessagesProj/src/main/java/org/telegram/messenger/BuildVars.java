/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

@SuppressWarnings("ConstantConditions")
public class BuildVars {

    public static boolean DEBUG_VERSION = BuildConfig.BUILD_TYPE.equals("debug");
    public static boolean DEBUG_PRIVATE_VERSION = DEBUG_VERSION;
    public static boolean LOGS_ENABLED;
    public static boolean USE_CLOUD_STRINGS = true;

    public static int BUILD_VERSION;
    public static String BUILD_VERSION_STRING;

    public static int OFFICAL_APP_ID = 4;
    public static String OFFICAL_APP_HASH = "014b35b6184100b085b0d0572f9b5103";

    public static int TGX_APP_ID = 21724;
    public static String TGX_APP_HASH = "3e0cb5efcd52300aec5994fdfc5bdc16";

    public static boolean isUnknown = !BuildConfig.BUILD_TYPE.startsWith("release");
    public static boolean isMini = "mini".equals(BuildConfig.FLAVOR);

    static {

        try {
            PackageInfo info = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            BUILD_VERSION = info.versionCode;
            BUILD_VERSION_STRING = info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            BUILD_VERSION = BuildConfig.VERSION_CODE;
            BUILD_VERSION_STRING = BuildConfig.VERSION_NAME;
        }

        if (ApplicationLoader.applicationContext != null) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Context.MODE_PRIVATE);
            LOGS_ENABLED = sharedPreferences.getBoolean("logsEnabled", LOGS_ENABLED);
        }
    }
}
