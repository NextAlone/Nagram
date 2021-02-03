package tw.nekomimi.nekogram.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.util.Base64
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.setPadding
import com.google.zxing.*
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.v2ray.ang.V2RayConfig.SSR_PROTOCOL
import com.v2ray.ang.V2RayConfig.SS_PROTOCOL
import com.v2ray.ang.V2RayConfig.TROJAN_PROTOCOL
import com.v2ray.ang.V2RayConfig.VMESS1_PROTOCOL
import com.v2ray.ang.V2RayConfig.VMESS_PROTOCOL
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.telegram.messenger.*
import org.telegram.messenger.browser.Browser
import org.telegram.ui.ActionBar.BottomSheet
import java.io.File
import java.net.NetworkInterface
import java.util.*
import kotlin.collections.HashMap


object ProxyUtil {

    @JvmStatic
    fun isVPNEnabled(): Boolean {

        val networkList = mutableListOf<String>()

        runCatching {

            Collections.list(NetworkInterface.getNetworkInterfaces()).forEach {

                if (it.isUp) networkList.add(it.name)

            }

        }

        return networkList.contains("tun0")

    }

    @JvmStatic
    fun parseProxies(_text: String): MutableList<String> {

        val text = runCatching {

            String(Base64.decode(_text, Base64.NO_PADDING))

        }.recover {

            _text

        }.getOrThrow()

        val proxies = mutableListOf<String>()

        text.split('\n').map { it.split(" ") }.forEach {

            it.forEach { line ->

                if (line.startsWith("tg://proxy") ||
                        line.startsWith("tg://socks") ||
                        line.startsWith("https://t.me/proxy") ||
                        line.startsWith("https://t.me/socks") ||
                        line.startsWith(VMESS_PROTOCOL) ||
                        line.startsWith(VMESS1_PROTOCOL) ||
                        line.startsWith(SS_PROTOCOL) ||
                        line.startsWith(SSR_PROTOCOL) ||
                        line.startsWith(TROJAN_PROTOCOL) /*||
                    line.startsWith(RB_PROTOCOL)*/) {

                    runCatching { proxies.add(SharedConfig.parseProxyInfo(line).toUrl()) }

                }

            }

        }

        if (proxies.isEmpty()) error("no proxy link found")

        return proxies

    }

    @JvmStatic
    fun importFromClipboard(ctx: Activity) {

        var text = (ApplicationLoader.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip?.getItemAt(0)?.text?.toString()

        val proxies = mutableListOf<SharedConfig.ProxyInfo>()

        var error = false

        text?.trim()?.split('\n')?.map { it.split(" ") }?.forEach {

            it.forEach { line ->

                if (line.startsWith("tg://proxy") ||
                        line.startsWith("tg://socks") ||
                        line.startsWith("https://t.me/proxy") ||
                        line.startsWith("https://t.me/socks") ||
                        line.startsWith(VMESS_PROTOCOL) ||
                        line.startsWith(VMESS1_PROTOCOL) ||
                        line.startsWith(SS_PROTOCOL) ||
                        line.startsWith(SSR_PROTOCOL) ||
                        line.startsWith(TROJAN_PROTOCOL) /*||
                    line.startsWith(RB_PROTOCOL)*/) {

                    runCatching { proxies.add(SharedConfig.parseProxyInfo(line)) }.onFailure {

                        error = true

                        AlertUtil.showToast(LocaleController.getString("BrokenLink", R.string.BrokenLink) + ": ${it.message ?: it.javaClass.simpleName}")

                    }

                }

            }

        }

        runCatching {

            if (proxies.isNullOrEmpty() && !error) {

                String(Base64.decode(text, Base64.NO_PADDING)).trim().split('\n').map { it.split(" ") }.forEach { str ->

                    str.forEach { line ->

                        if (line.startsWith("tg://proxy") ||
                                line.startsWith("tg://socks") ||
                                line.startsWith("https://t.me/proxy") ||
                                line.startsWith("https://t.me/socks") ||
                                line.startsWith(VMESS_PROTOCOL) ||
                                line.startsWith(VMESS1_PROTOCOL) ||
                                line.startsWith(SS_PROTOCOL) ||
                                line.startsWith(SSR_PROTOCOL) ||
                                line.startsWith(TROJAN_PROTOCOL) /*||
                    line.startsWith(RB_PROTOCOL)*/) {

                            runCatching { proxies.add(SharedConfig.parseProxyInfo(line)) }.onFailure {

                                error = true

                                AlertUtil.showToast(LocaleController.getString("BrokenLink", R.string.BrokenLink) + ": ${it.message ?: it.javaClass.simpleName}")

                            }

                        }

                    }

                }

            }

        }

        if (proxies.isNullOrEmpty()) {

            if (!error) AlertUtil.showToast(LocaleController.getString("BrokenLink", R.string.BrokenLink))

            return

        } else if (!error) {

            AlertUtil.showSimpleAlert(ctx, LocaleController.getString("ImportedProxies", R.string.ImportedProxies) + "\n\n" + proxies.joinToString("\n") { it.title })

        }

        proxies.forEach {

            SharedConfig.addProxy(it)

        }

        UIUtil.runOnUIThread {

            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged)

        }

    }

    @JvmStatic
    fun importProxy(ctx: Context, link: String): Boolean {

        runCatching {

            if (link.startsWith(VMESS_PROTOCOL) || link.startsWith(VMESS1_PROTOCOL)) {

                AndroidUtilities.showVmessAlert(ctx, SharedConfig.VmessProxy(link))

            } else if (link.startsWith(TROJAN_PROTOCOL)) {

                AndroidUtilities.showTrojanAlert(ctx, SharedConfig.VmessProxy(link))

            } else if (link.startsWith(SS_PROTOCOL)) {

                AndroidUtilities.showShadowsocksAlert(ctx, SharedConfig.ShadowsocksProxy(link))

            } else if (link.startsWith(SSR_PROTOCOL)) {

                AndroidUtilities.showShadowsocksRAlert(ctx, SharedConfig.ShadowsocksRProxy(link))

            } else {

                val url = link.replace("tg://", "https://t.me/").toHttpUrlOrNull()!!

                AndroidUtilities.showProxyAlert(ctx,
                        url.queryParameter("server") ?: return false,
                        url.queryParameter("port") ?: return false,
                        url.queryParameter("user"),
                        url.queryParameter("pass"),
                        url.queryParameter("secret"),
                        url.fragment)


            }

            return true

        }.onFailure {

            FileLog.e(it)

            if (BuildVars.LOGS_ENABLED) {

                AlertUtil.showSimpleAlert(ctx, it)

            } else {

                AlertUtil.showToast("${LocaleController.getString("BrokenLink", R.string.BrokenLink)}: ${it.message}")

            }

        }

        return false

    }

    @JvmStatic
    fun importInBackground(link: String): SharedConfig.ProxyInfo {

        val info = runCatching {

            if (link.startsWith(VMESS_PROTOCOL) || link.startsWith(VMESS1_PROTOCOL)) {

                SharedConfig.VmessProxy(link)

            } else if (link.startsWith(SS_PROTOCOL)) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                    error(LocaleController.getString("MinApi21Required", R.string.MinApi21Required))

                }

                SharedConfig.ShadowsocksProxy(link)

            } else if (link.startsWith(SSR_PROTOCOL)) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                    error(LocaleController.getString("MinApi21Required", R.string.MinApi21Required))

                }

                SharedConfig.ShadowsocksRProxy(link)

            } else {

                SharedConfig.ProxyInfo.fromUrl(link)

            }

        }.getOrThrow()

        if (!(SharedConfig.addProxy(info) === info)) {

            error("already exists")

        }

        return info

    }

    @JvmStatic
    fun shareProxy(ctx: Activity, info: SharedConfig.ProxyInfo, type: Int) {

        val url = info.toUrl();

        if (type == 1) {

            AndroidUtilities.addToClipboard(url)

            Toast.makeText(ctx, LocaleController.getString("LinkCopied", R.string.LinkCopied), Toast.LENGTH_LONG).show()

        } else if (type == 0) {

            val shareIntent = Intent(Intent.ACTION_SEND)

            shareIntent.type = "text/plain"

            shareIntent.putExtra(Intent.EXTRA_TEXT, url)

            val chooserIntent = Intent.createChooser(shareIntent, LocaleController.getString("ShareLink", R.string.ShareLink))

            chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            ctx.startActivity(chooserIntent)

        } else {

            showQrDialog(ctx, url)

        }

    }

    @JvmStatic
    fun getOwnerActivity(ctx: Context): Activity {

        if (ctx is Activity) return ctx

        if (ctx is ContextWrapper) return getOwnerActivity(ctx.baseContext)

        error("unable cast ${ctx.javaClass.name} to activity")

    }

    @JvmStatic
    @JvmOverloads
    fun showQrDialog(ctx: Context, text: String, icon: ((Int) -> Bitmap)? = null): AlertDialog {

        val code = createQRCode(text, icon = icon)

        ctx.setTheme(R.style.Theme_TMessages)

        return AlertDialog.Builder(ctx).setView(LinearLayout(ctx).apply {

            gravity = Gravity.CENTER
            setBackgroundColor(Color.TRANSPARENT)

            addView(LinearLayout(ctx).apply {
                val root = this

                gravity = Gravity.CENTER
                setBackgroundColor(Color.WHITE)
                setPadding(AndroidUtilities.dp(16f))

                val width = AndroidUtilities.dp(260f)

                addView(ImageView(ctx).apply {

                    setImageBitmap(code)

                    scaleType = ImageView.ScaleType.FIT_XY

                    setOnLongClickListener {

                        BottomSheet.Builder(ctx).setItems(arrayOf(

                                LocaleController.getString("SaveToGallery", R.string.SaveToGallery),
                                LocaleController.getString("Cancel", R.string.Cancel)

                        ), intArrayOf(

                                R.drawable.baseline_image_24,
                                R.drawable.baseline_cancel_24

                        )) { _, i ->

                            if (i == 0) {

                                if (Build.VERSION.SDK_INT >= 23 && ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                                    getOwnerActivity(ctx).requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 4)

                                    return@setItems

                                }

                                val saveTo = File(Environment.getExternalStorageDirectory(), "${Environment.DIRECTORY_PICTURES}/share_${text.hashCode()}.jpg")

                                saveTo.parentFile?.mkdirs()

                                runCatching {

                                    saveTo.createNewFile()

                                    saveTo.outputStream().use {

                                        loadBitmapFromView(root).compress(Bitmap.CompressFormat.JPEG, 100, it);

                                    }

                                    AndroidUtilities.addMediaToGallery(saveTo.path)
                                    AlertUtil.showToast(LocaleController.getString("PhotoSavedHint", R.string.PhotoSavedHint))

                                }.onFailure {
                                    FileLog.e(it)
                                    AlertUtil.showToast(it)
                                }

                            }

                        }.show()

                        return@setOnLongClickListener true

                    }

                }, LinearLayout.LayoutParams(width, width))

            }, LinearLayout.LayoutParams(-2, -2).apply {

                gravity = Gravity.CENTER

            })

        }).create().apply {

            show()
            window?.setBackgroundDrawableResource(android.R.color.transparent)

        }

    }

    private fun loadBitmapFromView(v: View): Bitmap {
        val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }

    @JvmStatic
    fun createQRCode(text: String, size: Int = 768, icon: ((Int) -> Bitmap)? = null): Bitmap {
        return try {
            val hints = HashMap<EncodeHintType, Any>()
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M
            QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints, null, null, icon)
        } catch (e: WriterException) {
            FileLog.e(e);
            Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        }
    }

    val qrReader = QRCodeReader()

    @JvmStatic
    fun tryReadQR(ctx: Activity, bitmap: Bitmap) {

        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)

        try {

            val result = qrReader.decode(BinaryBitmap(GlobalHistogramBinarizer(source)), mapOf(
                    DecodeHintType.TRY_HARDER to true
            ))

            showLinkAlert(ctx, result.text)

        } catch (e: Throwable) {

            AlertUtil.showToast(LocaleController.getString("NoQrFound", R.string.NoQrFound))

        }

    }

    @JvmStatic
    fun showLinkAlert(ctx: Activity, text: String) {

        val builder = BottomSheet.Builder(ctx)

        var isUrl = false

        runCatching {
            text.replace("tg://", "https://t.me/").toHttpUrlOrNull()!!
            if (Browser.isInternalUrl(text, booleanArrayOf(false))) {
                Browser.openUrl(ctx, text)
                return
            }
            isUrl = true
        }

        builder.setTitle(text)

        builder.setItems(arrayOf(
                if (isUrl) LocaleController.getString("Open", R.string.OpenUrlTitle) else null,
                LocaleController.getString("Copy", R.string.Copy),
                LocaleController.getString("Cancel", R.string.Cancel)
        )) { _, i ->
            if (i == 0) {
                Browser.openUrl(ctx, text)
            } else if (i == 1) {
                AndroidUtilities.addToClipboard(text)
                Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("LinkCopied", R.string.LinkCopied), Toast.LENGTH_LONG).show()
            }
        }

        builder.show()

    }

}
