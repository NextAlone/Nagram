package tw.nekomimi.nekogram.parts

import org.telegram.tgnet.TLRPC
import tw.nekomimi.nekogram.transtale.TranslateDb
import tw.nekomimi.nekogram.transtale.Translator
import tw.nekomimi.nekogram.transtale.html.TranslationEntities
import xyz.nextalone.nagram.NaConfig

/**
 * Core of the NekoX translation pipeline.
 *
 * A translation is always produced by merging the original [TLRPC.TL_textWithEntities] with the
 * translated plain text, so that the result can be stored back into the official upstream field
 * `TLRPC.Message.translatedText` without losing any entity.
 */
internal object MessageTransText {

    private const val MESSAGE_SEPARATOR = "\n\n--------\n\n"
    private const val POLL_SEPARATOR = " | "

    /** Separator placed between the original text and its translation. */
    fun separatorFor(isPoll: Boolean) = if (isPoll) POLL_SEPARATOR else MESSAGE_SEPARATOR

    /** Rich text format of the translator currently selected by the user. */
    private fun currentFormat() = Translator.currentTranslator().format

    /**
     * Converts [source] into the string that is sent to the translation provider.
     *
     * When formatting preservation is enabled the text is converted to HTML so that the provider
     * keeps the bold/italic/code/... markup, otherwise the raw text is used.
     */
    fun queryTextOf(source: TLRPC.TL_textWithEntities): String {
        if (!NaConfig.keepFormatting.Bool() || source.entities.isNullOrEmpty()) {
            return source.text
        }
        return TranslationEntities.encode(source, currentFormat())
    }

    /**
     * Merges [translated] back into [source] and returns the new rich text, decoding the entities
     * with the format of the current translator.
     */
    fun merge(source: TLRPC.TL_textWithEntities, translated: String, separator: String): TLRPC.TL_textWithEntities =
        merge(source, translated, separator, currentFormat())

    /**
     * Merges [translated] back into [source] and returns the new rich text.
     *
     * Depending on `hideOriginAfterTranslation` the original text is either dropped or kept as
     * a prefix; entities of the translated part are shifted accordingly.
     */
    fun merge(
        source: TLRPC.TL_textWithEntities,
        translated: String,
        separator: String,
        format: TranslationEntities.Format
    ): TLRPC.TL_textWithEntities {
        val parsed = parseEntities(translated, source, format)
        val result = TLRPC.TL_textWithEntities()

        if (NaConfig.hideOriginAfterTranslation.Bool()) {
            result.text = parsed.text
            result.entities = parsed.entities
            return result
        }

        val shift = source.text.length + separator.length
        result.text = source.text + separator + parsed.text
        result.entities = ArrayList<TLRPC.MessageEntity>().also { combined ->
            source.entities?.let { combined.addAll(it) }
            parsed.entities.forEach { entity ->
                entity.offset += shift
                combined.add(entity)
            }
        }
        return result
    }

    /**
     * Same as [merge] but only consults the cache, returning null on a cache miss.
     *
     * A cache key is produced by encoding [source] with a given format, and the provider (hence the
     * format) may have changed since an entry was stored, so every format is tried. The matching
     * entry is then decoded with the very format that produced its key.
     */
    fun mergeCached(source: TLRPC.TL_textWithEntities, db: TranslateDb, separator: String): TLRPC.TL_textWithEntities? {
        for (format in cacheFormats(source)) {
            val key = TranslationEntities.encode(source, format)
            val cached = db.query(key)?.takeIf { it.isNotBlank() } ?: continue
            return merge(source, cached, separator, format)
        }
        return null
    }

    /**
     * The formats to probe when reading the cache, the current one first so the most likely key is
     * queried before the others.
     */
    private fun cacheFormats(source: TLRPC.TL_textWithEntities): List<TranslationEntities.Format> {
        // Without rich entities every format collapses to the raw text, so one lookup is enough.
        if (source.entities.isNullOrEmpty()) {
            return listOf(TranslationEntities.Format.NO_HTML)
        }
        val current = currentFormat()
        return buildList {
            add(current)
            TranslationEntities.Format.values().forEach { if (it != current) add(it) }
        }
    }

    private fun parseEntities(
        translated: String,
        source: TLRPC.TL_textWithEntities,
        format: TranslationEntities.Format
    ): TLRPC.TL_textWithEntities {
        if (!NaConfig.keepFormatting.Bool() || source.entities.isNullOrEmpty()) {
            return TLRPC.TL_textWithEntities().also {
                it.text = translated
                it.entities = ArrayList()
            }
        }
        return TranslationEntities.decode(source, translated, format)
    }
}
