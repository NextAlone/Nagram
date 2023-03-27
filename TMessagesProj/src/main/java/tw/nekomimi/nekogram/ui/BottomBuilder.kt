package tw.nekomimi.nekogram.ui

import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.widget.*
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.BottomSheet
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Cells.HeaderCell
import org.telegram.ui.Cells.RadioButtonCell
import org.telegram.ui.Cells.TextCell
import org.telegram.ui.Cells.TextCheckCell
import org.telegram.ui.Components.LayoutHelper
import java.util.*


class BottomBuilder(val ctx: Context, val needFocus: Boolean = true, val bgColor: Int = Theme.getColor(Theme.key_dialogBackground)) {
    constructor(ctx: Context) : this(ctx, true) {}
    constructor(ctx: Context, needFocus: Boolean) : this(ctx, needFocus, Theme.getColor(Theme.key_dialogBackground)) {}
    val builder = BottomSheet.Builder(ctx, needFocus)

    private val rootView = LinearLayout(ctx).apply {
        orientation = LinearLayout.VERTICAL
    }
    private val rtl = (if (LocaleController.isRTL) Gravity.RIGHT else Gravity.LEFT)

    private val _root = LinearLayout(ctx).apply {

        addView(ScrollView(ctx).apply {

            addView(this@BottomBuilder.rootView)
            isFillViewport = true
            isVerticalScrollBarEnabled = false

        }, LinearLayout.LayoutParams(-1, -1))

        builder.setCustomView(this)

    }


    private val buttonsView by lazy {

        FrameLayout(ctx).apply {

            setBackgroundColor(bgColor)

            this@BottomBuilder.rootView.addView(this, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 50, Gravity.LEFT or Gravity.BOTTOM))

            addView(rightButtonsView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.TOP or Gravity.RIGHT))

        }

    }

    private val rightButtonsView by lazy {

        LinearLayout(ctx).apply {

            orientation = LinearLayout.HORIZONTAL
            weightSum = 1F

        }

    }

    @JvmOverloads
    fun addTitle(title: CharSequence, bigTitle: Boolean = false): HeaderCell {
        return addTitle(title, bigTitle, null)
    }

    fun addTitle(title: CharSequence, subTitle: CharSequence): HeaderCell {
        return addTitle(title, true, subTitle)
    }

    fun addTitle(title: CharSequence, bigTitle: Boolean, subTitle: CharSequence?): HeaderCell {

        val headerCell = HeaderCell(ctx, Theme.key_dialogTextBlue2, 23, 15, false)
        headerCell.setBigTitle(bigTitle)
        headerCell.setText(if (title is String) AndroidUtilities.replaceTags(title) else title)
        subTitle?.also {
            headerCell.setText2(it)
        }
        rootView.addView(headerCell, LayoutHelper.createLinear(-1, -2).apply {
            bottomMargin = AndroidUtilities.dp(8F)
        })
        return headerCell
    }

    @JvmOverloads
    fun addCheckItem(text: String, value: Boolean, switch: Boolean = false, valueText: String? = null, listener: ((cell: TextCheckCell, isChecked: Boolean) -> Unit)?): TextCheckCell {

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
            val target = !checkBoxCell.isChecked
            checkBoxCell.isChecked = target
            listener?.invoke(checkBoxCell, target)
        }

        if (checkBoxCell.checkBox != null) {
            checkBoxCell.checkBox.setOnClickListener { checkBoxCell.performClick() }
        } else {
            checkBoxCell.checkBoxSquare.setOnClickListener { checkBoxCell.performClick() }
        }

        return checkBoxCell

    }

    @JvmOverloads
    fun addCheckItems(text: Array<String>, value: (Int) -> Boolean, switch: Boolean = false, valueText: ((Int) -> String)? = null, listener: (index: Int, text: String, cell: TextCheckCell, isChecked: Boolean) -> Unit): List<TextCheckCell> {
        val list = mutableListOf<TextCheckCell>()
        text.forEachIndexed { index, textI ->
            list.add(addCheckItem(textI, value(index), switch, valueText?.invoke(index)) { cell, isChecked ->
                listener(index, textI, cell, isChecked)
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
    fun addRadioItems(text: Array<String>, value: (Int, String) -> Boolean, valueText: ((Int, String) -> String)? = null, listener: (index: Int, text: String, cell: RadioButtonCell) -> Unit): List<RadioButtonCell> {
        val list = mutableListOf<RadioButtonCell>()
        text.forEachIndexed { index, textI ->
            list.add(addRadioItem(textI, value(index, textI), valueText?.invoke(index, textI)) { cell ->
                listener(index, textI, cell)
            })
        }
        return list
    }

    fun addCancelItem() {
        addItem(LocaleController.getString("Cancel", R.string.Cancel), R.drawable.baseline_cancel_24) {}
    }

    @JvmOverloads
    fun addCancelButton(left: Boolean = true) {
        addButton(LocaleController.getString("Cancel", R.string.Cancel), left = left) {}
    }


    @JvmOverloads
    fun addOkButton(listener: ((TextView) -> Unit), noAutoDismiss: Boolean = false) {
        addButton(LocaleController.getString("OK", R.string.OK), noAutoDismiss) { listener(it); }
    }

    @JvmOverloads
    fun addButton(text: String, noAutoDismiss: Boolean = false, left: Boolean = false, listener: ((TextView) -> Unit)): TextView {

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
            (if (left) buttonsView else rightButtonsView).addView(this, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, rtl))
            setOnClickListener { if (!noAutoDismiss) dismiss();listener(this) }

        }

    }

    @JvmOverloads
    fun addItem(text: CharSequence, icon: Int = 0, red: Boolean = false, listener: ((cell: TextCell) -> Unit)?): TextCell {
        return TextCell(ctx).apply {
            background = Theme.getSelectorDrawable(false)
            setTextAndIcon(text, icon, false)
            setOnClickListener {
                dismiss()
                listener?.invoke(this)
            }
            if (red) {
                setColors("key_dialogTextRed2", "key_dialogTextRed2")
            }
            this@BottomBuilder.rootView.addView(this, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48, rtl))
        }
    }

    fun addItems(text: Array<CharSequence?>, icon: IntArray?, listener: (index: Int, text: CharSequence, cell: TextCell) -> Unit): List<TextCell> {
        val list = mutableListOf<TextCell>()
        text.forEachIndexed { index, textI ->
            list.add(addItem(textI ?: return@forEachIndexed, icon?.get(index) ?: 0) { cell ->
                listener(index, textI, cell)
            })
        }
        return list
    }

    @JvmOverloads
    fun addEditText(hintText: String? = null): EditText {
        return EditText(ctx).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            setTextColor(Theme.getColor(Theme.key_dialogTextBlack))
            setHintTextColor(Theme.getColor(Theme.key_dialogTextBlue4))
            hintText?.also { hint = it }
            isSingleLine = true
            isFocusable = true
            setBackgroundDrawable(null)

            this@BottomBuilder.rootView.addView(this, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, -2, rtl, AndroidUtilities.dp(6F), 0, 0, 0))
        }
    }

    fun setTitleMultipleLines(multilines: Boolean) {
        builder.setTitleMultipleLines(multilines)
    }

    fun create() = builder.create()
    fun show() = builder.show()
    fun dismiss() {
        builder.dismissRunnable.run()
    }

    fun setOnPreDismissListener(onDismissListener: DialogInterface.OnDismissListener?): BottomBuilder {
        builder.setOnPreDismissListener(onDismissListener)
        return this
    }

}