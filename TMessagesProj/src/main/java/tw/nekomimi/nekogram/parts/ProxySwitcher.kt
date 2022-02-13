package tw.nekomimi.nekogram.parts

import org.telegram.messenger.SharedConfig
import org.telegram.tgnet.ConnectionsManager
import tw.nekomimi.nekogram.NekoConfig
import java.util.*

object ProxySwitcher {

    var currentConnectionState = ConnectionsManager.ConnectionStateWaitingForNetwork
    val switchTimer by lazy { Timer("Proxy Switch Timer") }
    var currentTask: SwitchTask? = null

    fun cancel() {

        currentTask = null
        switchTimer.purge()

    }

    fun reschedule() {

        cancel()
        switchTimer.schedule(SwitchTask().also { currentTask = it }, 3333L)

    }

    @JvmStatic
    fun didReceivedNotification(connectionState: Int) {

        if (!NekoConfig.proxyAutoSwitch.Bool()) return

        currentConnectionState = connectionState

        if (currentConnectionState == ConnectionsManager.ConnectionStateConnectingToProxy) {

            reschedule()

        } else {

            cancel()

        }

    }

    class SwitchTask : TimerTask() {

        override fun run() {

            if (this != currentTask) return

            if (currentConnectionState != ConnectionsManager.ConnectionStateConnectingToProxy) return

            var proxyList = SharedConfig.getProxyList().takeIf { it.size > 1 } ?: return

            val current = SharedConfig.currentProxy ?: return

            val currIndex = proxyList.indexOf(current)

            if (currIndex > 0) {

                val proxyListNew = LinkedList<SharedConfig.ProxyInfo>()

                proxyListNew.addAll(proxyList.subList(currIndex, proxyList.size))
                proxyListNew.addAll(proxyList.subList(0, currIndex + 1))

                proxyList = proxyListNew

            }

            if (proxyList.all { it.availableCheckTime == 0L }) {

                if (proxyList.all { !it.checking }) {

                    repeat(3) { postCheckProxyList() }

                }

                if (currentConnectionState != ConnectionsManager.ConnectionStateConnectingToProxy) return

                SharedConfig.setCurrentProxy(proxyList[0])

                reschedule()

                return

            }

            proxyList.forEach {

                if (it.availableCheckTime != 0L && !it.available) return@forEach

                if (currentConnectionState != ConnectionsManager.ConnectionStateConnectingToProxy) return

                SharedConfig.setCurrentProxy(it)

                reschedule()

                return

            }

        }

    }

}