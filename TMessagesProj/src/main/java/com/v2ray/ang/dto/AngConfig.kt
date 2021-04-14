package com.v2ray.ang.dto

import cn.hutool.core.codec.Base64
import com.google.gson.Gson
import com.v2ray.ang.V2RayConfig
import com.v2ray.ang.V2RayConfig.TROJAN_PROTOCOL
import com.v2ray.ang.V2RayConfig.VMESS_PROTOCOL
import com.v2ray.ang.util.Utils

data class AngConfig(
        var index: Int,
        var vmess: ArrayList<VmessBean>,
        var subItem: ArrayList<SubItemBean>
) {
    data class VmessBean(var guid: String = "123456",
                         var address: String = "",
                         var port: Int = 443,
                         var id: String = "",
                         var alterId: Int = 0,
                         var security: String = "auto",
                         var network: String = "tcp",
                         var remarks: String = "",
                         var headerType: String = "none",
                         var requestHost: String = "",
                         var path: String = "",
                         var streamSecurity: String = "",
                         var configType: Int = 1,
                         var configVersion: Int = 2,
                         var testResult: String = "") {

        override fun equals(other: Any?): Boolean {
            return super.equals(other) || (other is VmessBean &&
                    address == other.address &&
                    port == other.port &&
                    id == other.id &&
                    network == other.network &&
                    headerType == other.headerType &&
                    requestHost == other.requestHost &&
                    path == other.path)
        }

        override fun toString(): String {

            if (configType == V2RayConfig.EConfigType.Vmess) {

                val vmessQRCode = VmessQRCode()

                vmessQRCode.v = configVersion.toString()
                vmessQRCode.ps = remarks
                vmessQRCode.add = address
                vmessQRCode.port = port.toString()
                vmessQRCode.id = id
                vmessQRCode.aid = alterId.toString()
                vmessQRCode.net = network
                vmessQRCode.type = headerType
                vmessQRCode.host = requestHost
                vmessQRCode.path = path
                vmessQRCode.tls = streamSecurity

                return VMESS_PROTOCOL + Base64.encode(Gson().toJson(vmessQRCode))

            } else if (configType == V2RayConfig.EConfigType.Trojan) {

                val params = if (requestHost.isNotBlank()) "?sni=" + Utils.urlEncode(requestHost) else ""
                val remark = if (remarks.isNotBlank()) "#" + Utils.urlEncode(remarks) else ""

                return TROJAN_PROTOCOL + Utils.urlEncode(id) + "@" + address + ":" + port + params + remark

            } else {

                error("invalid vmess bean type")

            }

        }

    }

    data class SubItemBean(var id: String = "",
                           var remarks: String = "",
                           var url: String = "")
}
