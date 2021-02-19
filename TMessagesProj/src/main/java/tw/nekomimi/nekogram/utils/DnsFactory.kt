package tw.nekomimi.nekogram.utils

import cn.hutool.core.util.ArrayUtil
import okhttp3.Dns
import org.telegram.messenger.FileLog
import org.telegram.tgnet.ConnectionsManager
import org.xbill.DNS.*
import java.net.InetAddress
import java.util.*
import kotlin.collections.ArrayList


open class DnsFactory : Dns {

    val providers = LinkedList<DohResolver>()

    companion object : DnsFactory() {

        init {

            addProvider("https://dns.twnic.tw/dns-query")
            addProvider("https://mozilla.cloudflare-dns.com/dns-query")
            addProvider("https://dns.google/dns-query")

        }
    }


    fun addProvider(url: String) = providers.add(DohResolver(url))

    override fun lookup(domain: String): List<InetAddress> {

        FileLog.d("Lookup $domain")

        for (provider in providers) {
            FileLog.d("Provider ${provider.uriTemplate}")

            val lookup = Lookup(Name.fromConstantString(domain), if (!ConnectionsManager.useIpv6Address()) Type.A else Type.AAAA)
            lookup.setSearchPath(* emptyArray<String>())
            lookup.setCache(null)
            lookup.setResolver(provider)
            lookup.run()

            if (lookup.result != Lookup.SUCCESSFUL) continue

            FileLog.d("Results: " + ArrayUtil.toString(lookup.answers))
            return lookup.answers.map { (it as? ARecord)?.address ?: (it as AAAARecord).address }
        }

        runCatching { return InetAddress.getAllByName(domain).toList() }
        return listOf()

    }

    fun getTxts(domain: String): List<String> {

        FileLog.d("Lookup $domain for txts")

        for (provider in providers) {
            FileLog.d("Provider ${provider.uriTemplate}")

            val lookup = Lookup(Name.fromConstantString(domain), Type.TXT)
            lookup.setSearchPath(* emptyArray<String>())
            lookup.setCache(null)
            lookup.setResolver(provider)
            lookup.run()

            if (lookup.result != Lookup.SUCCESSFUL) continue
            FileLog.d("Results: " + ArrayUtil.toString(lookup.answers))

            val result = ArrayList<String>()
            for (record in lookup.answers.filterIsInstance<TXTRecord>()) result.addAll(record.strings)
            return result
        }

        return listOf()

    }

}