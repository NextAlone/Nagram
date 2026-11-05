package xyz.nextalone.nagram.ui.drawer

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Components.LayoutHelper

class DrawerAddCell(context: Context) : FrameLayout(context) {

    private val textView: TextView

    init {
        textView = TextView(context)
        textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
        textView.typeface = AndroidUtilities.bold()
        textView.setLines(1)
        textView.maxLines = 1
        textView.isSingleLine = true
        textView.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
        textView.compoundDrawablePadding = AndroidUtilities.dp(29f)
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT.toFloat(), Gravity.LEFT or Gravity.TOP, 19f, 0f, 16f, 0f))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48f), MeasureSpec.EXACTLY)
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText))
        textView.text = LocaleController.getString(R.string.AddAccount)
        val drawable = ContextCompat.getDrawable(context, R.drawable.msg_add)
        drawable?.colorFilter = PorterDuffColorFilter(Theme.getColor(Theme.key_chats_menuItemIcon), PorterDuff.Mode.MULTIPLY)
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }
}
