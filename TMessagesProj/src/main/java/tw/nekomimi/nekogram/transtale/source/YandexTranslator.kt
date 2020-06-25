package tw.nekomimi.nekogram.transtale.source

import cn.hutool.core.lang.UUID
import okhttp3.FormBody
import okhttp3.Request
import org.json.JSONObject
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.utils.HttpUtil
import tw.nekomimi.nekogram.utils.applyUserAgent

object YandexTranslator : Translator {

    val uuid = UUID.fastUUID().toString(true)

    override fun doTranslate(from: String, to: String, query: String): String {

        val uuid2 = UUID.fastUUID().toString(true)

        val request = Request.Builder()
                .url("https://translate.yandex.net/api/v1/tr.json/translate?srv=android&uuid=$uuid&id=$uuid2-9-0")
                .applyUserAgent()
                .post(FormBody.Builder()
                        .add("text", query)
                        .add("lang", if (from == "auto") to else "$from-$to")
                        .build()).build()

        val response = runCatching {
            HttpUtil.okHttpClient.newCall(request).execute()
        }.recoverCatching {
            HttpUtil.okHttpClientWithCurrProxy.newCall(request).execute()
        }.getOrThrow()

        if (response.code != 200) {

            error("HTTP ${response.code} : ${response.body?.string()}")

        }

        val respObj = JSONObject(response.body!!.string())

        if (respObj.optInt("code",-1) != 200) error(respObj.toString(4))

        return respObj.getJSONArray("text").getString(0)

    }

}