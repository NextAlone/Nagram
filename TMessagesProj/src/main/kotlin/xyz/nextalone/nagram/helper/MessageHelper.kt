package xyz.nextalone.nagram.helper

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.text.TextUtils
import androidx.core.content.FileProvider
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.BuildConfig
import org.telegram.messenger.ChatObject
import org.telegram.messenger.DialogObject
import org.telegram.messenger.Emoji
import org.telegram.messenger.FileLoader
import org.telegram.messenger.FileLog
import org.telegram.messenger.LocaleController
import org.telegram.messenger.MediaDataController
import org.telegram.messenger.MessageObject
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.TLRPC.Chat
import org.telegram.tgnet.TLRPC.TL_messageEntityBankCard
import org.telegram.tgnet.TLRPC.TL_messageEntityBotCommand
import org.telegram.tgnet.TLRPC.TL_messageEntityCashtag
import org.telegram.tgnet.TLRPC.TL_messageEntityEmail
import org.telegram.tgnet.TLRPC.TL_messageEntityHashtag
import org.telegram.tgnet.TLRPC.TL_messageEntityMention
import org.telegram.tgnet.TLRPC.TL_messageEntityPhone
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl
import org.telegram.tgnet.TLRPC.TL_messageMediaPoll
import org.telegram.ui.ChatActivity
import xyz.nextalone.nagram.NaConfig
import java.io.File
import java.io.FileOutputStream


object MessageHelper {
    fun getPathToMessage(messageObject: MessageObject): File? {
        var path = messageObject.messageOwner.attachPath
        if (!TextUtils.isEmpty(path)) {
            val file = File(path)
            if (file.exists()) {
                return file
            } else {
                path = null
            }
        }
        if (TextUtils.isEmpty(path)) {
            val file = FileLoader.getInstance(messageObject.currentAccount)
                .getPathToMessage(messageObject.messageOwner)
            if (file != null && file.exists()) {
                return file
            } else {
                path = null
            }
        }
        if (TextUtils.isEmpty(path)) {
            val file = FileLoader.getInstance(messageObject.currentAccount)
                .getPathToAttach(messageObject.document, true)
            return if (file != null && file.exists()) {
                file
            } else {
                null
            }
        }
        return null
    }


    fun addMessageToClipboard(selectedObject: MessageObject, callback: Runnable) {
        val file = getPathToMessage(selectedObject)
        if (file != null) {
            if (file.exists()) {
                addFileToClipboard(file, callback)
            }
        }
    }

    fun addMessageToClipboardAsSticker(selectedObject: MessageObject, callback: Runnable) {
        val file = getPathToMessage(selectedObject)
        try {
            if (file != null) {
                val path = file.path
                val image = BitmapFactory.decodeFile(path)
                if (image != null && !TextUtils.isEmpty(path)) {
                    val file2 = File(
                        if (path.endsWith(".jpg")) path.replace(
                            ".jpg",
                            ".webp"
                        ) else "$path.webp"
                    )
                    val stream = FileOutputStream(file2)
                    if (Build.VERSION.SDK_INT >= 30) {
                        image.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, stream)
                    } else {
                        image.compress(Bitmap.CompressFormat.WEBP, 100, stream)
                    }
                    stream.close()
                    addFileToClipboard(file2, callback)
                }
            }
        } catch (ignored: java.lang.Exception) {
        }
    }

    fun addFileToClipboard(file: File?, callback: Runnable) {
        try {
            val context = ApplicationLoader.applicationContext
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val uri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                file!!
            )
            val clip = ClipData.newUri(context.contentResolver, "label", uri)
            clipboard.setPrimaryClip(clip)
            callback.run()
        } catch (e: Exception) {
            FileLog.e(e)
        }
    }

    @JvmStatic
    fun showForwardDate(obj: MessageObject, orig: String): String {
        val date: Long = obj.messageOwner.fwd_from.date.toLong()
        val day: String = LocaleController.formatDate(date)
        val time: String = LocaleController.getInstance().formatterDay.format(date * 1000)
        return if (!NaConfig.dateOfForwardedMsg.Bool() || date == 0L) {
            orig
        } else {
            if (day == time) {
                "$orig · $day"
            } else "$orig · $day $time"
        }
    }

    fun zalgoFilter(
        text: String
    ): String {
        return zalgoFilter(text as CharSequence).toString()
    }

    fun zalgoFilter(
        text: CharSequence?
    ): CharSequence {
        return if (text == null) {
            ""
        } else if (NaConfig.zalgoFilter.Bool() && text.matches(
                ".*\\p{Mn}{4}.*".toRegex()
            )
        ) {
            text.replace(
                "(?i)([aeiouy]̈)|[̀-ͯ҉]".toRegex(),
                ""
            )
                .replace(
                    "[\\p{Mn}]".toRegex(),
                    ""
                )
        } else {
            text
        }
    }

    @JvmStatic
    fun getDCLocation(dc: Int): String {
        return when (dc) {
            1, 3 -> "Miami"
            2, 4 -> "Amsterdam"
            5 -> "Singapore"
            else -> "Unknown"
        }
    }

    @JvmStatic
    fun getDCName(dc: Int): String {
        return when (dc) {
            1 -> "Pluto"
            2 -> "Venus"
            3 -> "Aurora"
            4 -> "Vesta"
            5 -> "Flora"
            else -> "Unknown"
        }
    }

    @JvmStatic
    fun containsMarkdown(text: CharSequence?): Boolean {
        val newText = AndroidUtilities.getTrimmedString(text)
        val message = arrayOf(AndroidUtilities.getTrimmedString(newText))
        return MediaDataController.getInstance(UserConfig.selectedAccount)
            .getEntities(message, true).size > 0
    }

    @JvmStatic
    fun canSendAsDice(text: String, parentFragment: ChatActivity, dialog_id: Long): Boolean {
        var canSendGames = true
        if (DialogObject.isChatDialog(dialog_id)) {
            val chat: Chat = parentFragment.messagesController.getChat(-dialog_id)
            canSendGames = ChatObject.canSendStickers(chat)
        }
        return canSendGames && parentFragment.messagesController.diceEmojies.contains(
            text.replace(
                "\ufe0f",
                ""
            )
        )
    }

    @JvmStatic
    fun isLinkOrEmojiOnlyMessage(messageObject: MessageObject): Boolean {
        val entities = messageObject.messageOwner.entities
        if (entities != null) {
            for (entity in entities) {
                if (entity is TL_messageEntityBotCommand ||
                    entity is TL_messageEntityEmail ||
                    entity is TL_messageEntityUrl ||
                    entity is TL_messageEntityMention ||
                    entity is TL_messageEntityCashtag ||
                    entity is TL_messageEntityHashtag ||
                    entity is TL_messageEntityBankCard ||
                    entity is TL_messageEntityPhone
                ) {
                    if (entity.offset == 0 && entity.length == messageObject.messageOwner.message.length) {
                        return true
                    }
                }
            }
        }
        return Emoji.fullyConsistsOfEmojis(messageObject.messageOwner.message)
    }

    @JvmStatic
    fun isMessageObjectAutoTranslatable(messageObject: MessageObject): Boolean {
        if (messageObject.messageOwner.translated || messageObject.translating || messageObject.isOutOwner) {
            return false
        }
        return if (messageObject.isPoll) {
            true
        } else !TextUtils.isEmpty(messageObject.messageOwner.message) && !isLinkOrEmojiOnlyMessage(
            messageObject
        )
    }

    @JvmStatic
    fun getMessagePlainText(messageObject: MessageObject): String {
        val message: String = if (messageObject.isPoll) {
            val poll = (messageObject.messageOwner.media as TL_messageMediaPoll).poll
            val pollText = StringBuilder(poll.question.text).append("\n")
            for (answer in poll.answers) {
                pollText.append("\n\uD83D\uDD18 ")
                pollText.append(answer.text.text)
            }
            pollText.toString()
        } else if (messageObject.isVoiceTranscriptionOpen) {
            messageObject.messageOwner.voiceTranscription
        } else {
            messageObject.messageOwner.message
        }
        return message
    }
}
