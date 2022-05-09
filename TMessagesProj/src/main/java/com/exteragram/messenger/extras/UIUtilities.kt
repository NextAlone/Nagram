package com.exteragram.messenger.extras

import kotlinx.coroutines.*

import org.telegram.messenger.ApplicationLoader
import java.lang.Runnable

object UIUtilities {
    @JvmStatic
    fun runOnUIThread(runnable: Runnable) = ApplicationLoader.applicationHandler.post(runnable)

    fun runOnUIThread(runnable: () -> Unit) = ApplicationLoader.applicationHandler.post(runnable)

    @JvmStatic
    @JvmOverloads
    fun runOnIoDispatcher(runnable: Runnable, delay: Long = 0) {
        GlobalScope.launch(Dispatchers.IO) {
            delay(delay)
            runnable.run()
        }
    }

    fun runOnIoDispatcher(runnable: suspend () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            runnable()
        }
    }
}