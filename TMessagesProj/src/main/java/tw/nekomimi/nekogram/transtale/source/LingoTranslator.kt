package tw.nekomimi.nekogram.transtale.source

import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.utils.HttpUtil
import tw.nekomimi.nekogram.utils.applyUserAgent

object LingoTranslator : Translator {

    override fun doTranslate(from: String, to: String, query: String): String {

        if (to !in listOf("zh", "en", "es", "fr", "ja", "ru")) {

            error(LocaleController.getString("TranslateApiUnsupported", R.string.TranslateApiUnsupported))

        }

        val response = HttpUtil.okHttpClient.newCall(Request.Builder()
                .url("https://api.interpreter.caiyunai.com/v1/translator")
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("X-Authorization", "token 9sdftiq37bnv410eon2l") // 白嫖
                .applyUserAgent()
                .post(JSONObject().apply {

                    put("source", query)
                    put("trans_type", "${from}2$to")
                    put("request_id", System.currentTimeMillis().toString())
                    put("detect", true)

                }.toString().toRequestBody()).build()).execute()

        if (response.code != 200) {

            error("HTTP ${response.code} : ${response.body?.string()}")

        }

        return JSONObject(response.body!!.string()).getString("target")

    }

}