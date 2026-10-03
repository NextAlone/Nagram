package xyz.nextalone.nagram.helper;

import java.util.Locale;

public class BackupFileHelper {

    public static final String STICKERS_SUFFIX = ".nagram-stickers.json";
    public static final String SETTINGS_SUFFIX = ".nagram-settings.json";

    // backups exported by NekoX and older Nagram builds
    private static final String LEGACY_STICKERS_SUFFIX = ".nekox-stickers.json";
    private static final String LEGACY_SETTINGS_SUFFIX = ".nekox-settings.json";

    public static boolean isStickersFile(String name) {
        return endsWith(name, STICKERS_SUFFIX) || endsWith(name, LEGACY_STICKERS_SUFFIX);
    }

    public static boolean isSettingsFile(String name) {
        return endsWith(name, SETTINGS_SUFFIX) || endsWith(name, LEGACY_SETTINGS_SUFFIX);
    }

    private static boolean endsWith(String name, String suffix) {
        return name != null && name.toLowerCase(Locale.ROOT).endsWith(suffix);
    }
}
