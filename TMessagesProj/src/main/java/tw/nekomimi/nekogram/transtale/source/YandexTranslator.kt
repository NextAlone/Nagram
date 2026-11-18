package tw.nekomimi.nekogram.transtale.source

import cn.hutool.core.lang.UUID
import org.json.JSONObject
import tw.nekomimi.nekogram.transtale.Translator
import xyz.nextalone.nagram.network.NetworkRequestBuilder

object YandexTranslator : Translator {

    val uuid = UUID.fastUUID().toString(true)

    override suspend fun doTranslate(from: String, to: String, query: String): String {

        val uuid2 = UUID.fastUUID().toString(true)

        val url = "https://translate.yandex.net/api/v1/tr.json/translate?srv=android&uuid=$uuid&id=$uuid2-9-0"

        val response = NetworkRequestBuilder.post(url) {
            header("User-Agent", "Mozilla/5.0 (Linux; Android 10; SM-G9600) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.0 Mobile Safari/537.36")
            parameter("text", query)
            parameter("lang", if (from == "auto") to else "$from-$to")
        }.execute()

        if (response.statusCode != 200) {

            error("HTTP ${response.statusCode} : ${response.body}")

        }

        val respObj = JSONObject(response.body)

        if (respObj.optInt("code", -1) != 200) error(respObj.toString(4))

        return respObj.getJSONArray("text").getString(0)

    }

}
