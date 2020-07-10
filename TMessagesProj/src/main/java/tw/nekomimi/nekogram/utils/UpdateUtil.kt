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
import org.telegram.messenger.BuildVars
import org.telegram.messenger.FileLog
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.tukaani.xz.XZInputStream
import tw.nekomimi.nekogram.BottomBuilder
import tw.nekomimi.nekogram.ExternalGcm
import tw.nekomimi.nekogram.NekoXConfig
import java.io.File
import java.util.zip.ZipInputStream

object UpdateUtil {

    val updateUrls = arrayOf("https://raw.githubusercontent.com/NekoX-Dev/Resources/master")

    @JvmStatic
    fun checkUpdate(ctx: Activity) = UIUtil.runOnIoDispatcher {

        if (BuildVars.isUnknown) {

            FileLog.d("${BuildConfig.BUILD_TYPE} version, skip update check.")

            return@runOnIoDispatcher

        }

        if (ExternalGcm.checkPlayServices()) {

            FileLog.d("checking updates from google play")

            ExternalGcm.checkUpdate(ctx)

            return@runOnIoDispatcher

        }

        FileLog.d("checking updates from repo")

        if (System.currentTimeMillis() - NekoXConfig.preferences.getLong("ignored_update_at", -1) < 1 * 60 * 60 * 1000L) {

            FileLog.d("ignored")

            return@runOnIoDispatcher

        }

        updateUrls.forEach { url ->

            runCatching {

                val updateInfo = JSONObject(HttpUtil.get("$url/update.json"))

                val code = updateInfo.getInt("versionCode")

                if (code > BuildConfig.VERSION_CODE.coerceAtLeast(NekoXConfig.preferences.getInt("ignore_update", -1))) UIUtil.runOnUIThread {

                    FileLog.d("update available")

                    val builder = BottomBuilder(ctx)

                    builder.addTitle(LocaleController.getString("UpdateAvailable", R.string.UpdateAvailable), updateInfo.getString("version"))

                    builder.addItem(LocaleController.getString("UpdateUpdate", R.string.UpdateUpdate), R.drawable.baseline_system_update_24, false) {

                        doUpdate(ctx, code, updateInfo.getString("defaultFlavor"))

                        NekoXConfig.preferences.edit().remove("ignored_update_at").remove("ignore_update_at").apply()

                    }

                    builder.addItem(LocaleController.getString("UpdateLater", R.string.UpdateLater), R.drawable.baseline_watch_later_24, false) {

                        NekoXConfig.preferences.edit().putLong("ignored_update_at", System.currentTimeMillis()).apply()

                    }

                    builder.addItem(LocaleController.getString("Ignore", R.string.Ignore), R.drawable.baseline_block_24, true) {

                        NekoXConfig.preferences.edit().putInt("ignore_update", code).apply()

                    }

                    runCatching {

                        builder.show()

                    }

                } else {

                    FileLog.d("no updates")

                }

                return@runOnIoDispatcher

            }.onFailure {

                FileLog.d(it.toString())

            }

        }

    }

    fun doUpdate(ctx: Activity, targetVer: Int, defFlavor: String, buildType: String = BuildConfig.BUILD_TYPE, flavor: String = BuildConfig.FLAVOR) {

        val pro = AlertUtil.showProgress(ctx)
        pro.setCanCacnel(false)
        pro.show()

        fun update(message: String) = UIUtil.runOnUIThread { pro.setMessage(message) }
        fun dismiss() = UIUtil.runOnUIThread { pro.dismiss() }

        var exception: Exception? = null

        UIUtil.runOnIoDispatcher {

            var fileName = "NekoX-$flavor-${Build.CPU_ABI}-$buildType.apk.xz"

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

                            fileName = "NekoX-$defFlavor-${Build.CPU_ABI}-$buildType.apk.xz"

                            response = HttpUtil.okHttpClient.newCall(Request.Builder().url("$url/$fileName").build()).execute()

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

            val target = File(ctx.externalCacheDir, "nekox-$targetVer-$flavor-$buildType.apk")

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

                            update("$progressSize / $size")

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