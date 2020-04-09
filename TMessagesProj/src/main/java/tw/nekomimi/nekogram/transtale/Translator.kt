package tw.nekomimi.nekogram.transtale

import org.telegram.messenger.FileLog
import org.telegram.messenger.LocaleController
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.transtale.source.*
import tw.nekomimi.nekogram.utils.UIUtil
import java.util.*

interface Translator {

    fun doTranslate(from: String, to: String, query: String): String

    companion object {

        @Throws(Exception::class)
        @JvmStatic
        @JvmOverloads
        fun translate(to: Locale = LocaleController.getInstance().currentLocale, query: String): String {

            var toLang = to.language

            if (NekoConfig.translationProvider < 3) {

                if (to.language == "zh" && (to.country.toUpperCase() == "CN" || to.country.toUpperCase() == "TW")) {
                    toLang = to.language + "-" + to.country.toUpperCase()
                } else if (to.language == "pt" && to.country in arrayOf("PT","BR")) {
                    toLang = to.language + "-" + to.country.toUpperCase()
                }

            }

            val translator = when (NekoConfig.translationProvider) {
                in 1..2 -> GoogleWebTranslator
                3 -> LingoTranslator
                else -> throw IllegalArgumentException()
            }

           // FileLog.d("[Trans] use provider ${translator.javaClass.simpleName}, toLang: $toLang, query: $query")

            return translator.doTranslate("auto", toLang, query)

        }

        @JvmStatic
        @JvmOverloads
        fun translate(to: Locale = LocaleController.getInstance().currentLocale, query: String, translateCallBack: TranslateCallBack) {

            UIUtil.runOnIoDispatcher(Runnable {

                runCatching {

                    val result = translate(to, query)

                    UIUtil.runOnUIThread(Runnable {

                        translateCallBack.onSuccess(result)

                    })

                }.onFailure {

                    translateCallBack.onFailed(it is UnsupportedOperationException, it.message ?: it.javaClass.simpleName)

                }

            })

        }

        interface TranslateCallBack {

            fun onSuccess(translation: String)
            fun onFailed(unsupported: Boolean, message: String)

        }

    }

}