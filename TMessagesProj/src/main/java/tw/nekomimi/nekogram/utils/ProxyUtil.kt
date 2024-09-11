@file:Suppress("UNCHECKED_CAST")

package tw.nekomimi.nekogram.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Environment
import android.util.Base64
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.setPadding
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.EncodeHintType
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.WriterException
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import org.telegram.messenger.LocaleController
import org.telegram.messenger.NotificationCenter
import org.telegram.messenger.R
import org.telegram.messenger.SharedConfig
import org.telegram.messenger.browser.Browser
import tw.nekomimi.nekogram.ui.BottomBuilder
import tw.nekomimi.nekogram.utils.AlertUtil.showToast
import java.io.File


object ProxyUtil {

    @JvmStatic
    fun isVPNEnabled(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false
        }
        runCatching {
            val connectivityManager = ApplicationLoader.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            return networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        }
        return false
    }

    @JvmStatic
    fun registerNetworkCallback() {
        val connectivityManager = ApplicationLoader.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback: ConnectivityManager.NetworkCallback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    val networkCapabilities =
                        connectivityManager.getNetworkCapabilities(network) ?: return
                    val vpn = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                    if (!vpn) {
                        if (SharedConfig.currentProxy == null) {
                            if (!SharedConfig.proxyList.isEmpty()) {
                                SharedConfig.setCurrentProxy(SharedConfig.proxyList[0])
                            } else {
                                return
                            }
                        }
                    }
                    if ((SharedConfig.isProxyEnabled() && vpn) || (!SharedConfig.isProxyEnabled() && !vpn)) {
                        SharedConfig.setProxyEnable(!vpn)
                        UIUtil.runOnUIThread(Runnable {
                            NotificationCenter.getGlobalInstance()
                                .postNotificationName(NotificationCenter.proxySettingsChanged)
                        })
                    }
                }
            }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
            } else {
                val request: NetworkRequest = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
                connectivityManager.registerNetworkCallback(request, networkCallback)
            }
        } catch (ignored: Exception) {}
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

                        val builder = BottomBuilder(ctx)

                        builder.addItems(arrayOf(

                                LocaleController.getString(R.string.SaveToGallery),
                                LocaleController.getString(R.string.Cancel)

                        ), intArrayOf(

                                R.drawable.baseline_image_24,
                                R.drawable.baseline_cancel_24

                        )) { i, _, _ ->

                            if (i == 0) {

                                if (Build.VERSION.SDK_INT >= 23 && ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                                    getOwnerActivity(ctx).requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 4)

                                    return@addItems

                                }

                                val saveTo = File(Environment.getExternalStorageDirectory(), "${Environment.DIRECTORY_PICTURES}/share_${text.hashCode()}.jpg")

                                saveTo.parentFile?.mkdirs()

                                runCatching {

                                    saveTo.createNewFile()

                                    saveTo.outputStream().use {

                                        loadBitmapFromView(root).compress(Bitmap.CompressFormat.JPEG, 100, it);

                                    }

                                    AndroidUtilities.addMediaToGallery(saveTo.path)
                                    showToast(LocaleController.getString(R.string.PhotoSavedHint))

                                }.onFailure {
                                    FileLog.e(it)
                                    showToast(it)
                                }

                            }

                        }

                        builder.show()

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

            val result = try {
                qrReader.decode(BinaryBitmap(GlobalHistogramBinarizer(source)), mapOf(
                        DecodeHintType.TRY_HARDER to true
                ))
            } catch (e: NotFoundException) {
                qrReader.decode(BinaryBitmap(GlobalHistogramBinarizer(source.invert())), mapOf(
                        DecodeHintType.TRY_HARDER to true
                ))
            }

            showLinkAlert(ctx, result.text)

        } catch (e: Throwable) {

            showToast(LocaleController.getString(R.string.NoQrFound))

        }

    }

    @JvmStatic
    @JvmOverloads
    fun showLinkAlert(ctx: Activity, text: String, tryInternal: Boolean = true) {

        val builder = BottomBuilder(ctx)

        if (tryInternal) {
            runCatching {
                if (Browser.isInternalUrl(text, booleanArrayOf(false))) {
                    Browser.openUrl(ctx, text)
                    return
                }
            }
        }

        builder.addTitle(text)

        builder.addItems(arrayOf(
                LocaleController.getString(R.string.Open),
                LocaleController.getString(R.string.Copy),
                LocaleController.getString(R.string.ShareQRCode)
        ), intArrayOf(
                R.drawable.baseline_open_in_browser_24,
                R.drawable.baseline_content_copy_24,
                R.drawable.wallet_qr
        )) { which, _, _ ->
            when (which) {
                0 -> Browser.openUrl(ctx, text)
                1 -> {
                    AndroidUtilities.addToClipboard(text)
                    showToast(LocaleController.getString(R.string.LinkCopied))
                }
                else -> showQrDialog(ctx, text)
            }
        }

        builder.show()

    }

    @JvmStatic
    fun importFromClipboard(ctx: Activity) {

        val text = (ApplicationLoader.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip?.getItemAt(0)?.text?.toString()

        val proxies = mutableListOf<SharedConfig.ProxyInfo>()

        var error = false

        text?.trim()?.split('\n')?.map { it.split(" ") }?.forEach {

            it.forEach { line ->

                if (line.startsWith("tg://proxy") ||
                    line.startsWith("tg://socks") ||
                    line.startsWith("https://t.me/proxy") ||
                    line.startsWith("https://t.me/socks")) {

                    runCatching { proxies.add(SharedConfig.ProxyInfo.fromUrl(line)) }.onFailure {

                        error = true

                        showToast(LocaleController.getString(R.string.BrokenLink) + ": ${it.message ?: it.javaClass.simpleName}")

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
                            line.startsWith("https://t.me/socks")) {

                            runCatching { proxies.add(SharedConfig.ProxyInfo.fromUrl(line)) }.onFailure {

                                error = true

                                showToast(LocaleController.getString(R.string.BrokenLink) + ": ${it.message ?: it.javaClass.simpleName}")

                            }

                        }

                    }

                }

            }

        }

        if (proxies.isNullOrEmpty()) {

            if (!error) showToast(LocaleController.getString(R.string.BrokenLink))

            return

        } else if (!error) {

            AlertUtil.showSimpleAlert(ctx, LocaleController.getString(R.string.ImportedProxies) + "\n\n" + proxies.joinToString("\n") { it.address })

        }

        proxies.forEach {

            SharedConfig.addProxy(it)

        }

        UIUtil.runOnUIThread {

            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged)

        }

    }

    @JvmStatic
    fun isIpv6Address(value: String): Boolean {
        var addr = value
        if (addr.indexOf("[") == 0 && addr.lastIndexOf("]") > 0) {
            addr = addr.drop(1)
            addr = addr.dropLast(addr.count() - addr.lastIndexOf("]"))
        }
        val regV6 = Regex("^((?:[0-9A-Fa-f]{1,4}))?((?::[0-9A-Fa-f]{1,4}))*::((?:[0-9A-Fa-f]{1,4}))?((?::[0-9A-Fa-f]{1,4}))*|((?:[0-9A-Fa-f]{1,4}))((?::[0-9A-Fa-f]{1,4})){7}$")
        return regV6.matches(addr)
    }
}
