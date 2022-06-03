package top.qwq2333.nullgram.helpers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.LaunchActivity;

import java.util.Objects;

import top.qwq2333.nullgram.utils.LogUtils;
import top.qwq2333.nullgram.utils.Utils;

public class PasscodeHelper {
    private static final SharedPreferences preferences =
        ApplicationLoader.applicationContext.getSharedPreferences("passcodeConfig",
            Activity.MODE_PRIVATE);

    public static boolean checkPasscode(Activity activity, String passcode) {
        if (hasPasscodeForAccount(Integer.MAX_VALUE)) {
            String passcodeHash = preferences.getString("passcodeHash" + Integer.MAX_VALUE, "");
            String passcodeSaltString = preferences.getString("passcodeSalt" + Integer.MAX_VALUE, "");
            if (checkPasscodeHash(passcode, passcodeHash, passcodeSaltString)) {
                for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
                    if (UserConfig.getInstance(a).isClientActivated() && isAccountAllowPanic(a)) {
                        MessagesController.getInstance(a).performLogout(1);
                    }
                }
                return false;
            }
        }
        for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
            if (UserConfig.getInstance(a).isClientActivated() && hasPasscodeForAccount(a)) {
                String passcodeHash = preferences.getString("passcodeHash" + a, "");
                String passcodeSaltString = preferences.getString("passcodeSalt" + a, "");
                if (checkPasscodeHash(passcode, passcodeHash, passcodeSaltString)) {
                    if (activity instanceof LaunchActivity) {
                        LaunchActivity launchActivity = (LaunchActivity) activity;
                        launchActivity.switchToAccount(a, true);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkPasscodeHash(String inputPasscode, String passcodeHash, String passcodeSaltString) {
        try {
            return Objects.equals(passcodeHash, Utils.getSecurePassword(inputPasscode, passcodeSaltString));
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return false;
    }

    public static void removePasscodeForAccount(int account) {
        preferences.edit()
            .remove("passcodeHash" + account)
            .remove("passcodeSalt" + account)
            .remove("hide" + account)
            .apply();
    }

    public static boolean isAccountAllowPanic(int account) {
        return preferences.getBoolean("allowPanic" + account, true);
    }

    public static boolean isAccountHidden(int account) {
        return hasPasscodeForAccount(account) && preferences.getBoolean("hide" + account, false);
    }

    public static void setAccountAllowPanic(int account, boolean panic) {
        preferences.edit()
            .putBoolean("allowPanic" + account, panic)
            .apply();
    }

    public static void setHideAccount(int account, boolean hide) {
        preferences.edit()
            .putBoolean("hide" + account, hide)
            .apply();
    }

    public static void setPasscodeForAccount(String firstPassword, int account) {
        try {
            byte[] passcodeSalt = Utils.getSalt();
            preferences.edit()
                .putString("passcodeHash" + account, Utils.getSecurePassword(firstPassword, Base64.encodeToString(passcodeSalt, Base64.DEFAULT)))
                .putString("passcodeSalt" + account, Base64.encodeToString(passcodeSalt, Base64.DEFAULT))
                .apply();
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    public static boolean hasPasscodeForAccount(int account) {
        return preferences.contains("passcodeHash" + account) && preferences.contains("passcodeSalt" + account);
    }

    public static boolean hasPanicCode() {
        return hasPasscodeForAccount(Integer.MAX_VALUE);
    }

    public static String getSettingsKey() {
        var settingsHash = preferences.getString("settingsHash", "");
        if (!TextUtils.isEmpty(settingsHash)) {
            return settingsHash;
        }
        var hash = Base64.encodeToString(Utils.getSalt(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
        preferences.edit().putString("settingsHash", hash).apply();
        return hash;
    }

    public static boolean isSettingsHidden() {
        return preferences.getBoolean("hideSettings", false);
    }

    public static void setHideSettings(boolean hide) {
        preferences.edit()
            .putBoolean("hideSettings", hide)
            .apply();
    }

    public static boolean isEnabled() {
        return preferences.getAll().size() != 0;
    }

    public static void clearAll() {
        preferences.edit().clear().apply();
    }

}
