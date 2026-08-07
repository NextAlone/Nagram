package xyz.nextalone.nagram.helper

import org.json.JSONObject
import org.telegram.messenger.FileLoader
import org.telegram.messenger.Utilities
import java.io.File
import java.net.URLDecoder
import java.util.LinkedHashMap

object LyricsHelper {

    const val OPLUS_LYRIC_INFO_KEY = "lyricInfo"

    private const val MAX_CACHE_SIZE = 16

    private val lyricsCache = object : LinkedHashMap<String, String>(8, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>?) = size > MAX_CACHE_SIZE
    }

    @Synchronized
    private fun getCachedLyrics(path: String): String? = lyricsCache[path]

    @Synchronized
    private fun cacheLyrics(path: String, lyrics: String) {
        lyricsCache[path] = lyrics
    }

    @JvmStatic
    private fun getTempFilePath(athumbUrl: String): File {
        val songName = extractSongName(athumbUrl)
        return File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE), Utilities.MD5(songName) + ".lrc")
    }

    @JvmStatic
    private fun extractSongName(athumbUrl: String): String {
        val queryStart = athumbUrl.indexOf('?')
        if (queryStart < 0) return athumbUrl
        val query = athumbUrl.substring(queryStart + 1)
        for (pair in query.split('&')) {
            val idx = pair.indexOf('=')
            if (idx < 0) continue
            val key = pair.substring(0, idx)
            if (key == "term" || key == "keyword") {
                return URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
            }
        }
        return athumbUrl
    }

    @JvmStatic
    fun saveLyrics(athumbUrl: String?, jsonObject: JSONObject) {
        if (athumbUrl.isNullOrEmpty()) return
        try {
            val lyrics = jsonObject.getString("lyrics")
            if (lyrics.isEmpty()) return
            val file = getTempFilePath(athumbUrl)
            file.writeText(lyrics, Charsets.UTF_8)
            cacheLyrics(file.path, lyrics)
        } catch (_: Exception) {
        }
    }

    @JvmStatic
    fun getLyrics(athumbUrl: String?): String {
        if (athumbUrl.isNullOrEmpty()) return ""
        return try {
            val file = getTempFilePath(athumbUrl)
            getCachedLyrics(file.path) ?: file.readText(Charsets.UTF_8).also { cacheLyrics(file.path, it) }
        } catch (_: Exception) {
            ""
        }
    }

    @JvmStatic
    fun getLyricsInfo(contentTitle: String, contentText: String, lyrics: String): String {
        return JSONObject()
            .put("songName", contentTitle)
            .put("artist", contentText)
            .put("songId", contentTitle + contentText)
            .put("lyric", lyrics)
            .toString()
    }
}
