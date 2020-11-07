
package tw.nekomimi.nekogram.parts

import android.app.Activity
import android.content.IntentSender
import cn.hutool.core.util.StrUtil
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.delay
import org.json.JSONObject
import org.telegram.messenger.BuildConfig
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.BottomBuilder
import tw.nekomimi.nekogram.ExternalGcm
import tw.nekomimi.nekogram.NekoXConfig
import tw.nekomimi.nekogram.utils.*
import java.util.*

fun Activity.checkUpdate() {

    val progress = AlertUtil.showProgress(this)

    progress.show()

    UIUtil.runOnIoDispatcher {

        progress.uUpdate(LocaleController.getString("Checking", R.string.Checking) + " (Play Store)")

        val manager = AppUpdateManagerFactory.create(this)

        manager.registerListener {

            if (it.installStatus() == InstallStatus.DOWNLOADED) {

                val builder = BottomBuilder(this)

                builder.addTitle(LocaleController.getString("UpdateDownloaded", R.string.UpdateDownloaded))

                builder.addItem(LocaleController.getString("UpdateUpdate", R.string.UpdateUpdate), R.drawable.baseline_system_update_24, false) {

                    manager.completeUpdate()

                }

                builder.addItem(LocaleController.getString("UpdateLater", R.string.UpdateLater), R.drawable.baseline_watch_later_24, false, null)

                builder.show()

            }

        }

        manager.appUpdateInfo.addOnSuccessListener {

            progress.dismiss()

            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it.availableVersionCode() > BuildConfig.VERSION_CODE) {

                try {

                    manager.startUpdateFlowForResult(it, AppUpdateType.FLEXIBLE, this, 114514)

                } catch (ignored: IntentSender.SendIntentException) {
                }

            } else {

                UIUtil.runOnIoDispatcher {

                    delay(1000L)

                    AlertUtil.showToast(LocaleController.getString("NoUpdate", R.string.NoUpdate))

                }

            }

        }.addOnFailureListener {

            progress.uDismiss()

            AlertUtil.showToast(it.message ?: it.javaClass.simpleName)

        }

        return@runOnIoDispatcher

    }


}