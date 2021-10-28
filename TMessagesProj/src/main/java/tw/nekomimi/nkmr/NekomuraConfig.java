package tw.nekomimi.nkmr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import android.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SuppressLint("ApplySharedPref")
public class NekomuraConfig {
    private static final int configTypeBool = 0;
    private static final int configTypeInt = 1;
    private static final int configTypeString = 2;
    private static final int configTypeSetInt = 3;
    private static final int configTypeMapIntInt = 4;

    public static class ConfigItem {
        private String key;
        private int type;

        private Object defaultValue;
        private Object value;
        private boolean forceDefault;//针对某些人的垃圾选项，应该不允许改变默认值

        private ConfigItem(String k, int t, Object d) {
            key = k;
            type = t;
            defaultValue = d;
        }

        public String getKey() {
            return key;
        }

        // 读配置

        public boolean Bool() {
            return (boolean) value;
        }

        public int Int() {
            return (int) value;
        }

        public String String() {
            return value.toString();
        }

        public HashSet<Integer> SetInt() {
            return (HashSet<Integer>) value;
        }

        public HashMap<Integer, Integer> MapIntInt() {
            return (HashMap<Integer, Integer>) value;
        }

        public boolean SetIntContains(Integer v) {
            return ((HashSet<Integer>) value).contains(v);
        }


        public void changed(Object o) {
            value = o;
        }

        //写配置
        //这里没有检查类型哦

        public boolean toggleConfigBool() {
            value = !this.Bool();
            if (forceDefault) {
                value = defaultValue;
            }
            saveConfig();
            return this.Bool();//返回toggle之后的
        }

        public void setConfigBool(boolean v) {
            value = v;
            if (forceDefault) {
                value = defaultValue;
            }
            saveConfig();
        }

        public void setConfigInt(int v) {
            if (forceDefault) {
                value = defaultValue;
            }
            value = v;
            saveConfig();
        }

        public void setConfigString(String v) {
            value = v;
            if (forceDefault) {
                value = defaultValue;
            }
            saveConfig();
        }

        public void setConfigSetInt(HashSet<Integer> v) {
            value = v;
            if (forceDefault) {
                value = defaultValue;
            }
            saveConfig();
        }

        public void setConfigMapInt(HashMap<Integer, Integer> v) {
            value = v;
            saveConfig();
        }
    }

    private static boolean configLoaded;
    private static final Object sync = new Object();
    private static ArrayList<ConfigItem> configs = new ArrayList<>();

    // Configs
    public static ConfigItem largeAvatarInDrawer = addConfig("largeAvatarInDrawer", configTypeInt, 0); // 0:TG Default 1:NekoX Default 2:Large Avatar
    //TODO NekoConfig 那个 useAvatar 还没有迁移过来，所以只实现了 largeAvatarInDrawer=2 的情况。。。
    public static ConfigItem unreadBadgeOnBackButton = addConfig("unreadBadgeOnBackButton", configTypeBool, false);


    static {
        loadConfig(false);
    }

    public static ConfigItem addConfig(String k, int t, Object d) {
        ConfigItem a = new ConfigItem(k, t, d);
        configs.add(a);
        return a;
    }

    public static ConfigItem findOne(String key) {
        for (int i = 0; i < configs.size(); i++) {
            ConfigItem o = configs.get(i);
            if (key.equals(o.key)) {
                return o;
            }
        }
        return null;
    }

    public static void loadConfig(boolean force) {
        synchronized (sync) {
            if (configLoaded && !force) {
                return;
            }
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nkmrcfg", Activity.MODE_PRIVATE);
            for (int i = 0; i < configs.size(); i++) {
                ConfigItem o = configs.get(i);

                if (o.forceDefault) {
                    o.value = o.defaultValue;
                    continue;
                }

                if (o.type == configTypeBool) {
                    o.value = preferences.getBoolean(o.key, (boolean) o.defaultValue);
                }
                if (o.type == configTypeInt) {
                    o.value = preferences.getInt(o.key, (int) o.defaultValue);
                }
                if (o.type == configTypeString) {
                    o.value = preferences.getString(o.key, (String) o.defaultValue);
                }
                if (o.type == configTypeSetInt) {
                    Set<String> ss = preferences.getStringSet(o.key, new HashSet<>());
                    HashSet<Integer> si = new HashSet<>();
                    for (String s : ss) {
                        si.add(Integer.parseInt(s));
                    }
                    o.value = si;
                }
                if (o.type == configTypeMapIntInt) {
                    String cv = preferences.getString(o.key, "");
                    // Log.e("NC", String.format("Getting pref %s val %s", o.key, cv));
                    if (cv.length() == 0) {
                        o.value = new HashMap<Integer, Integer>();
                    } else {
                        try {
                            byte[] data = Base64.decode(cv, Base64.DEFAULT);
                            ObjectInputStream ois = new ObjectInputStream(
                                    new ByteArrayInputStream(data));
                            o.value = (HashMap<Integer, Integer>) ois.readObject();
                            if (o.value == null) {
                                o.value = new HashMap<Integer, Integer>();
                            }
                            ois.close();
                        } catch (Exception e) {
                            o.value = new HashMap<Integer, Integer>();
                        }
                    }
                }
            }
            configLoaded = true;
        }
    }

    public static void saveConfig() {
        synchronized (sync) {
            try {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nkmrcfg", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                for (int i = 0; i < configs.size(); i++) {
                    ConfigItem o = configs.get(i);

                    if (o.type == configTypeBool) {
                        editor.putBoolean(o.key, (boolean) o.value);
                    }
                    if (o.type == configTypeInt) {
                        editor.putInt(o.key, (int) o.value);
                    }
                    if (o.type == configTypeString) {
                        editor.putString(o.key, o.value.toString());
                    }
                    if (o.type == configTypeSetInt) {
                        HashSet<String> ss = new HashSet<>();
                        for (Integer n : (Set<Integer>) o.value) {
                            ss.add(Integer.toString(n));
                        }
                        editor.putStringSet(o.key, ss);
                    }
                    if (o.type == configTypeMapIntInt) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(o.value);
                        oos.close();
                        editor.putString(o.key, Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
                    }
                }
                editor.commit();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }
}