package tw.nekomimi.nekogram.proxy

import org.dizitart.no2.objects.filters.ObjectFilters
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.database.mkDatabase

object SubManager {

    val database by lazy { mkDatabase("proxy_sub") }

    const val publicProxySubID = 1L

    @JvmStatic
    val count
        get() = subList.find().totalCount()

    @JvmStatic
    val subList by lazy {

        database.getRepository("proxy_sub", SubInfo::class.java).apply {

            val public = find(ObjectFilters.eq("id", publicProxySubID)).firstOrDefault()

            update(SubInfo().apply {
                // SubManager.kt -> SubInfo.java -> ProxyLoads.kt

                name = LocaleController.getString("TeleTuxProxy", R.string.TeleTuxProxy)
                enable = public?.enable ?: true

                urls = listOf(
                        "https://nekox.pages.dev/proxy_list_pro",  // Note: NO DoH apply to here and neko.services now.
                        "https://github.com/NekoX-Dev/ProxyList/blob/master/proxy_list_pro@js-file-line\">@<",
                        "https://api.github.com/repos/NekoX-Dev/ProxyList/contents/proxy_list_pro?ref=master@\"content\": \"@\"",
                )

                id = publicProxySubID
                internal = true

                proxies = public?.proxies ?: listOf()

            }, true)

        }

    }

}