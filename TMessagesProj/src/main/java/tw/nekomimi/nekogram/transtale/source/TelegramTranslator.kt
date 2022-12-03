package tw.nekomimi.nekogram.transtale.source

import android.text.TextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.TLObject
import org.telegram.tgnet.TLRPC.*
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.transtale.TransUtils
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.transtale.applyProxy
import tw.nekomimi.nekogram.utils.applyIf
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference


object TelegramTranslator : Translator {

    @JvmStatic
    fun convertLanguageCode(language: String, country: String): String {
        val languageLowerCase = language.lowercase(Locale.getDefault())
        val code: String = if (!TextUtils.isEmpty(country)) {
            val countryUpperCase = country.uppercase(Locale.getDefault())
            if (targetLanguages.contains("$languageLowerCase-$countryUpperCase")) {
                "$languageLowerCase-$countryUpperCase"
            } else if (languageLowerCase == "zh") {
                when (countryUpperCase) {
                    "DG" -> "zh-CN"
                    "zh-TW" -> "zh-TW"
                    else -> languageLowerCase
                }
            } else {
                languageLowerCase
            }
        } else {
            languageLowerCase
        }
        return code
    }

    override suspend fun doTranslate(from: String, to: String, query: String): String {
        val result: AtomicReference<Any> = AtomicReference<Any>()
        val latch = CountDownLatch(1)

        val req = TL_messages_translateText()
        req.flags = req.flags or 2
        req.to_lang = to
        req.text = query
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(
            req
        ) { res: TLObject?, error: TL_error? ->
            if (error == null) {
                if (res is TL_messages_translateResultText) {
                    result.set(res.text)
                } else {
                    result.set(UnsupportedOperationException(LocaleController.getString("TranslateApiUnsupported", R.string.TranslateApiUnsupported)))
                }
            } else {
                result.set(UnsupportedOperationException(error.text))
            }
            latch.countDown()
        }

        withContext(Dispatchers.IO) {
            latch.await()
        }
        val s: Any = result.get()
        return if (s is String) {
            s.toString()
        } else {
            error(s.toString())
        }
    }

    private val targetLanguages = listOf(
        "sq", "ar", "am", "az", "ga", "et", "eu", "be", "bg", "is", "pl", "bs", "fa",
        "af", "da", "de", "ru", "fr", "tl", "fi", "fy", "km", "ka", "gu", "kk", "ht",
        "ko", "ha", "nl", "ky", "gl", "ca", "cs", "kn", "co", "hr", "ku", "la", "lv",
        "lo", "lt", "lb", "ro", "mg", "mt", "mr", "ml", "ms", "mk", "mi", "mn", "bn",
        "my", "hmn", "xh", "zu", "ne", "no", "pa", "pt", "ps", "ny", "ja", "sv", "sm",
        "sr", "st", "si", "eo", "sk", "sl", "sw", "gd", "ceb", "so", "tg", "te", "ta",
        "th", "tr", "cy", "ur", "uk", "uz", "es", "iw", "el", "haw", "sd", "hu", "sn",
        "hy", "ig", "it", "yi", "hi", "su", "id", "jw", "en", "yo", "vi", "zh-TW", "zh-CN", "zh")
}
