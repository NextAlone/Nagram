package tw.nekomimi.nekogram.utils

import android.app.Activity
import android.os.Build
import cn.hutool.core.io.IoUtil
import cn.hutool.core.io.StreamProgress
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly
import org.json.JSONObject
import org.telegram.messenger.BuildConfig
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.tukaani.xz.XZInputStream
import tw.nekomimi.nekogram.BottomBuilder
import tw.nekomimi.nekogram.ExternalGcm
import tw.nekomimi.nekogram.NekoXConfig
import java.io.File
import java.util.zip.ZipInputStream

object UpdateUtil {

    val updateUrls = arrayOf(
            "https://gitlab.com/NekoX/Resources/-/raw/master",
            "https://raw.githubusercontent.com/NekoX-Dev/Resources/master"
    )

    @JvmStatic
    fun checkUpdate(ctx: Activity) = UIUtil.runOnIoDispatcher {

        if (ExternalGcm.checkPlayServices()) {

            ExternalGcm.checkUpdate(ctx)

            return@runOnIoDispatcher

        }

        if (System.currentTimeMillis() - NekoXConfig.preferences.getLong("ignored_update_at", -1) > 1 * 60 * 60 * 1000L) {

            // ignored

            return@runOnIoDispatcher

        }

        updateUrls.forEach { url ->

            runCatching {

                val updateInfo = JSONObject(HttpUtil.get("$url/update.json"))

                val code = updateInfo.getInt("versionCode")

                if (code > BuildConfig.VERSION_CODE.coerceAtLeast(NekoXConfig.preferences.getInt("ignore_update", -1))) UIUtil.runOnUIThread {

                    val builder = BottomBuilder(ctx)

                    builder.addTitle(LocaleController.getString("UpdateAvailable", R.string.UpdateAvailable), updateInfo.getString("version"))

                    builder.addItem(LocaleController.getString("UpdateUpdate", R.string.UpdateUpdate), R.drawable.baseline_system_update_24, false) {

                        doUpdate(ctx, code, updateInfo.getString("defaultApkName"))

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

                }

                return@runOnIoDispatcher

            }

        }

    }

    fun doUpdate(ctx: Activity, targetVer: Int, defFileName: String) {

        val pro = AlertUtil.showProgress(ctx)
        pro.setCanCacnel(false)
        pro.show()

        fun update(message: String) = UIUtil.runOnUIThread { pro.setMessage(message) }
        fun dismiss() = UIUtil.runOnUIThread { pro.dismiss() }

        var exception: Exception? = null

        UIUtil.runOnIoDispatcher {

            val fileName = "NekoX-${BuildConfig.FLAVOR}-${Build.CPU_ABI}-${BuildConfig.BUILD_TYPE}.apk.xz"

            var response: Response? = null

            runCatching {

                for (url in updateUrls) {

                    try {

                        response = HttpUtil.okHttpClient.newCall(Request.Builder().url("$url/$fileName").build()).execute()

                        if (response!!.code != 200) error("HTTP ${response!!.code} :${response!!.body!!.string()}")

                        break

                    } catch (e: Exception) {

                        exception = e

                    }

                }

                if (response == null) {

                    for (url in updateUrls) {

                        try {

                            response = HttpUtil.okHttpClient.newCall(Request.Builder().url("$url/$defFileName").build()).execute()

                            if (response!!.code != 200) error("HTTP ${response!!.code} :${response!!.body!!.string()}")

                            break

                        } catch (e: Exception) {

                            exception = e

                        }

                    }

                }

            }.onFailure {

                dismiss()

                AlertUtil.showSimpleAlert(ctx, LocaleController.getString("DownloadFailed", R.string.DownloadFailed), it.toString())

                return@runOnIoDispatcher

            }

            if (response == null) {

                dismiss()

                AlertUtil.showSimpleAlert(ctx, LocaleController.getString("DownloadFailed", R.string.DownloadFailed), exception!!.toString())

                return@runOnIoDispatcher

            }

            val body = response!!.body!!

            val size = body.contentLength()

            val target = File(ctx.externalCacheDir, "nekox-$targetVer.apk")

            update("Downloading...")

            if (target.isFile) {

                runCatching {

                    ZipInputStream(target.inputStream()).use { it.nextEntry }

                    dismiss()

                    ShareUtil.openFile(ctx, target)

                    return@runOnIoDispatcher

                }.onFailure {

                    target.delete()

                }

            }

            runCatching {

                val input = XZInputStream(body.byteStream())

                FileUtil.initFile(target)

                target.outputStream().use {

                    IoUtil.copy(input, it, IoUtil.DEFAULT_BUFFER_SIZE, object : StreamProgress {

                        override fun progress(progressSize: Long) {

                            update("$progressSize / $size ( ${((progressSize * 100 / size).toFloat() / 100F).toInt()}% )")

                        }

                        override fun start() {

                            update("0 / $size")

                        }

                        override fun finish() {

                            update("Finish")

                        }
                    })

                }

                input.closeQuietly()

                dismiss()

                ShareUtil.openFile(ctx, target)

            }.onFailure {

                dismiss()

                AlertUtil.showSimpleAlert(ctx, LocaleController.getString("DownloadFailed", R.string.DownloadFailed), it.toString())

            }

        }

    }

}