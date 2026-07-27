package xyz.nextalone.nagram.helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import org.telegram.messenger.AndroidUtilities

object ColorOsHelper {
    val isColorOS: Boolean = !TextUtils.isEmpty(AndroidUtilities.getSystemProperty("ro.build.version.oplusrom"))
    val colorOSVersion: Int = try {
        AndroidUtilities.getSystemProperty("ro.build.version.release").toInt()
    } catch (_: Exception) { 0 }
    fun isColorOSAiAvailable(): Boolean {
        if (!isColorOS) {
            return false
        }
        if (colorOSVersion != 15 && colorOSVersion != 16) {
            return false
        }
        return true
    }
    fun startColorOsAiService(view: View, text: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        try {
            val intent = Intent().apply {
                `package` = "com.oplus.aiwriter"
                action = Intent.ACTION_PROCESS_TEXT
                type = "text/plain"
                putExtra("android.intent.extra.PROCESS_TEXT", text)
                putExtra("extra_is_edittext_view", true)
            }
            view.context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("ColorOsHelper", "Failed to start ColorOS AI service", e)
        }
    }
    fun startColorOsAiService(context: Context, text: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        try {
            val intent = Intent().apply {
                `package` = "com.heytap.speechassist"
                action = Intent.ACTION_PROCESS_TEXT
                type = "text/plain"
                putExtra("open_with_zoomwindow", true)
                putExtra("android.intent.extra.PROCESS_TEXT", text)
            }
            context.startActivity(intent)
            return true
        } catch (e: Exception) {
            Log.e("ColorOsHelper", "Failed to start ColorOS AI service", e)
        }
        return false
    }
    fun startColorOsAiService(context: Context, uri: Uri): Boolean {
        try {
            val intent = Intent().apply {
                `package` = "com.heytap.speechassist"
                action = Intent.ACTION_SEND
                type = "image/*"
                putExtra("open_with_zoomwindow", true)
                putExtra("android.intent.extra.STREAM", uri)
            }
            context.startActivity(intent)
            return true
        } catch (e: Exception) {
            Log.e("ColorOsHelper", "Failed to start ColorOS AI service", e)
        }
        return false
    }
}
