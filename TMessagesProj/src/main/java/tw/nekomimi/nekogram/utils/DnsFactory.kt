package tw.nekomimi.nekogram.utils

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.dnsoverhttps.DnsOverHttps
import org.telegram.tgnet.ConnectionsManager
import java.net.InetAddress
import java.util.*

open class DnsFactory {

    companion object : DnsFactory() {

        init {

            addProvider("https://mozilla.cloudflare-dns.com/dns-query")
            addProvider("https://cloudflare-dns.com/dns-query")
            addProvider("https://dns.google/dns-query")
            addProvider("https://dns.twnic.tw/dns-query")
            addProvider("https://dns.adguard.com/dns-query")

        }

    }

    val providers = LinkedList<DnsOverHttps>()

    fun addProvider(url: String) {

        providers.add(DnsOverHttps.Builder()
                .client(HttpUtil.okhttpClient)
                .url(url.toHttpUrl())
                .includeIPv6(ConnectionsManager.useIpv6Address())
                .build())

    }

    fun lookUp(host: String) : Array<InetAddress> {

        providers.forEach {

            runCatching {

                return it.lookup(host).toTypedArray()

            }

        }

        return arrayOf()

    }

}