package tw.nekomimi.nekogram;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.iid.FirebaseInstanceId;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GcmPushListenerService;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;

import javax.validation.constraints.NotNull;

public class ExternalGcm {

    @SuppressWarnings("ConstantConditions")
    public static boolean noGcm = !"release".equals(BuildConfig.BUILD_TYPE);

    private static boolean hasPlayServices;

    public static void initPlayServices() {

        if (noGcm) return;

        AndroidUtilities.runOnUIThread(() -> {
            if (hasPlayServices = checkPlayServices()) {
                final String currentPushString = SharedConfig.pushString;
                if (!TextUtils.isEmpty(currentPushString)) {
                    if (BuildVars.DEBUG_PRIVATE_VERSION && BuildVars.LOGS_ENABLED) {
                        FileLog.d("GCM regId = " + currentPushString);
                    }
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("GCM Registration not found.");
                    }
                }
                Utilities.globalQueue.postRunnable(() -> {
                    try {
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                            String token = instanceIdResult.getToken();
                            if (!TextUtils.isEmpty(token)) {
                                GcmPushListenerService.sendRegistrationToServer(token);
                            }
                        }).addOnFailureListener(e -> {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("Failed to get regid");
                            }
                            SharedConfig.pushStringStatus = "__FIREBASE_FAILED__";
                            GcmPushListenerService.sendRegistrationToServer(null);
                        });
                        FirebaseCrashlytics.getInstance().setCustomKey("flavor",BuildConfig.FLAVOR);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                });
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("No valid Google Play Services APK found.");
                }
                SharedConfig.pushStringStatus = "__NO_GOOGLE_PLAY_SERVICES__";
                GcmPushListenerService.sendRegistrationToServer(null);
            }
        }, 1000);

    }

    private static boolean checkPlayServices() {
        try {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(ApplicationLoader.applicationContext);
            return resultCode == ConnectionResult.SUCCESS;
        } catch (Exception e) {
            FileLog.e(e);
        }
        return true;
    }

    public static void sendRegistrationToServer() {
        if (noGcm) return;
        GcmPushListenerService.sendRegistrationToServer(SharedConfig.pushString);
    }

    public static void reportLog(@NotNull String report) {

        if (noGcm) return;

        FirebaseCrashlytics.getInstance().log(report);

    }

}
