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
    // Removed icons whose aliases stay in the manifest (showing the default icon) for migration.
    private static final String[] REMOVED_ICON_KEYS = {"NeonIcon", "PremiumIcon"};

    public static void tryFixLauncherIconIfNeeded() {
        if (migrateRemovedIcons()) {
            return;
        }
        for (LauncherIcon icon : LauncherIcon.values()) {
            if (isEnabled(icon)) {
                return;
            }
        }

        setIcon(LauncherIcon.DEFAULT);
    }

    private static boolean migrateRemovedIcons() {
        Context ctx = ApplicationLoader.applicationContext;
        PackageManager pm = ctx.getPackageManager();
        boolean migrated = false;
        for (String key : REMOVED_ICON_KEYS) {
            ComponentName component = new ComponentName(ctx.getPackageName(), "org.telegram.messenger." + key);
            if (pm.getComponentEnabledSetting(component) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                continue;
            }
            if (!migrated) {
                // Enable the default icon before disabling the old alias so a launcher entry always exists.
                setIcon(LauncherIcon.DEFAULT);
                migrated = true;
            }
            pm.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
        return migrated;
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
        // Flat icons from the Nagram Desktop SVGs. DEFAULT keeps the manifest splash theme (animated Nagram icon).
        DEFAULT("DefaultIcon", R.drawable.ic_launcher_nagram_background, R.drawable.ic_launcher_nagram_foreground, R.string.AppIconDefault, Resources.ID_NULL),
        BLURBLUE("BlurBlueIcon", R.drawable.ic_launcher_nagram_blurblue_background, R.drawable.ic_launcher_nagram_blurblue_foreground, R.string.AppIconBlurBlue, R.style.Theme_Nagram_Splash_BlurBlue),
        GOOGLE("GoogleIcon", R.drawable.ic_launcher_nagram_google_background, R.drawable.ic_launcher_nagram_google_foreground, R.string.AppIconGoogle, R.style.Theme_Nagram_Splash_Google),
        COLORFUL("ColorfulIcon", R.drawable.ic_launcher_nagram_colorful_background, R.drawable.ic_launcher_nagram_colorful_foreground, R.string.AppIconColorful, R.style.Theme_Nagram_Splash_Colorful),
        NIELLO("NielloIcon", R.drawable.ic_launcher_nagram_niello_background, R.drawable.ic_launcher_nagram_niello_foreground, R.string.AppIconNiello, R.style.Theme_Nagram_Splash_Niello),
        BLUE("BlueIcon", R.drawable.ic_launcher_nagram_blue_background, R.drawable.ic_launcher_nagram_blue_foreground, R.string.AppIconBlue, R.style.Theme_Nagram_Splash_Blue),
        DARKBLUE("DarkBlueIcon", R.drawable.ic_launcher_nagram_darkblue_background, R.drawable.ic_launcher_nagram_darkblue_foreground, R.string.AppIconDarkBlue, R.style.Theme_Nagram_Splash_DarkBlue),
        OUTLINE("OutlineIcon", R.drawable.ic_launcher_nagram_outline_background, R.drawable.ic_launcher_nagram_outline_foreground, R.string.AppIconOutline, Resources.ID_NULL),
        BLOCKGOOGLE("BlockGoogleIcon", R.drawable.ic_launcher_nagram_blockgoogle_background, R.drawable.ic_launcher_nagram_blockgoogle_foreground, R.string.AppIconBlockGoogle, R.style.Theme_Nagram_Splash_Google),
        // Glass icons rendered from the iOS Icon Composer sources ship as a single full-bleed layer, so they
        // have no separate foreground (Resources.ID_NULL).
        GLASS_DEFAULT("GlassDefaultIcon", R.mipmap.ic_launcher_nagram_glass_classic_background, Resources.ID_NULL, R.string.AppIconGlassDefault, Resources.ID_NULL),
        GLASS_BLURBLUE("GlassBlurBlueIcon", R.mipmap.ic_launcher_nagram_glass_blue_background, Resources.ID_NULL, R.string.AppIconGlassBlurBlue, R.style.Theme_Nagram_Splash_BlurBlue),
        GLASS_GOOGLE("GlassGoogleIcon", R.mipmap.ic_launcher_nagram_glass_colorful_background, Resources.ID_NULL, R.string.AppIconGlassGoogle, R.style.Theme_Nagram_Splash_Google),
        GLASS_DARKGREEN("DarkGreenIcon", R.mipmap.ic_launcher_nagram_glass_cyan_background, Resources.ID_NULL, R.string.AppIconGlassDarkGreen, R.style.Theme_Nagram_Splash_DarkGreen),
        GLASS_NIELLO("GlassNielloIcon", R.mipmap.ic_launcher_nagram_glass_niello_background, Resources.ID_NULL, R.string.AppIconGlassNiello, R.style.Theme_Nagram_Splash_Niello),
        GLASS_BLUE("GlassBlueIcon", R.mipmap.ic_launcher_nagram_glass_block_background, Resources.ID_NULL, R.string.AppIconGlassBlue, R.style.Theme_Nagram_Splash_Blue),
        GLASS_DARKBLUE("GlassDarkBlueIcon", R.mipmap.ic_launcher_nagram_glass_blockblue_background, Resources.ID_NULL, R.string.AppIconGlassDarkBlue, R.style.Theme_Nagram_Splash_DarkBlue),
        BLACK("BlackIcon", R.mipmap.ic_launcher_nagram_glass_black_background, Resources.ID_NULL, R.string.AppIconBlack, Resources.ID_NULL),
        BLOCKBLACK("BlockBlackIcon", R.mipmap.ic_launcher_nagram_glass_blockblack_background, Resources.ID_NULL, R.string.AppIconBlockBlack, Resources.ID_NULL),
        BLOCKPURPLE("BlockPurpleIcon", R.mipmap.ic_launcher_nagram_glass_blockpurple_background, Resources.ID_NULL, R.string.AppIconBlockPurple, Resources.ID_NULL);

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
    }
}
