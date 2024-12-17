// all credits to @Nekogram

package com.exteragram.messenger.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PatternMatcher;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.graphics.ColorUtils;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.LauncherIconController;

import java.io.File;
import java.util.LinkedHashMap;

@RequiresApi(api = Build.VERSION_CODES.S)
public class MonetUtils {

    private static final LinkedHashMap<String, Integer> ids = new LinkedHashMap<>();
    private static final String ACTION_OVERLAY_CHANGED = "android.intent.action.OVERLAY_CHANGED";
    private static final OverlayChangeReceiver overlayChangeReceiver = new OverlayChangeReceiver();

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ids.put("mBlack", R.color.black);
            ids.put("mWhite", R.color.white);

            //static m colors
            ids.put("mRed200", R.color.mRed200);
            ids.put("mRed500", R.color.mRed500);
            ids.put("mRed800", R.color.mRed800);
            ids.put("mGreen200", R.color.mGreen200);
            ids.put("mGreen500", R.color.mGreen500);
            ids.put("mGreen800", R.color.mGreen800);

            //dynamic monet engine colors
            ids.put("a1_10", android.R.color.system_accent1_10);
            ids.put("a1_50", android.R.color.system_accent1_50);
            ids.put("a1_100", android.R.color.system_accent1_100);
            ids.put("a1_200", android.R.color.system_accent1_200);
            ids.put("a1_300", android.R.color.system_accent1_300);
            ids.put("a1_400", android.R.color.system_accent1_400);
            ids.put("a1_500", android.R.color.system_accent1_500);
            ids.put("a1_600", android.R.color.system_accent1_600);
            ids.put("a1_700", android.R.color.system_accent1_700);
            ids.put("a1_800", android.R.color.system_accent1_800);
            ids.put("a1_900", android.R.color.system_accent1_900);
            ids.put("a2_10", android.R.color.system_accent2_10);
            ids.put("a2_50", android.R.color.system_accent2_50);
            ids.put("a2_100", android.R.color.system_accent2_100);
            ids.put("a2_200", android.R.color.system_accent2_200);
            ids.put("a2_300", android.R.color.system_accent2_300);
            ids.put("a2_400", android.R.color.system_accent2_400);
            ids.put("a2_500", android.R.color.system_accent2_500);
            ids.put("a2_600", android.R.color.system_accent2_600);
            ids.put("a2_700", android.R.color.system_accent2_700);
            ids.put("a2_800", android.R.color.system_accent2_800);
            ids.put("a2_900", android.R.color.system_accent2_900);
            ids.put("a3_10", android.R.color.system_accent3_10);
            ids.put("a3_50", android.R.color.system_accent3_50);
            ids.put("a3_100", android.R.color.system_accent3_100);
            ids.put("a3_200", android.R.color.system_accent3_200);
            ids.put("a3_300", android.R.color.system_accent3_300);
            ids.put("a3_400", android.R.color.system_accent3_400);
            ids.put("a3_500", android.R.color.system_accent3_500);
            ids.put("a3_600", android.R.color.system_accent3_600);
            ids.put("a3_700", android.R.color.system_accent3_700);
            ids.put("a3_800", android.R.color.system_accent3_800);
            ids.put("a3_900", android.R.color.system_accent3_900);
            ids.put("n1_10", android.R.color.system_neutral1_10);
            ids.put("n1_50", android.R.color.system_neutral1_50);
            ids.put("n1_100", android.R.color.system_neutral1_100);
            ids.put("n1_200", android.R.color.system_neutral1_200);
            ids.put("n1_300", android.R.color.system_neutral1_300);
            ids.put("n1_400", android.R.color.system_neutral1_400);
            ids.put("n1_500", android.R.color.system_neutral1_500);
            ids.put("n1_600", android.R.color.system_neutral1_600);
            ids.put("n1_700", android.R.color.system_neutral1_700);
            ids.put("n1_800", android.R.color.system_neutral1_800);
            ids.put("n1_900", android.R.color.system_neutral1_900);
            ids.put("n2_10", android.R.color.system_neutral2_10);
            ids.put("n2_50", android.R.color.system_neutral2_50);
            ids.put("n2_100", android.R.color.system_neutral2_100);
            ids.put("n2_200", android.R.color.system_neutral2_200);
            ids.put("n2_300", android.R.color.system_neutral2_300);
            ids.put("n2_400", android.R.color.system_neutral2_400);
            ids.put("n2_500", android.R.color.system_neutral2_500);
            ids.put("n2_600", android.R.color.system_neutral2_600);
            ids.put("n2_700", android.R.color.system_neutral2_700);
            ids.put("n2_800", android.R.color.system_neutral2_800);
            ids.put("n2_900", android.R.color.system_neutral2_900);
        }
    }

    public static int getColor(String color) {
        try {
            int alpha = 100;
            if (color.matches(".*\\(.*\\).*")) {
                alpha = Integer.parseInt(color.substring(color.indexOf("(") + 1, color.indexOf(")")));
                color = color.substring(0, color.indexOf("("));
            }
            int id = ids.getOrDefault(color, 0);
            int c = ApplicationLoader.applicationContext.getColor(id);
            return ColorUtils.setAlphaComponent(c, (int) (alpha * 2.55f));
        } catch (Exception e) {
            Log.e("Theme", "Error loading color " + color);
            e.printStackTrace();
            return 0;
        }
    }

    private static class OverlayChangeReceiver extends BroadcastReceiver {

        public void register(Context context) {
            IntentFilter packageFilter = new IntentFilter(ACTION_OVERLAY_CHANGED);
            packageFilter.addDataScheme("package");
            packageFilter.addDataSchemeSpecificPart("android", PatternMatcher.PATTERN_LITERAL);
            context.registerReceiver(this, packageFilter);
        }

        public void unregister(Context context) {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_OVERLAY_CHANGED.equals(intent.getAction())) {
                if (Theme.getActiveTheme().isMonet()) {
                    String themeToReset = "monet_" + (Theme.getActiveTheme().isDark() ? "dark" : "light") + ".attheme";
                    File theme = new File(ApplicationLoader.getFilesDirFixed(), themeToReset);
                    if (theme.exists()) {
                        theme.delete();
                    }
                    Theme.applyTheme(Theme.getActiveTheme());
                }
            }
        }
    }

    public static void registerReceiver(Context context) {
        overlayChangeReceiver.register(context);
    }

    public static void unregisterReceiver(Context context) {
        try {
            overlayChangeReceiver.unregister(context);
        } catch (Exception ignored) {
        }
    }
}
