package xyz.nextalone.nagram.helper

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.EditText
import xyz.nextalone.nagram.NaConfig

object SystemAiServiceHelper {
    fun isSystemAiAvailable(context: Context): Boolean {
        if (!NaConfig.useSystemAiService.Bool()) {
            return false
        }
        if (HyperOsHelper.isHyperAiAvailable(context) || ColorOsHelper.isColorOSAiAvailable()) {
            return true
        }
        return false
    }
    @JvmOverloads
    fun startSystemAiService(view: View, text: String = "") {
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
        if (HyperOsHelper.IS_HYPEROS) {
            HyperOsHelper.startHyperOsAiService(view, selectedText)
        } else if (ColorOsHelper.isColorOS) {
            ColorOsHelper.startColorOsAiService(view, selectedText)
        }
    }
    fun startSystemAiService(context: Context, uri: Uri): Boolean {
        if (ColorOsHelper.isColorOS) {
            return ColorOsHelper.startColorOsAiService(context, uri)
        }
        return false
    }
}
