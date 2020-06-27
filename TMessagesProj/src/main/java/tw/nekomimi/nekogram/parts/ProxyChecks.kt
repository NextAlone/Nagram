package tw.nekomimi.nekogram.parts

import android.os.SystemClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.NotificationCenter
import org.telegram.messenger.SharedConfig
import org.telegram.messenger.SharedConfig.ExternalSocks5Proxy
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.ConnectionsManager
import org.telegram.ui.ProxyListActivity
import tw.nekomimi.nekogram.utils.UIUtil
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicBoolean

private suspend fun postCheckSingleProxy(proxyInfo: SharedConfig.ProxyInfo, repeat: Int) {

    val lock = AtomicBoolean()

    if (proxyInfo is ExternalSocks5Proxy && !proxyInfo.isStarted) {
        proxyInfo.start()
        delay(233L)
    }

    var time = -1L
    val startAt = SystemClock.elapsedRealtime()

    proxyInfo.proxyCheckPingId = ConnectionsManager.getInstance(UserConfig.selectedAccount).checkProxy(proxyInfo.address, proxyInfo.port, proxyInfo.username, proxyInfo.password, proxyInfo.secret) {
        time = it
        lock.set(true)
    }

    while (!lock.get() && SystemClock.elapsedRealtime() - startAt < 4000L) delay(100L)

    if (!lock.get()) {

        proxyInfo.availableCheckTime = SystemClock.elapsedRealtime()
        proxyInfo.checking = false
        proxyInfo.available = false
        proxyInfo.ping = 0
        if (proxyInfo is ExternalSocks5Proxy && proxyInfo !== SharedConfig.currentProxy) {
            proxyInfo.stop()
        }

        return

    }

    if (time == -1L) {
        if (repeat > 0) {
            postCheckSingleProxy(proxyInfo, repeat - 1)
        } else {
            proxyInfo.availableCheckTime = SystemClock.elapsedRealtime()
            proxyInfo.checking = false
            proxyInfo.available = false
            proxyInfo.ping = -1L
            if (proxyInfo is ExternalSocks5Proxy && proxyInfo !== SharedConfig.currentProxy) {
                proxyInfo.stop()
            }
        }
    } else {
        proxyInfo.availableCheckTime = SystemClock.elapsedRealtime()
        proxyInfo.checking = false
        proxyInfo.ping = time
        proxyInfo.available = true
        if (proxyInfo is ExternalSocks5Proxy && proxyInfo !== SharedConfig.currentProxy) {
            proxyInfo.stop()
        }
    }

}

fun postCheckProxyList() = GlobalScope.launch(Dispatchers.IO) {

    SharedConfig.getProxyList().forEach { proxyInfo ->

        if (proxyInfo.checking || SystemClock.elapsedRealtime() - proxyInfo.availableCheckTime < 2 * 60 * 1000L) {

            return@forEach

        }

        synchronized(proxyInfo) {

            if (proxyInfo.checking || SystemClock.elapsedRealtime() - proxyInfo.availableCheckTime < 2 * 60 * 1000L) {

                return@forEach

            }

            proxyInfo.checking = true

        }

        runCatching {

            postCheckSingleProxy(proxyInfo, 1)

        }.onFailure {

            proxyInfo.availableCheckTime = SystemClock.elapsedRealtime()
            proxyInfo.checking = false
            proxyInfo.available = false
            proxyInfo.ping = 0

        }

    }

}

fun ProxyListActivity.checkProxyList(force: Boolean, context: ExecutorService) {

    GlobalScope.launch(Dispatchers.IO) {

        SharedConfig.proxyList.toList().forEach {

            if (it.checking || SystemClock.elapsedRealtime() - it.availableCheckTime < 2 * 60 * 1000L && !force) {

                return@forEach

            }

            it.checking = true

            runCatching {

                context.execute {

                    runCatching {

                        val lock = AtomicBoolean()

                        val startAt = SystemClock.elapsedRealtime()

                        UIUtil.runOnUIThread { NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxyCheckDone, it) }

                        checkSingleProxy(it, if (it is ExternalSocks5Proxy) 3 else 1) {

                            AndroidUtilities.runOnUIThread {

                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxyCheckDone, it)

                            }

                            lock.set(true)

                        }

                        while (!lock.get() && SystemClock.elapsedRealtime() - startAt < 4000L) Thread.sleep(100L)

                        if (!lock.get()) {

                            it.availableCheckTime = SystemClock.elapsedRealtime()
                            it.checking = false
                            it.available = false
                            it.ping = 0
                            if (it is ExternalSocks5Proxy && it !== SharedConfig.currentProxy) {
                                it.stop()
                            }

                            AndroidUtilities.runOnUIThread {

                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxyCheckDone, it)

                            }


                        }
                    }

                }

            }.onFailure {

                return@launch

            }

        }

    }

}