package tw.nekomimi.nekogram.proxy

import cn.hutool.core.codec.Base64
import com.v2ray.ang.V2RayConfig.SSR_PROTOCOL
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.json.JSONObject
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import tw.nekomimi.nekogram.utils.FileUtil
import java.io.File
import java.util.*
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class ShadowsocksRLoader {

    lateinit var bean: Bean
    var port by Delegates.notNull<Int>()
    var shadowsocksProcess: GuardedProcessPool? = null

    fun initConfig(bean: Bean, port: Int) {

        this.bean = bean
        this.port = port

    }

    fun start() {

        stop()

        val cacheCfg = File(ApplicationLoader.applicationContext.cacheDir, "ssr_cfg_${bean.hash}.json")

        cacheCfg.writeText(bean.toJson().toString())

        shadowsocksProcess = GuardedProcessPool {

            FileLog.e(it)

        }.apply {

            runCatching {

                start(listOf(FileUtil.extLib("ssr-local").path,
                        "-b", "127.0.0.1",
                        "--host", bean.host,
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
            var protocol: String = "origin",
            var protocol_param: String = "",
            var obfs: String = "plain",
            var obfs_param: String = "",
            var method: String = "aes-256-cfb",
            var remarks: String? = null
    ) {

        val hash get() = (host + remotePort + password + protocol + obfs + method).hashCode()

        override fun equals(other: Any?): Boolean {
            return super.equals(other) || (other is Bean && hash == other.hash)
        }

        /*
        init {

            if (method !in methods) error("method $method not supported")
            if (protocol !in protocols) error("protocol $protocol not supported")
            if (obfs !in obfses) error("obfs $obfs not supported")

        }
         */

        fun toJson(): JSONObject = JSONObject().apply {
            put("server", host)
            put("server_port", remotePort)
            put("password", password)
            put("method", method)
            put("protocol", protocol)
            put("protocol_param", protocol_param)
            put("obfs", obfs)
            put("obfs_param", obfs_param)
            put("remarks", remarks)
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

                val params = Base64.decodeStr(url.substringAfter(SSR_PROTOCOL)).split(":")

                val bean = Bean(params[0],
                        params[1].toInt(),
                        protocol = params[2],
                        method = params[3],
                        obfs = params[4],
                        password = Base64.decodeStr(params[5].substringBefore("/")))

                val httpUrl = ("https://localhost" + params[5].substringAfter("/")).toHttpUrl()

                runCatching {

                    bean.obfs_param = Base64.decodeStr(httpUrl.queryParameter("obfsparam")!!)

                }

                runCatching {

                    bean.protocol_param = Base64.decodeStr(httpUrl.queryParameter("protoparam")!!)

                }

                runCatching {

                    val remarks = httpUrl.queryParameter("remarks")

                    if (remarks?.isNotBlank() == true) {

                        bean.remarks = Base64.decodeStr(remarks)

                    }

                }

                return bean

            }

        }

        override fun toString(): String {

            return "ssr://" + Base64.encodeUrlSafe("%s:%d:%s:%s:%s:%s/?obfsparam=%s&protoparam=%s&remarks=%s".format(Locale.ENGLISH, host, remotePort, protocol, method, obfs,
                    Base64.encodeUrlSafe("%s".format(Locale.ENGLISH, password)),
                    Base64.encodeUrlSafe("%s".format(Locale.ENGLISH, obfs_param)),
                    Base64.encodeUrlSafe("%s".format(Locale.ENGLISH, protocol_param)),
                    Base64.encodeUrlSafe("%s".format(Locale.ENGLISH, remarks ?: ""))))
        }

    }

    companion object {

        val methods = arrayOf(

                "none",
                "table",
                "rc4",
                "rc4-md5",
                "rc4-md5-6",
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
                "chacha20-ietf"

        )

        val protocols = arrayOf(
                "origin",
                "verify_simple",
                "verify_sha1",
                "auth_sha1",
                "auth_sha1_v2",
                "auth_sha1_v4",
                "auth_aes128_sha1",
                "auth_aes128_md5",
                "auth_chain_a",
                "auth_chain_b"
        )

        val obfses = arrayOf(
                "plain",
                "http_simple",
                "http_post",
                "tls_simple",
                "tls1.2_ticket_auth"
        )

    }

}