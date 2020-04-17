package tw.nekomimi.nekogram

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.LocaleController
import org.telegram.ui.ActionBar.BottomSheet
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Cells.CheckBoxCell
import org.telegram.ui.Cells.HeaderCell
import org.telegram.ui.Components.LayoutHelper

class BottomBuilder(val ctx: Context) {

    val builder = BottomSheet.Builder(ctx)

    private val rootView = LinearLayout(ctx).apply {

        orientation = LinearLayout.VERTICAL

        builder.setCustomView(this)

    }

    private val buttonsView by lazy {

        LinearLayout(ctx).apply {

            orientation = LinearLayout.HORIZONTAL

            gravity = Gravity.CENTER_VERTICAL or if (LocaleController.isRTL) Gravity.LEFT else Gravity.RIGHT

            minimumHeight = AndroidUtilities.dp(48F)

            this@BottomBuilder.rootView.addView(this, LinearLayout.LayoutParams(-1, -2))

        }

    }

    @JvmOverloads
    fun addTitle(title: String, bigTitle: Boolean = true) {

        val headerCell = if (bigTitle) HeaderCell(ctx, Theme.key_dialogTextBlue2, 21, 15, false) else HeaderCell(ctx)

        headerCell.setText(title)

        rootView.addView(headerCell, LayoutHelper.createLinear(-1, -2))

    }

    @JvmOverloads
    fun addCheckBox(text: String, value: Boolean, valueText: String? = null, listener: View.OnClickListener) {

        val checkBoxCell = CheckBoxCell(ctx, 1, 21)
        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false))
        checkBoxCell.minimumHeight = AndroidUtilities.dp(50F)
        rootView.addView(checkBoxCell, LayoutHelper.createLinear(-1, -2))
        checkBoxCell.setText(text, valueText, value, true)

        checkBoxCell.setOnClickListener(listener)

    }

    @FunctionalInterface
    interface IndexedListener {

        fun onClick(index: Int, view: CheckBoxCell)

    }

    @JvmOverloads
    fun setCheckItems(text: Array<String>, value: (Int) -> Boolean, valueText: ((Int) -> String)? = null, listener: IndexedListener) {

        text.forEachIndexed { index, textI ->

            addCheckBox(textI, value(index), valueText?.invoke(index), View.OnClickListener {

                listener.onClick(index, it as CheckBoxCell)

            })

        }

    }

    @JvmOverloads
    fun addButton(text: String, icon: Int = 0, red: Boolean = false, listener: View.OnClickListener?): BottomSheet.BottomSheetCell {

        return BottomSheet.BottomSheetCell(ctx, 1).apply {

            setBackgroundDrawable(Theme.getSelectorDrawable(false))
            setTextAndIcon(text, icon)
            setTextColor(Theme.getColor(if (red) Theme.key_windowBackgroundWhiteRedText else Theme.key_dialogTextBlue2))
            setOnClickListener { v ->
                if (listener != null) {
                    listener.onClick(v)
                } else {
                    dismiss()
                }
            }

            buttonsView.addView(this, LinearLayout.LayoutParams(-2, -1))

        }

    }

    fun create() = builder.create()
    fun show() = builder.show()
    fun dismiss() {
        builder.dismissRunnable.run()
    }

}