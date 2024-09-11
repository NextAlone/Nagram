package tw.nekomimi.nekogram.transtale.source

import android.os.SystemClock
import cn.hutool.http.HttpUtil
import org.json.JSONArray
import org.json.JSONObject
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.utils.applyUserAgent

object LingoTranslator : Translator {

    override suspend fun doTranslate(from: String, to: String, query: String): String {

        if (to !in listOf("zh", "en", "es", "fr", "ja", "ru")) {

            error(LocaleController.getString(R.string.TranslateApiUnsupported))

        }

        val source = JSONArray()
        for (s in query.split("\n")) {
            source.put(s)
        }

        val response = HttpUtil.createPost("https://api.interpreter.caiyunai.com/v1/translator")
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("X-Authorization", "token 9sdftiq37bnv410eon2l") // 白嫖
                .applyUserAgent()
                .body(JSONObject().apply {
                    put("source", source)
                    put("trans_type", "${from}2$to")
                    put("request_id", SystemClock.elapsedRealtime().toString())
                    put("detect", true)
                }.toString())
                .execute()

        if (response.status != 200) {

            error("HTTP ${response.status} : ${response.body()}")

        }

        val target: JSONArray = JSONObject(response.body()).getJSONArray("target")
        val result = StringBuilder()
        for (i in 0 until target.length()) {
            result.append(target.getString(i))
            if (i != target.length() - 1) {
                result.append("\n")
            }
        }

        return result.toString()

    }

}