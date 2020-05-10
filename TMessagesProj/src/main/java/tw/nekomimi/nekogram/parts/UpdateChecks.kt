package tw.nekomimi.nekogram.parts

import android.app.Activity
import org.json.JSONObject
import org.telegram.messenger.BuildConfig
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.ui.Cells.TextCell
import tw.nekomimi.nekogram.BottomBuilder
import tw.nekomimi.nekogram.NekoXConfig
import tw.nekomimi.nekogram.utils.*
import java.util.*

fun Activity.switchVersion() {

    val builder = BottomBuilder(this)

    builder.addItems(arrayOf(
            "Mini Release",
            "Mini Release NoGcm",
            "Full Release",
            "Full Release NoGcm"
    ).filterIndexed { index, _ ->

        !(BuildConfig.BUILD_TYPE == when {
            index % 2 != 0 -> "release"
            else -> "releaseNoGcm"
        } && BuildConfig.FLAVOR == when {
            index < 3 -> "mini"
            else -> "release"
        })

    }.toTypedArray()) { index: Int, _: String, _: TextCell ->

        val buildType = when {
            index % 2 != 0 -> "release"
            else -> "releaseNoGcm"
        }

        val flavor = when {
            index < 3 -> "mini"
            else -> "release"
        }

        val progress = AlertUtil.showProgress(this)

        progress.show()

        UIUtil.runOnIoDispatcher {

            val ex = mutableListOf<Throwable>()

            UpdateUtil.updateUrls.forEach { url ->

                runCatching {

                    val updateInfo = JSONObject(HttpUtil.get("$url/update.json"))

                    val code = updateInfo.getInt("versionCode")

                    progress.dismiss()

                    UpdateUtil.doUpdate(this, code, updateInfo.getString("defaultFlavor"), buildType, flavor)

                    return@runOnIoDispatcher

                }.onFailure {

                    ex.add(it)

                }

            }

            progress.dismiss()

            AlertUtil.showToast(ex.joinToString("\n") { it.message ?: it.javaClass.simpleName })

        }

    }

}

@JvmOverloads
fun Activity.checkUpdate(force: Boolean = false) {

    val progress = AlertUtil.showProgress(this)

    progress.show()

    UIUtil.runOnIoDispatcher {

        progress.uUpdate(LocaleController.getString("Checking", R.string.Checking) + " (Repo)")

        val ex = LinkedList<Throwable>()

        UpdateUtil.updateUrls.forEach { url ->

            runCatching {

                val updateInfo = JSONObject(HttpUtil.get("$url/update.json"))

                val code = updateInfo.getInt("versionCode")

                progress.uDismiss()

                if (code > BuildConfig.VERSION_CODE || force) UIUtil.runOnUIThread {

                    val builder = BottomBuilder(this)

                    builder.addTitle(LocaleController.getString("UpdateAvailable", R.string.UpdateAvailable), updateInfo.getString("version"))

                    builder.addItem(LocaleController.getString("UpdateUpdate", R.string.UpdateUpdate), R.drawable.baseline_system_update_24, false) {

                        UpdateUtil.doUpdate(this, code, updateInfo.getString("defaultFlavor"))

                        builder.dismiss()

                        NekoXConfig.preferences.edit().remove("ignored_update_at").remove("ignore_update_at").apply()

                    }

                    builder.addItem(LocaleController.getString("UpdateLater", R.string.UpdateLater), R.drawable.baseline_watch_later_24, false) {

                        builder.dismiss()

                        NekoXConfig.preferences.edit().putLong("ignored_update_at", System.currentTimeMillis()).apply()

                    }

                    builder.addItem(LocaleController.getString("Ignore", R.string.Ignore), R.drawable.baseline_block_24, true) {

                        builder.dismiss()

                        NekoXConfig.preferences.edit().putInt("ignore_update", code).apply()

                    }

                    builder.show()

                } else {

                    AlertUtil.showToast(LocaleController.getString("NoUpdate", R.string.NoUpdate))

                }

                return@runOnIoDispatcher

            }.onFailure {

                ex.add(it)

            }

        }

        progress.uDismiss()

        AlertUtil.showToast(ex.joinToString("\n") { it.message ?: it.javaClass.simpleName })

    }

}