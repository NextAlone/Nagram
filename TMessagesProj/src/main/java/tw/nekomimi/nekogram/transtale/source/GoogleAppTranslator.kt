package tw.nekomimi.nekogram.transtale.source

import cn.hutool.core.util.StrUtil
import org.json.JSONObject
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.transtale.TransUtils
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.transtale.applyProxy
import tw.nekomimi.nekogram.utils.applyIf

object GoogleAppTranslator : Translator {

    override suspend fun doTranslate(from: String, to: String, query: String): String {

        if (NekoConfig.translationProvider.Int() != 2 && StrUtil.isNotBlank(
                NekoConfig.googleCloudTranslateKey.String())) return GoogleCloudTranslator.doTranslate(from, to, query)

        if (to !in targetLanguages) {

            throw UnsupportedOperationException(LocaleController.getString(R.string.TranslateApiUnsupported))

        }

        val url = "https://translate.google." + (if (NekoConfig.translationProvider.Int() == 2) "cn" else "com") + "/translate_a/single?dj=1" +
                "&q=" + TransUtils.encodeURIComponent(query) +
                "&sl=auto" +
                "&tl=" + to +
                "&ie=UTF-8&oe=UTF-8&client=at&dt=t&otf=2"

        val response = cn.hutool.http.HttpUtil
                .createGet(url)
                .applyIf(NekoConfig.translationProvider.Int() != 2) { applyProxy() }
                .header("User-Agent", "GoogleTranslate/6.14.0.04.343003216 (Linux; U; Android 10; Redmi K20 Pro)")
                .execute()

        if (response.status != 200) {

            error("HTTP ${response.status} : ${response.body()}")

        }

        val result = StringBuilder()

        val array = JSONObject(response.body()).getJSONArray("sentences")
        for (index in 0 until array.length()) {
            result.append(array.getJSONObject(index).getString("trans"))
        }

        return result.toString()
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
