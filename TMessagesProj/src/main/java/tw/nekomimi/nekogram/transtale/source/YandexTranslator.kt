package tw.nekomimi.nekogram.transtale.source

import cn.hutool.core.lang.UUID
import cn.hutool.http.HttpUtil
import org.json.JSONObject
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.transtale.applyProxy
import tw.nekomimi.nekogram.utils.applyUserAgent

object YandexTranslator : Translator {

    val uuid = UUID.fastUUID().toString(true)

    override suspend fun doTranslate(from: String, to: String, query: String): String {

        val uuid2 = UUID.fastUUID().toString(true)

        val response = HttpUtil.createPost("https://translate.yandex.net/api/v1/tr.json/translate?srv=android&uuid=$uuid&id=$uuid2-9-0")
                .applyUserAgent()
                .applyProxy()
                .form("text", query)
                .form("lang", if (from == "auto") to else "$from-$to")
                .execute()

        if (response.status != 200) {

            error("HTTP ${response.status} : ${response.body()}")

        }

        val respObj = JSONObject(response.body())

        if (respObj.optInt("code", -1) != 200) error(respObj.toString(4))

        return respObj.getJSONArray("text").getString(0)

    }

}