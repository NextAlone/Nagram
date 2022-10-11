/*

 This is the source code of exteraGram for Android.

 We do not and cannot prevent the use of our code,
 but be respectful and credit the original author.

 Copyright @immat0x1, 2022.

 cherrygram dev kys

*/

package com.exteragram.messenger.updater;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import androidx.core.content.FileProvider;

import com.exteragram.messenger.ExteraConfig;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.TypefaceSpan;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class UpdaterUtils {

    private static String uri = "https://api.github.com/repos/exteraSquad/exteraGram/releases/latest";
    private static String downloadURL = null;
    public static String version, changelog, size, uploadDate;
    public static File otaPath, versionPath, apkFile;

    private static long id = 0L;
    private static final long updateCheckInterval = 3600000L; // 1 hour

    public static boolean updateDownloaded = false;

    private static final String[] userAgents = {
        "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36,gzip(gfe)",
        "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)",
        "Mozilla/5.0 (Linux; Android 6.0; Nexus 7 Build/MRA51D) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.133 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/600.8.9 (KHTML, like Gecko) Version/8.0.8 Safari/600.8.9",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/44.0.2403.89 Chrome/44.0.2403.89 Safari/537.36",
        "Mozilla/5.0 (Linux; Android 5.0.2; SAMSUNG SM-G920F Build/LRX22G) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/3.0 Chrome/38.0.2125.102 Mobile Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; rv:40.0) Gecko/20100101 Firefox/40.0"
    };

    public static String getRandomUserAgent() {
        int randomNum = Utilities.random.nextInt(userAgents.length);
        return userAgents[randomNum];
    }

    public static void checkDirs() {
        File externalDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
        otaPath = new File(externalDir, "ota");
        versionPath = new File(otaPath, version);
        apkFile = new File(versionPath, "update.apk");

        if (!versionPath.exists()) {
            versionPath.mkdirs();
        }
        updateDownloaded = apkFile.exists();
    }

    public static void checkUpdates(Context context, boolean manual) {
        checkUpdates(context, manual, () -> {}, () -> {});
    }
    public interface OnUpdateNotFound {
        void run();
    }
    public interface OnUpdateFound {
        void run();
    }
    public static void checkUpdates(Context context, boolean manual, OnUpdateNotFound onUpdateNotFound, OnUpdateFound onUpdateFound) {

        if (BuildVars.PM_BUILD) {
            return;
        }

        Utilities.globalQueue.postRunnable(() -> {
            ExteraConfig.editor.putLong("lastUpdateCheckTime", ExteraConfig.lastUpdateCheckTime = System.currentTimeMillis()).apply();

            if (id != 0L || (System.currentTimeMillis() - ExteraConfig.updateScheduleTimestamp < updateCheckInterval && !manual)) {
                return;
            }

            try {
                if (BuildVars.isBetaApp()) uri = uri.replace("/exteraGram/", "/exteraGram-Beta/");
                HttpURLConnection connection = (HttpURLConnection) new URI(uri).toURL().openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", getRandomUserAgent());
                connection.setRequestProperty("Content-Type", "application/json");

                StringBuilder textBuilder = new StringBuilder();
                try (Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    int c;
                    while ((c = reader.read()) != -1) {
                        textBuilder.append((char) c);
                    }
                }

                JSONObject obj = new JSONObject(textBuilder.toString());
                JSONArray arr = obj.getJSONArray("assets");

                if (arr.length() == 0) {
                    return;
                }

                String link, cpu = null;
                try {
                    PackageInfo info = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                    switch (info.versionCode % 10) {
                        case 1:
                        case 3:
                            cpu = "arm-v7a";
                            break;
                        case 2:
                        case 4:
                            cpu = "x86";
                            break;
                        case 5:
                        case 7:
                            cpu = "arm64-v8a";
                            break;
                        case 6:
                        case 8:
                            cpu = "x86_64";
                            break;
                        case 0:
                        case 9:
                            cpu = "universal";
                            break;
                    }
                } catch (Exception e) {
                    cpu = Build.SUPPORTED_ABIS[0];
                }

                for (int i = 0; i < arr.length(); i++) {
                    downloadURL = link = arr.getJSONObject(i).getString("browser_download_url");
                    size = AndroidUtilities.formatFileSize(arr.getJSONObject(i).getLong("size"));
                    if (link.contains("arm64") && Objects.equals(cpu, "arm64-v8a") ||
                        link.contains("arm7") && Objects.equals(cpu, "armeabi-v7a") ||
                        link.contains("x86") && Objects.equals(cpu, "x86") ||
                        link.contains("x64") && Objects.equals(cpu, "x86_64") ||
                        link.contains("universal") && Objects.equals(cpu, "universal") && !BuildVars.isBetaApp() ||
                        link.contains("beta") && BuildVars.isBetaApp()) {
                        break;
                    }
                }
                version = obj.getString("tag_name");
                changelog = obj.getString("body");
                uploadDate = obj.getString("published_at").replaceAll("[TZ]", " ");
                uploadDate = LocaleController.formatDateTime(getMillisFromDate(uploadDate, "yyyy-M-dd hh:mm:ss") / 1000);

                if (isNewVersion(BuildVars.BUILD_VERSION_STRING, version)) {
                    checkDirs();
                    AndroidUtilities.runOnUIThread(() -> {
                        (new UpdaterBottomSheet(context, true, version, changelog, size, downloadURL, uploadDate)).show();
                        if (onUpdateFound != null) {
                            onUpdateFound.run();
                        }
                    });
                } else {
                    if (onUpdateNotFound != null) {
                        AndroidUtilities.runOnUIThread(onUpdateNotFound::run);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void downloadApk(Context context, String link, String title) {
        if (!updateDownloaded) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link));

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setTitle(title);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(context, "ota/" + version, "update.apk");

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            id = manager.enqueue(request);

            DownloadReceiver downloadBroadcastReceiver = new DownloadReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.DOWNLOAD_COMPLETE");
            intentFilter.addAction("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED");
            context.registerReceiver(downloadBroadcastReceiver, intentFilter);
        } else {
            installApk(context, apkFile.getAbsolutePath());
        }
    }

    public static void installApk(Context context, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        Intent install = new Intent(Intent.ACTION_VIEW);
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = FileProvider.getUriForFile(context, ApplicationLoader.getApplicationId() + ".provider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ApplicationLoader.applicationContext.getPackageManager().canRequestPackageInstalls()) {
            AlertsCreator.createApkRestrictedDialog(context, null).show();
            return;
        }
        if (fileUri != null) {
            install.setDataAndType(fileUri, "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (install.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(install);
            }
        }
    }

    public static boolean isNewVersion(String... v) {
        if (v.length != 2) {
            return false;
        }
        for (int i = 0; i < 2; i++) {
            v[i] = v[i].replaceAll("[^0-9]+", "");
            if (Integer.parseInt(v[i]) <= 999) {
                v[i] += "0";
            }
        }
        return Integer.parseInt(v[0]) < Integer.parseInt(v[1]);
    }

    public static String getOtaDirSize() {
        checkDirs();
        return AndroidUtilities.formatFileSize(Utilities.getDirSize(otaPath.getAbsolutePath(), 5, true), true);
    }

    public static void cleanOtaDir() {
        checkDirs();
        cleanFolder(otaPath);
    }

    public static void cleanFolder(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f: files) {
                if (f.isDirectory()) {
                    cleanFolder(f);
                }
                f.delete();
            }
        }
    }

    public static long getMillisFromDate(String d, String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
           Date date = sdf.parse(d);
           assert date != null;
           return date.getTime();
        } catch (Exception ignore) {
           return 1L;
        }
    }

    public static SpannableStringBuilder replaceTags(String str) {
        try {
            int start;
            int end;
            StringBuilder stringBuilder = new StringBuilder(str);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
            String symbol = "", font = "fonts/rregular.ttf";
            for (int i = 0; i < 3; i++) {
                switch (i) {
                    case 0:
                        symbol = "**";
                        font = "fonts/rmedium.ttf";
                        break;
                    case 1:
                        symbol = "_";
                        font = "fonts/ritalic.ttf";
                        break;
                    case 2:
                        symbol = "`";
                        font = "fonts/rmono.ttf";
                        break;
                }
                while ((start = stringBuilder.indexOf(symbol)) != -1) {
                    stringBuilder.replace(start, start + symbol.length(), "");
                    spannableStringBuilder.replace(start, start + symbol.length(), "");
                    end = stringBuilder.indexOf(symbol);
                    if (end >= 0) {
                        stringBuilder.replace(end, end + symbol.length(), "");
                        spannableStringBuilder.replace(end, end + symbol.length(), "");
                        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface(font)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
            return spannableStringBuilder;
        } catch (Exception e) {
            FileLog.e(e);
        }
        return new SpannableStringBuilder(str);
    }

    public interface OnTranslationSuccess {
        void run(String translated);
    }
    public interface OnTranslationFail {
        void run();
    }
    public static void translate(CharSequence text, OnTranslationSuccess onSuccess, OnTranslationFail onFail) {
        Utilities.globalQueue.postRunnable(() -> {
            String uri;
            HttpURLConnection connection;
            try {
                uri = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=";
                uri += Uri.encode(LocaleController.getInstance().getCurrentLocale().getLanguage());
                uri += "&dt=t&ie=UTF-8&oe=UTF-8&otf=1&ssel=0&tsel=0&kc=7&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&q=";
                uri += Uri.encode(text.toString());
                connection = (HttpURLConnection) new URI(uri).toURL().openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", getRandomUserAgent());
                connection.setRequestProperty("Content-Type", "application/json");

                StringBuilder textBuilder = new StringBuilder();
                try (Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    int c;
                    while ((c = reader.read()) != -1) textBuilder.append((char) c);
                }
                JSONTokener tokener = new JSONTokener(textBuilder.toString());
                JSONArray array = new JSONArray(tokener);
                JSONArray array1 = array.getJSONArray(0);
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < array1.length(); ++i) {
                    String blockText = array1.getJSONArray(i).getString(0);
                    if (blockText != null && !blockText.equals("null")) result.append(blockText);
                }
                if (text.length() > 0 && text.charAt(0) == '\n') result.insert(0, "\n");
                if (onSuccess != null) AndroidUtilities.runOnUIThread(() -> onSuccess.run(result.toString()));
            } catch (Exception e) {
                e.printStackTrace();
                if (onFail != null) AndroidUtilities.runOnUIThread(onFail::run);
            }
        });
    }

    public static class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                if (id == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)) {
                    installApk(context, apkFile.getAbsolutePath());
                    id = 0L;
                    updateDownloaded = false;
                }
            } else if (Objects.equals(intent.getAction(), DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                Intent viewDownloadIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                viewDownloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(viewDownloadIntent);
            }
        }
    }
}