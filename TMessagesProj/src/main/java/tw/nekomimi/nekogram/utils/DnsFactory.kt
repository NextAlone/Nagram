package tw.nekomimi.nekogram.utils

import cn.hutool.http.Header
import cn.hutool.http.HttpUtil
import org.telegram.messenger.FileLog
import org.telegram.tgnet.ConnectionsManager
import org.xbill.DNS.*
import tw.nekomimi.nekogram.NekoConfig
import java.net.InetAddress
import java.util.*
import kotlin.collections.ArrayList


object DnsFactory {

    fun providers() = if (NekoConfig.customDoH.isNotBlank()) arrayOf(NekoConfig.customDoH)
    else if (Locale.getDefault().country == "CN") arrayOf(
        "https://doh.dns.sb/dns-query"
    ) else arrayOf(
        "https://mozilla.cloudflare-dns.com/dns-query",
        "https://dns.google/dns-query",
    )

    val cache = Cache()

    @JvmStatic
    @JvmOverloads
    fun lookup(domain: String, fallback: Boolean = false): List<InetAddress> {

        if (!NekoConfig.useSystemDNS) {

            FileLog.d("Lookup $domain")

            ConnectionsManager.getIpStrategy()

            val noFallback = !ConnectionsManager.hasIpv4 || !ConnectionsManager.hasIpv6

            val type = if (noFallback) {
                if (ConnectionsManager.hasIpv4) Type.A else Type.AAAA
            } else if (NekoConfig.useIPv6 xor !fallback) Type.A else Type.AAAA

            val dc = DClass.IN
            val name = Name.fromConstantString("$domain.")
            val message = Message.newQuery(Record.newRecord(name, type, dc)).toWire()
            var sr = cache.lookupRecords(name, type, dc)

            if (!sr.isSuccessful) for (provider in providers()) {
                    FileLog.d("Provider $provider")
                    try {
                        val response = HttpUtil.createPost(provider)
                            .contentType("application/dns-message")
                            .header(Header.ACCEPT, "application/dns-message")
                            .body(message)
                            .setConnectionTimeout(5000)
                            .execute()
                        if (!response.isOk) continue
                        val result = Message(response.bodyBytes())
                        val rcode = result.header.rcode
                        if (rcode != Rcode.NOERROR && rcode != Rcode.NXDOMAIN && rcode != Rcode.NXRRSET) continue
                        cache.addMessage(result)
                        sr = cache.lookupRecords(name, type, dc)
                        if (sr == null) sr = cache.lookupRecords(name, type, dc)
                        break
                    } catch (e: Exception) {
                    }
                }

            if (sr.isSuccessful) {
                val records = ArrayList<Record>()
                for (set in sr.answers()) {
                    records.addAll(set.rrs(true))
                }
                val addresses = records.map { (it as? ARecord)?.address ?: (it as AAAARecord).address }
                FileLog.d(addresses.toString())
                return addresses
            }

            FileLog.d("DNS Result $domain: $sr")

            if (sr.isCNAME) {
                FileLog.d("DNS CNAME: origin:$domain, CNAME ${sr.cname.target.toString(true)}")
                return lookup(sr.cname.target.toString(true), false)
            }

            if ((sr.isNXRRSET && !noFallback && !fallback)) {
                return lookup(domain, true)
            }
        }

        FileLog.d("Try system dns")

        try {
            return InetAddress.getAllByName(domain).toList()
        } catch (e: Exception) {
            FileLog.d("System dns fail: ${e.message ?: e.javaClass.simpleName}")
        }

        return listOf()
    }

    @JvmStatic
    fun getTxts(domain: String): List<String> {

        FileLog.d("Lookup $domain for txts")

        val type = Type.TXT
        val dc = DClass.IN

        val name = Name.fromConstantString("$domain.")
        val message = Message.newQuery(Record.newRecord(name, type, dc)).toWire()
        var sr = cache.lookupRecords(name, type, dc)

        if (!sr.isSuccessful) for (provider in providers()) {
            FileLog.d("Provider $provider")

            try {

                val response = HttpUtil.createPost(provider)
                    .contentType("application/dns-message")
                    .header(Header.ACCEPT, "application/dns-message")
                    .body(message)
                    .setConnectionTimeout(5000)
                    .execute()
                if (!response.isOk) continue
                val result = Message(response.bodyBytes())
                val rcode = result.header.rcode
                if (rcode != Rcode.NOERROR && rcode != Rcode.NXDOMAIN && rcode != Rcode.NXRRSET) continue
                cache.addMessage(result)
                sr = cache.lookupRecords(name, type, dc)
                if (sr == null) sr = cache.lookupRecords(name, type, dc)
                break
            } catch (e: Exception) {
                FileLog.e(e)
            }
        }

        if (sr.isSuccessful) {
            val txts = ArrayList<String>().apply {
                sr.answers().forEach { rRset -> rRset.rrs(true).filterIsInstance<TXTRecord>().forEach { addAll(it.strings) } }
            }
            FileLog.d(txts.toString())
        }

        FileLog.d(sr.toString())

        return listOf()

    }

}