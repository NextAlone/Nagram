package tw.nekomimi.nekogram

import android.util.Base64
import com.v2ray.ang.V2RayConfig.SS_PROTOCOL
import com.v2ray.ang.util.Utils
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import tw.nekomimi.nekogram.utils.FileUtil
import java.io.File
import java.util.*
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class ShadowsocksLoader {

    lateinit var bean: Bean
    var port by Delegates.notNull<Int>()
    var shadowsocksProcess: GuardedProcessPool? = null

    fun initConfig(bean: Bean, port: Int) {

        this.bean = bean
        this.port = port

    }

    fun start() {

        stop()

        val cacheCfg = File(ApplicationLoader.applicationContext.cacheDir, "ss_cfg_${bean.hash}.json")

        cacheCfg.writeText(bean.toJson().toString())

        shadowsocksProcess = GuardedProcessPool {

            FileLog.e(it)

        }.apply {

            runCatching {

                start(listOf(FileUtil.extLib("ss-local").path,
                        "-b", "127.0.0.1",
                        "-t", "600",
                        "-c", cacheCfg.path,
                        "-l", port.toString())) {

                    cacheCfg.delete()

                }

            }.onFailure {

                cacheCfg.delete()

                FileLog.e(it)

            }

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
            var method: String = "aes-256-cfb",
            var remarks: String? = null
    ) {
        override fun equals(other: Any?): Boolean {
            return super.equals(other) || (other is Bean && hash == other.hash)
        }

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
            put("method",method)
            put("ipv6", true)
        }

        companion object {

            fun parse(url: String): Bean {

                if (url.contains("@")) {

                    // ss-android style

                    val link = url.replace(SS_PROTOCOL, "https://").toHttpUrlOrNull()
                            ?: error("invalid ss-android link $url")

                    if (link.password.isNotBlank()) {

                        return Bean(
                                link.host,
                                link.port,
                                link.password,
                                link.username,
                                link.fragment
                        )

                    }

                    val methodAndPswd = Utils.decode(link.username)

                    return Bean(
                            link.host,
                            link.port,
                            methodAndPswd.substringAfter(":"),
                            methodAndPswd.substringBefore(":"),
                            link.fragment
                    )

                } else {

                    // v2rayNG style

                    var v2Url = url

                    if (v2Url.contains("#")) v2Url = v2Url.substringBefore("#")

                    val link = ("https://" + Utils.decode(v2Url.substringAfter(SS_PROTOCOL))).toHttpUrlOrNull()
                            ?: error("invalid v2rayNG link $url")

                    return Bean(
                            link.host,
                            link.port,
                            link.password,
                            link.username,
                            link.fragment
                    )

                }

            }

        }

        override fun toString(): String {

            var url = "ss://" + Base64.encode("$method:$password".toByteArray(),Base64.NO_WRAP or Base64.URL_SAFE) + "@$host:$remotePort"

            if (remarks?.isNotBlank() == true) url += "#" + Utils.urlEncode(remarks!!)

            return url

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