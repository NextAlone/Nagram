package tw.nekomimi.nekogram.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.LocaleController
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.TLRPC.TL_langPackDifference
import org.telegram.tgnet.TLRPC.TL_langpack_getLangPack
import tw.nekomimi.nekogram.utils.FileUtil.delete
import tw.nekomimi.nekogram.utils.FileUtil.initDir
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

object LocaleUtil {

    @JvmField
    val cacheDir = File(ApplicationLoader.applicationContext.cacheDir, "builtIn_lang_export")

    @JvmStatic
    fun fetchAndExportLang(account: Int) = runBlocking {

        delete(cacheDir)
        initDir(cacheDir)

        val finish = AtomicInteger()

        for (localeInfo in LocaleController.getInstance().languages) {

            if (!localeInfo.builtIn || localeInfo.pathToFile != "unofficial") continue

            if (localeInfo.hasBaseLang()) {

                finish.incrementAndGet()

                ConnectionsManager.getInstance(account).sendRequest(TL_langpack_getLangPack().apply {
                    lang_code = localeInfo.getBaseLangCode()
                }, { response, _ ->

                    if (response is TL_langPackDifference) {

                        LocaleController.getInstance().saveRemoteLocaleStrings(localeInfo, response, account)

                    }

                    localeInfo.pathToBaseFile.copyTo(File(cacheDir,localeInfo.pathToBaseFile.name))

                    finish.decrementAndGet()

                }, ConnectionsManager.RequestFlagWithoutLogin)

            }

            finish.incrementAndGet()

            ConnectionsManager.getInstance(account).sendRequest(TL_langpack_getLangPack().apply {
                lang_code = localeInfo.langCode
            }, { response, _ ->

                if (response is TL_langPackDifference) {

                    LocaleController.getInstance().saveRemoteLocaleStrings(localeInfo, response, account)

                }

                localeInfo.getPathToFile().copyTo(File(cacheDir,localeInfo.getPathToFile().name))

                finish.decrementAndGet()

            }, ConnectionsManager.RequestFlagWithoutLogin)

        }

        while (finish.get() != 0) delay(100L)

    }

}