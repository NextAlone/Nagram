package xyz.nextalone.nagram.helper

import org.json.JSONObject
import org.telegram.messenger.FileLoader
import org.telegram.messenger.Utilities
import java.io.File
import java.net.URLDecoder
import java.util.LinkedHashMap
import java.util.regex.Matcher
import java.util.regex.Pattern

object LyricsHelper {

    const val OPLUS_LYRIC_INFO_KEY = "lyricInfo"

    private const val MAX_CACHE_SIZE = 16

    private val lyricsCache = object : LinkedHashMap<String, String>(8, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>?) = size > MAX_CACHE_SIZE
    }

    private val TIME_TAG = Pattern.compile("\\[(\\d{1,3}):(\\d{1,2})(?:[.:](\\d{1,3}))?\\]")
    private val META_TAG = Pattern.compile("\\[[a-zA-Z]+:[^\\]]*\\]")
    private val ANY_TAG = Pattern.compile("\\[[^\\]]*\\]")

    class LyricsLine(@JvmField val timeMs: Long, @JvmField val text: String)

    class LyricsData(
        @JvmField val rawText: String,
        @JvmField val isSynced: Boolean,
        @JvmField val lines: List<LyricsLine>,
    )

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

    /**
     * Parses timed LRC lyrics or plain-text lyrics into a list of [LyricsLine].
     * Returns null for empty input or when no lyrics line could be extracted.
     * Migrated from LyricsDisplayHelper (Java).
     */
    @JvmStatic
    fun fromRawText(raw: String?): LyricsData? {
        if (raw.isNullOrEmpty()) {
            return null
        }
        val lines = ArrayList<LyricsLine>()
        var offsetMs = 0L
        var anySynced = false
        for (row in raw.split("\r?\n".toRegex())) {
            val trimmed = row.trim()
            if (trimmed.isEmpty()) {
                continue
            }
            val timeMatcher = TIME_TAG.matcher(trimmed)
            val times = ArrayList<Long>()
            while (timeMatcher.find()) {
                times.add(parseTime(timeMatcher))
                anySynced = true
            }
            if (times.isEmpty()) {
                val metaMatcher = META_TAG.matcher(trimmed)
                var isMeta = false
                while (metaMatcher.find()) {
                    isMeta = true
                    val tag = metaMatcher.group().trim()
                    if (tag.regionMatches(1, "offset:", 0, 7, ignoreCase = true)) {
                        try {
                            offsetMs = tag.substring(8, tag.length - 1).trim().toLong()
                        } catch (_: Exception) {
                            // invalid offset, ignore
                        }
                    }
                }
                if (!isMeta) {
                    lines.add(LyricsLine(-1, trimmed))
                }
            } else {
                val text = ANY_TAG.matcher(trimmed).replaceAll("").trim()
                for (t in times) {
                    lines.add(LyricsLine(Math.max(0, t + offsetMs), text))
                }
            }
        }
        if (lines.isEmpty()) {
            return null
        }
        val synced = anySynced
        if (synced) {
            // stable sort keeps the original order of same-timestamp lines (e.g. bilingual groups)
            lines.sortBy { it.timeMs }
        }
        return LyricsData(raw, synced, lines)
    }

    private fun parseTime(matcher: Matcher): Long {
        val minutes = matcher.group(1)?.toLong() ?: 0L
        val seconds = matcher.group(2)?.toLong() ?: 0L
        var fractionMs = 0L
        val fraction = matcher.group(3)
        if (!fraction.isNullOrEmpty()) {
            fractionMs = when (fraction.length) {
                1 -> fraction.toLong() * 100
                2 -> fraction.toLong() * 10
                else -> fraction.toLong()
            }
        }
        return (minutes * 60 + seconds) * 1000 + fractionMs
    }
}
