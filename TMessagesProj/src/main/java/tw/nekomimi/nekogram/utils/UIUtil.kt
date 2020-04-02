package tw.nekomimi.nekogram.utils

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import org.telegram.messenger.ApplicationLoader

object UIUtil {

    @JvmStatic
    fun runOnUIThread(runnable: Runnable) = ApplicationLoader.applicationHandler.post(runnable)

    @JvmStatic
    fun runOnIoDispatcher(runnable: Runnable) {

        GlobalScope.launch(Dispatchers.IO) {

            runnable.run()

        }

    }

}