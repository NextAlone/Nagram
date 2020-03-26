package tw.nekomimi.nekogram

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.ui.LaunchActivity

@SuppressLint("OverrideAbstract")
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class NekoXPushService : NotificationListenerService() {

    override fun onCreate() {
        super.onCreate()
        ApplicationLoader.postInitApplication()
        if (NekoConfig.residentNotification) {
            val activityIntent = Intent(this, LaunchActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                val channel = NotificationChannel("nekogram", LocaleController.getString("NekogramRunning", R.string.NekogramRunning), NotificationManager.IMPORTANCE_DEFAULT)
                channel.enableLights(false)
                channel.enableVibration(false)
                channel.setSound(null, null)
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
            val notification = NotificationCompat.Builder(this, "nekogram")
                    .setSmallIcon(R.drawable.notification)
                    .setColor(-0xee5306)
                    .setContentTitle(LocaleController.getString("NekogramRunning", R.string.NekogramRunning))
                    .setContentIntent(pendingIntent)
                    .setCategory(NotificationCompat.CATEGORY_STATUS)
                    .build()
            startForeground(38264, notification)
        }
    }

}