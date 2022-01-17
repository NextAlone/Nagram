package tw.nekomimi.nekogram.proxy

import java.net.InetSocketAddress
import java.net.ServerSocket
import kotlin.random.Random

object ProxyManager {

    @JvmStatic
    fun mkPort(): Int {

        var port: Int

        do {

            port = mkNewPort()

        } while (!isProxyAvailable(port))

        return port

    }

    private fun mkNewPort() = Random.nextInt(2048, 32768)

    @JvmStatic
    fun isProxyAvailable(port: Int): Boolean {

        if (port !in 2048 until 32768) return false

        runCatching {

            val server = ServerSocket()

            server.bind(InetSocketAddress("127.0.0.1",port))

            server.close()

            Thread.sleep(1000L)

        }.onFailure {

            return false

        }

        return true

    }

}
