package tw.nekomimi.nekogram.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.telegram.messenger.ApplicationLoader

object UIUtil {

    @JvmStatic
    fun runOnUIThread(runnable: Runnable) = ApplicationLoader.applicationHandler.post(runnable)

    fun runOnUIThread(runnable: () -> Unit) = ApplicationLoader.applicationHandler.post(runnable)

    @JvmStatic
    fun runOnIoDispatcher(runnable: Runnable) {

        GlobalScope.launch(Dispatchers.IO) {

            runnable.run()

        }

    }

    fun runOnIoDispatcher(runnable: () -> Unit) {

        GlobalScope.launch(Dispatchers.IO) {

            runnable()

        }

    }

}