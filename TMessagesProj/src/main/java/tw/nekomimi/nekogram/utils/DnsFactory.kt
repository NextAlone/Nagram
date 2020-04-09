package tw.nekomimi.nekogram.utils

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.dnsoverhttps.DnsOverHttps
import org.telegram.tgnet.ConnectionsManager
import org.xbill.DNS.DohResolver
import org.xbill.DNS.Lookup
import org.xbill.DNS.TXTRecord
import org.xbill.DNS.Type
import java.net.InetAddress
import java.util.*
import kotlin.collections.ArrayList

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
    val dnsJavaProviders  = LinkedList<DohResolver>()

    fun addProvider(url: String) {

        providers.add(DnsOverHttps.Builder()
                .client(HttpUtil.okHttpClient)
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

    fun getTxts(domain: String) : ArrayList<String> {

        val results = ArrayList<String>()

        dnsJavaProviders.forEach {

            runCatching {

                val lookup = Lookup(domain, Type.TXT)

                lookup.setResolver(it)

                lookup.run()

                if (lookup.result == Lookup.SUCCESSFUL) {

                    lookup.answers.forEach {

                        (it as TXTRecord).strings.forEach {

                            results.add(it)

                        }

                    }

                }

            }

        }

        return results

    }

}