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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

import kotlin.text.StringsKt;
import top.qwq2333.nullgram.utils.Log;

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
     * 获取Float值
     *
     * @param key key
     * @param def 默认值
     * @return key所对应值
     */
    public static float getFloatOrDefault(@NonNull String key, float def) {
        return preferences.getFloat(key, def);
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
                Log.e("putInt: ", thr);
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
                Log.e("putLong: ", thr);
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
                Log.e("putBoolean: ", thr);
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
                if (value.equals("")) {
                    preferences.edit().remove(key).apply();
                }
                preferences.edit().putString(key, value).apply();
            } catch (Throwable thr) {
                Log.e("putString: ", thr);
            }
        }
    }

    /**
     * 设置Float值
     *
     * @param key   key
     * @param value 值
     */
    public static void putFloat(@NonNull String key, float value) {
        synchronized (preferences) {
            try {
                preferences.edit().putFloat(key, value).apply();
            } catch (Throwable thr) {
                Log.e("putFloat: ", thr);
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
                Log.e(thr);
            }
        }
    }

    /**
     * 删除key所对应Value 无视value类型
     *
     * @param key key
     */
    public static void deleteValue(@NonNull String key) {
        synchronized (preferences) {
            try {
                preferences.edit().remove(key).apply();
            } catch (Throwable thr) {
                Log.e(thr);
            }
        }
    }

    /**
     * 导出配置
     *
     * @return json格式的配置
     */
    @NonNull
    public static String exportConfigurationToJson() throws JSONException {
        JSONObject json = new JSONObject();
        ArrayList<String> userconfig = new ArrayList<>();
        userconfig.add("saveIncomingPhotos");
        userconfig.add("passcodeHash");
        userconfig.add("passcodeType");
        userconfig.add("passcodeHash");
        userconfig.add("autoLockIn");
        userconfig.add("useFingerprint");
        SharedPreferenceToJSON("userconfing", json, userconfig::contains);

        ArrayList<String> mainconfig = new ArrayList<>();
        mainconfig.add("saveToGallery");
        mainconfig.add("autoplayGifs");
        mainconfig.add("autoplayVideo");
        mainconfig.add("mapPreviewType");
        mainconfig.add("raiseToSpeak");
        mainconfig.add("customTabs");
        mainconfig.add("directShare");
        mainconfig.add("shuffleMusic");
        mainconfig.add("playOrderReversed");
        mainconfig.add("inappCamera");
        mainconfig.add("repeatMode");
        mainconfig.add("fontSize");
        mainconfig.add("bubbleRadius");
        mainconfig.add("ivFontSize");
        mainconfig.add("allowBigEmoji");
        mainconfig.add("streamMedia");
        mainconfig.add("saveStreamMedia");
        mainconfig.add("smoothKeyboard");
        mainconfig.add("pauseMusicOnRecord");
        mainconfig.add("streamAllVideo");
        mainconfig.add("streamMkv");
        mainconfig.add("suggestStickers");
        mainconfig.add("sortContactsByName");
        mainconfig.add("sortFilesByName");
        mainconfig.add("noSoundHintShowed");
        mainconfig.add("directShareHash");
        mainconfig.add("useThreeLinesLayout");
        mainconfig.add("archiveHidden");
        mainconfig.add("distanceSystemType");
        mainconfig.add("loopStickers");
        mainconfig.add("keepMedia");
        mainconfig.add("noStatusBar");
        mainconfig.add("lastKeepMediaCheckTime");
        mainconfig.add("searchMessagesAsListHintShows");
        mainconfig.add("searchMessagesAsListUsed");
        mainconfig.add("stickersReorderingHintUsed");
        mainconfig.add("textSelectionHintShows");
        mainconfig.add("scheduledOrNoSoundHintShows");
        mainconfig.add("lockRecordAudioVideoHint");
        mainconfig.add("disableVoiceAudioEffects");
        mainconfig.add("chatSwipeAction");

        mainconfig.add("theme");
        mainconfig.add("selectedAutoNightType");
        mainconfig.add("autoNightScheduleByLocation");
        mainconfig.add("autoNightBrighnessThreshold");
        mainconfig.add("autoNightDayStartTime");
        mainconfig.add("autoNightDayEndTime");
        mainconfig.add("autoNightSunriseTime");
        mainconfig.add("autoNightCityName");
        mainconfig.add("autoNightSunsetTime");
        mainconfig.add("autoNightLocationLatitude3");
        mainconfig.add("autoNightLocationLongitude3");
        mainconfig.add("autoNightLastSunCheckDay");

        mainconfig.add("lang_code");

        SharedPreferenceToJSON("mainconfig", json, mainconfig::contains);
        SharedPreferenceToJSON("themeconfig", json, null);
        SharedPreferenceToJSON("globalConfig", json, null);
        return json.toString();
    }

    /**
     * 将SharePreference的数据转换成json
     *
     * @param sp     SharePreferences name
     * @param object 传入的JsonObject将会被传入SharePreferences中的配置
     * @param filter 过滤 只接收哪些key
     * @throws JSONException Ignore 一般不会发生
     */
    private static void SharedPreferenceToJSON(@NonNull String sp, @NonNull JSONObject object,
                                               @Nullable Function<String, Boolean> filter) throws JSONException {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(
            sp, Activity.MODE_PRIVATE);
        JSONObject jsonConfig = new JSONObject();
        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet()) {
            String key = entry.getKey();
            if (filter != null && !filter.apply(key)) {
                continue;
            }
            if (entry.getValue() instanceof Long) {
                key = key + "_long";
            } else if (entry.getValue() instanceof Float) {
                key = key + "_float";
            }
            jsonConfig.put(key, entry.getValue());
        }
        object.put(sp, jsonConfig);
    }

    /**
     * 导入配置
     *
     * @param configJson 待导入配置 格式为Json
     * @throws JSONException 若传入配置不为json或者json不合法就抛出这个错误
     */
    @SuppressLint("ApplySharedPref")
    public static void importSettings(@NonNull JsonObject configJson) throws JSONException {
        for (Map.Entry<String, JsonElement> element : configJson.entrySet()) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(
                element.getKey(), Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            for (Map.Entry<String, JsonElement> config : ((JsonObject) element.getValue()).entrySet()) {
                String key = config.getKey();
                JsonPrimitive value = (JsonPrimitive) config.getValue();
                if (value.isBoolean()) {
                    editor.putBoolean(key, value.getAsBoolean());
                } else if (value.isNumber()) {
                    boolean isLong = false;
                    boolean isFloat = false;
                    if (key.endsWith("_long")) {
                        key = StringsKt.substringBeforeLast(key, "_long", key);
                        isLong = true;
                    } else if (key.endsWith("_float")) {
                        key = StringsKt.substringBeforeLast(key, "_float", key);
                        isFloat = true;
                    }
                    if (isLong) {
                        editor.putLong(key, value.getAsLong());
                    } else if (isFloat) {
                        editor.putFloat(key, value.getAsFloat());
                    } else {
                        editor.putInt(key, value.getAsInt());
                    }
                } else {
                    editor.putString(key, value.getAsString());
                }
            }
            editor.commit();
        }

    }

}
