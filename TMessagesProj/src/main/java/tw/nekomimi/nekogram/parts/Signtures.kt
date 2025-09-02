/***
 * If you modify and release but do not release the source code, you violate the GPL, so this is made.
 *
 * @author nekohasekai
 */
package tw.nekomimi.nekogram.parts

import android.content.Context
import android.os.Build
import android.os.Process
import org.telegram.messenger.AndroidUtilities

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
}
