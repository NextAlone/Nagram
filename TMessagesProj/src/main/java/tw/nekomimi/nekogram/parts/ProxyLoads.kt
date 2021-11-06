package tw.nekomimi.nekogram.parts

import android.util.Base64
import cn.hutool.http.HttpResponse
import cn.hutool.http.HttpUtil
import kotlinx.coroutines.*
import org.telegram.messenger.FileLog
import tw.nekomimi.nekogram.utils.ProxyUtil.parseProxies
import tw.nekomimi.nkmr.NekomuraUtil
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun loadProxiesPublic(urls: List<String>, exceptions: MutableMap<String, Exception>): List<String> {
    val urlsDoH = ArrayList<String>()
    val urlsOld = ArrayList<String>()

    for (url in urls) {
        if (url.startsWith("doh://")) {
            urlsDoH.add(url)
        } else {
            urlsOld.add(url)
        }
    }

    // Try DoH first ( github.com is often blocked
    try {
        return runBlocking {
            var jobs = ArrayList<Job>()

            var a: List<String> = suspendCoroutine {
                val ret = AtomicBoolean()
                val cl = AtomicInteger(urlsDoH.size)

                for (url in urlsDoH) {
                    jobs.add(launch(Dispatchers.IO) {
                        try {
                            val para = "?name=nekogramx-public-proxy-v1.seyana.moe&type=TXT"
                            val reqURL = url.replace("doh://", "https://", false) + para

                            val req = HttpUtil.createGet(reqURL)
                            req.addHeaders(mapOf("accept" to "application/dns-json"))
                            req.timeout(10 * 1000)

                            var content = req.execute().body()
                            content = content.replace("\\\"", "", false)
                            content = content.replace(" ", "", false)

                            val proxiesString = NekomuraUtil.getSubString(content, "#NekoXStart#", "#NekoXEnd#")
                            if (proxiesString.equals(content)) {
                                throw Exception("DoH get public proxy: Not found")
                            }

                            val proxies = parseProxies(proxiesString)
                            if (proxies.count() == 0) {
                                throw Exception("DoH get public proxy: Empty")
                            }

                            if (ret.getAndSet(true)) return@launch
//                            Log.e("NekoPublicProxy", reqURL)
                            it.resume(proxies)
                        } catch (e: Exception) {
//                            Log.e("NekoPublicProxy", e.stackTraceToString())
                            FileLog.d(url)
                            FileLog.e(e.stackTraceToString())
                            exceptions[url] = e
                            if (cl.decrementAndGet() == 0) {
                                it.resumeWithException(e)
                            }
                        }
                    })
                }
            }

            // Quit when the first success
            for (job in jobs) {
                // TODO cannot cancel Hutool HTTPRequest now...
                job.cancel()
            }
            a
        }
    } catch (e: Exception) {
        // Try Other Urls
        return loadProxies(urlsOld, exceptions)
    }
}


fun loadProxies(urls: List<String>, exceptions: MutableMap<String, Exception>): List<String> {

    return runBlocking {

        suspendCoroutine {

            val ret = AtomicBoolean()
            val cl = AtomicInteger(urls.size)
            var defer: List<String>? = null

            for (url in urls) {
                launch(Dispatchers.IO) {
                    try {
                        var subX = ""
                        var subY = ""
                        var urlFinal = url
                        if (url.count { it == '@' } == 2) {
                            subX = url.substringAfter("@")
                                    .substringBefore("@")
                            subY = url.substringAfterLast("@")
                            urlFinal = url.substringBefore("@")
                        }
                        var nextUrl = url
                        var resp: HttpResponse
                        while (true) {
                            resp = HttpUtil.createGet(nextUrl).timeout(10 * 1000).execute();
                            if (resp.status == 301 || resp.status == 302 || resp.status == 307) {
                                nextUrl = resp.header("Location");
                                continue;
                            }
                            break;
                        }
                        var content = resp.body()
                        if (subX.isNotBlank()) {
                            content = content.substringAfter(subX)
                                    .substringBefore(subY)
                        }

                        if (url.contains("https://api.github.com")) {
                            content = content.replace("\\n", "", false)
                            content = String(Base64.decode(content, Base64.DEFAULT))
                        }

                        val proxies = parseProxies(content)
                        if (urlFinal.contains("https://gitee.com/") && cl.decrementAndGet() > 0) {
                            defer = proxies
                        } else {
                            if (ret.getAndSet(true)) return@launch
                            it.resume(proxies)
                        }
                        FileLog.d(url)
                        FileLog.d("Success")
                    } catch (e: Exception) {
                        FileLog.d(url)
                        FileLog.e(e)
                        exceptions[url] = e
                        if (cl.decrementAndGet() == 0) {
                            if (defer != null) {
                                it.resume(defer!!)
                            } else {
                                it.resumeWithException(e)
                            }
                        }
                    }
                }
            }
        }
    }
}