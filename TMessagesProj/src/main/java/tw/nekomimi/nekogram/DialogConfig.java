package tw.nekomimi.nekogram;

import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;

import xyz.nextalone.nagram.NaConfig;

public class DialogConfig {
    private static final SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekodialogconfig", Context.MODE_PRIVATE);

    public static String getAutoTranslateKey(long dialogId, long topicId) {
        return "autoTranslate_" + dialogId + (topicId != 0 ? "_" + topicId : "");
    }

    public static boolean isAutoTranslateEnable(long dialogId, long topicId) {
        return preferences.getBoolean(getAutoTranslateKey(dialogId, topicId), NaConfig.INSTANCE.getAutoTranslate().Bool());
    }

    public static boolean hasAutoTranslateConfig(long dialogId, long topicId) {
        return preferences.contains(getAutoTranslateKey(dialogId, topicId));
    }

    public static void setAutoTranslateEnable(long dialogId, long topicId, boolean enable) {
        preferences.edit().putBoolean(getAutoTranslateKey(dialogId, topicId), enable).apply();
    }

    public static void removeAutoTranslateConfig(long dialogId, long topicId) {
        preferences.edit().remove(getAutoTranslateKey(dialogId, topicId)).apply();
    }

    public static String getCustomForumTabsKey(long dialogId) {
        return "customForumTabs_" + dialogId;
    }

    public static boolean isCustomForumTabsEnable(long dialogId) {
        return preferences.getBoolean(getCustomForumTabsKey(dialogId), false);
    }

    public static boolean hasCustomForumTabsConfig(long dialogId) {
        return preferences.contains(getCustomForumTabsKey(dialogId));
    }

    public static void setCustomForumTabsEnable(long dialogId, boolean enable) {
        preferences.edit().putBoolean(getCustomForumTabsKey(dialogId), enable).apply();
    }

    public static void removeCustomForumTabsConfig(long dialogId) {
        preferences.edit().remove(getCustomForumTabsKey(dialogId)).apply();
    }
}
