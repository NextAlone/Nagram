package tw.nekomimi.nekogram.transtale.source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.transtale.Translator
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.*

object YouDaoTranslator : Translator {

    private val targetLanguages = Arrays.asList("zh-CHS", "en", "es", "fr", "ja", "ru", "ko", "pt", "vi", "de", "id", "ar")

    override suspend fun doTranslate(from: String, to: String, query: String): String {
        if (to !in targetLanguages) {
            throw UnsupportedOperationException(LocaleController.getString(R.string.TranslateApiUnsupported))
        }

        return withContext(Dispatchers.IO) {
            val param = "q=" + URLEncoder.encode(query, "UTF-8") +
                    "&from=Auto" +
                    "&to=en" + to
            val response = request(param)
            val jsonObject = JSONObject(response)
            if (!jsonObject.has("translation") && jsonObject.has("errorCode")) {
                throw IOException(response)
            }
            val array = jsonObject.getJSONArray("translation")
            array.getString(0)
        }
    }

    @Throws(IOException::class)
    private fun request(param: String): String {
        val httpConnectionStream: InputStream
        val downloadUrl = URL("https://aidemo.youdao.com/trans")
        val httpConnection = downloadUrl.openConnection() as HttpURLConnection
        httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1")
        httpConnection.connectTimeout = 1000
        //httpConnection.setReadTimeout(2000);
        httpConnection.requestMethod = "POST"
        httpConnection.doOutput = true
        val dataOutputStream = DataOutputStream(httpConnection.outputStream)
        val t = param.toByteArray(Charset.defaultCharset())
        dataOutputStream.write(t)
        dataOutputStream.flush()
        dataOutputStream.close()
        httpConnection.connect()
        httpConnectionStream = if (httpConnection.responseCode != HttpURLConnection.HTTP_OK) {
            httpConnection.errorStream
        } else {
            httpConnection.inputStream
        }
        val outbuf = ByteArrayOutputStream()
        val data = ByteArray(1024 * 32)
        while (true) {
            val read = httpConnectionStream.read(data)
            if (read > 0) {
                outbuf.write(data, 0, read)
            } else if (read == -1) {
                break
            } else {
                break
            }
        }
        val result = String(outbuf.toByteArray())
        httpConnectionStream.close()
        outbuf.close()
        return result
    }
}