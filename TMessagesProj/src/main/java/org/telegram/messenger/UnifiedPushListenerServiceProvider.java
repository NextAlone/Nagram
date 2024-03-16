package org.telegram.messenger;

import android.os.SystemClock;
import android.text.TextUtils;

import org.unifiedpush.android.connector.UnifiedPush;

import java.util.ArrayList;
import java.util.List;

public class UnifiedPushListenerServiceProvider implements PushListenerController.IPushListenerServiceProvider {
    public UnifiedPushListenerServiceProvider(){};

    @Override
    public boolean hasServices() {
        return !UnifiedPush.getDistributors(ApplicationLoader.applicationContext, new ArrayList<>()).isEmpty();
    }

    @Override
    public String getLogTitle() {
        return "UnifiedPush";
    }

    @Override
    public void onRequestPushToken() {
        String currentPushString = SharedConfig.pushString;
        if (!TextUtils.isEmpty(currentPushString)) {
            if (BuildVars.DEBUG_PRIVATE_VERSION && BuildVars.LOGS_ENABLED) {
                FileLog.d("UnifiedPush endpoint = " + currentPushString);
            }
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("No UnifiedPush string found");
            }
        }
        Utilities.globalQueue.postRunnable(() -> {
            try {
                SharedConfig.pushStringGetTimeStart = SystemClock.elapsedRealtime();
                SharedConfig.saveConfig();
                if (UnifiedPush.getAckDistributor(ApplicationLoader.applicationContext) == null) {
                    List<String> distributors = UnifiedPush.getDistributors(ApplicationLoader.applicationContext, new ArrayList<>());
                    if (distributors.size() > 0) {
                        String distributor =  distributors.get(0);
                        UnifiedPush.saveDistributor(ApplicationLoader.applicationContext, distributor);
                    }
                }
                UnifiedPush.registerApp(
                        ApplicationLoader.applicationContext,
                        "default",
                        new ArrayList<>(),
                        "Telegram Simple Push"
                );
            } catch (Throwable e) {
                FileLog.e(e);
            }
        });
    }

    @Override
    public int getPushType() {
        return PushListenerController.PUSH_TYPE_SIMPLE;
    }
}
