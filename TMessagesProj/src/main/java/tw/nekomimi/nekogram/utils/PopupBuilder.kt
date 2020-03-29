package tw.nekomimi.nekogram.utils

import android.view.View
import org.telegram.ui.ActionBar.ActionBarMenuItem
import org.telegram.ui.ActionBar.Theme

class PopupBuilder(anchor: View) : ActionBarMenuItem(anchor.context, null, Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, -0x4c4c4d) {

    init {

        setAnchor(anchor)

        isVerticalScrollBarEnabled = true

    }

    @FunctionalInterface
    interface ItemListener {

        fun onClick(item: CharSequence)

    }

    fun setItems(items: Array<CharSequence>, listener: ItemListener) {

        removeAllSubItems()

        items.forEachIndexed { i, v ->

            addSubItem(i, v)

        }

        setDelegate {

            listener.onClick(items[it])

        }

    }

    fun show() {

        toggleSubMenu()

    }

}