package xyz.nextalone.nagram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;

@SuppressLint("ApplySharedPref")
public class NaConfig {
    private static final Object sync = new Object();
    
    public static boolean forceAllowCopy = false;
    public static boolean disableChatActionSending = false;
    public static boolean hideGroupSticker = false;
    public static boolean showReReply = false;
    
    public static boolean showTextStrike = false;
    public static boolean showTextSpoiler = false;
    public static boolean showTextBold = false;
    public static boolean showTextItalic = false;
    public static boolean showTextMono = false;
    public static boolean showTextUnderline = false;
    public static boolean showTextCreateLink = false;
    public static boolean showTextCreateMention = false;
    public static boolean showTextRegular = false;
    private static boolean configLoaded;
    
    static {
        loadConfig();
    }
    
    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
                return;
            }
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
            forceAllowCopy = preferences.getBoolean("forceAllowCopy", false);
            disableChatActionSending = preferences.getBoolean("disableChatActionSending", false);
            hideGroupSticker = preferences.getBoolean("hideGroupSticker", false);
            showReReply = preferences.getBoolean("showReReply", false);
            showTextStrike = preferences.getBoolean("showTextStrike", true);
            showTextSpoiler = preferences.getBoolean("showTextSpoiler", true);
            showTextBold = preferences.getBoolean("showTextBold", true);
            showTextItalic = preferences.getBoolean("showTextItalic", true);
            showTextMono = preferences.getBoolean("showTextMono", true);
            showTextUnderline = preferences.getBoolean("showTextUnderline", true);
            showTextCreateLink = preferences.getBoolean("showTextCreateLink", true);
            showTextCreateMention = preferences.getBoolean("showTextCreateMention", true);
            showTextRegular = preferences.getBoolean("showTextRegular", true);
            configLoaded = true;
        }
    }
    public static void toggleForceAllowCopy() {
        forceAllowCopy = !forceAllowCopy;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("forceAllowCopy", forceAllowCopy);
        editor.commit();
    }
    
    public static void toggleDisableChatActionSending() {
        disableChatActionSending = !disableChatActionSending;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableChatActionSending", disableChatActionSending);
        editor.commit();
    }
    
    public static void toggleHideGroupSticker() {
        hideGroupSticker = !hideGroupSticker;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideGroupSticker", hideGroupSticker);
        editor.commit();
    }
    public static void toggleShowReReply() {
        showReReply = !showReReply;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showReReply", showReReply);
        editor.commit();
    }
    
    public static void toggleShowTextStrike() {
        showTextStrike = !showTextStrike;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTextStrike", showTextStrike);
        editor.commit();
    }
    
    public static void toggleShowTextSpoiler() {
        showTextSpoiler = !showTextSpoiler;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTextSpoiler", showTextSpoiler);
        editor.commit();
    }
    
    public static void toggleShowTextBold() {
        showTextBold = !showTextBold;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTextBold", showTextBold);
        editor.commit();
    }
    
    public static void toggleShowTextItalic() {
        showTextItalic = !showTextItalic;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTextItalic", showTextItalic);
        editor.commit();
    }
    
    public static void toggleShowTextMono() {
        showTextMono = !showTextMono;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTextMono", showTextMono);
        editor.commit();
    }
    
    public static void toggleShowTextUnderline() {
        showTextUnderline = !showTextUnderline;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTextUnderline", showTextUnderline);
        editor.commit();
    }
    
    public static void toggleShowTextCreateLink() {
        showTextCreateLink = !showTextCreateLink;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTextCreateLink", showTextCreateLink);
        editor.commit();
    }
    
    public static void toggleShowTextCreateMention() {
        showTextCreateMention = !showTextCreateMention;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTextCreateMention", showTextCreateMention);
        editor.commit();
    }
    
    public static void toggleShowTextRegular() {
        showTextRegular = !showTextRegular;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTextRegular", showTextRegular);
        editor.commit();
    }
}
