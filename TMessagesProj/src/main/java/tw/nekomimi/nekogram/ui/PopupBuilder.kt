package tw.nekomimi.nekogram.ui

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.AlertDialog
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Cells.RadioColorCell
import org.telegram.ui.Cells.TextSettingsCell
import org.telegram.ui.Components.LayoutHelper

class PopupBuilder {

    fun interface PopupDelegate {
        fun onItemClick(id: Int)
    }

    private data class Item(val id: Int, val text: CharSequence)

    private val context: Context?
    private var anchor: View? = null
    private var resourcesProvider: Theme.ResourcesProvider? = null

    private val items = ArrayList<Item>()
    private var title: CharSequence? = null
    private var selectedIndex: Int = -1
    private var delegate: PopupDelegate? = null
    private var listener: ((Int, CharSequence) -> Unit)? = null

    var isShowOnTop: Boolean = false
    var isVerticalScrollBarEnabled: Boolean = false

    @JvmOverloads
    constructor(anchor: View, dialog: Boolean = false, resourcesProvider: Theme.ResourcesProvider? = null) {
        this.anchor = anchor
        this.context = anchor.context
        this.isShowOnTop = dialog
        this.resourcesProvider = resourcesProvider
    }

    @JvmOverloads
    constructor(context: Context, resourcesProvider: Theme.ResourcesProvider? = null) {
        this.anchor = null
        this.context = context
        this.resourcesProvider = resourcesProvider
    }

    fun setAnchor(anchor: View): PopupBuilder {
        this.anchor = anchor
        return this
    }

    fun setTitle(title: CharSequence?): PopupBuilder {
        this.title = title
        return this
    }

    fun setTitle(resId: Int): PopupBuilder {
        this.title = LocaleController.getString(resId)
        return this
    }

    fun setSelectedItem(index: Int): PopupBuilder {
        this.selectedIndex = index
        return this
    }

    fun setCheckIndex(index: Int): PopupBuilder {
        this.selectedIndex = index
        return this
    }

    fun addSubItem(id: Int, text: CharSequence): PopupBuilder {
        items.add(Item(id, text))
        return this
    }

    fun removeAllSubItems(): PopupBuilder {
        items.clear()
        return this
    }

    fun setDelegate(delegate: PopupDelegate?): PopupBuilder {
        this.delegate = delegate
        return this
    }

    fun setItems(items: Array<out CharSequence?>, listener: (Int, CharSequence) -> Unit): PopupBuilder {
        removeAllSubItems()
        for (i in items.indices) {
            val item = items[i] ?: continue
            addSubItem(i, item)
        }
        this.listener = listener
        return this
    }

    fun setItems(items: List<CharSequence?>, listener: (Int, CharSequence) -> Unit): PopupBuilder {
        removeAllSubItems()
        for (i in items.indices) {
            val item = items[i] ?: continue
            addSubItem(i, item)
        }
        this.listener = listener
        return this
    }

    fun show(): AlertDialog? {
        val ctx = anchor?.context ?: context ?: return null
        val activity = AndroidUtilities.findActivity(ctx)
        if (activity != null && (activity.isFinishing || activity.isDestroyed)) {
            return null
        }

        val builder = AlertDialog.Builder(ctx, resourcesProvider)

        var dialogTitle = title
        if (dialogTitle.isNullOrEmpty() && anchor is TextSettingsCell) {
            dialogTitle = (anchor as TextSettingsCell).textView?.text
        }
        if (!dialogTitle.isNullOrEmpty()) {
            builder.setTitle(dialogTitle)
        }

        val linearLayout = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
        }
        builder.setView(linearLayout)

        var currentSelected = selectedIndex
        if (currentSelected < 0 && anchor is TextSettingsCell) {
            val currentValue = (anchor as TextSettingsCell).valueTextView?.text?.toString()
            if (!currentValue.isNullOrEmpty()) {
                val found = items.indexOfFirst { it.text.toString() == currentValue }
                if (found >= 0) {
                    currentSelected = items[found].id
                }
            }
        }

        var dismissed = false
        var dialogRef: AlertDialog? = null

        for (i in items.indices) {
            val item = items[i]
            val cell = RadioColorCell(ctx, resourcesProvider).apply {
                setPadding(AndroidUtilities.dp(4f), 0, AndroidUtilities.dp(4f), 0)
                tag = i
                setCheckColor(
                    Theme.getColor(Theme.key_radioBackground, resourcesProvider),
                    Theme.getColor(Theme.key_dialogRadioBackgroundChecked, resourcesProvider)
                )
                setTextAndValue(item.text, currentSelected == item.id)
                setBackground(
                    Theme.createSelectorDrawable(
                        Theme.getColor(Theme.key_listSelector, resourcesProvider),
                        Theme.RIPPLE_MASK_ALL
                    )
                )
                setOnClickListener { v ->
                    if (dismissed) return@setOnClickListener
                    dismissed = true
                    val which = v.tag as Int
                    val clickedItem = items[which]
                    setChecked(true, true)
                    builder.dismissRunnable?.run() ?: dialogRef?.dismiss()
                    delegate?.onItemClick(clickedItem.id)
                    listener?.invoke(clickedItem.id, clickedItem.text)
                }
            }
            linearLayout.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT))
        }

        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null)

        val dialog = builder.create()
        dialogRef = dialog
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
        return dialog
    }

    fun toggleSubMenu(): AlertDialog? {
        return show()
    }
}
