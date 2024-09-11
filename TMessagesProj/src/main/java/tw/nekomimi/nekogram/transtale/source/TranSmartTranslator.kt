package tw.nekomimi.nekogram.transtale.source

import cn.hutool.http.HttpUtil
import org.json.JSONArray
import org.json.JSONObject
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.utils.applyUserAgent
import java.util.Date
import java.util.UUID

object TranSmartTranslator : Translator {

    private val targetLanguages = listOf(
        "ar", "fr", "fil", "lo", "ja", "it", "hi", "id", "vi", "de", "km", "ms", "th", "tr", "zh", "ru", "ko", "pt", "es"
    )

    private fun getRandomBrowserVersion(): String {
        val majorVersion = (Math.random() * 17).toInt() + 100
        val minorVersion = (Math.random() * 20).toInt()
        val patchVersion = (Math.random() * 20).toInt()
        return "$majorVersion.$minorVersion.$patchVersion"
    }

    private fun getRandomOperatingSystem(): String {
        val operatingSystems = arrayOf("Mac OS", "Windows")
        val randomIndex = (Math.random() * operatingSystems.size).toInt()
        return operatingSystems[randomIndex]
    }

    override suspend fun doTranslate(from: String, to: String, query: String): String {

        if (to !in targetLanguages) {
            error(LocaleController.getString(R.string.TranslateApiUnsupported))
        }

        val source = JSONArray()
        for (s in query.split("\n")) {
            source.put(s)
        }

        val response = HttpUtil.createPost("https://transmart.qq.com/api/imt")
            .header("Content-Type", "application/json; charset=UTF-8")
            .applyUserAgent()
            .body(JSONObject().apply {
                put("header", JSONObject().apply{
                    put("client_key", "browser-chrome-${getRandomBrowserVersion()}-${getRandomOperatingSystem()}-${UUID.randomUUID()}-${Date().time}")
                    put("fn", "auto_translation")
                    put("session", "")
                    put("user", "")
                })
                put("source", JSONObject().apply{
                    put("lang", if (targetLanguages.contains(from)) from else "en")
                    put("text_list", source)
                })
                put("target", JSONObject().apply{
                    put("lang", to)
                })
                put("model_category", "normal")
                put("text_domain", "")
                put("type", "plain")
            }.toString())
            .execute()

        if (response.status != 200) {
            error("HTTP ${response.status} : ${response.body()}")
        }

        val target: JSONArray = JSONObject(response.body()).getJSONArray("auto_translation")
        val result = StringBuilder()
        for (i in 0 until target.length()) {
            result.append(target.getString(i))
            if (i != target.length() - 1) {
                result.append("\n")
            }
        }

        return result.toString().trimEnd()

    }

}
