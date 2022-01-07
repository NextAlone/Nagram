package tw.nekomimi.nkmr;

import static tw.nekomimi.nkmr.NekomuraConfig.saveConfig;

import java.util.HashMap;
import java.util.HashSet;

public class ConfigItem {
    String key;
    int type;

    Object defaultValue;
    Object value;
    boolean forceDefault;//针对某些人的垃圾选项，应该不允许改变默认值

    ConfigItem(String k, int t, Object d) {
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

    public Long Long() {
        return (Long) value;
    }

    public Float Float() {
        return (Float) value;
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
        saveConfig(this);
        return this.Bool();//返回toggle之后的
    }

    public void setConfigBool(boolean v) {
        value = v;
        if (forceDefault) {
            value = defaultValue;
        }
        saveConfig(this);
    }

    public void setConfigInt(int v) {
        if (forceDefault) {
            value = defaultValue;
        }
        value = v;
        saveConfig(this);
    }

    public void setConfigLong(Long v) {
        if (forceDefault) {
            value = defaultValue;
        }
        value = v;
        saveConfig(this);
    }

    public void setConfigFloat(Float v) {
        if (forceDefault) {
            value = defaultValue;
        }
        value = v;
        saveConfig(this);
    }

    public void setConfigString(String v) {
        if (v == null) {
            value = "";
        } else {
            value = v;
        }
        if (forceDefault) {
            value = defaultValue;
        }
        saveConfig(this);
    }

    public void setConfigSetInt(HashSet<Integer> v) {
        value = v;
        if (forceDefault) {
            value = defaultValue;
        }
        saveConfig(this);
    }

    public void setConfigMapInt(HashMap<Integer, Integer> v) {
        value = v;
        saveConfig(this);
    }
}