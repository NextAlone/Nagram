package tw.nekomimi.nekogram;

import static org.telegram.ui.Components.BlockingUpdateView.checkApkInstallPermissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
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
import java.util.ArrayList;
import java.util.Locale;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import tw.nekomimi.nekogram.utils.FileUtil;
import tw.nekomimi.nkmr.CellGroup;
import tw.nekomimi.nkmr.NekomuraConfig;
import tw.nekomimi.nkmr.NekomuraUtil;
import tw.nekomimi.nkmr.cells.NekomuraTGSelectBox;

//TODO use UpdateAppAlertDialog / BlockingUpdateView?

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
            String urlChannel = "";
            String sha1 = "";
            try {
                final String newBody = HttpUtil.get("https://api.github.com/repos/NekoX-Dev/updates/contents/" + release.name + ".txt?ref=main");
                final GithubApiContents releaseNoteApi = new Gson().fromJson(newBody, GithubApiContents.class);
                final String releaseNoteString = new String(Base64.decode(releaseNoteApi.content, Base64.DEFAULT));
                final byte[] gzipped = Base64.decode(NekomuraUtil.getSubString(releaseNoteString, "#NekoXStart#", "#NekoXEnd#"), Base64.NO_PADDING);
                final NekoXReleaseNote nekoXReleaseNote = new Gson().fromJson(new String(NekomuraUtil.uncompress(gzipped)), NekoXReleaseNote.class);

                if (nekoXReleaseNote != null && nekoXReleaseNote.apks != null) {
                    for (NekoXAPK napk : nekoXReleaseNote.apks) {
                        if (napk.name.equals(apk.name)) {
                            sha1 = napk.sha1;
                            for (String url : napk.urls) {
                                if (url.startsWith("https://t.me/")) urlChannel = url;
                            }
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {
            }

            String finalsha1 = sha1;
            String finalUrlChannel = urlChannel;
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
                    showSelectDownloadSource(ctx, apk != null ? apk.name : finalRelease.name,
                            apk != null ? apk.browser_download_url : finalRelease.html_url,
                            finalUrlChannel, finalsha1);
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

    public static void showSelectDownloadSource(Context ctx, String title, String browser_download_url, String urlChannel, String sha1) {
        CellGroup nkmrCells = new CellGroup(null);

        nkmrCells.callBackSettingsChanged = ((k, v) -> {
            int source = NekomuraConfig.update_download_soucre.Int();
            switch (source) {
                case 0:
                    Browser.openUrl(ctx, browser_download_url);
                    break;
                case 1:
                    Browser.openUrl(ctx, urlChannel);
                    break;
            }
        });

        ArrayList<String> sources = new ArrayList<>();
        sources.add("Github Release"); // base of Current
        if (!urlChannel.isEmpty()) sources.add("Telegram Channel");

        String[] sources_ = new String[sources.size()];
        sources.toArray(sources_);

        NekomuraTGSelectBox sb = new NekomuraTGSelectBox(null, NekomuraConfig.update_download_soucre, sources_, null);
        nkmrCells.appendCell(sb); // new
        sb.onClickWithDialog(ctx);
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