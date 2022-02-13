package tw.nekomimi.nekogram.proxy

import android.annotation.SuppressLint
import cn.hutool.core.codec.Base64
import com.github.shadowsocks.plugin.PluginConfiguration
import com.github.shadowsocks.plugin.PluginManager
import com.github.shadowsocks.plugin.PluginOptions
import com.v2ray.ang.V2RayConfig.SS_PROTOCOL
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import tw.nekomimi.nekogram.utils.AlertUtil
import tw.nekomimi.nekogram.utils.FileUtil
import java.io.File
import kotlin.concurrent.thread
import kotlin.properties.Delegates

@SuppressLint("NewApi")
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

        shadowsocksProcess = GuardedProcessPool {

            FileLog.e(it)

        }.apply {

            runCatching {

                cacheCfg.writeText(bean.toJson().toString())

                start(listOf(FileUtil.extLib("ss-local").path,
                        "--local-addr", "127.0.0.1:$port",
                        "--config", cacheCfg.path)) {

                    cacheCfg.delete()

                }

            }.onFailure { it ->

                AlertUtil.showToast("${it.javaClass.simpleName}: ${it.message}")

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
            var plugin: String = "",
            var remarks: String? = null
    ) {

        init {

            if (method == "plain") method = "none"

            val pl = PluginConfiguration(plugin)

            if (pl.selected.contains("v2ray") && pl.selected != "v2ray-plugin") {

                pl.pluginsOptions["v2ray-plugin"] = pl.getOptions().apply { id = "v2ray-plugin" }
                pl.pluginsOptions.remove(pl.selected)
                pl.selected = "v2ray-plugin"

                // reslove v2ray plugin

            }

            if (pl.selected == "obfs") {

                pl.pluginsOptions["obfs-local"] = pl.getOptions().apply { id = "obfs-local" }
                pl.pluginsOptions.remove(pl.selected)
                pl.selected = "obfs-local"

                // reslove clash obfs

            }

            plugin = pl.toString()


        }

        override fun equals(other: Any?): Boolean {
            return super.equals(other) || (other is Bean && hash == other.hash)
        }

        /*
        init {

            if (method !in methods) error("method $method not supported")

        }
         */

        val hash = (host + remotePort + password + method).hashCode()

        val pluginInitResult by lazy {
            PluginManager.init(PluginConfiguration(plugin ?: ""))
        }

        fun toJson(): JSONObject = JSONObject().apply {
            put("server", host)
            put("server_port", remotePort)
            put("password", password)
            put("method", method)
            put("ipv6", true)
            if (pluginInitResult != null) {
                put("plugin", pluginInitResult!!.first)
                put("plugin_opts", pluginInitResult!!.second.toString())
            }
        }

        companion object {

            fun parseJson(ssObj: JSONObject): Bean {
                var pluginStr = ""
                val pId = ssObj.optString("plugin")
                if (!pId.isNullOrBlank()) {
                    val plugin = PluginOptions(pId, ssObj.optString("plugin_opts"))
                    pluginStr = plugin.toString(false)
                }
                return Bean(
                        ssObj.getString("server"),
                        ssObj.getInt("server_port"),
                        ssObj.getString("password"),
                        ssObj.getString("method"),
                        pluginStr,
                        ssObj.optString("remarks")
                )
            }

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
                                link.queryParameter("plugin") ?: "",
                                link.fragment
                        )

                    }

                    val methodAndPswd = Base64.decodeStr(link.username)

                    return Bean(
                            link.host,
                            link.port,
                            methodAndPswd.substringAfter(":"),
                            methodAndPswd.substringBefore(":"),
                            link.queryParameter("plugin") ?: "",
                            link.fragment
                    )

                } else {

                    // v2rayNG style

                    var v2Url = url

                    if (v2Url.contains("#")) v2Url = v2Url.substringBefore("#")

                    val link = ("https://" + Base64.decodeStr(v2Url.substringAfter(SS_PROTOCOL))).toHttpUrlOrNull()
                        ?: error("invalid v2rayNG link $url")

                    return Bean(
                            link.host,
                            link.port,
                            link.password,
                            link.username,
                            "",
                            link.fragment
                    )

                }

            }

        }

        override fun toString(): String {

            val url = HttpUrl.Builder()
                    .scheme("https")
                    .encodedUsername(Base64.encodeUrlSafe("$method:$password"))
                    .host(host)
                    .port(remotePort)

            if (!remarks.isNullOrBlank()) url.fragment(remarks)

            if (plugin.isNotBlank()) url.addQueryParameter("plugin", plugin)

            return url.build().toString().replace("https://", "ss://")

        }

    }

    companion object {

        val methods = arrayOf(

                "none",
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