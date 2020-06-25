package tw.nekomimi.nekogram.transtale.source

import android.text.TextUtils
import cn.hutool.core.util.StrUtil
import okhttp3.Request
import org.json.JSONArray
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.NekoXPushService
import tw.nekomimi.nekogram.transtale.TransUtils
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.utils.HttpUtil
import tw.nekomimi.nekogram.utils.applyUserAgent
import java.util.*
import java.util.regex.Pattern

object GoogleWebTranslator : Translator {

    lateinit var tkk: LongArray

    override fun doTranslate(from: String, to: String, query: String): String {

        if (NekoConfig.translationProvider != 2 && StrUtil.isNotBlank(NekoConfig.googleCloudTranslateKey)) return GoogleCloudTranslator.doTranslate(from, to, query)

        if (to !in targetLanguages) {

            throw UnsupportedOperationException(LocaleController.getString("TranslateApiUnsupported", R.string.TranslateApiUnsupported))

        }

        if (!GoogleWebTranslator::tkk.isInitialized) {

            val response = HttpUtil.get("https://translate.google." + if (NekoConfig.translationProvider == 2) "cn" else "com")

            if (TextUtils.isEmpty(response)) {

                error("Tkk init failed")

            }

            val matcher = Pattern.compile("tkk\\s*[:=]\\s*['\"]([0-9]+)\\.([0-9]+)['\"]", Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE).matcher(response)

            tkk = if (matcher.find() && matcher.group(1) != null && matcher.group(2) != null) {
                longArrayOf(matcher.group(1).toLong(), matcher.group(2).toLong())
            } else error("Tkk match failed")

        }

        val tk = TransUtils.signWeb(query, tkk[0], tkk[1])

        val url = "https://translate.google." + (if (NekoConfig.translationProvider == 2) "cn" else "com") + "/translate_a/single?client=webapp&dt=t&sl=auto" +
                "&tl=" + to +
                "&tk=" + tk +
                "&q=" + TransUtils.encodeURIComponent(query) // 不能用URLEncoder

        val response = runCatching {
            (if (NekoConfig.translationProvider == 2) HttpUtil.okHttpClientNoDoh else HttpUtil.okHttpClient).newCall(Request.Builder().url(url).applyUserAgent().build()).execute()
        }.recoverCatching {
            HttpUtil.okHttpClientWithCurrProxy.newCall(Request.Builder().url(url).applyUserAgent().build()).execute()
        }.getOrThrow()

        if (response.code != 200) {

            error("HTTP ${response.code} : ${response.body?.string()}")

        }

        val result = StringBuilder()

        val array = JSONArray(response.body!!.string()).getJSONArray(0)
        for (index in 0 until array.length()) {
            result.append(array.getJSONArray(index).getString(0))
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
