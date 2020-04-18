package tw.nekomimi.nekogram

import android.content.Context
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.BottomSheet
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Cells.HeaderCell
import org.telegram.ui.Cells.RadioButtonCell
import org.telegram.ui.Cells.ShadowSectionCell
import org.telegram.ui.Cells.TextCheckCell
import org.telegram.ui.Components.HintEditText
import org.telegram.ui.Components.LayoutHelper
import java.util.*

class BottomBuilder(val ctx: Context) {

    val builder = BottomSheet.Builder(ctx, true)

    private val rootView = LinearLayout(ctx).apply {

        orientation = LinearLayout.VERTICAL

    }

    private val _root = LinearLayout(ctx).apply {

        addView(ScrollView(ctx).apply {

            addView(this@BottomBuilder.rootView)
            isFillViewport = true
            isVerticalScrollBarEnabled = false

        },LinearLayout.LayoutParams(-1,-1))

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
    fun addTitle(title: String, bigTitle: Boolean = true, subTitle: String? = null): HeaderCell {

        val headerCell = if (bigTitle) HeaderCell(ctx, Theme.key_dialogTextBlue2, 21, 15, subTitle != null) else HeaderCell(ctx)

        headerCell.setText(title)

        subTitle?.also { headerCell.setText2(it) }

        rootView.addView(headerCell, LayoutHelper.createLinear(-1, -2).apply {

            bottomMargin = AndroidUtilities.dp(12F)

        })

        rootView.addView(ShadowSectionCell(ctx, 3))

        return headerCell

    }

    @JvmOverloads
    fun addCheckItem(text: String, value: Boolean, switch: Boolean = false, valueText: String? = null, listener: (cell: TextCheckCell) -> Unit): TextCheckCell {

        val checkBoxCell = TextCheckCell(ctx, 21, !switch)
        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false))
        checkBoxCell.minimumHeight = AndroidUtilities.dp(50F)
        rootView.addView(checkBoxCell, LayoutHelper.createLinear(-1, -2))

        if (valueText == null) {

            checkBoxCell.setTextAndCheck(text, value, true)

        } else {

            checkBoxCell.setTextAndValueAndCheck(text, valueText, value, true, true)

        }

        checkBoxCell.setOnClickListener {

            listener.invoke(checkBoxCell)

        }

        return checkBoxCell

    }

    @JvmOverloads
    fun addCheckItems(text: Array<String>, value: (Int) -> Boolean, switch: Boolean = false, valueText: ((Int) -> String)? = null, listener: (index: Int, text: String, cell: TextCheckCell) -> Unit): List<TextCheckCell> {

        val list = mutableListOf<TextCheckCell>()

        text.forEachIndexed { index, textI ->

            list.add(addCheckItem(textI, value(index), switch, valueText?.invoke(index)) { cell ->

                listener(index, textI, cell)

            })

        }

        return list

    }

    private val radioButtonGroup by lazy { LinkedList<RadioButtonCell>() }

    fun doRadioCheck(cell: RadioButtonCell) {

        if (!cell.isChecked) {

            radioButtonGroup.forEach {

                if (it.isChecked) {

                    it.setChecked(false, true)

                }

            }

            cell.setChecked(true, true)

        }

    }

    @JvmOverloads
    fun addRadioItem(text: String, value: Boolean, valueText: String? = null, listener: (cell: RadioButtonCell) -> Unit): RadioButtonCell {

        val checkBoxCell = RadioButtonCell(ctx, true)
        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false))
        checkBoxCell.minimumHeight = AndroidUtilities.dp(50F)
        rootView.addView(checkBoxCell, LayoutHelper.createLinear(-1, -2))

        if (valueText == null) {

            checkBoxCell.setTextAndValue(text, true, value)

        } else {

            checkBoxCell.setTextAndValueAndCheck(text, valueText, true, value)

        }

        radioButtonGroup.add(checkBoxCell)

        checkBoxCell.setOnClickListener {

            listener(checkBoxCell)

        }

        return checkBoxCell

    }

    @JvmOverloads
    fun addRadioItems(text: Array<String>, value: (Int) -> Boolean, valueText: ((Int) -> String)? = null, listener: (index: Int, text: String, cell: RadioButtonCell) -> Unit): List<RadioButtonCell> {

        val list = mutableListOf<RadioButtonCell>()

        text.forEachIndexed { index, textI ->

            list.add(addRadioItem(textI, value(index), valueText?.invoke(index)) { cell ->

                listener(index, textI, cell)

            })

        }

        return list

    }

    @JvmOverloads
    fun addCancelButton(left: Boolean = true) {

        addButton(LocaleController.getString("Cancel", R.string.Cancel), left = left) { dismiss() }

    }

    @JvmOverloads
    fun addButton(text: String, red: Boolean = false, left: Boolean = false, listener: ((TextView) -> Unit)): TextView {

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
            setOnClickListener { listener(this) }

        }

    }

    @JvmOverloads
    fun addItem(text: String, icon: Int = 0, listener: (cell: BottomSheetCell) -> Unit): BottomSheetCell {

        return BottomSheetCell(ctx, 0).apply {

            setTextAndIcon(text, icon)

            setOnClickListener {

                listener(this)

            }

            this@BottomBuilder.rootView.addView(this, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50, Gravity.LEFT or Gravity.TOP))

        }

    }

    fun addItems(text: Array<String>, icon: (Int) -> Int, listener: (index: Int, text: String, cell: BottomSheetCell) -> Unit): List<BottomSheetCell> {

        val list = mutableListOf<BottomSheetCell>()

        text.forEachIndexed { index, textI ->

            list.add(addItem(textI, icon(index)) { cell ->

                listener(index, textI, cell)

            })

        }

        return list

    }

    fun addEditText(hintText: String): EditText {

        return EditText(ctx).apply {

            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            setTextColor(Theme.getColor(Theme.key_dialogTextBlack))
            hint = hintText
            isSingleLine = true
            isFocusable = true
            setBackgroundDrawable(null)

            this@BottomBuilder.rootView.addView(this, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, -2, if (LocaleController.isRTL) Gravity.RIGHT else Gravity.LEFT, AndroidUtilities.dp(8F), 0, 0, 0))

        }

    }

    fun create() = builder.create()
    fun show() = builder.show()
    fun dismiss() {
        builder.dismissRunnable.run()
    }

}