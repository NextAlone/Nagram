package tw.nekomimi.nekogram;

import android.content.SharedPreferences;

import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import cn.hutool.core.util.ArrayUtil;
import tw.nekomimi.nekogram.database.NitritesKt;

public class NekoXConfig {

    //  public static String FAQ_URL = "https://telegra.ph/NekoX-FAQ-03-31";
    public static String FAQ_URL = "https://github.com/NekoX-Dev/NekoX#faq";
    public static long[] officialChats = {
            1305127566, // NekoX Updates
            1151172683, // NekoX Chat
            1299578049, // NekoX Chat Channel
            1137038259, // NekoX APKs
    };

    public static long[] developers = {
            896711046, // nekohasekai
            380570774, // Haruhi
    };

    public static SharedPreferences preferences = NitritesKt.openMainSharedPreference("nekox_config");

    public static boolean developerMode = preferences.getBoolean("developer_mode", false);

    public static boolean disableFlagSecure = preferences.getBoolean("disable_flag_secure", false);
    public static boolean disableScreenshotDetection = preferences.getBoolean("disable_screenshot_detection", false);

    public static boolean disableStatusUpdate = preferences.getBoolean("disable_status_update", false);
    public static boolean keepOnlineStatus = preferences.getBoolean("keepOnlineStatus", false);

    public static int autoUpdateReleaseChannel = preferences.getInt("autoUpdateReleaseChannel", 2);
    public static String ignoredUpdateTag = preferences.getString("ignoredUpdateTag", "");
    public static long nextUpdateCheck = preferences.getLong("nextUpdateCheckTimestamp", 0);


    public static void toggleDeveloperMode() {
        preferences.edit().putBoolean("developer_mode", developerMode = !developerMode).apply();
        if (!developerMode) {
            preferences.edit()
                    .putBoolean("disable_flag_secure", disableFlagSecure = false)
                    .putBoolean("disable_screenshot_detection", disableScreenshotDetection = false)
                    .putBoolean("disable_status_update", disableStatusUpdate = false)
                    .apply();
        }
    }

    public static void toggleDisableFlagSecure() {
        preferences.edit().putBoolean("disable_flag_secure", disableFlagSecure = !disableFlagSecure).apply();
    }

    public static void toggleDisableScreenshotDetection() {
        preferences.edit().putBoolean("disable_screenshot_detection", disableScreenshotDetection = !disableScreenshotDetection).apply();
    }

    public static int customApi = preferences.getInt("custom_api", 0);
    public static int customAppId = preferences.getInt("custom_app_id", 0);
    public static String customAppHash = preferences.getString("custom_app_hash", "");

    public static int currentAppId() {
        switch (customApi) {
            case 0:
                return BuildConfig.APP_ID;
            case 1:
                return BuildVars.OFFICAL_APP_ID;
            case 2:
                return BuildVars.TGX_APP_ID;
            default:
                return customAppId;
        }
    }

    public static String currentAppHash() {
        switch (customApi) {
            case 0:
                return BuildConfig.APP_HASH;
            case 1:
                return BuildVars.OFFICAL_APP_HASH;
            case 2:
                return BuildVars.TGX_APP_HASH;
            default:
                return customAppHash;
        }
    }

    public static void saveCustomApi() {
        preferences.edit()
                .putInt("custom_api", customApi)
                .putInt("custom_app_id", customAppId)
                .putString("custom_app_hash", customAppHash)
                .apply();
    }

    public static void toggleDisableStatusUpdate() {
        preferences.edit().putBoolean("disable_status_update", disableStatusUpdate = !disableStatusUpdate).apply();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.updateUserStatus, (Object) null);
    }

    public static void toggleKeepOnlineStatus() {
        preferences.edit().putBoolean("keepOnlineStatus", keepOnlineStatus = !keepOnlineStatus).apply();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.updateUserStatus, (Object) null);
    }

    public static void setAutoUpdateReleaseChannel(int channel) {
        preferences.edit().putInt("autoUpdateReleaseChannel", autoUpdateReleaseChannel = channel).apply();
    }

    public static void setIgnoredUpdateTag(String ignored) {
        preferences.edit().putString("ignoredUpdateTag", ignoredUpdateTag = ignored).apply();
    }

    public static void setNextUpdateCheck(long timestamp) {
        preferences.edit().putLong("nextUpdateCheckTimestamp",  nextUpdateCheck = timestamp).apply();
    }

    public static boolean showCensoredFeatures() {
        long myId = UserConfig.getInstance(UserConfig.selectedAccount).clientUserId;
        return NekoXConfig.developerMode || NekoXConfig.customApi > 0 || ArrayUtil.contains(NekoXConfig.developers, myId);
    }
}