package xyz.nextalone.nagram.helper

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import androidx.core.content.FileProvider
import org.telegram.messenger.*
import org.telegram.tgnet.TLRPC.*
import java.io.File
import java.io.FileOutputStream

import xyz.nextalone.nagram.NaConfig


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
            path = FileLoader.getPathToMessage(messageObject.messageOwner).toString()
            val temp = File(path)
            if (!temp.exists()) {
                path = null
            }
        }
        if (TextUtils.isEmpty(path)) {
            path = FileLoader.getPathToAttach(messageObject.document, true).toString()
            val temp = File(path)
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
        return if (!NaConfig.DateOfForwardedMsg.Bool()) {
            orig.toString()
        } else "$orig • ${LocaleController.formatDate(obj.messageOwner.fwd_from.date.toLong())}"
    }
}
