package top.qwq2333.nullgram.helpers;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.AlertDialog;
import top.qwq2333.nullgram.config.ConfigManager;
import top.qwq2333.nullgram.utils.APKUtils;
import top.qwq2333.nullgram.utils.Defines;
import top.qwq2333.nullgram.utils.LogUtilsKt;

public class UpdateHelper {

    private static final String API_URL_RELEASE = "https://api.github.com/repos/qwq233/Nullgram/releases/latest";

    private static JSONObject matchBuild(@NonNull JSONArray assets) {
        try {
            String target = APKUtils.getAbi();
            for (Object obj : assets) {
                JSONObject jsonObject = (JSONObject) obj;
                LogUtilsKt.d(jsonObject.getString("name"));
                if (jsonObject.getString("name").contains(target)) {
                    return jsonObject;
                }
            }
            return null;
        } catch (Exception e) {
            LogUtilsKt.e(e);
            return null;
        }

    }

    public static void checkUpdate(Context ctx, boolean isAutoCheck) {
        try {
            ConfigManager.putLong(Defines.lastCheckUpdateTime, System.currentTimeMillis());
            ConfigManager.putLong(Defines.nextUpdateCheckTime,
                System.currentTimeMillis() / 1000 + 24 * 3600);

            String ret = HttpRequest.get(API_URL_RELEASE)
                .header("accept", "application/vnd.github.v3+json").execute().body();
            /*
             * 0为release
             * 1为ci构筑
             * */
            int releaseChannel = ConfigManager.getIntOrDefault(Defines.releaseChannel, 0);

            /*
             * 查找latest release
             * 如果最新版的release name为v+BuildConfig.VERSION_NAME，则认为是已经安装了最新的release
             * 如果release name包含preview。则认为是ci构筑
             * 如果release name包含skipUpdate，则认为是跳过该更新
             * 如果上面两种情况都不满足，则认为是未安装最新的release
             */
            JSONObject jsonObject = JSONObject.parseObject(ret);
            boolean isLatest = false;
            // 一般不太可能发生 也有可能超过到github api请求数限制了
            if (jsonObject.getString("name") == null) {
                throw new NullPointerException("Cannot get String \"name\" from JSONObject");
            }
            if (jsonObject.getString("name").equals("v" + BuildConfig.VERSION_NAME)) {
                isLatest = true;
            }
            if (jsonObject.getString("name").contains(Defines.ignoredUpdateTag)) {
                isLatest = true;
            }
            if (jsonObject.getString("name").contains("preview") && releaseChannel != 1) {
                isLatest = true;
            }
            if (isLatest) {
                LogUtilsKt.d("Already installed latest release - " + jsonObject.getString("name"));
                if (!isAutoCheck) {
                    AndroidUtilities.runOnUIThread(() -> Toast.makeText(ctx,
                        LocaleController.getString("noAvailableUpdate",
                            R.string.noAvailableUpdate), Toast.LENGTH_SHORT).show());
                }
                return;
            } else if (
                jsonObject.getString("name")
                    .equals(ConfigManager.getStringOrDefault(Defines.skipUpdateVersion, ""))
                    && isAutoCheck) {
                LogUtilsKt.d("Skip update:" + jsonObject.getString("name"));
                return;
            }

            JSONArray assets = jsonObject.getJSONArray("assets");
            JSONObject targetAPK = matchBuild(assets);
            //ReleaseMetadata finalRelease = rel;
            AndroidUtilities.runOnUIThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle(
                    LocaleController.getString("updateAvailable", R.string.updateAvailable));

                String message = null;
                try {
                    message = targetAPK.getString("name") + "   " + LocaleController.formatDateChat(
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(
                            jsonObject.getString("published_at")).getTime() / 1000) + "\n\n";
                } catch (Exception e) {
                    LogUtilsKt.e(e);
                }
                if (targetAPK == null) {
                    message += LocaleController.getString("variantsNotFound",
                        R.string.variantsNotFound);
                } else {
                    message += targetAPK.getString("name").replace(".apk", "");
                }
                builder.setMessage(message);

                builder.setPositiveButton(LocaleController.getString("VersionUpdateConfirm",
                    R.string.updateConfirm), (dialog, which) -> {
                    if (targetAPK != null) {
                        Browser.openUrl(ctx, targetAPK.getString("browser_download_url"));
                    } else {
                        Browser.openUrl(ctx, targetAPK.getString("html_url"));
                    }
                });
                builder.setNeutralButton(
                    LocaleController.getString("skipUpdate", R.string.skipUpdate),
                    (dialog, which) -> ConfigManager.putString(Defines.skipUpdateVersion,
                        targetAPK.getString("name")));
                builder.setNegativeButton(
                    LocaleController.getString("notNow", R.string.notNow),
                    (dialog, which) -> ConfigManager.putLong(Defines.nextUpdateCheckTime,
                        System.currentTimeMillis() / 1000 + 24 * 3600));
                builder.show();
            });
        } catch (Exception e) {
            LogUtilsKt.e(e);
            if (!isAutoCheck) {
                AndroidUtilities.runOnUIThread(
                    () -> Toast.makeText(ctx, "An exception occurred during checking updates.",
                        Toast.LENGTH_SHORT).show());
            }
        }
    }

    /**
     * @param date {long} - date in milliseconds
     */
    public static String formatDateUpdate(long date) {
        long epoch;
        try {
            epoch = ConfigManager.getLongOrDefault(Defines.lastCheckUpdateTime, 0);
        } catch (Exception e) {
            epoch = 0;
        }
        if (date <= epoch) {
            return LocaleController.formatString("LastUpdateNever", R.string.LastUpdateNever);
        }
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(Calendar.DAY_OF_YEAR);
            int year = rightNow.get(Calendar.YEAR);
            rightNow.setTimeInMillis(date);
            int dateDay = rightNow.get(Calendar.DAY_OF_YEAR);
            int dateYear = rightNow.get(Calendar.YEAR);

            if (dateDay == day && year == dateYear) {
                return LocaleController.formatString("LastUpdateFormatted",
                    R.string.LastUpdateFormatted,
                    LocaleController.formatString("TodayAtFormatted", R.string.TodayAtFormatted,
                        LocaleController.getInstance().formatterDay.format(new Date(date))));
            } else if (dateDay + 1 == day && year == dateYear) {
                return LocaleController.formatString("LastUpdateFormatted",
                    R.string.LastUpdateFormatted,
                    LocaleController.formatString("YesterdayAtFormatted",
                        R.string.YesterdayAtFormatted,
                        LocaleController.getInstance().formatterDay.format(new Date(date))));
            } else if (Math.abs(System.currentTimeMillis() - date) < 31536000000L) {
                String format = LocaleController.formatString("formatDateAtTime",
                    R.string.formatDateAtTime,
                    LocaleController.getInstance().formatterDayMonth.format(new Date(date)),
                    LocaleController.getInstance().formatterDay.format(new Date(date)));
                return LocaleController.formatString("LastUpdateDateFormatted",
                    R.string.LastUpdateDateFormatted, format);
            } else {
                String format = LocaleController.formatString("formatDateAtTime",
                    R.string.formatDateAtTime,
                    LocaleController.getInstance().formatterYear.format(new Date(date)),
                    LocaleController.getInstance().formatterDay.format(new Date(date)));
                return LocaleController.formatString("LastUpdateDateFormatted",
                    R.string.LastUpdateDateFormatted, format);
            }
        } catch (Exception e) {
            LogUtilsKt.e(e);
        }
        return "LOC_ERR";
    }


}
