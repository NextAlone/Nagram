package tw.nekomimi.nekogram.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonWriter
import java.io.StringWriter

object GsonUtil {

    private val gson = Gson()

    @JvmStatic
    fun formatObject(obj: Any?): String {
        if (obj == null) return "null"
        val json = gson.toJsonTree(obj)
        val stringWriter = StringWriter()
        val jsonWriter = JsonWriter(stringWriter)
        jsonWriter.setIndent("    ")
        jsonWriter.isLenient = true
        Streams.write(json, jsonWriter)
        return stringWriter.toString()
    }

    @JvmStatic
    fun toJsonObject(json: String): JsonObject {
        return gson.fromJson(json, JsonObject::class.java)
    }

}