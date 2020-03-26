package tw.nekomimi.nekogram.utils

import android.os.Handler
import android.os.Looper

object UIUtil {

    val handler = Handler(Looper.getMainLooper())

    @JvmStatic
    fun runOnUIThread(runnable: Runnable) = handler.post(runnable)

}