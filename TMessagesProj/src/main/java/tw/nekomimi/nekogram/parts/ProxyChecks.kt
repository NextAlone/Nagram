package tw.nekomimi.nekogram.parts

import android.os.SystemClock
import cn.hutool.core.thread.ThreadUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.NotificationCenter
import org.telegram.messenger.SharedConfig
import org.telegram.messenger.SharedConfig.ExternalSocks5Proxy
import org.telegram.ui.ProxyListActivity
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicBoolean

@JvmOverloads
fun ProxyListActivity.checkProxyList(force: Boolean, context: ExecutorService) {

    GlobalScope.launch(Dispatchers.IO) {

        SharedConfig.proxyList.forEach {

            if (it.checking || SystemClock.elapsedRealtime() - it.availableCheckTime < 2 * 60 * 1000L && !force) {

                return@forEach

            }

            it.checking = true

            context.execute {

                runCatching {

                    val lock = AtomicBoolean()

                    checkSingleProxy(it, if (it is ExternalSocks5Proxy) 3 else 0) {

                        AndroidUtilities.runOnUIThread {

                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxyCheckDone, it)

                        }

                        lock.set(true)

                    }

                    while (!lock.get()) ThreadUtil.sleep(100L)

                }

            }

        }

    }

}