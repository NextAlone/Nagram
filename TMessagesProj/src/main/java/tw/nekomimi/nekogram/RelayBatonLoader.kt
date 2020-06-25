package tw.nekomimi.nekogram

import android.annotation.SuppressLint
import com.v2ray.ang.V2RayConfig
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import tw.nekomimi.nekogram.utils.FileUtil
import java.io.File
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class RelayBatonLoader {

    companion object {

        @SuppressLint("AuthLeak")
        const val publicServer = "rb://anonymous:nya@net.neko.services"

    }

    lateinit var bean: Bean
    var port by Delegates.notNull<Int>()
    var rbProcess: GuardedProcessPool? = null

    fun initConfig(bean: Bean, port: Int) {

        this.bean = bean
        this.port = port

    }

    fun start() {

        stop()

        val cacheCfg = File(ApplicationLoader.applicationContext.cacheDir, "config.toml")

        cacheCfg.writeText(bean.toToml(port))

        val geoip = File(ApplicationLoader.applicationContext.cacheDir, "GeoLite2-Country.mmdb")

        if (!geoip.isFile) {

            geoip.createNewFile()

            geoip.outputStream().use {  out ->

                ApplicationLoader.applicationContext.assets.open("GeoLite2-Country.mmdb").use {

                    it.copyTo(out)

                }

            }

        }

        rbProcess = GuardedProcessPool {

            FileLog.e(it)

        }.apply {

            runCatching {

                start(listOf(FileUtil.extLib("relaybaton").path, "client")) {

                    cacheCfg.delete()

                }

            }.onFailure {

                cacheCfg.delete()

                FileLog.e(it)

            }

        }

    }

    fun stop() {

        if (rbProcess != null) {

            val proc = rbProcess!!

            thread {

                runCatching {

                    runBlocking { proc.close(this) }

                }

            }

            rbProcess = null

        }

    }

    data class Bean(
            var server: String = "",
            var username: String = "",
            var password: String = "",
            var esni: Boolean = true,
            var remarks: String? = null
    ) {

        override fun equals(other: Any?): Boolean {
            return super.equals(other) || (other is Bean && hash == other.hash)
        }

        val hash = (server + username + password).hashCode()

        fun toToml(port: Int) = """
[log]
file="./log.txt"
level="error"

[dns]
type="dot"
server="cloudflare-dns.com"
addr="1.0.0.1:853"
local_resolve=false

[clients]
port=$port
  [[clients.client]]
  id="1"
  server="$server"
  username="$username"
  password="$password"
  esni=$esni
  timeout=15
            
[routes]
geoip_file="GeoLite2-Country.mmdb"
  [[routes.route]]
  type="default"
  cond=""
  target="1"
"""

        companion object {

            fun parse(url: String): Bean {

                // ss-android style

                val link = url.replace(V2RayConfig.RB_PROTOCOL, "https://").toHttpUrlOrNull() ?: error("invalid relaybaton link $url")

                return Bean(
                        link.host,
                        link.username,
                        link.password,
                        link.queryParameter("esni").takeIf { it in arrayOf("true", "false") }?.toBoolean() ?: true,
                        link.fragment
                )

            }

        }

        override fun toString(): String {

            val url = HttpUrl.Builder()
                    .scheme("https")
                    .username(username)
                    .password(password)
                    .host(server)

            if (!remarks.isNullOrBlank()) url.fragment(remarks)

            if (!esni) url.addQueryParameter("esni","false")

            return url.build().toString().replace("https://", V2RayConfig.RB_PROTOCOL)

        }

    }


}