package tw.nekomimi.nekogram

import cn.hutool.core.codec.Base64
import com.google.gson.Gson
import com.v2ray.ang.V2RayConfig
import com.v2ray.ang.V2RayConfig.SOCKS_PROTOCOL
import com.v2ray.ang.V2RayConfig.TROJAN_PROTOCOL
import com.v2ray.ang.V2RayConfig.VMESS1_PROTOCOL
import com.v2ray.ang.V2RayConfig.VMESS_PROTOCOL
import com.v2ray.ang.dto.AngConfig.VmessBean
import com.v2ray.ang.dto.VmessQRCode
import com.v2ray.ang.util.Utils
import com.v2ray.ang.util.V2rayConfigUtil
import libv2ray.Libv2ray
import libv2ray.V2RayVPNServiceSupportsSet
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.telegram.messenger.FileLog
import kotlin.concurrent.thread
import kotlin.random.Random

class VmessLoader {

    private val point = Libv2ray.newV2RayPoint(EmptyCallback(), false)

    companion object {

        fun parseVmess1Link(server: String): VmessBean {

            val lnk = ("https://" + server.substringAfter(VMESS1_PROTOCOL)).toHttpUrl()

            val bean = VmessBean()

            bean.configType = V2RayConfig.EConfigType.Vmess
            bean.address = lnk.host
            bean.port = lnk.port
            bean.id = lnk.username
            bean.remarks = lnk.fragment ?: ""

            lnk.queryParameterNames.forEach {

                when (it) {

                    "tls" -> if (lnk.queryParameter(it) == "true") bean.streamSecurity = "tls"

                    "network" -> {

                        bean.network = lnk.queryParameter(it)!!

                        if (bean.network in arrayOf("http", "ws")) {

                            bean.path = Utils.urlDecode(lnk.encodedPath)

                        }

                    }

                    "header" -> {

                        bean.headerType = lnk.queryParameter(it)!!

                    }

                }

            }

            return bean

        }

        @JvmStatic
        fun parseVmessLink(server: String): VmessBean {

            try {
                if (server.isBlank()) error("empty link")

                var vmess = VmessBean()

                if (server.startsWith(VMESS_PROTOCOL)) {

                    val indexSplit = server.indexOf("?")
                    if (indexSplit > 0) {
                        vmess = resolveSimpleVmess1(server)
                    } else {

                        var result = server.replace(VMESS_PROTOCOL, "")
                        result = Base64.decodeStr(result)
                        if (result.isBlank()) {
                            error("invalid url format")
                        }

                        if (result.contains("= vmess")) {

                            vmess = resolveSomeIOSAppShitCsvLink(result)

                        } else {

                            val vmessQRCode = Gson().fromJson(result, VmessQRCode::class.java)
                            if (vmessQRCode.add.isBlank()
                                    || vmessQRCode.port.isBlank()
                                    || vmessQRCode.id.isBlank()
                                    || vmessQRCode.aid.isBlank()
                                    || vmessQRCode.net.isBlank()
                            ) {
                                error("invalid protocol")
                            }

                            vmess.configType = V2RayConfig.EConfigType.Vmess
                            vmess.security = "auto"
                            vmess.network = "tcp"
                            vmess.headerType = "none"

                            vmess.configVersion = Utils.parseInt(vmessQRCode.v)
                            vmess.remarks = vmessQRCode.ps
                            vmess.address = vmessQRCode.add
                            vmess.port = Utils.parseInt(vmessQRCode.port)
                            vmess.id = vmessQRCode.id
                            vmess.alterId = Utils.parseInt(vmessQRCode.aid)
                            vmess.network = vmessQRCode.net
                            vmess.headerType = vmessQRCode.type
                            vmess.requestHost = vmessQRCode.host
                            vmess.path = vmessQRCode.path
                            vmess.streamSecurity = vmessQRCode.tls
                        }
                    }

                    upgradeServerVersion(vmess)

                    return vmess
                } else if (server.startsWith(VMESS1_PROTOCOL)) {

                    return parseVmess1Link(server)

                } else if (server.startsWith(TROJAN_PROTOCOL)) {

                    vmess.configType = V2RayConfig.EConfigType.Trojan

                    val link = server.replace(TROJAN_PROTOCOL, "https://").toHttpUrlOrNull()
                            ?: error("invalid trojan link $server")

                    vmess.address = link.host
                    vmess.port = link.port
                    vmess.id = link.username

                    if (link.password.isNotBlank()) {

                        // https://github.com/trojan-gfw/igniter/issues/318

                        vmess.id += ":" + link.password

                    }

                    vmess.remarks = link.fragment ?: ""

                    return vmess

                } else if (server.startsWith(SOCKS_PROTOCOL)) {
                    var result = server.replace(SOCKS_PROTOCOL, "")
                    val indexSplit = result.indexOf("#")
                    if (indexSplit > 0) {
                        try {
                            vmess.remarks = Utils.urlDecode(result.substring(indexSplit + 1, result.length))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        result = result.substring(0, indexSplit)
                    }

                    //part decode
                    val indexS = result.indexOf(":")
                    if (indexS < 0) {
                        result = Base64.decodeStr(result)
                    }

                    val legacyPattern = "^(.+?):(\\d+?)$".toRegex()
                    val match = legacyPattern.matchEntire(result) ?: error("invalid protocol")
                    vmess.address = match.groupValues[1]
                    if (vmess.address.firstOrNull() == '[' && vmess.address.lastOrNull() == ']')
                        vmess.address = vmess.address.substring(1, vmess.address.length - 1)
                    vmess.port = match.groupValues[2].toInt()

                    return vmess
                } else {
                    error("invalid protocol")
                }
            } catch (e: Exception) {

                throw IllegalArgumentException(e)

            }

        }

        private fun resolveSomeIOSAppShitCsvLink(csv: String): VmessBean {

            val args = csv.split(",")

            val bean = VmessBean()

            bean.configType = V2RayConfig.EConfigType.Vmess
            bean.address = args[1]
            bean.port = args[2].toInt()
            bean.security = args[3]
            bean.id = args[4].replace("\"", "")

            args.subList(5, args.size).forEach {

                when {

                    it == "over-tls=true" -> {

                        bean.streamSecurity = "tls"

                    }

                    it.startsWith("tls-host=") -> {

                        bean.requestHost = it.substringAfter("=")

                    }

                    it.startsWith("obfs=") -> {

                        bean.network = it.substringAfter("=")

                    }

                    it.startsWith("obfs-path=") || it.contains("Host:") -> {

                        runCatching {

                            bean.path = it
                                    .substringAfter("obfs-path=\"")
                                    .substringBefore("\"obfs")

                        }

                        runCatching {

                            bean.requestHost = it
                                    .substringAfter("Host:")
                                    .substringBefore("[")

                        }

                    }

                }

            }

            return bean

        }

        /**
         * upgrade
         */
        private fun upgradeServerVersion(vmess: VmessBean): Int {
            try {
                if (vmess.configVersion == 2) {
                    return 0
                }

                when (vmess.network) {
                    "kcp" -> {
                    }
                    "ws" -> {
                        var path = ""
                        var host = ""
                        val lstParameter = vmess.requestHost.split(";")
                        if (lstParameter.size > 0) {
                            path = lstParameter.get(0).trim()
                        }
                        if (lstParameter.size > 1) {
                            path = lstParameter.get(0).trim()
                            host = lstParameter.get(1).trim()
                        }
                        vmess.path = path
                        vmess.requestHost = host
                    }
                    "h2" -> {
                        var path = ""
                        var host = ""
                        val lstParameter = vmess.requestHost.split(";")
                        if (lstParameter.isNotEmpty()) {
                            path = lstParameter[0].trim()
                        }
                        if (lstParameter.size > 1) {
                            path = lstParameter[0].trim()
                            host = lstParameter[1].trim()
                        }
                        vmess.path = path
                        vmess.requestHost = host
                    }
                    else -> {
                    }
                }
                vmess.configVersion = 2
                return 0
            } catch (e: Exception) {
                e.printStackTrace()
                return -1
            }
        }

        private fun resolveSimpleVmess1(server: String): VmessBean {

            val vmess = VmessBean()

            var result = server.replace(VMESS_PROTOCOL, "")
            val indexSplit = result.indexOf("?")
            if (indexSplit > 0) {
                result = result.substring(0, indexSplit)
            }
            result = Base64.decodeStr(result)

            val arr1 = result.split('@')
            if (arr1.count() != 2) {
                return vmess
            }
            val arr21 = arr1[0].split(':')
            val arr22 = arr1[1].split(':')
            if (arr21.count() != 2 || arr21.count() != 2) {
                return vmess
            }

            vmess.address = arr22[0]
            vmess.port = Utils.parseInt(arr22[1])
            vmess.security = arr21[0]
            vmess.id = arr21[1]

            vmess.security = "chacha20-poly1305"
            vmess.network = "tcp"
            vmess.headerType = "none"
            vmess.remarks = ""
            vmess.alterId = 0

            return vmess
        }

    }

    lateinit var bean: VmessBean

    fun initConfig(config: VmessBean) {

        this.bean = config

    }

    fun start(): Int {

        var retry = 3

        do {

            try {

                val port = Random.nextInt(4096, 32768)

                val conf = V2rayConfigUtil.getV2rayConfig(bean, port).content

                FileLog.d(conf)

                runCatching {

                    Libv2ray.testConfig(conf)

                }.onFailure {

                    FileLog.e(it)

                    return -1

                }

                point.configureFileContent = conf
                point.domainName = V2rayConfigUtil.currDomain

                point.runLoop(true)

                return port

            } catch (e: Throwable) {

                retry--

                if (retry <= 0) {

                    FileLog.e(e)

                    return -1

                }

            }

        } while (true)

    }

    fun stop() {

        thread {

            runCatching {

                point.stopLoop()

            }

        }

    }

    class EmptyCallback : V2RayVPNServiceSupportsSet {
        override fun onEmitStatus(p0: Long, p1: String?): Long {
            return 0
        }

        override fun setup(p0: String?): Long {
            return 0
        }

        override fun prepare(): Long {
            return 0
        }

        override fun shutdown(): Long {
            return 0
        }

        override fun protect(p0: Long): Boolean {
            return true
        }
    }

}