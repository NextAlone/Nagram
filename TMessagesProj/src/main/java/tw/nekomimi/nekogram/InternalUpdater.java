package tw.nekomimi.nekogram;

import static org.telegram.ui.Components.BlockingUpdateView.checkApkInstallPermissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.AlertDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.hutool.http.HttpRequest;
import tw.nekomimi.nekogram.utils.FileUtil;

//TODO use UpdateAppAlertDialog / BlockingUpdateView?

public class InternalUpdater {
    private static final String API_URL_RELEASE = "https://api.github.com/repos/NekoX-Dev/NekoX/releases?per_page=10";

    private static class ReleaseMetadata {
        String name;
        String body;
        String published_at;
        String html_url;
        ApkMetadata[] assets;
    }

    private static class ApkMetadata {
        String name;
        String browser_download_url;
    }

    private static class GithubApiContents {
        String content;
    }

    // as a base64 encoded json
    private static class NekoXReleaseNote {
        NekoXAPK[] apks;
    }

    private static class NekoXAPK {
        String name;
        String sha1;
        String[] urls;  // https://t.me/xxx or bdex://xxx, bdex removed
    }

    private static ApkMetadata matchBuild(ApkMetadata[] apks) {
        String target = BuildConfig.FLAVOR + "-" + FileUtil.getAbi() + "-" + BuildConfig.BUILD_TYPE + ".apk";
        FileLog.e(target);
        for (ApkMetadata apk : apks) {
            if (apk.name.contains(target))
                return apk;
        }
        return null;
    }

    public static void checkUpdate(Context ctx, boolean isAutoCheck) {
        if (BuildVars.isFdroid)
            return;

        //cleanup
        File f = new File(ApplicationLoader.getDataDirFixed(), "cache/new.apk");
        if (f.exists()) f.delete();

        try {
            NekoXConfig.setNextUpdateCheck(System.currentTimeMillis() / 1000 + 24 * 3600);

            //TODO update URL when api.github.com get banned.
            String ret = HttpRequest.get(API_URL_RELEASE).header("accept", "application/vnd.github.v3+json").execute().body();
            ReleaseMetadata[] releases = new Gson().fromJson(ret, ReleaseMetadata[].class);
            ReleaseMetadata release = null;

            // Not now.
            String releaseChannel = "stable";
            switch (NekoXConfig.autoUpdateReleaseChannel) {
                case 2:
                    releaseChannel = "rc";
                    break;
                case 3:
                    releaseChannel = "preview";
                    break;
            }

            for (ReleaseMetadata rel : releases) {
                if (rel.name.equals("v" + BuildConfig.VERSION_NAME))
                    break;
                if (rel.name.contains("rc") && NekoXConfig.autoUpdateReleaseChannel < 2 || rel.name.contains("preview") && NekoXConfig.autoUpdateReleaseChannel < 3)
                    continue;
                release = rel;
                break;
            }
            if (release == null) {
                FileLog.d("no update");
                if (!isAutoCheck)
                    AndroidUtilities.runOnUIThread(() -> Toast.makeText(ctx, LocaleController.getString("VersionUpdateNoUpdate", R.string.VersionUpdateNoUpdate), Toast.LENGTH_SHORT).show());
                return;
            } else if (release.name.equals(NekoXConfig.ignoredUpdateTag) && isAutoCheck) {
                FileLog.d("ignored tag " + release.name);
                return;
            }

            // match release apk urls
            final ApkMetadata apk = matchBuild(release.assets);

            // match apk urls. these can be empty.

            ReleaseMetadata finalRelease = release;

            AndroidUtilities.runOnUIThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle(LocaleController.getString("VersionUpdateTitle", R.string.VersionUpdateTitle));

                String message = null;
                try {
                    message = finalRelease.name + "   " + LocaleController.formatDateChat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(finalRelease.published_at).getTime() / 1000) + "\n\n";
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (apk == null)
                    message += LocaleController.getString("VersionUpdateVariantNotMatch", R.string.VersionUpdateVariantNotMatch);
                else
                    message += apk.name.replace(".apk", "");
                builder.setMessage(message);

                builder.setPositiveButton(LocaleController.getString("VersionUpdateConfirm", R.string.VersionUpdateConfirm), (dialog, which) -> {
                    Browser.openUrl(ctx, apk.browser_download_url);
                });
                builder.setNeutralButton(LocaleController.getString("VersionUpdateIgnore", R.string.VersionUpdateIgnore), (dialog, which) -> NekoXConfig.setIgnoredUpdateTag(finalRelease.name));
                builder.setNegativeButton(LocaleController.getString("VersionUpdateNotNow", R.string.VersionUpdateNotNow), (dialog, which) -> NekoXConfig.setNextUpdateCheck(System.currentTimeMillis() / 1000 + 3 * 24 * 3600));
                builder.show();
            });
        } catch (Exception e) {
            FileLog.e(e);
            if (!isAutoCheck)
                AndroidUtilities.runOnUIThread(() -> Toast.makeText(ctx, "An exception occurred during checking updates.", Toast.LENGTH_SHORT).show());
        }
    }

    public static boolean openApkInstall(Activity activity, File f) {
        if (!checkApkInstallPermissions(activity)) {
            return false;
        }

        boolean exists = false;
        try {
            if (exists = f.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (Build.VERSION.SDK_INT >= 24) {
                    intent.setDataAndType(FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", f), "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
                }
                try {
                    activity.startActivityForResult(intent, 500);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return exists;
    }

}