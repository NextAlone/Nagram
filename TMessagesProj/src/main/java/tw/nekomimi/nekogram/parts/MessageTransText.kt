package tw.nekomimi.nekogram.parts

import org.telegram.tgnet.TLRPC
import tw.nekomimi.nekogram.transtale.TranslateDb
import tw.nekomimi.nekogram.transtale.html.HTMLKeeper
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

    /**
     * Converts [source] into the string that is sent to the translation provider.
     *
     * When formatting preservation is enabled the text is converted to HTML so that the provider
     * keeps the bold/italic/code/... markup, otherwise the raw text is used.
     */
    fun queryTextOf(source: TLRPC.TL_textWithEntities): String {
        val text = source.text
        if (!NaConfig.keepFormatting.Bool() || source.entities.isNullOrEmpty()) {
            return text
        }
        return HTMLKeeper.entitiesToHtml(text, source.entities, false)
    }

    /**
     * Merges [translated] back into [source] and returns the new rich text.
     *
     * Depending on `hideOriginAfterTranslation` the original text is either dropped or kept as
     * a prefix; entities of the translated part are shifted accordingly.
     */
    fun merge(source: TLRPC.TL_textWithEntities, translated: String, separator: String): TLRPC.TL_textWithEntities {
        val parsed = parseEntities(translated, source)
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

    /** Same as [merge] but only consults the cache, returning null on a cache miss. */
    fun mergeCached(source: TLRPC.TL_textWithEntities, db: TranslateDb, separator: String): TLRPC.TL_textWithEntities? {
        val cached = db.query(queryTextOf(source))?.takeIf { it.isNotBlank() } ?: return null
        return merge(source, cached, separator)
    }

    private fun parseEntities(translated: String, source: TLRPC.TL_textWithEntities): TLRPC.TL_textWithEntities {
        if (!NaConfig.keepFormatting.Bool() || source.entities.isNullOrEmpty()) {
            return TLRPC.TL_textWithEntities().also {
                it.text = translated
                it.entities = ArrayList()
            }
        }
        return HTMLKeeper.htmlToEntities(translated, source.entities, false)
    }
}