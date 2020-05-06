package tw.nekomimi.nekogram;

import android.app.Activity;
import android.content.IntentSender;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.iid.FirebaseInstanceId;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GcmPushListenerService;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;

import javax.validation.constraints.NotNull;

import kotlin.Unit;
import tw.nekomimi.nekogram.utils.UIUtil;

public class ExternalGcm {

    @SuppressWarnings("ConstantConditions")
    private static boolean noGcm = !"release".equals(BuildConfig.BUILD_TYPE);

    private static Boolean hasPlayServices;

    public static void initPlayServices() {

        AndroidUtilities.runOnUIThread(() -> {
            if (checkPlayServices()) {
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
                        FirebaseCrashlytics.getInstance().setCustomKey("flavor", BuildConfig.FLAVOR);
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

    public static boolean checkPlayServices() {
        if (noGcm) return false;
        if (hasPlayServices != null) return hasPlayServices;
        try {
            int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ApplicationLoader.applicationContext);
            hasPlayServices = resultCode == ConnectionResult.SUCCESS;
        } catch (Exception e) {
            hasPlayServices = false;
            FileLog.e(e);
        }
        return hasPlayServices;
    }

    public static void sendRegistrationToServer() {
        if (!checkPlayServices()) return;
        GcmPushListenerService.sendRegistrationToServer(SharedConfig.pushString);
    }

    public static void reportLog(@NotNull String report) {
        if (!checkPlayServices()) return;
        UIUtil.runOnIoDispatcher(() -> FirebaseCrashlytics.getInstance().log(report));
    }

    public static void recordException(@NotNull Throwable throwable) {
        if (!checkPlayServices()) return;
        UIUtil.runOnIoDispatcher(() -> FirebaseCrashlytics.getInstance().recordException(throwable));
    }

    public static void checkUpdate(Activity ctx) {
        if (!checkPlayServices()) return;

        AppUpdateManager manager = AppUpdateManagerFactory.create(ctx);

        InstallStateUpdatedListener listener = (installState) -> {

            if (installState.installStatus() == InstallStatus.DOWNLOADED) {

                BottomBuilder builder = new BottomBuilder(ctx);

                builder.addTitle(LocaleController.getString("UpdateDownloaded", R.string.UpdateDownloaded), false);

                builder.addItem(LocaleController.getString("UpdateUpdate", R.string.UpdateUpdate), R.drawable.baseline_system_update_24, false, (it) -> {

                    manager.completeUpdate();

                    return Unit.INSTANCE;

                });

                builder.addItem(LocaleController.getString("UpdateLater", R.string.UpdateLater), R.drawable.baseline_watch_later_24, false, null);

                builder.show();

            }

        };

        manager.registerListener(listener);

        manager.getAppUpdateInfo().addOnSuccessListener((appUpdateInfo) -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {

                if (appUpdateInfo.availableVersionCode() <= BuildConfig.VERSION_CODE) return;

                try {

                    manager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, ctx, 114514);

                } catch (IntentSender.SendIntentException ignored) {
                }

            }

        });

    }

}
