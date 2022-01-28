/*
 * Copyright (C) 2019-2022 qwq233 <qwq233@qwq2333.top>
 * https://github.com/qwq233/Nullgram
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this software.
 *  If not, see
 * <https://www.gnu.org/licenses/>
 */

package top.qwq2333.nullgram.config;

import android.app.Activity;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.telegram.messenger.ApplicationLoader;
import top.qwq2333.nullgram.utils.LogUtilsKt;

public class ConfigManager {

    private static final SharedPreferences preferences =
        ApplicationLoader.applicationContext.getSharedPreferences("globalConfig",
            Activity.MODE_PRIVATE);

    /**
     * 获取Int值
     *
     * @param key key
     * @param def 默认值
     * @return key所对应值
     */
    public static int getIntOrDefault(@NonNull String key, int def) {
        return preferences.getInt(key, def);
    }

    /**
     * 获取Long值
     *
     * @param key key
     * @param def 默认值
     */
    public static long getLongOrDefault(@NonNull String key, long def) {
        return preferences.getLong(key, def);
    }

    /**
     * 获取boolean值
     *
     * @param key key
     * @param def 默认值
     * @return key所对应值
     */
    public static boolean getBooleanOrDefault(@NonNull String key, boolean def) {
        return preferences.getBoolean(key, def);
    }

    /**
     * 获取boolean值
     *
     * @param key key
     * @return key所对应值 默认为false
     */
    public static boolean getBooleanOrFalse(@NonNull String key) {
        return preferences.getBoolean(key, false);
    }

    /**
     * 获取String值
     *
     * @param key key
     * @param def 默认值
     * @return key所对应值
     */
    @NonNull
    public static String getStringOrDefault(@NonNull String key, @Nullable String def) {
        return preferences.getString(key, def);
    }

    /**
     * 设置Int值
     *
     * @param key   key
     * @param value 值
     */
    public static void putInt(@NonNull String key, int value) {
        synchronized (preferences) {
            try {
                preferences.edit().putInt(key, value).apply();
            } catch (Throwable thr) {
                LogUtilsKt.e("putInt: ", thr);
            }
        }
    }

    /**
     * 设置Long值
     *
     * @param key   key
     * @param value 值
     */
    public static void putLong(@NonNull String key, long value) {
        synchronized (preferences) {
            try {
                preferences.edit().putLong(key, value).apply();
            } catch (Throwable thr) {
                LogUtilsKt.e("putLong: ", thr);
            }
        }
    }

    /**
     * 设置boolean值
     *
     * @param key   key
     * @param value 值
     */
    public static void putBoolean(@NonNull String key, boolean value) {
        synchronized (preferences) {
            try {
                preferences.edit().putBoolean(key, value).apply();
            } catch (Throwable thr) {
                LogUtilsKt.e("putBoolean: ", thr);
            }
        }
    }

    /**
     * 设置String值
     *
     * @param key   key
     * @param value 值
     */
    public static void putString(@NonNull String key, String value) {
        synchronized (preferences) {
            try {
                if(value.equals("")){
                    preferences.edit().remove(key).apply();
                }
                preferences.edit().putString(key, value).apply();
            } catch (Throwable thr) {
                LogUtilsKt.e("putString: ", thr);
            }
        }
    }

    /**
     * 切换boolean值 若原为false或未设置则切换为true 若原为true则切换为false
     *
     * @param key key
     */
    public static void toggleBoolean(@NonNull String key) {
        synchronized (preferences) {
            try {
                boolean originValue = preferences.getBoolean(key, false);
                preferences.edit().putBoolean(key, !originValue).apply();
            } catch (Throwable thr) {
                LogUtilsKt.e(thr);
            }
        }
    }

    /**
     * 删除key所对应Value 无视value类型
     *
     * @param key key
     */
    public static void deleteValue(@NonNull String key){
        synchronized (preferences){
            try {
                preferences.edit().remove(key).apply();
            } catch (Throwable thr) {
                LogUtilsKt.e(thr);
            }
        }
    }
}
