/***
 * If you modify and release but do not release the source code, you violate the GPL, so this is made.
 *
 * @author nekohasekai
 */
package tw.nekomimi.nekogram.parts

import android.content.Context
import android.content.pm.PackageManager.GET_SIGNATURES
import android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES
import android.content.pm.Signature
import android.os.Build
import android.os.Process
import cn.hutool.crypto.digest.DigestUtil
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.FileLog

val devKeys = arrayOf(
    "32250A4B5F3A6733DF57A3B9EC16C38D2C7FC5F2F693A9636F8F7B3BE3549641"
)

fun Context.getSignature(): Signature {
    val appInfo = packageManager.getPackageInfo(
        packageName,
        if (Build.VERSION.SDK_INT >= 28) GET_SIGNING_CERTIFICATES else GET_SIGNATURES
    )
    return if (Build.VERSION.SDK_INT >= 28) {
        appInfo.signingInfo.apkContentsSigners[0]
    } else {
        appInfo.signatures[0]
    }
}

fun Context.getSha256Signature(): String {
    return DigestUtil.sha256Hex(getSignature().toByteArray()).uppercase()
}

fun Context.isVerified(): Boolean {
    val packageName = packageName
    if (!packageName.contains("nekox")) {
        FileLog.w("packageName changed, don't check signature")
        return true
    }
    when (val s = getSha256Signature()) {
        in devKeys,
        -> return true
        else -> {
            FileLog.w("Unknown signature: $s")
        }
    }
    return false
}

fun Context.checkMT() {
    val fuckMT = Runnable {
        Thread.setDefaultUncaughtExceptionHandler(null)
        Thread.currentThread().uncaughtExceptionHandler = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Process.killProcess(Process.myPid())
            } catch (e: Exception) {
            }
        }
        Runtime.getRuntime().exit(0)
    }

    try {
        Class.forName("bin.mt.apksignaturekillerplus.HookApplication")
        AndroidUtilities.runOnUIThread(fuckMT)
        return
    } catch (ignored: ClassNotFoundException) {
    }

    if (isVerified()) return

    val manifestMF = javaClass.getResourceAsStream("/META-INF/MANIFEST.MF")
    if (manifestMF == null) {
        FileLog.w("/META-INF/MANIFEST.MF not found")
        return
    }

    val input = manifestMF.bufferedReader()
    val headers = input.use { (0 until 5).map { readLine() } }.joinToString("\n")

    // WTF version?
    if (headers.contains("Android Gradle 3.5.0")) {
        AndroidUtilities.runOnUIThread(fuckMT)
    }

}