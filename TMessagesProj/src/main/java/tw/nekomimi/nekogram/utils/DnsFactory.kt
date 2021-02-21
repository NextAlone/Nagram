package tw.nekomimi.nekogram.utils

import cn.hutool.http.Header
import cn.hutool.http.HttpUtil
import org.telegram.messenger.FileLog
import org.telegram.tgnet.ConnectionsManager
import org.xbill.DNS.*
import java.net.InetAddress


object DnsFactory {

    val providers = arrayOf(
            "https://dns.twnic.tw/dns-query",
            "https://mozilla.cloudflare-dns.com/dns-query",
            "https://dns.google/dns-query"
    )

    val cache = Cache()

    @JvmStatic
    fun lookup(domain: String): List<InetAddress> {

        FileLog.d("Lookup $domain")

        val type = if (!ConnectionsManager.useIpv6Address()) Type.A else Type.AAAA
        val dc = DClass.IN

        val name = Name.fromConstantString("$domain.")
        val message = Message.newQuery(Record.newRecord(name, type, dc)).toWire()
        var sr = cache.lookupRecords(name, type, dc)

        if (!sr.isSuccessful) for (provider in providers) {
            FileLog.d("Provider $provider")

            try {

                val response = HttpUtil.createPost(provider)
                        .contentType("application/dns-message")
                        .header(Header.ACCEPT, "application/dns-message")
                        .body(message)
                        .execute()
                if (!response.isOk) continue
                val result = Message(response.bodyBytes())
                val rcode = result.header.rcode
                if (rcode != Rcode.NOERROR && rcode != Rcode.NXDOMAIN) continue
                cache.addMessage(result)
                sr = cache.lookupRecords(name, type, dc)
                if (sr == null) sr = cache.lookupRecords(name, type, dc)
                if (sr.isSuccessful || sr.isNXDOMAIN) break
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
        } else if (sr.isNXDOMAIN) {
            FileLog.d("NXDOMAIN")
            return listOf()
        }

        runCatching { return InetAddress.getAllByName(domain).toList() }
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

        if (!sr.isSuccessful) for (provider in providers) {
            FileLog.d("Provider $provider")

            try {

                val response = HttpUtil.createPost(provider)
                        .contentType("application/dns-message")
                        .header(Header.ACCEPT, "application/dns-message")
                        .body(message)
                        .execute()
                if (!response.isOk) continue
                val result = Message(response.bodyBytes())
                val rcode = result.header.rcode
                if (rcode != Rcode.NOERROR && rcode != Rcode.NXDOMAIN) continue
                cache.addMessage(result)
                sr = cache.lookupRecords(name, type, dc)
                if (sr == null) sr = cache.lookupRecords(name, type, dc)
                if (sr.isSuccessful || sr.isNXDOMAIN) break
            } catch (e: Exception) {
                FileLog.e(e)
            }
        }

        if (sr.isSuccessful) {
            val txts = ArrayList<String>().apply {
                sr.answers().forEach { rRset -> rRset.rrs(true).filterIsInstance<TXTRecord>().forEach { addAll(it.strings) } }
            }
            FileLog.d(txts.toString())
        } else if (sr.isNXDOMAIN) {
            FileLog.d("NXDOMAIN")
            return listOf()
        }

        return listOf()

    }

}