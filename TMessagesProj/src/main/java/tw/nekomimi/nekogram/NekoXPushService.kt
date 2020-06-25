package tw.nekomimi.nekogram

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.service.notification.NotificationListenerService
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.KeepAliveJob
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R

@SuppressLint("OverrideAbstract")
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class NekoXPushService : NotificationListenerService() {

    override fun onCreate() {

        super.onCreate()

        ApplicationLoader.postInitApplication()
        KeepAliveJob.startJob()

    }

}