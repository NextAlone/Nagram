package tw.nekomimi.nekogram

import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import com.vanniktech.emoji.emoji.Emoji
import com.vanniktech.emoji.facebook.FacebookEmojiProvider
import com.vanniktech.emoji.google.GoogleEmojiProvider
import com.vanniktech.emoji.ios.IosEmojiProvider
import com.vanniktech.emoji.twitter.TwitterEmojiProvider
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.BuildConfig
import java.util.*


object EmojiProvider {

    val type = BuildConfig.FLAVOR

    @JvmField
    val noEmoji = type.contains("NoEmoji")

    // default use blob
    @JvmField
    val isFont = !type.contains("Emoji")

    @JvmStatic
    val font by lazy {
        if (!isFont) throw IllegalStateException()
        val resName = when {
            !type.contains("emoji") -> "blob_compat.ttf"
            else -> throw IllegalStateException()
        }
        Typeface.createFromAsset(ApplicationLoader.applicationContext.assets, "fonts/$resName");
    }

    private val isApple = type.contains("Apple")
    private val isNoto = type.contains("Noto")
    private val isTwitter = type.contains("Twitter")
    private val isFacebook = type.contains("Facebook")

    private val emojiMap = LinkedHashMap<String, Emoji>()

    init {

        if (!isFont && !noEmoji) {

            val provider = when {
                isApple -> IosEmojiProvider()
                isNoto -> GoogleEmojiProvider()
                isTwitter -> TwitterEmojiProvider()
                isFacebook -> FacebookEmojiProvider()
                else -> throw IllegalStateException()
            }

            val categoriesSize = provider.categories.size
            //noinspection ForLoopReplaceableByForEach
            for (i in 0 until categoriesSize) {
                val emojis = provider.categories[i].emojis
                val emojisSize = emojis.size
                for (j in 0 until emojisSize) {
                    val emoji = emojis[j]
                    val unicode = emoji.unicode
                    val variants = emoji.variants
                    emojiMap[unicode] = emoji
                    for (k in variants.indices) {
                        val variant = variants[k]
                        val variantUnicode = variant.unicode
                        emojiMap[variantUnicode] = variant
                    }
                }

            }

        }

    }

    @JvmStatic
    fun contains(emoji: String) = emojiMap.contains(emoji)

    @JvmStatic
    fun readDrawable(emoji: String) = emojiMap[emoji]!!.getDrawable(ApplicationLoader.applicationContext) as BitmapDrawable

}