package tw.nekomimi.nekogram.transtale.source

import android.text.TextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.telegram.messenger.FileLog
import org.telegram.messenger.SharedConfig
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.TLObject
import org.telegram.tgnet.TLRPC
import org.telegram.tgnet.TLRPC.TL_error
import org.telegram.tgnet.TLRPC.TL_messages_translateResult
import org.telegram.tgnet.TLRPC.TL_messages_translateText
import tw.nekomimi.nekogram.transtale.Translator
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object TelegramAPITranslator : Translator {

//    val targetLanguages = listOf("DE", "EN", "ES", "FR", "IT", "JA", "NL", "PL", "PT", "RU", "ZH")

    @OptIn(InternalCoroutinesApi::class)
    override suspend fun doTranslate(from: String, to: String, query: String): String {

        return suspendCoroutine {
            val req = TL_messages_translateText()
            req.peer = null
            req.flags = req.flags or 2
            req.text.add(TLRPC.TL_textWithEntities().apply {
                text = query
            })
            req.to_lang = to

            try {
                ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req) { res: TLObject?, err: TL_error? ->
                    if (res is TL_messages_translateResult && res.result.isNotEmpty()) {
                        it.resume(res.result[0].text)
                    } else {
                        FileLog.e(err?.text)
                        it.resumeWithException(RuntimeException("Failed to translate by Telegram API"))
                    }
                }
            } catch (e: Exception) {
                FileLog.e(e)
                it.resumeWithException(e)
            }
        }
    }

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
