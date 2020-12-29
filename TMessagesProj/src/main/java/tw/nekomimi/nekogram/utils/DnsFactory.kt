package tw.nekomimi.nekogram.utils

import android.os.Build
import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import org.telegram.tgnet.ConnectionsManager
import org.xbill.DNS.DohResolver
import org.xbill.DNS.Lookup
import org.xbill.DNS.TXTRecord
import org.xbill.DNS.Type
import java.net.InetAddress
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

open class DnsFactory : Dns {

    companion object : DnsFactory() {

        init {

            if (Build.VERSION.SDK_INT >= 21) {

                addProvider("https://mozilla.cloudflare-dns.com/dns-query")
                addProvider("https://dns.google/dns-query")
                addProvider("https://dns.twnic.tw/dns-query")
                addProvider("https://dns.adguard.com/dns-query")

            }

        }
    }

    val providers = LinkedList<DnsOverHttps>()
    val dnsJavaProviders = LinkedList<DohResolver>()

    val client = OkHttpClient.Builder().connectTimeout(3,TimeUnit.SECONDS).build()

    fun addProvider(url: String) {

        providers.add(DnsOverHttps.Builder()
                .client(client)
                .url(url.toHttpUrl())
                .includeIPv6(ConnectionsManager.useIpv6Address())
                .build())

    }

    override fun lookup(hostname: String): List<InetAddress> {

        providers.forEach {

            runCatching {

                return it.lookup(hostname)

            }

        }

        runCatching {

            return Dns.SYSTEM.lookup(hostname)

        }

        return listOf()

    }

    fun getTxts(domain: String): ArrayList<String> {

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