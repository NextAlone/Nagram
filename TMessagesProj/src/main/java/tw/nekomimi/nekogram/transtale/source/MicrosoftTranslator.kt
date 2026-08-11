package tw.nekomimi.nekogram.transtale.source

import io.ktor.http.ContentType
import org.json.JSONArray
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.transtale.Translator
import xyz.nextalone.nagram.network.NetworkRequestBuilder
import java.util.Arrays

object MicrosoftTranslator : Translator {

    private val targetLanguages = Arrays.asList(
            "ar", "as", "bn", "bs", "bg", "yue", "ca", "zh", "zh-Hans", "zh-Hant",
            "hr", "cs", "da", "prs", "nl", "en", "et", "fj", "fil", "fi",
            "fr", "de", "el", "gu", "ht", "he", "hi", "mww", "hu", "is",
            "id", "ga", "it", "ja", "kn", "kk", "tlh", "ko", "ku", "kmr",
            "lv", "lt", "mg", "ms", "ml", "mt", "mi", "mr", "nb", "or", "ps",
            "fa", "pl", "pt", "pa", "otq", "ro", "ru", "sm", "sr", "sk", "sl",
            "es", "sw", "sv", "ty", "ta", "te", "th", "to", "tr", "uk", "ur",
            "vi", "cy", "yua")

    override suspend fun doTranslate(from: String, to: String, query: String): String {
        if (to !in targetLanguages) {
            throw UnsupportedOperationException(LocaleController.getString(R.string.TranslateApiUnsupported))
        }

        val source = JSONArray()
        for (s in query.split("\n")) {
            source.put(s)
        }

        val response = NetworkRequestBuilder.post("https://edge.microsoft.com/translate/translatetext") {
            contentType(ContentType.Application.Json)
            parameter("from", "")
            parameter("to", to)
            parameter("isEnterpriseClient", "false")
            setBody(source.toString())
        }.execute()

        if (response.statusCode != 200) {
            error("HTTP ${response.statusCode} : ${response.body}")
        }

        val target = JSONArray(response.body)
        val result = StringBuilder()
        for (i in 0 until target.length()) {
            val obj = target.getJSONObject(i)
            val tra = obj.getJSONArray("translations")
            if (tra.length() >= 1) {
                val traObj = tra.getJSONObject(0)
                val text = traObj.getString("text")
                result.append(text)
                if (i != target.length() - 1) {
                    result.append("\n")
                }
            }
        }

        return result.toString()
    }
}