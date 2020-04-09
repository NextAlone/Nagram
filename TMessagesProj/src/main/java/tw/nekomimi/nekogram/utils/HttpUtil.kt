package tw.nekomimi.nekogram.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import org.telegram.messenger.SharedConfig
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

fun Request.Builder.applyUserAgent(): Request.Builder {

    header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1")
    header("X-Requested-With", "XMLHttpRequest")

    return this

}

object HttpUtil {

    @JvmField
    val okHttpClient = OkHttpClient().newBuilder().connectTimeout(5, TimeUnit.SECONDS).build()

    @JvmStatic
    val okHttpClientWithCurrProxy: OkHttpClient
        get() {

            return if (!SharedConfig.proxyEnabled || SharedConfig.currentProxy?.secret != null) {

                okHttpClient

            } else {

                okHttpClient.newBuilder()
                        .proxy(Proxy(Proxy.Type.SOCKS, InetSocketAddress(SharedConfig.currentProxy.address, SharedConfig.currentProxy.port)))
                        .build()

            }

        }

    @JvmStatic
    fun get(url: String): String {

        val request = Request.Builder().url(url)
                .applyUserAgent()
                .build()

        okHttpClient.newCall(request).execute().apply {

            val body = body

            return body?.string() ?: error("HTTP ERROR $code")

        }

    }

    @JvmStatic
    fun getByteArray(url: String): ByteArray {

        val request = Request.Builder()
                .url(url)
                .applyUserAgent()
                .build()

        okHttpClient.newCall(request).execute().apply {

            return body?.bytes() ?: error("HTTP ERROR $code")

        }

    }

}