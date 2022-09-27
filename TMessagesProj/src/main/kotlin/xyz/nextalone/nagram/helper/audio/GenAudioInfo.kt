package xyz.nextalone.nagram.helper.audio

import android.graphics.Bitmap
import android.graphics.BitmapFactory

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag

import org.telegram.messenger.audioinfo.AudioInfo

import java.io.ByteArrayInputStream
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

import kotlin.math.max


class GenAudioInfo @JvmOverloads constructor(file: File?, format: String? = "FLAC", debugLevel: Level? = Level.FINEST) :
    AudioInfo() {
    init {
        brand = format
        version = "0"
        val f = AudioFileIO.read(file)
        val info = f.tag as Tag
        val header = f.audioHeader
        album = info.getFirst(FieldKey.ALBUM)
        albumArtist = info.getFirst(FieldKey.ALBUM_ARTIST)
        artist = info.getFirst(FieldKey.ARTIST)
        comment = info.getFirst(FieldKey.COMMENT)
        val image = info.artworkList[0]
        cover = if (image != null) {
            BitmapFactory.decodeStream(ByteArrayInputStream(image.binaryData))
        } else {
            null
        }
        if (cover != null) {
            val scale = max(cover.width, cover.height) / 120.0f
            smallCover = if (scale > 0) {
                Bitmap.createScaledBitmap(
                    cover,
                    (cover.width / scale).toInt(),
                    (cover.height / scale).toInt(),
                    true
                )
            } else {
                cover
            }
            if (smallCover == null) {
                smallCover = cover
            }
        }
        compilation = info.getFirst(FieldKey.IS_COMPILATION).toBoolean()
        composer = info.getFirst(FieldKey.COMPOSER)
        copyright = ""
        try {
            disc = info.getFirst(FieldKey.DISC_NO).toShort()
            discs = info.getFirst(FieldKey.DISC_TOTAL).toShort()
        } catch (ignored: NumberFormatException) {}
        duration = header.trackLength.toLong()
        genre = info.getFirst(FieldKey.GENRE)
        grouping = info.getFirst(FieldKey.GROUPING)
        lyrics = info.getFirst(FieldKey.LYRICS)
        title = info.getFirst(FieldKey.TITLE)
        try {
            track = info.getFirst(FieldKey.TRACK).toShort()
            tracks = info.getFirst(FieldKey.TRACK_TOTAL).toShort()
        } catch (ignored: NumberFormatException) {}
        try {
            year = info.getFirst(FieldKey.YEAR).toShort()
        } catch (ignored: NumberFormatException) {}

        if (duration <= 0 || duration >= 3600000L) {
            if (debugLevel?.let { LOGGER.isLoggable(it) } == true) {
                LOGGER.log(debugLevel, "Maybe false $format duration.")
            }
        }
    }

    companion object {
        val LOGGER: Logger = Logger.getLogger(GenAudioInfo::class.java.name)
    }
}
