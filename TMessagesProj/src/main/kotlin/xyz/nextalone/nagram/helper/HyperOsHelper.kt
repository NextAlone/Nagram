package xyz.nextalone.nagram.helper

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.EditText
import org.telegram.messenger.AndroidUtilities


object HyperOsHelper {
    private var IS_HYPEROS: Boolean =
        AndroidUtilities.getSystemProperty("ro.mi.os.version.name") != null
    private const val HYPEROS_NOTES_PKG: String = "com.miui.notes"
    private const val HYPEROS_AI_SERVICE: String = "com.miui.notes.ai.AiTextWidgetService"
    fun isHyperAiAvailable(context: Context): Boolean {
        // Check if is HyperOS
        if (!IS_HYPEROS) {
            return false
        }
        val packageManager: PackageManager = context.packageManager ?: return false
        try {
            // Retrieve package information for HyperOS Notes
            val packageInfo = packageManager.getPackageInfo(HYPEROS_NOTES_PKG, 0)
            if (packageInfo.versionCode < 1100) {
                return false
            }
        } catch (e: PackageManager.NameNotFoundException) {
            // Package not found, exit gracefully
            return false
        }
        return true
    }
    @JvmOverloads
    fun startHyperOsAiService(view: View, text: String = "") {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        try {
            val currentPackage: String = view.context.packageName
            val serviceIntent = Intent()
            // Pass the package name
            serviceIntent.putExtra("packageName", currentPackage)
            // Handle selection logic
            var selectedText = ""
            if (view is EditText) {
                if (view.hasSelection()) {
                    val selectionStart: Int = view.selectionStart
                    val selectionEnd: Int = view.selectionEnd
                    if (selectionStart != selectionEnd) {
                        selectedText =
                            view.getText().subSequence(selectionStart, selectionEnd).toString()
                    }
                }
            } else {
                selectedText = text
            }
            serviceIntent.putExtra("selectedText", selectedText)
            // Store original view bounds
            serviceIntent.putExtra("originalViewLeft", view.left)
            serviceIntent.putExtra("originalViewTop", view.top)
            serviceIntent.putExtra("originalViewRight", view.right)
            serviceIntent.putExtra("originalViewBottom", view.bottom)
            serviceIntent.putExtra("originalViewName", javaClass.name)
            serviceIntent.putExtra("isEditor", true)
            // Get the active screen location
            val screenCoordinates = IntArray(2)
            val focusedRect = Rect()
            view.getLocationOnScreen(screenCoordinates)
            view.getFocusedRect(focusedRect)
            focusedRect.offset(screenCoordinates[0], screenCoordinates[1])
            val currentActivity = view.context as Activity?
            if (currentActivity != null) {
                val windowFrame = Rect()
                currentActivity.window.decorView.getWindowVisibleDisplayFrame(windowFrame)
                // Putting the visible window bounds into the Intent
                serviceIntent.putExtra("left", windowFrame.left)
                serviceIntent.putExtra("top", windowFrame.top)
                serviceIntent.putExtra("right", windowFrame.right)
                serviceIntent.putExtra("bottom", windowFrame.bottom)
                serviceIntent.putExtra("taskId", currentActivity.taskId)
            }
            // Prepare and start the service
            serviceIntent.setComponent(
                ComponentName(
                    HYPEROS_NOTES_PKG,
                    HYPEROS_AI_SERVICE
                )
            )
            view.context.startForegroundService(serviceIntent)
        } catch (e: Exception) {
            Log.e("HyperOsHelper", "Failed to start HyperOS AI service")
        }
    }
}
