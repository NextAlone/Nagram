package tw.nekomimi.nekogram;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.hutool.http.HttpRequest;
import tw.nekomimi.nekogram.utils.FileUtil;


public class InternalUpdater {
    private static final String API_URL_RELEASE = "https://api.github.com/repos/TeleTux/TeleTux/releases?per_page=10";

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
        try {
            NekoXConfig.setNextUpdateCheck(System.currentTimeMillis() / 1000 + 24 * 3600);

            String ret = HttpRequest.get(API_URL_RELEASE).header("accept", "application/vnd.github.v3+json").execute().body();
            ReleaseMetadata[] releases = new Gson().fromJson(ret, ReleaseMetadata[].class);

            ReleaseMetadata release = null;
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

            final ApkMetadata apk = matchBuild(release.assets);
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
                    if (apk != null)
                        Browser.openUrl(ctx, apk.browser_download_url);
                    else
                        Browser.openUrl(ctx, finalRelease.html_url);
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

}
