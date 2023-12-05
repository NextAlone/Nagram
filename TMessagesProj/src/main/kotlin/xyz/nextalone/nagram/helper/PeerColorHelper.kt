package xyz.nextalone.nagram.helper

import com.google.gson.Gson
import org.telegram.tgnet.TLRPC
import xyz.nextalone.nagram.NaConfig


data class LocalQuoteColorData (
        var colorId: Int?,
        var emojiId: Long?,
        var profileColorId: Int?,
        var profileEmojiId: Long?
)


object PeerColorHelper {
    var loaded: Boolean = false
    var data: LocalQuoteColorData? = null

    @JvmStatic
    fun getColorId(user: TLRPC.User): Int? {
        if (!NaConfig.useLocalQuoteColor.Bool()) return null
        init()
        if (user.self && data != null) {
            return data!!.colorId
        }
        return null
    }

    @JvmStatic
    fun getEmojiId(user: TLRPC.User?): Long? {
        if (!NaConfig.useLocalQuoteColor.Bool()) return null
        init()
        if (user != null && user.self && data != null) {
            return data!!.emojiId
        }
        return null
    }

    @JvmStatic
    fun getProfileColorId(user: TLRPC.User): Int? {
        if (!NaConfig.useLocalQuoteColor.Bool()) return null
        init()
        if (user.self && data != null) {
            return data!!.profileColorId
        }
        return null
    }

    @JvmStatic
    fun getProfileEmojiId(user: TLRPC.User?): Long? {
        if (!NaConfig.useLocalQuoteColor.Bool()) return null
        init()
        if (user != null && user.self && data != null) {
            return data!!.profileEmojiId
        }
        return null
    }

    @JvmStatic
    fun init(force: Boolean = false) {
        if (loaded && !force) return
        loaded = true
        try {
            val gson = Gson()
            data = gson.fromJson(NaConfig.useLocalQuoteColorData.String(), LocalQuoteColorData::class.java)
        } catch (_: Exception) {}
    }

    @JvmStatic
    fun apply(colorId: Int, emojiId: Long, profileColorId: Int, profileEmojiId: Long) {
        if (!NaConfig.useLocalQuoteColor.Bool()) return
        var localData = data
        if (localData == null) {
            localData = LocalQuoteColorData(colorId, emojiId, profileColorId, profileEmojiId)
        } else {
            localData.colorId = colorId
            localData.emojiId = emojiId
            localData.profileColorId = profileColorId
            localData.profileEmojiId = profileEmojiId
        }
        NaConfig.useLocalQuoteColorData.setConfigString(Gson().toJson(localData))
        init(true)
    }
}
