/*
 * Copyright (C) 2019-2022 qwq233 <qwq233@qwq2333.top>
 * https://github.com/qwq233/Nullgram
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this software.
 *  If not, see
 * <https://www.gnu.org/licenses/>
 */

package top.qwq2333.nullgram.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonWriter
import java.io.StringWriter

object JsonUtils {

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
