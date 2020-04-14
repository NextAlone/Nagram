package tw.nekomimi.nekogram;

import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;

import java.util.LinkedList;

import tw.nekomimi.nekogram.database.NitritesKt;

public class NekoXConfig {

    public static String FAQ_URL = "https://telegra.ph/NekoX-FAQ-03-31";

    public static boolean developerModeEntrance;
    public static boolean developerMode;

    public static boolean disableFlagSecure;
    public static boolean disableScreenshotDetection;

    public static boolean showTestBackend;
    public static boolean showBotLogin;

    private static SharedPreferences preferences;

    static {

        preferences = NitritesKt.openMainSharedPreference("nekox_config");

        developerMode = preferences.getBoolean("developer_mode", false);

        disableFlagSecure = preferences.getBoolean("disable_flag_secure", false);
        disableScreenshotDetection = preferences.getBoolean("disable_screenshot_detection", false);

        showTestBackend = preferences.getBoolean("show_test_backend", false);
        showBotLogin = preferences.getBoolean("show_bot_login", false);

    }

    public static void toggleDeveloperMode() {

        preferences.edit().putBoolean("developer_mode", developerMode = !developerMode).apply();

    }

    public static void toggleDisableFlagSecure() {

        preferences.edit().putBoolean("disable_flag_secure", disableFlagSecure = !disableFlagSecure).apply();

    }

    public static void toggleDisableScreenshotDetection() {

        preferences.edit().putBoolean("disable_screenshot_detection", disableScreenshotDetection = !disableScreenshotDetection).apply();

    }

    public static void toggleShowTestBackend() {

        preferences.edit().putBoolean("show_test_backend", showTestBackend = !showTestBackend).apply();

    }

    public static void toggleShowBotLogin() {

        preferences.edit().putBoolean("show_bot_login", showBotLogin = !showBotLogin).apply();

    }


}