package tw.nekomimi.nekogram.sub

import org.dizitart.no2.objects.filters.ObjectFilters
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.database.mkDatabase

object SubManager {

    val database by lazy { mkDatabase("proxy_sub") }

    @JvmStatic
    val count
        get() = subList.find().totalCount()

    @JvmStatic
    val subList by lazy {

        database.getRepository("proxy_sub", SubInfo::class.java).apply {

            val public = find(ObjectFilters.eq("id", 1L)).firstOrDefault()

            update(SubInfo().apply {
                // SubManager.kt -> SubInfo.java -> ProxyLoads.kt

                name = LocaleController.getString("NekoXProxy", R.string.NekoXProxy)
                enable = public?.enable ?: true

                urls = listOf(
                        "doh://1.1.1.1/dns-query", // Domain is hardcoded in ProxyLoads.kt
                        "doh://1.0.0.1/dns-query",
                        "doh://101.101.101.101/dns-query",
                        "doh://8.8.8.8/resolve",
                        "doh://8.8.4.4/resolve",
                        "doh://[2606:4700:4700::1111]/dns-query",
                        "https://nekox.pages.dev/proxy_list_pro",  // Note: NO DoH apply to here and neko.services now.
                        "https://github.com/NekoX-Dev/ProxyList/blob/master/proxy_list_pro@js-file-line\">@<",
                        "https://api.github.com/repos/NekoX-Dev/ProxyList/contents/proxy_list_pro?ref=master@\"content\": \"@\"",
                )

                id = 1L
                internal = true

                proxies = public?.proxies ?: listOf()

            }, true)

        }

    }

}