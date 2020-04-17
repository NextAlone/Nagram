package tw.nekomimi.nekogram

import android.content.Context
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import org.telegram.messenger.AndroidUtilities
import org.telegram.ui.ActionBar.BottomSheet
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Cells.CheckBoxCell
import org.telegram.ui.Cells.HeaderCell
import org.telegram.ui.Cells.ShadowSectionCell
import org.telegram.ui.Cells.TextCheckCell
import org.telegram.ui.Components.LayoutHelper

class BottomBuilder(val ctx: Context) {

    val builder = BottomSheet.Builder(ctx)

    private val rootView = LinearLayout(ctx).apply {

        orientation = LinearLayout.VERTICAL

        builder.setCustomView(this)

    }

    private val buttonsView by lazy {

        FrameLayout(ctx).apply {

            setBackgroundColor(Theme.getColor(Theme.key_dialogBackground))

            this@BottomBuilder.rootView.addView(this, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 50, Gravity.LEFT or Gravity.BOTTOM))

            addView(rightButtonsView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.TOP or Gravity.RIGHT));

        }

    }

    private val rightButtonsView by lazy {

        LinearLayout(ctx).apply {

            orientation = LinearLayout.HORIZONTAL

            weightSum = 1F

        }

    }

    @JvmOverloads
    fun addTitle(title: String, bigTitle: Boolean = true, subTitle: String? = null) {

        val headerCell = if (bigTitle) HeaderCell(ctx, Theme.key_dialogTextBlue2, 21, 15, false) else HeaderCell(ctx)

        headerCell.setText(title)

        subTitle?.also { headerCell.setText2(it) }

        rootView.addView(headerCell, LayoutHelper.createLinear(-1, -2).apply {

            bottomMargin = AndroidUtilities.dp(12F)

        })

        rootView.addView(ShadowSectionCell(ctx,3))

    }

    @JvmOverloads
    fun addCheckBox(text: String, value: Boolean, valueText: String? = null, listener: View.OnClickListener) {

        val checkBoxCell = TextCheckCell(ctx, 21,true)
        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false))
        checkBoxCell.minimumHeight = AndroidUtilities.dp(50F)
        rootView.addView(checkBoxCell, LayoutHelper.createLinear(-1, -2))

        if (valueText == null) {

            checkBoxCell.setTextAndCheck(text, value, true)

        } else {

            checkBoxCell.setTextAndValueAndCheck(text,valueText,value,true,true)

        }

        checkBoxCell.setOnClickListener(listener)

    }

    @FunctionalInterface
    interface IndexedListener {

        fun onClick(index: Int, view: TextCheckCell)

    }

    @JvmOverloads
    fun setCheckItems(text: Array<String>, value: (Int) -> Boolean, valueText: ((Int) -> String)? = null, listener: IndexedListener) {

        text.forEachIndexed { index, textI ->

            addCheckBox(textI, value(index), valueText?.invoke(index), View.OnClickListener {

                listener.onClick(index, it as TextCheckCell)

            })

        }

    }

    @JvmOverloads
    fun addButton(text: String, red: Boolean = false,left : Boolean = false, listener: View.OnClickListener?): TextView {

        return TextView(ctx).apply {

            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            setTextColor(Theme.getColor(Theme.key_dialogTextBlue4))
            gravity = Gravity.CENTER
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0))
            setPadding(AndroidUtilities.dp(18f), 0, AndroidUtilities.dp(18f), 0)
            setText(text)
            typeface = AndroidUtilities.getTypeface("fonts/rmedium.ttf")
            (if (left) buttonsView else rightButtonsView).addView(this, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.TOP or Gravity.LEFT))
            setOnClickListener(listener ?: View.OnClickListener { dismiss() })

        }

    }

    fun create() = builder.create()
    fun show() = builder.show()
    fun dismiss() {
        builder.dismissRunnable.run()
    }

}