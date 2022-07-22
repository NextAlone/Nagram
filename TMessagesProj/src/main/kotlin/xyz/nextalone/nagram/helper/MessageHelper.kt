package xyz.nextalone.nagram.helper

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import androidx.core.content.FileProvider
import org.telegram.messenger.*
import xyz.nextalone.nagram.NaConfig
import java.io.File


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
        return if (!NaConfig.DateOfForwardedMsg.Bool()) {
            orig
        } else {
            if (day == time) {
                "$orig · $day"
            } else "$orig · $day $time"
        }
    }
}
