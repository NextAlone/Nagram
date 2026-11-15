package org.telegram.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;

public class LauncherIconController {
    public static void tryFixLauncherIconIfNeeded() {
        for (LauncherIcon icon : LauncherIcon.values()) {
            if (isEnabled(icon)) {
                return;
            }
        }

        setIcon(LauncherIcon.DEFAULT);
    }

    public static boolean isEnabled(LauncherIcon icon) {
        Context ctx = ApplicationLoader.applicationContext;
        int i = ctx.getPackageManager().getComponentEnabledSetting(icon.getComponentName(ctx));
        return i == PackageManager.COMPONENT_ENABLED_STATE_ENABLED || i == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT && icon == LauncherIcon.DEFAULT;
    }

    public static void setIcon(LauncherIcon icon) {
        Context ctx = ApplicationLoader.applicationContext;
        PackageManager pm = ctx.getPackageManager();
        for (LauncherIcon i : LauncherIcon.values()) {
            pm.setComponentEnabledSetting(i.getComponentName(ctx), i == icon ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
        applySplashTheme(icon);
    }

    private static final String SPLASH_THEME_VERSION_KEY = "splashThemeAppliedVersion";

    public static LauncherIcon getCurrentIcon() {
        for (LauncherIcon icon : LauncherIcon.values()) {
            if (isEnabled(icon)) {
                return icon;
            }
        }
        return LauncherIcon.DEFAULT;
    }

    /**
     * The system persists the splash theme by name, so it only needs to be re-applied
     * after install or update (theme names may change between builds).
     */
    public static void applySplashThemeIfNeeded(LaunchActivity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (preferences.getInt(SPLASH_THEME_VERSION_KEY, 0) == BuildVars.BUILD_VERSION) return;
        if (setSplashTheme(activity, getCurrentIcon())) {
            preferences.edit().putInt(SPLASH_THEME_VERSION_KEY, BuildVars.BUILD_VERSION).apply();
        }
    }

    private static void applySplashTheme(LauncherIcon icon) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (setSplashTheme(LaunchActivity.instance, icon)) {
            preferences.edit().putInt(SPLASH_THEME_VERSION_KEY, BuildVars.BUILD_VERSION).apply();
        } else {
            // Retry from LaunchActivity on next start.
            preferences.edit().remove(SPLASH_THEME_VERSION_KEY).apply();
        }
    }

    private static boolean setSplashTheme(LaunchActivity activity, LauncherIcon icon) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || activity == null) return false;
        try {
            activity.getSplashScreen().setSplashScreenTheme(icon.splashTheme);
            return true;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public enum LauncherIcon {
        // DEFAULT keeps the manifest splash theme (animated Nagram icon).
        DEFAULT("DefaultIcon", R.mipmap.ic_launcher_nagram, R.mipmap.icon_background_nagram, R.string.AppIconDefault, Resources.ID_NULL),
        GOOGLE("GoogleIcon", R.mipmap.icon_background_google, R.mipmap.icon_foreground_google, R.string.AppIconGoogle, R.style.Theme_Nagram_Splash_Google),
        COLORFUL("ColorfulIcon", R.mipmap.icon_background_colorful, R.mipmap.icon_foreground_colorful, R.string.AppIconColorful, R.style.Theme_Nagram_Splash_Colorful),
        DARKGREEN("DarkGreenIcon", R.mipmap.icon_background_darkgreen, R.mipmap.icon_foreground_darkgreen, R.string.AppIconDarkGreen, R.style.Theme_Nagram_Splash_DarkGreen),
        NEON("NeonIcon", R.mipmap.icon_background_neon, R.mipmap.icon_foreground_neon, R.string.AppIconNeon, R.style.Theme_Nagram_Splash_Neon),
        NIELLO("NielloIcon", R.drawable.ic_launcher_nagram_round_niello_background, R.drawable.ic_launcher_nagram_round_niello_foreground, R.string.AppIconNiello, R.style.Theme_Nagram_Splash_Niello),
        BLUE("BlueIcon", R.color.nagram_block_round_background, R.drawable.ic_launcher_nagram_block_round_foreground, R.string.AppIconBlue, R.style.Theme_Nagram_Splash_Blue),
        DARKBLUE("DarkBlueIcon", R.color.nagram_round_blue_background, R.drawable.ic_launcher_nagram_round_blue_foreground, R.string.AppIconDarkBlue, R.style.Theme_Nagram_Splash_DarkBlue),
        BLURBLUE("BlurBlueIcon", R.drawable.ic_launcher_nagram_blue_background, R.drawable.ic_launcher_nagram_blue_foreground, R.string.AppIconBlurBlue, R.style.Theme_Nagram_Splash_BlurBlue),
        NOX("PremiumIcon", R.drawable.icon_3_background_sa, R.mipmap.icon_3_foreground_sa, R.string.AppIconPremium, R.style.Theme_Nagram_Splash_Telegram);

        public final String key;
        public final int background;
        public final int foreground;
        public final int title;
        public final boolean premium;
        public final int splashTheme;

        private ComponentName componentName;

        public ComponentName getComponentName(Context ctx) {
            if (componentName == null) {
                componentName = new ComponentName(ctx.getPackageName(), "org.telegram.messenger." + key);
            }
            return componentName;
        }

        LauncherIcon(String key, int background, int foreground, int title, int splashTheme) {
            this(key, background, foreground, title, false, splashTheme);
        }

        LauncherIcon(String key, int background, int foreground, int title, boolean premium, int splashTheme) {
            this.key = key;
            this.background = background;
            this.foreground = foreground;
            this.title = title;
            this.premium = premium;
            this.splashTheme = splashTheme;
        }

        public boolean isNekoX() {
            return this == DEFAULT;
        }
    }
}
