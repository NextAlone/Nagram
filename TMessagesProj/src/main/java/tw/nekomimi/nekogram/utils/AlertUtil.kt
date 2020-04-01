package tw.nekomimi.nekogram.utils

import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import org.telegram.messenger.*
import org.telegram.ui.ActionBar.AlertDialog
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.ChatActivity
import org.telegram.ui.Components.AvatarDrawable
import org.telegram.ui.Components.BackupImageView
import org.telegram.ui.Components.LayoutHelper
import tw.nekomimi.nekogram.MessageHelper

object AlertUtil {

    @JvmStatic
    fun showToast(text: String) = Toast.makeText(ApplicationLoader.applicationContext, text.takeIf { it.isNotBlank() }
            ?: "喵 !", Toast.LENGTH_LONG).show()

    @JvmStatic
    fun showSimpleAlert(ctx: Context, text: String) {

        val builder = AlertDialog.Builder(ctx)

        builder.setTitle(LocaleController.getString("NekoX", R.string.NekoX))
        builder.setMessage(text)

        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);

        builder.show()

    }

    @JvmStatic
    fun showComfirm(ctx: Context, title: String, text: String, button: String,red: Boolean = false, listener: DialogInterface.OnClickListener) {

        val builder = AlertDialog.Builder(ctx)

        builder.setTitle(title)

        builder.setMessage(AndroidUtilities.replaceTags(text))
        builder.setPositiveButton(button, listener)

        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null)
        val alertDialog = builder.show()

        if (red) {

            (alertDialog.getButton(DialogInterface.BUTTON_POSITIVE) as TextView?)?.setTextColor(Theme.getColor(Theme.key_dialogTextRed2))

        }

    }

}