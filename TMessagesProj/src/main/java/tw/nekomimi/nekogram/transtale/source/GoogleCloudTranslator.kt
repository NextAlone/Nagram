package tw.nekomimi.nekogram.transtale.source

import cn.hutool.core.util.StrUtil
import cn.hutool.http.HttpUtil
import org.json.JSONObject
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.transtale.Translator

object GoogleCloudTranslator : Translator {

    override suspend fun doTranslate(from: String, to: String, query: String): String {

        if (to !in targetLanguages) {

            throw UnsupportedOperationException(LocaleController.getString(R.string.TranslateApiUnsupported))

        }

        if (StrUtil.isBlank(NekoConfig.googleCloudTranslateKey.String())) error("Missing Cloud Translate Key")

        val response = HttpUtil.createPost("https://translation.googleapis.com/language/translate/v2")
                .form("q", query)
                .form("target", to)
                .form("format", "text")
                .form("key", NekoConfig.googleCloudTranslateKey.String())
                .apply {
                    if (from != "auto") form("source", from)
                }.execute()

        if (response.status != 200) {

            error("HTTP ${response.status} : ${response.body()}")

        }

        var respObj = JSONObject(response.body())

        if (respObj.isNull("data")) error(respObj.toString(4))

        respObj = respObj.getJSONObject("data")

        val respArr = respObj.getJSONArray("translations")

        if (respArr.length() == 0) error("Empty translation result")

        return respArr.getJSONObject(0).getString("translatedText")

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