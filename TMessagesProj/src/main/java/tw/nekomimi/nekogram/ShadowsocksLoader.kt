package tw.nekomimi.nekogram

import android.util.Base64
import android.util.LongSparseArray
import com.v2ray.ang.V2RayConfig.SS_PROTOCOL
import com.v2ray.ang.util.Utils
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONArray
import org.json.JSONObject
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import java.io.File
import java.io.FileDescriptor
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class ShadowsocksLoader {

    lateinit var bean: Bean
    var port by Delegates.notNull<Int>()
    var shadowsocksProcess: GuardedProcessPool? = null

    fun initConfig(bean: Bean,port: Int) {

        this.bean = bean
        this.port = port

    }

    fun start() {

        stop()

        val cacheCfg = File(ApplicationLoader.applicationContext.cacheDir,"ss_cfg_${bean.hash}.json")

        cacheCfg.writeText(bean.toJson().toString())

        shadowsocksProcess = GuardedProcessPool {

            FileLog.e(it)

        }.apply {

            start(listOf("${ApplicationLoader.applicationContext.applicationInfo.nativeLibraryDir}/libss-local.so",
                    "-b","127.0.0.1",
                    "-t", "600",
                    "-c", cacheCfg.path,
                    "-l", port.toString()))

        }

    }

    fun stop() {

        if (shadowsocksProcess != null) {

            val proc = shadowsocksProcess!!

            thread {

                runCatching {

                    runBlocking { proc.close(this) }

                }

            }

            shadowsocksProcess = null

        }

    }

    data class Bean(
            var host: String = "",
            var remotePort: Int = 443,
            var password: String = "",
            var method: String = "aes-256-cfb"
    ) {

        /*
        init {

            if (method !in methods) error("method $method not supported")

        }
         */

        val hash = (host + remotePort + password + method).hashCode()

        fun toJson(): JSONObject = JSONObject().apply {
            put("server", host)
            put("server_port", remotePort)
            put("password", password)
            put("remarks", "nekox-cache")
            put("route", "all")
            put("remote_dns", "8.8.8.8:53")
            put("ipv6", true)
            put("metered", false)
            put("proxy_apps", JSONObject().apply {
                put("enabled", false)
            })
            put("udpdns", false)
        }

        companion object {

            fun parse(url: String): Bean {

                if (url.contains("@")) {

                    // ss-android style

                    val link = url.replace(SS_PROTOCOL,"https://").toHttpUrlOrNull() ?: error("invalid ss-android link $url")

                    if (link.password.isNotBlank()) {

                        return Bean(
                                link.host,
                                link.port,
                                link.password,
                                link.username
                        )

                    }

                    val methodAndPswd = Utils.decode(link.username)

                    return Bean(
                            link.host,
                            link.port,
                            methodAndPswd.substringAfter(":"),
                            methodAndPswd.substringBefore(":")
                    )

                } else {

                    // v2rayNG style

                    var v2Url = url

                    if (v2Url.contains("#")) v2Url = v2Url.substringBefore("#")

                    val link = ("https://" + Utils.decode(v2Url.substringAfter(SS_PROTOCOL))).toHttpUrlOrNull() ?: error("invalid v2rayNG link $url")

                    return Bean(
                            link.host,
                            link.port,
                            link.password,
                            link.username
                    )

                }

            }

        }

        override fun toString(): String {

           return "ss://" + Utils.encode("$method:$password") + "@$host:$remotePort"

        }

    }

    companion object {

        val methods = arrayOf(

                "rc4-md5",
                "aes-128-cfb",
                "aes-192-cfb",
                "aes-256-cfb",
                "aes-128-ctr",
                "aes-192-ctr",
                "aes-256-ctr",
                "bf-cfb",
                "camellia-128-cfb",
                "camellia-192-cfb",
                "camellia-256-cfb",
                "salsa20",
                "chacha20",
                "chacha20-ietf",
                "aes-128-gcm",
                "aes-192-gcm",
                "aes-256-gcm",
                "chacha20-ietf-poly1305",
                "xchacha20-ietf-poly1305"
        )

    }

}