package tw.nekomimi.nekogram

import android.annotation.SuppressLint
import android.os.Build
import android.service.notification.NotificationListenerService
import androidx.annotation.RequiresApi
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.KeepAliveJob

@SuppressLint("OverrideAbstract")
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class NekoXPushService : NotificationListenerService() {

    override fun onCreate() {

        super.onCreate()

        ApplicationLoader.postInitApplication()
        KeepAliveJob.startJob()

    }

}