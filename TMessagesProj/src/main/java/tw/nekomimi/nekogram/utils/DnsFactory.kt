package tw.nekomimi.nekogram.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.telegram.messenger.FileLog
import org.telegram.tgnet.ConnectionsManager
import org.xbill.DNS.*
import tw.nekomimi.nekogram.NekoConfig
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object DnsFactory {

    fun providers() = if (NekoConfig.customDoH.String().isNotBlank()) arrayOf(
        NekoConfig.customDoH.String())
    else arrayOf(
            // behaviour: try all concurrently and stop when the first result returns.
            "https://1.1.1.1/dns-query",
            "https://1.0.0.1/dns-query",
            "https://8.8.8.8/dns-query",
            "https://101.101.101.101/dns-query",
            "https://9.9.9.9/dns-query",
            "https://185.222.222.222/dns-query",
            "https://45.11.45.11/dns-query",
            "https://[2606:4700:4700::1111]/dns-query",
    )

    val cache = Cache()

    class CustomException(message: String) : Exception(message)

    //cancel blocking
    private fun cancel(client: OkHttpClient) {
        for (call in client.dispatcher.queuedCalls()) {
            call.cancel()
        }
        for (call in client.dispatcher.runningCalls()) {
            call.cancel()
        }
    }

    @JvmStatic
    @JvmOverloads
    fun lookup(domain: String, fallback: Boolean = false): List<InetAddress> {

        if (!NekoConfig.useSystemDNS.Bool()) {

            FileLog.d("Lookup $domain")

            ConnectionsManager.getIpStrategy()

            val noFallback = !ConnectionsManager.hasIpv4 || !ConnectionsManager.hasIpv6
            val type = if (noFallback) {
                if (ConnectionsManager.hasIpv4) Type.A else Type.AAAA
            } else if (NekoConfig.useIPv6.Bool() xor !fallback) Type.A else Type.AAAA

            val dc = DClass.IN
            val name = Name.fromConstantString("$domain.")
            val message = Message.newQuery(Record.newRecord(name, type, dc)).toWire()
            val sr = cache.lookupRecords(name, type, dc)

            fun sr2Ret(sr: SetResponse?): List<InetAddress>? {
                if (sr == null) return null

                if (sr.isSuccessful) {
                    val records = ArrayList<Record>()
                    for (set in sr.answers()) {
                        records.addAll(set.rrs(true))
                    }
                    val addresses = records.map {
                        (it as? ARecord)?.address ?: (it as AAAARecord).address
                    }
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

                return null
            }

            val cachedSr = sr2Ret(sr)
            if (cachedSr != null) return cachedSr

            val counterAll = AtomicInteger(0)
            val counterGood = AtomicInteger(0)

            val ret = runBlocking {
                val client = OkHttpClient()

                val ret: List<InetAddress>? = suspendCoroutine {
                    for (provider in providers()) {
                        launch(Dispatchers.IO) {
                            try {
                                val request = Request.Builder().url(provider)
                                        .header("accept", "application/dns-message")
                                        .post(message.toRequestBody("application/dns-message".toMediaTypeOrNull()))
                                        .build()
                                val response = client.newCall(request).execute()
                                if (!response.isSuccessful) {
                                    throw CustomException("$provider not successful")
                                }

                                val result = Message(response.body!!.bytes())
                                val rcode = result.header.rcode
                                if (rcode != Rcode.NOERROR && rcode != Rcode.NXDOMAIN && rcode != Rcode.NXRRSET) {
                                    throw CustomException("$provider dns error")
                                }

                                val ret = sr2Ret(cache.addMessage(result))
                                if (ret != null && counterGood.incrementAndGet() == 1) {
                                    it.resume(ret)
                                }
                            } catch (e: Exception) {
                                if (e is CustomException) {
                                    FileLog.e(e)
                                } else {
                                    FileLog.w(e.stackTraceToString())
                                }
                            }
                            if (counterAll.incrementAndGet() == providers().size && counterGood.get() == 0) {
                                it.resume(null)
                            }
                        }
                    }

                    launch {
                        delay(5000L)
                        cancel(client)
                    }
                }

                cancel(client)
                ret
            }
            if (ret != null) return ret
        }

        FileLog.d("Try system dns to resolve $domain")

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

        fun sr2Ret(sr: SetResponse?): List<String>? {
            if (sr != null && sr.isSuccessful) {
                val txts = ArrayList<String>().apply {
                    sr.answers().forEach { rRset -> rRset.rrs(true).filterIsInstance<TXTRecord>().forEach { addAll(it.strings) } }
                }
                FileLog.d(sr.toString())
                FileLog.d(txts.toString())
                return txts
            }
            return null
        }

        val cachedSr = sr2Ret(sr)
        if (cachedSr != null) return cachedSr

        var counterAll = AtomicInteger(0)
        var counterGood = AtomicInteger(0)

        return runBlocking {
            val client = OkHttpClient()

            val ret: List<String> = suspendCoroutine {
                for (provider in providers()) {
                    launch(Dispatchers.IO) {
                        try {
                            val request = Request.Builder().url(provider)
                                    .header("accept", "application/dns-message")
                                    .post(message.toRequestBody("application/dns-message".toMediaTypeOrNull()))
                                    .build()
                            val response = client.newCall(request).execute()
                            if (!response.isSuccessful) {
                                throw CustomException("$provider not successful")
                            }

                            val result = Message(response.body!!.bytes())
                            val rcode = result.header.rcode
                            if (rcode != Rcode.NOERROR && rcode != Rcode.NXDOMAIN && rcode != Rcode.NXRRSET) {
                                throw CustomException("$provider dns error")
                            }

                            val ret = sr2Ret(cache.addMessage(result))
                            if (ret != null && counterGood.incrementAndGet() == 1) {
                                // first OK
                                it.resume(ret)
                            }
                        } catch (e: Exception) {
                            if (e is CustomException) {
                                FileLog.e(e)
                            } else {
                                FileLog.w(e.stackTraceToString())
                            }
                        }
                        if (counterAll.incrementAndGet() == providers().size && counterGood.get() == 0) {
                            //all failed
                            it.resume(listOf())
                        }
                    }
                }

                launch {
                    delay(5000L)
                    cancel(client)
                }
            }

            cancel(client)
            ret
        }
    }

}