package xyz.nextalone.nagram.helper

import android.text.TextUtils
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.MediaDataController
import org.telegram.messenger.MessageObject
import org.telegram.messenger.NotificationCenter
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.TLObject
import org.telegram.tgnet.TLRPC.TL_error
import org.telegram.tgnet.TLRPC.TL_inputStickerSetItem
import org.telegram.tgnet.TLRPC.TL_inputUserSelf
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet
import org.telegram.tgnet.TLRPC.TL_stickers_createStickerSet
import org.telegram.ui.Components.BulletinFactory

object StickerSetHelper {
    fun copyStickerSet(shortName: CharSequence, packName: CharSequence, oldStickerSet: TL_messages_stickerSet, currentAccount: Int) {
        val stickers = processOldStickerSet(oldStickerSet, currentAccount)
        createStickerSet(shortName, packName, stickers, currentAccount)
    }

    private fun processOldStickerSet(oldStickerSet: TL_messages_stickerSet, currentAccount: Int): ArrayList<TL_inputStickerSetItem> {
        val stickers = ArrayList<TL_inputStickerSetItem>()
        if (oldStickerSet.documents != null) {
            oldStickerSet.documents.forEach {
                if (it != null) {
                    var emoji = MessageObject.findAnimatedEmojiEmoticon(it, "\uD83D\uDE00", currentAccount)
                    if (TextUtils.isEmpty(emoji)) {
                        emoji = "\uD83D\uDE00"
                    }
                    val sticker = MediaDataController.getInputStickerSetItem(it, emoji)
                    stickers.add(sticker)
                }
            }
        }
        return stickers
    }

    private fun createStickerSet(shortName: CharSequence, packName: CharSequence, stickers: ArrayList<TL_inputStickerSetItem>, currentAccount: Int) {
        val req = TL_stickers_createStickerSet()
        req.user_id = TL_inputUserSelf()
        req.title = packName.toString()
        req.short_name = shortName.toString()
        req.stickers.addAll(stickers)
        ConnectionsManager.getInstance(currentAccount).sendRequest(req) { response: TLObject?, error: TL_error? ->
            AndroidUtilities.runOnUIThread {
                if (response is TL_messages_stickerSet) {
                    MediaDataController.getInstance(currentAccount).toggleStickerSet(null, response, 2, null, false, false)
                    AndroidUtilities.runOnUIThread({ NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationNameOnUIThread(NotificationCenter.customStickerCreated, false, response) }, 250)
                }
                if (error != null) {
                    BulletinFactory.showError(error)
                }
            }
        }
    }
}
