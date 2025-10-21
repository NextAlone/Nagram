package tw.nekomimi.nekogram.helpers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;


import io.sentry.Sentry;
import io.sentry.SentryLevel;
import io.sentry.android.core.SentryAndroid;

public class AnalyticsHelper {
    public static String DSN = "https://f7a6e4cc5c2b0a3aded76128a06d34e4@o416616.ingest.us.sentry.io/4507780440915968";
    public static boolean loaded = false;

    public static void start(Application application) {
        if (!getSentryStatus(application)) {
            return;
        }
        SentryAndroid.init(application, options -> {
            options.setDsn(DSN);
            options.setEnvironment(BuildVars.DEBUG_VERSION ? "debug" : "release");
            options.setEnableAutoSessionTracking(true);
            options.setTracesSampleRate(1.0);
            options.setAttachAnrThreadDump(true);
            options.setRelease(BuildConfig.APPLICATION_ID + "@" + BuildConfig.VERSION_NAME + "+" + BuildConfig.VERSION_CODE);
            options.setTag("buildTimestamp", BuildConfig.BUILD_TIMESTAMP + "");
            options.setBeforeScreenshotCaptureCallback((event, hint, debounce) -> {
                // always capture crashed events
                if (event.isCrashed()) {
                    return true;
                }

                // if debounce is active, skip capturing
                if (debounce) {
                    return false;
                } else {
                    // also capture fatal events
                    return event.getLevel() == SentryLevel.FATAL;
                }
            });
        });
        loaded = true;
    }

    public static void captureException(Throwable e) {
        if (loaded) {
            Sentry.captureException(e);
        }
    }

    public static boolean getSentryStatus(Application application) {
        SharedPreferences preferences = application.getApplicationContext().getSharedPreferences(
                        "nkmrcfg",
                        Context.MODE_PRIVATE
        );
        return preferences.getBoolean("SentryAnalytics", true);
    }
}
