package org.telegram.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
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

    public static void updateMonetIcon() {
        if (isEnabled(LauncherIcon.MONET)) {
            setIcon(LauncherIcon.DEFAULT);
            setIcon(LauncherIcon.MONET);
        }
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
    }

    public enum LauncherIcon {
        DEFAULT("DefaultIcon", BuildVars.isBetaApp() ? R.color.ic_background_beta : R.color.ic_background, R.drawable.ic_foreground, R.string.AppIconDefault),
        REVERTED("RevertedIcon", BuildVars.isBetaApp() ? R.color.ic_background : R.color.ic_background_beta, R.drawable.ic_foreground, BuildVars.isBetaApp() ? R.string.AppIconRelease : R.string.AppIconBeta),
        MONET("MonetIcon", R.color.ic_background_monet, R.drawable.ic_foreground_monet, R.string.AppIconMonet),
        INVERTED("InvertedIcon", BuildVars.isBetaApp() ? R.mipmap.ic_background_beta_inverted : R.mipmap.ic_background_inverted, BuildVars.isBetaApp() ? R.mipmap.ic_foreground_beta_inverted : R.mipmap.ic_foreground_inverted, R.string.AppIconInverted),
        GRADIENT("GradientIcon", R.mipmap.ic_background_gradient, R.drawable.ic_foreground, R.string.AppIconGradient),
        PIXEL("PixelIcon", R.mipmap.ic_background_pixel, R.mipmap.ic_foreground_pixel, R.string.AppIconPixel),
        GOOGLE("GoogleIcon", R.mipmap.ic_background_google, R.mipmap.ic_foreground_google, R.string.AppIconGoogle),
        RED("RedIcon", R.mipmap.ic_background_red, R.mipmap.ic_foreground_red, R.string.AppIconRed);

        public final String key;
        public final int background;
        public final int foreground;
        public final int title;
        public final boolean premium;

        private ComponentName componentName;

        public ComponentName getComponentName(Context ctx) {
            if (componentName == null) {
                componentName = new ComponentName(ctx.getPackageName(), "com.exteragram.messenger." + key);
            }
            return componentName;
        }

        LauncherIcon(String key, int background, int foreground, int title) {
            this(key, background, foreground, title, false);
        }

        LauncherIcon(String key, int background, int foreground, int title, boolean premium) {
            this.key = key;
            this.background = background;
            this.foreground = foreground;
            this.title = title;
            this.premium = premium;
        }
    }
}
