package tw.nekomimi.nekogram.transtale

import android.view.View
import cn.hutool.core.util.ArrayUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.http.HttpRequest
import org.apache.commons.lang3.LocaleUtils
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.messenger.SharedConfig
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.ui.PopupBuilder
import tw.nekomimi.nekogram.cc.CCConverter
import tw.nekomimi.nekogram.cc.CCTarget
import tw.nekomimi.nekogram.transtale.source.*
import tw.nekomimi.nekogram.utils.UIUtil
import tw.nekomimi.nekogram.utils.receive
import tw.nekomimi.nekogram.utils.receiveLazy
import java.util.*

fun <T : HttpRequest> T.applyProxy(): T {
    SharedConfig.getActiveSocks5Proxy()?.let { setProxy(it) }
    return this
}

val String.code2Locale: Locale by receiveLazy<String, Locale> {
    var ret: Locale
    if (this == null || this.isBlank()) {
        ret = LocaleController.getInstance().currentLocale
    } else {
        val args = replace('-', '_').split('_')

        if (args.size == 1) {
            ret = Locale(args[0])
        } else {
            ret = Locale(args[0], args[1])
        }
    }
    ret
}

val Locale.locale2code by receiveLazy<Locale, String> {

    if (StrUtil.isBlank(country)) {
        language
    } else {
        "$language-$country"
    }

}

val LocaleController.LocaleInfo.locale by receiveLazy<LocaleController.LocaleInfo, Locale> { pluralLangCode.code2Locale }

val Locale.transDb by receive<Locale, TranslateDb> {

    TranslateDb.repo[this] ?: TranslateDb(locale2code).also {

        TranslateDb.repo[this] = it

    }

}

val String.transDbByCode by receive<String, TranslateDb> { code2Locale.transDb }

interface Translator {

    suspend fun doTranslate(from: String, to: String, query: String): String

    companion object {

        @Throws(Exception::class)
        suspend fun translate(query: String) = translate(
            NekoConfig.translateToLang.String()?.code2Locale
                ?: LocaleController.getInstance().currentLocale, query)

        const val providerGoogle = 1
        const val providerGoogleCN = 2
        const val providerYandex = 3
        const val providerLingo = 4
        const val providerMicrosoft = 5
        const val providerYouDao = 6
        const val providerDeepL = 7
        const val providerTelegram = 8

        @Throws(Exception::class)
        suspend fun translate(to: Locale, query: String): String {

            var language = to.language
            var country = to.country

            if (language == "in") language = "id"
            if (country.lowercase() == "duang") country = "CN"

            val provider = NekoConfig.translationProvider.Int()
            when (provider) {
                providerYouDao -> if (language == "zh") {
                    language = "zh-CHS"
                }
                providerDeepL -> language = language.toUpperCase()
                providerMicrosoft,
                providerGoogle,
                providerGoogleCN -> if (language == "zh") {
                    val countryUpperCase = country.toUpperCase()
                    if (countryUpperCase == "CN" || countryUpperCase == "DUANG") {
                        language = if (provider == providerMicrosoft) "zh-Hans" else "zh-CN"
                    } else if (countryUpperCase == "TW" || countryUpperCase == "HK") {
                        language = if (provider == providerMicrosoft) "zh-HanT" else "zh-TW"
                    }
                }

            }
            val translator = when (provider) {
                providerGoogle, providerGoogleCN -> GoogleAppTranslator
                providerYandex -> YandexTranslator
                providerLingo -> LingoTranslator
                providerMicrosoft -> MicrosoftTranslator
                providerYouDao -> YouDaoTranslator
                providerDeepL -> DeepLTranslator
                providerTelegram -> TelegramAPITranslator
                else -> throw IllegalArgumentException()
            }

            // FileLog.d("[Trans] use provider ${translator.javaClass.simpleName}, toLang: $toLang, query: $query")

            val result =  translator.doTranslate("auto", language, query).also {

                to.transDb.save(query, it)

            }

            if (language == "zh") {
                val countryUpperCase = country.toUpperCase()
                if (countryUpperCase == "CN") {
                    return CCConverter.get(CCTarget.SP).convert(result)
                } else if (countryUpperCase == "TW") {
                    return CCConverter.get(CCTarget.TT).convert(result)
                }
            }

            return result

        }

        @JvmStatic
        @JvmOverloads
        fun showTargetLangSelect(anchor: View, input: Boolean = false, full: Boolean = false, callback: (Locale) -> Unit) {

            val builder = PopupBuilder(anchor)

            var locales = (if (full) LocaleUtils.availableLocaleList()
                    .filter { it.variant.isBlank() } else LocaleController.getInstance()
                    .languages
                    .map { it.pluralLangCode }
                    .toSet()
                    .filter { !it.lowercase().contains("duang") }
                    .map { it.code2Locale })
                    .toTypedArray()

            val currLocale = LocaleController.getInstance().currentLocale

            for (i in locales.indices) {

                val defLang = if (!input) currLocale else Locale.ENGLISH

                if (locales[i] == defLang) {

                    locales = ArrayUtil.remove(locales, i)
                    locales = ArrayUtil.insert(locales, 0, defLang)

                    break

                }

            }

            val localeNames = arrayOfNulls<String>(if (full) locales.size else locales.size + 1)

            for (i in locales.indices) {

                localeNames[i] = if (!full && i == 0) {

                    LocaleController.getString("Default", R.string.Default) + " ( " + locales[i].getDisplayName(currLocale) + " )"

                } else {

                    locales[i].getDisplayName(currLocale)

                }

            }

            if (!full) {

                localeNames[localeNames.size - 1] = LocaleController.getString("More", R.string.More)

            }

            builder.setItems(localeNames.filterIsInstance<CharSequence>().toTypedArray()) { index: Int, _ ->

                if (index == locales.size) {

                    showTargetLangSelect(anchor, input, true, callback)

                } else {

                    callback(locales[index])

                }

            }

            builder.show()

        }

        @JvmStatic
        @JvmOverloads
        fun showCCTargetSelect(anchor: View, input: Boolean = true, callback: (String) -> Unit) {

            val builder = PopupBuilder(anchor)

            builder.setItems(arrayOf(
                    if (!input) LocaleController.getString("CCNo", R.string.CCNo) else null,
                    LocaleController.getString("CCSC", R.string.CCSC),
                    LocaleController.getString("CCSP", R.string.CCSP),
                    LocaleController.getString("CCTC", R.string.CCTC),
                    LocaleController.getString("CCHK", R.string.CCHK),
                    LocaleController.getString("CCTT", R.string.CCTT),
                    LocaleController.getString("CCJP", R.string.CCJP)
            )) { index: Int, _ ->
                callback(when (index) {
                    1 -> CCTarget.SC.name
                    2 -> CCTarget.SP.name
                    3 -> CCTarget.TC.name
                    4 -> CCTarget.HK.name
                    5 -> CCTarget.TT.name
                    6 -> CCTarget.JP.name
                    else -> ""
                })
            }

            builder.show()

        }

        @JvmStatic
        @JvmOverloads
        fun translate(to: Locale = NekoConfig.translateToLang.String()?.code2Locale
                ?: LocaleController.getInstance().currentLocale, query: String, translateCallBack: TranslateCallBack) {

            UIUtil.runOnIoDispatcher {

                runCatching {

                    val result = translate(to, query)

                    UIUtil.runOnUIThread(Runnable {

                        translateCallBack.onSuccess(result)

                    })

                }.onFailure {

                    translateCallBack.onFailed(it is UnsupportedOperationException, it.message
                            ?: it.javaClass.simpleName)

                }

            }

        }

        interface TranslateCallBack {

            fun onSuccess(translation: String)
            fun onFailed(unsupported: Boolean, message: String)

        }

    }

}