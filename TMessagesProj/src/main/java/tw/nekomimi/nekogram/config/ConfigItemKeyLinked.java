package tw.nekomimi.nekogram.config;

import android.content.SharedPreferences;

import org.telegram.messenger.FileLog;

import tw.nekomimi.nekogram.NekoConfig;

public class ConfigItemKeyLinked extends ConfigItem {
    public final ConfigItem keyLinked;
    public final int flag;

    public ConfigItemKeyLinked(String key, ConfigItem keyLinked, int flag, Object defaultValue) {
        super(key, ConfigItem.configTypeBoolLinkInt, defaultValue);
        this.keyLinked = keyLinked;
        this.flag = (int) Math.pow(2, flag);
    }

    public ConfigItem getKeyLinked() {
        return keyLinked;
    }

    public void changedFromKeyLinked(int currentConfig) {
        changed((currentConfig & flag) != 0);
    }

    public boolean toggleConfigBool() {
        value = !this.Bool();
        saveConfig();
        return this.Bool();//返回toggle之后的
    }

    public void setConfigBool(boolean v) {
        value = v;
        saveConfig();
    }

    public void saveConfig() {
        synchronized (NekoConfig.sync) {
            try {
                SharedPreferences.Editor editor = NekoConfig.preferences.edit();

                if (this.type == configTypeBoolLinkInt) {
                    int currentConfig = this.keyLinked.Int();
                    int newConfig;
                    if ((boolean) this.value) {
                        newConfig = currentConfig | flag; // 开启对应位
                    } else {
                        newConfig = currentConfig & ~flag; // 关闭对应位
                    }
                    this.keyLinked.changed(newConfig);
                    editor.putInt(this.keyLinked.getKey(), newConfig);
                }
                editor.apply();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public Object checkConfigFromString(String value) {
        try {
            if (type == configTypeBoolLinkInt) {
                return Boolean.parseBoolean(value);
            }
            return null;
        } catch (Exception ignored) {}
        return null;
    }
}
