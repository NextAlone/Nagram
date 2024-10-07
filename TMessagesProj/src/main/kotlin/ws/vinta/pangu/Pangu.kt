/*
 * Created by Vinta Chen on 2014/11/05.
 * Modified by qwq233 on 2023/07/17
 */
package ws.vinta.pangu

import java.io.IOException
import java.util.regex.Pattern

/**
 * Paranoid text spacing for good readability, to automatically insert whitespace between
 * CJK (Chinese, Japanese, Korean), half-width English, digit and symbol characters.
 *
 *
 * These whitespaces between English and Chinese characters are called "Pangu Spacing" by sinologist, since it
 * separate the confusion between full-width and half-width characters. Studies showed that who dislike to
 * add whitespace between English and Chinese characters also have relationship problem. Almost 70 percent of them
 * will get married to the one they don't love, the rest only can left the heritage to their cat. Indeed,
 * love and writing need some space in good time.
 *
 * @author Vinta Chen
 * @author qwq233
 * @since 1.0.0
 */
class Pangu {
    companion object {
        /*
         * Some capturing group patterns for convenience.
         *
         * CJK: Chinese, Japanese, Korean
         * ANS: Alphabet, Number, Symbol
         */
        private val CJK_ANS = Pattern.compile(
            "([\\p{InHiragana}\\p{InKatakana}\\p{InBopomofo}\\p{InCJKCompatibilityIdeographs}\\p{InCJKUnifiedIdeographs}])" +
                    "([a-z0-9`~@\\$%\\^&\\*\\-_\\+=\\|\\\\/])",
            Pattern.CASE_INSENSITIVE
        )
        private val ANS_CJK = Pattern.compile(
            "([a-z0-9`~!\\$%\\^&\\*\\-_\\+=\\|\\\\;:,\\./\\?])" +
                    "([\\p{InHiragana}\\p{InKatakana}\\p{InBopomofo}\\p{InCJKCompatibilityIdeographs}\\p{InCJKUnifiedIdeographs}])",
            Pattern.CASE_INSENSITIVE
        )
        private val CJK_QUOTE = Pattern.compile(
            "([\\p{InHiragana}\\p{InKatakana}\\p{InBopomofo}\\p{InCJKCompatibilityIdeographs}\\p{InCJKUnifiedIdeographs}])" +
                    "([\"'])"
        )
        private val QUOTE_CJK = Pattern.compile(
            "([\"'])" +
                    "([\\p{InHiragana}\\p{InKatakana}\\p{InBopomofo}\\p{InCJKCompatibilityIdeographs}\\p{InCJKUnifiedIdeographs}])"
        )
        private val FIX_QUOTE = Pattern.compile("([\"'])(\\s*)(.+?)(\\s*)([\"'])")
        private val CJK_BRACKET_CJK = Pattern.compile(
            "([\\p{InHiragana}\\p{InKatakana}\\p{InBopomofo}\\p{InCJKCompatibilityIdeographs}\\p{InCJKUnifiedIdeographs}])" +
                    "([\\({\\[]+(.*?)[\\)}\\]]+)" +
                    "([\\p{InHiragana}\\p{InKatakana}\\p{InBopomofo}\\p{InCJKCompatibilityIdeographs}\\p{InCJKUnifiedIdeographs}])"
        )
        private val CJK_BRACKET = Pattern.compile(
            "([\\p{InHiragana}\\p{InKatakana}\\p{InBopomofo}\\p{InCJKCompatibilityIdeographs}\\p{InCJKUnifiedIdeographs}])" +
                    "([\\(\\){}\\[\\]<>])"
        )
        private val BRACKET_CJK = Pattern.compile(
            "([\\(\\){}\\[\\]<>])" +
                    "([\\p{InHiragana}\\p{InKatakana}\\p{InBopomofo}\\p{InCJKCompatibilityIdeographs}\\p{InCJKUnifiedIdeographs}])"
        )
        private val FIX_BRACKET = Pattern.compile("([(\\({\\[)]+)(\\s*)(.+?)(\\s*)([\\)}\\]]+)")
        private val CJK_HASH = Pattern.compile(
            "([\\p{InHiragana}\\p{InKatakana}\\p{InBopomofo}\\p{InCJKCompatibilityIdeographs}\\p{InCJKUnifiedIdeographs}])" +
                    "(#(\\S+))"
        )
        private val HASH_CJK = Pattern.compile(
            "((\\S+)#)" +
                    "([\\p{InHiragana}\\p{InKatakana}\\p{InBopomofo}\\p{InCJKCompatibilityIdeographs}\\p{InCJKUnifiedIdeographs}])"
        )
    }

    private fun processUrl(text: String) = Pattern.compile("://").matcher(text).let { matcher ->
        if (!matcher.find()) {
            throw NullPointerException("No URL found in text")
        }
        var prefixOffset = 0
        val prefix: String = StringBuilder().apply {
            arrayListOf<Char>().apply {
                for (i in matcher.start() - 1 downTo 0) {
                    val c = text[i]
                    if (c.isWhitespace()) {
                        prefixOffset = i + 1
                        break
                    }
                    add(c)
                }
            }.reversed().forEach { append(it) }
        }.toString()

        var suffixOffset = text.lastIndex
        val suffix: String = StringBuilder().apply {
            arrayListOf<Char>().apply {
                for (i in matcher.end() until text.length) {
                    val c = text[i]
                    if (c.isWhitespace()) {
                        suffixOffset = i
                        break
                    }
                    add(c)
                }
            }.forEach { append(it) }
        }.toString()

        val url = "$prefix://$suffix"
        val first: String? = if (0 != prefixOffset) text.substring(0, prefixOffset) else null
        val last: String? = if (text.lastIndex != suffixOffset) text.substring(suffixOffset) else null

        Triple(first, url, last)
    }


    /**
     * Performs a paranoid text spacing on `text`.
     *
     * @param text  the string you want to process, must not be `null`.
     * @return a comfortable and readable version of `text` for paranoiac.
     */
    fun spacingText(text: String): String {
        var text: String = text

        // URL
        if (text.contains("://")) {
            val (first, url, last) = processUrl(text)
            var result = String()
            if (first != null) {
                result += spacingText(first)
            }
            result += url
            if (last != null) {
                result += spacingText(last)
            }
            return result
        }

        // CJK and quotes
        val cqMatcher = CJK_QUOTE.matcher(text)
        text = cqMatcher.replaceAll("$1 $2")
        val qcMatcher = QUOTE_CJK.matcher(text)
        text = qcMatcher.replaceAll("$1 $2")
        val fixQuoteMatcher = FIX_QUOTE.matcher(text)
        text = fixQuoteMatcher.replaceAll("$1$3$5")

        // CJK and brackets
        val oldText = text
        val cbcMatcher = CJK_BRACKET_CJK.matcher(text)
        val newText = cbcMatcher.replaceAll("$1 $2 $4")
        text = newText
        if (oldText == newText) {
            val cbMatcher = CJK_BRACKET.matcher(text)
            text = cbMatcher.replaceAll("$1 $2")
            val bcMatcher = BRACKET_CJK.matcher(text)
            text = bcMatcher.replaceAll("$1 $2")
        }
        val fixBracketMatcher = FIX_BRACKET.matcher(text)
        text = fixBracketMatcher.replaceAll("$1$3$5")

        // CJK and hash
        val chMatcher = CJK_HASH.matcher(text)
        text = chMatcher.replaceAll("$1 $2")
        val hcMatcher = HASH_CJK.matcher(text)
        text = hcMatcher.replaceAll("$1 $3")

        // CJK and ANS
        val caMatcher = CJK_ANS.matcher(text)
        text = caMatcher.replaceAll("$1 $2")
        val acMatcher = ANS_CJK.matcher(text)
        text = acMatcher.replaceAll("$1 $2")

        return text
    }

    private fun log(text: String) {
        println("panguTrace: $text")
    }

}

internal object Test {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val pangu = Pangu()
        println(pangu.spacingText("當你凝視著 https://telegra.ph/八尋ぽち-ひみチュッ-中国翻訳-無修正-DL版-06-17-3 ，bug也凝視著 https://telegra.ph/ASDF-DL版-06-17-3"))
    }
}
