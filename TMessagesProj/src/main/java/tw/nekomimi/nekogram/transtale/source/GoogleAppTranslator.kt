package tw.nekomimi.nekogram.transtale.source

import cn.hutool.core.util.StrUtil
import okhttp3.Request
import org.json.JSONObject
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.transtale.TransUtils
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.utils.HttpUtil
import tw.nekomimi.nekogram.utils.applyUserAgent

object GoogleAppTranslator : Translator {

    override suspend fun doTranslate(from: String, to: String, query: String): String {

        if (NekoConfig.translationProvider != 2 && StrUtil.isNotBlank(NekoConfig.googleCloudTranslateKey)) return GoogleCloudTranslator.doTranslate(from, to, query)

        if (to !in targetLanguages) {

            throw UnsupportedOperationException(LocaleController.getString("TranslateApiUnsupported", R.string.TranslateApiUnsupported))

        }

        val url = "https://translate.google." + (if (NekoConfig.translationProvider == 2) "cn" else "com") + "/translate_a/single?dj=1" +
                "&q=" + TransUtils.encodeURIComponent(query) +
                "&sl=auto" +
                "&tl=" + to +
                "&ie=UTF-8&oe=UTF-8&client=at&dt=t&otf=2"

        val response = runCatching {
            (if (NekoConfig.translationProvider == 2) HttpUtil.okHttpClientNoDoh else HttpUtil.okHttpClient).newCall(Request.Builder().url(url)
                    .header("User-Agent", "GoogleTranslate/6.14.0.04.343003216 (Linux; U; Android 10; Redmi K20 Pro)").build()).execute()
        }.recoverCatching {
            HttpUtil.okHttpClientWithCurrProxy.newCall(Request.Builder().url(url).applyUserAgent().build()).execute()
        }.getOrThrow()

        if (response.code != 200) {

            error("HTTP ${response.code} : ${response.body?.string()}")

        }

        val result = StringBuilder()

        val array = JSONObject(response.body!!.string()).getJSONArray("sentences")
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
