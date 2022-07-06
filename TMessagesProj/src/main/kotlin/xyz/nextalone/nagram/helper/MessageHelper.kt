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
    fun getPathToMessage(messageObject: MessageObject): String? {
        var path = messageObject.messageOwner.attachPath
        if (!TextUtils.isEmpty(path)) {
            val temp = File(path)
            if (!temp.exists()) {
                path = null
            }
        }
        if (TextUtils.isEmpty(path)) {
            val temp = FileLoader.getInstance(messageObject.currentAccount).getPathToMessage(messageObject.messageOwner)
            if (!temp.exists()) {
                path = null
            }
        }
        if (TextUtils.isEmpty(path)) {
            val temp = FileLoader.getInstance(messageObject.currentAccount).getPathToAttach(messageObject.document, true)
            if (!temp.exists()) {
                return null
            }
        }
        return path
    }


    fun addMessageToClipboard(selectedObject: MessageObject, callback: Runnable) {
        val path = getPathToMessage(selectedObject)
        if (!TextUtils.isEmpty(path)) {
            addFileToClipboard(File(path), callback)
        }
    }


    fun addFileToClipboard(file: File?, callback: Runnable) {
        try {
            val context = ApplicationLoader.applicationContext
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file!!)
            val clip = ClipData.newUri(context.contentResolver, "label", uri)
            clipboard.setPrimaryClip(clip)
            callback.run()
        } catch (e: Exception) {
            FileLog.e(e)
        }
    }

    @JvmStatic
    fun showForwardDate(obj: MessageObject, orig: CharSequence): String {
        val date: Long = obj.messageOwner.fwd_from.date.toLong()
        val day: String = LocaleController.formatDate(date)
        val time: String = LocaleController.getInstance().formatterDay.format(date * 1000)
        return if (!NaConfig.DateOfForwardedMsg.Bool()) {
            orig.toString()
        } else {
            if (day == time) {"$orig · $day"} else "$orig · $day $time"
        }
    }
}
