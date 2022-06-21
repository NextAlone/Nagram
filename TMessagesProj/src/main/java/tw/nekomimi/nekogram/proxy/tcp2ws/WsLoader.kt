package tw.nekomimi.nekogram.proxy.tcp2ws

import cn.hutool.core.codec.Base64
import cn.hutool.core.util.StrUtil
import okhttp3.Dns
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import tw.nekomimi.nekogram.NekoConfig
import java.net.InetAddress

class WsLoader {

    lateinit var server: Tcp2wsServer

    fun init(bean: Bean, port: Int) {
        server = Tcp2wsServer(bean, port)
    }

    fun start() {
        server.start()
    }

    fun stop() {
        if (::server.isInitialized) {
            server.interrupt()
        }
    }

    companion object {

        fun parse(url: String): Bean {
            val lnk = url.replace("ws://", "http://")
                    .replace("wss://", "https://")
                    .toHttpUrlOrNull() ?: error("Invalid link")
            val payloadStr = lnk.queryParameter("payload") ?: error("Missing payload")
            val payload = Base64.decodeStr(payloadStr).split(",")
            if (payload.size < 5) error("Invalid payload")
            return Bean(
                    lnk.host,
                    payload,
                    lnk.isHttps,
                    lnk.fragment ?: ""
            )
        }

    }

    data class Bean(
            var server: String = "",
            var payload: List<String> = arrayListOf(),
            var tls: Boolean = true,
            var remarks: String = ""
    ) {

        var payloadStr: String
            get() = Base64.encodeUrlSafe(payload.joinToString(","))
            set(value) {
                payload = Base64.decodeStr(value).split(",")
            }

        override fun toString(): String {
            val builder = HttpUrl.Builder()
                    .scheme("http")
                    .host(server)
                    .addQueryParameter("payload", payloadStr);
            if (remarks.isNotBlank()) {
                builder.fragment(remarks)
            }
            return builder.build().toString().replace("http://", if (tls) "wss://" else "ws://")
        }
    }

}