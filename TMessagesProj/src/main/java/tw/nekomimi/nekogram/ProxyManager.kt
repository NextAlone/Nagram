package tw.nekomimi.nekogram

import android.content.Context
import com.v2ray.ang.dto.AngConfig
import org.telegram.messenger.ApplicationLoader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import kotlin.random.Random

object ProxyManager {

    val pref by lazy { ApplicationLoader.applicationContext.getSharedPreferences("port_cfg", Context.MODE_PRIVATE) }

    @JvmStatic
    fun getPortForBean(bean: AngConfig.VmessBean): Int {

        val hash = (bean.address + bean.port + bean.id + bean.network + bean.path).hashCode().toString()

        var port = pref.getInt(hash, -1)

        if (!isTcpPortAvailable(port)) {

            port = mkNewPort()

            pref.edit().putInt(hash, port).apply()

        }

        return port

    }

    @JvmStatic
    fun getPortForBean(bean: ShadowsocksLoader.Bean): Int {

        val hash = bean.hash.toString()

        var port = pref.getInt(hash, -1)

        if (!isTcpPortAvailable(port)) {

            port = mkNewPort()

            pref.edit().putInt(hash, port).apply()

        }

        return port

    }

    @JvmStatic
    fun getPortForBean(bean: ShadowsocksRLoader.Bean): Int {

        val hash = bean.hash.toString()

        var port = pref.getInt(hash, -1)

        if (!isTcpPortAvailable(port)) {

            port = mkNewPort()

            pref.edit().putInt(hash, port).apply()

        }

        return port

    }

    @JvmStatic
    fun mkNewPort() = Random.nextInt(2048, 32768)

    @JvmStatic
    open fun isTcpPortAvailable(port: Int): Boolean {
        try {
            ServerSocket(port).use {
                it.reuseAddress = true
                return true
            }
        } catch (ex: Exception) {
            return false
        }
    }

}
