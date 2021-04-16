package tw.nekomimi.nekogram.parts

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_SIGNATURES
import android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES
import android.os.Build
import cn.hutool.crypto.digest.DigestUtil
import org.telegram.messenger.FileLog

val fdroidKeys = arrayOf(
        "06665358EFD8BA05BE236A47A12CB0958D7D75DD939D77C2B31F5398537EBDC5",
        "AF1A476E2D85FA33C55E44FC51D9CE93223A94F5D089F47F8CE06372E597041D"
)

const val devKey = "32250A4B5F3A6733DF57A3B9EC16C38D2C7FC5F2F693A9636F8F7B3BE3549641"

@Throws(PackageManager.NameNotFoundException::class)
fun Context.getSha256Signature(packageName: String): String {
    val appInfo = packageManager.getPackageInfo(
            packageName,
            if (Build.VERSION.SDK_INT >= 28) GET_SIGNING_CERTIFICATES else GET_SIGNATURES
    )

    return DigestUtil.sha256Hex(
            if (Build.VERSION.SDK_INT >= 28) {
                appInfo.signingInfo.apkContentsSigners[0].toByteArray()
            } else {
                appInfo.signatures[0].toByteArray()
            }
    ).toUpperCase()
}

fun Context.isVerified(): Boolean {
    val packageName = packageName
    if (!packageName.contains("nekox")) {
        FileLog.w("packageName changed, don't check signature")
        return true
    }
    when (val s = getSha256Signature(packageName)) {
        devKey,
        in fdroidKeys -> return true
        else -> {
            FileLog.w("Unknown signature: $s")
        }
    }
    return false
}