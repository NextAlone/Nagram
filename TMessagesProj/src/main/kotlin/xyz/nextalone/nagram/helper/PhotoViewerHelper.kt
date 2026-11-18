package xyz.nextalone.nagram.helper

import org.telegram.messenger.MessageObject
import tw.nekomimi.nekogram.NekoConfig

object PhotoViewerHelper {
    @JvmStatic
    fun gifAsVideo(msg: MessageObject?): Boolean =
        msg != null && msg.isNewGif && NekoConfig.takeGIFasVideo.Bool()
}
