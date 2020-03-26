package tw.nekomimi.nekogram.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.http2.Header
import org.telegram.messenger.SharedConfig
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

object HttpUtil {

    @JvmField
    val okhttpClient = OkHttpClient().newBuilder().readTimeout(500,TimeUnit.MILLISECONDS).build()

    @JvmStatic
    val okhttpClientWithCurrProxy: OkHttpClient get() {

        return if (!SharedConfig.proxyEnabled || SharedConfig.currentProxy?.secret != null ) {

            okhttpClient

        } else {

            okhttpClient.newBuilder()
                    .proxy(Proxy(Proxy.Type.SOCKS,InetSocketAddress(SharedConfig.currentProxy.address,SharedConfig.currentProxy.port)))
                    .build()

        }

    }

    @JvmStatic
    fun get(url: String): String {

        val request = Request.Builder().url(url).build()

        okhttpClient.newCall(request).execute().apply {

            val body = body

            return body?.string() ?: error("HTTP ERROR $code")

        }

    }

    @JvmStatic
    fun get(url: String,ua: String): String {

        val request = Request.Builder().url(url).addHeader("User-Agent",ua).build()

        okhttpClient.newCall(request).execute().apply {

            return body?.string() ?: error("HTTP ERROR $code")

        }

    }

    @JvmStatic
    fun getByteArray(url: String): ByteArray {

        val request = Request.Builder().url(url).build()

        okhttpClient.newCall(request).execute().apply {

            return body?.bytes() ?: error("HTTP ERROR $code")

        }

    }

}