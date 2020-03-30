package tw.nekomimi.nekogram.utils

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

object UIUtil {

    val handler = Handler(Looper.getMainLooper())

    @JvmStatic
    fun runOnUIThread(runnable: Runnable) = handler.post(runnable)

    @JvmStatic
    fun runOnIoDispatcher(runnable: Runnable) {

        GlobalScope.launch(Dispatchers.IO) {

            runnable.run()

        }

    }

}