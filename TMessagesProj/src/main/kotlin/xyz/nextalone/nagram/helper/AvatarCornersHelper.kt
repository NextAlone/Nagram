package xyz.nextalone.nagram.helper

import android.graphics.drawable.Drawable
import org.telegram.messenger.ChatObject
import org.telegram.messenger.ImageLocation
import org.telegram.messenger.MessagesController
import org.telegram.tgnet.TLRPC
import org.telegram.ui.Components.AvatarDrawable
import org.telegram.ui.Components.VectorAvatarThumbDrawable
import xyz.nextalone.nagram.NaConfig

/**
 * Avatar corners are a percentage of the circular avatar radius: 100 keeps the circle, 0 is a square.
 * Forums, monoforums and communities keep their own shapes, like the desktop client does by default.
 */
object AvatarCornersHelper {
    const val DEFAULT_PERCENT = 100

    @JvmStatic
    fun percent(): Int = NaConfig.avatarCorners.Int().coerceIn(0, DEFAULT_PERCENT)

    @JvmStatic
    fun isCustom(): Boolean = percent() != DEFAULT_PERCENT

    /** Scales a radius that the caller chose for a circular avatar. */
    @JvmStatic
    fun scale(radius: Int): Int {
        val percent = percent()
        return if (percent == DEFAULT_PERCENT) radius else Math.round(radius * percent / 100f)
    }

    @JvmStatic
    fun scale(radius: Float): Float {
        val percent = percent()
        return if (percent == DEFAULT_PERCENT) radius else radius * percent / 100f
    }

    /** Corner radius for an avatar of [size] that would otherwise be drawn as a circle. */
    @JvmStatic
    fun radius(size: Float): Float = scale(size / 2f)

    /**
     * Whether an image set on an ImageReceiver is a peer avatar that follows the setting.
     * Peer photos carry a photoPeer; avatars without a photo are drawn by an AvatarDrawable,
     * and premium emoji avatars by a VectorAvatarThumbDrawable.
     */
    @JvmStatic
    fun isCircleAvatar(
        currentAccount: Int,
        parentObject: Any?,
        thumb: Drawable?,
        mediaLocation: ImageLocation?,
        imageLocation: ImageLocation?,
        thumbLocation: ImageLocation?
    ): Boolean {
        val peer = mediaLocation?.photoPeer ?: imageLocation?.photoPeer ?: thumbLocation?.photoPeer
        if (peer == null && thumb !is AvatarDrawable && thumb !is VectorAvatarThumbDrawable) {
            return false
        }
        val chat = parentObject as? TLRPC.Chat
            ?: peer?.channel_id?.takeIf { it != 0L }?.let {
                MessagesController.getInstance(currentAccount).getChat(it)
            }
        return !keepsOwnShape(chat)
    }

    private fun keepsOwnShape(chat: TLRPC.Chat?): Boolean =
        ChatObject.isForum(chat) || ChatObject.isMonoForum(chat) || ChatObject.isCommunity(chat)
}
