package xyz.nextalone.nagram.ui.drawer

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.LocaleController
import org.telegram.messenger.MediaDataController
import org.telegram.messenger.MessagesController
import org.telegram.messenger.R
import org.telegram.messenger.SharedConfig
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.TLRPC
import org.telegram.ui.ActionBar.DrawerLayoutContainer
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Cells.DividerCell
import org.telegram.ui.Cells.EmptyCell
import org.telegram.ui.Components.RecyclerListView
import tw.nekomimi.nekogram.helpers.PasscodeHelper

class DrawerLayoutAdapter(
    private val mContext: Context,
    private val itemAnimator: SideMenultItemAnimator,
    private val mDrawerLayoutContainer: DrawerLayoutContainer,
    private val onProxySwitchToggled: ((Boolean) -> Unit)? = null,
) : RecyclerListView.SelectionAdapter() {

    private val items = ArrayList<Item?>(11)
    val accountNumbers = ArrayList<Int>()
    private var accountsShown: Boolean

    @JvmField
    var profileCell: DrawerProfileCell? = null

    init {
        accountsShown = UserConfig.getActivatedAccountsCount() > 1 &&
            MessagesController.getGlobalMainSettings().getBoolean("accountsShown", true)
        Theme.createCommonDialogResources(mContext)
        resetItems()
    }

    private fun getAccountRowsCount(): Int {
        var count = accountNumbers.size + 1
        if (canAddAccount()) count++
        return count
    }

    private fun canAddAccount(): Boolean =
        UserConfig.getActivatedAccountsCount() < UserConfig.MAX_ACCOUNT_COUNT

    override fun getItemCount(): Int {
        var count = items.size + 2
        if (accountsShown) count += getAccountRowsCount()
        return count
    }

    fun setAccountsShown(value: Boolean, animated: Boolean) {
        if (accountsShown == value || itemAnimator.isRunning) return
        accountsShown = value
        profileCell?.setAccountsShown(accountsShown, animated)
        MessagesController.getGlobalMainSettings().edit().putBoolean("accountsShown", accountsShown).apply()
        if (animated) {
            if (accountsShown) {
                notifyItemRangeInserted(2, getAccountRowsCount())
            } else {
                notifyItemRangeRemoved(2, getAccountRowsCount())
            }
        } else {
            notifyDataSetChanged()
        }
    }

    fun isAccountsShown(): Boolean = accountsShown

    fun toggleAccountsShown() {
        setAccountsShown(!accountsShown, true)
    }

    override fun notifyDataSetChanged() {
        resetItems()
        super.notifyDataSetChanged()
    }

    override fun isEnabled(holder: RecyclerView.ViewHolder): Boolean {
        val t = holder.itemViewType
        return t == 3 || t == 4 || t == 5 || t == 6 || t == 7
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = when (viewType) {
            0 -> DrawerProfileCell(mContext, mDrawerLayoutContainer, ::toggleAccountsShown).also { profileCell = it }
            2 -> DividerCell(mContext)
            3 -> DrawerActionCell(mContext)
            4 -> DrawerUserCell(mContext)
            5 -> DrawerAddCell(mContext)
            7 -> DrawerProxyCell(mContext)
            else -> EmptyCell(mContext, AndroidUtilities.dp(8f))
        }
        view.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return RecyclerListView.Holder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                val cell = holder.itemView as DrawerProfileCell
                cell.setUser(
                    MessagesController.getInstance(UserConfig.selectedAccount)
                        .getUser(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()),
                    accountsShown
                )
            }

            3 -> {
                val cell = holder.itemView as DrawerActionCell
                var pos = position - 2
                if (accountsShown) pos -= getAccountRowsCount()
                items[pos]?.bind(cell)
                cell.setPadding(0, 0, 0, 0)
            }

            4 -> {
                val cell = holder.itemView as DrawerUserCell
                cell.setAccount(accountNumbers[position - 2])
            }

            7 -> {
                val cell = holder.itemView as DrawerProxyCell
                var pos = position - 2
                if (accountsShown) pos -= getAccountRowsCount()
                val item = items[pos]
                if (item != null) cell.bind(item.text ?: "", item.icon)
                val hasProxies = SharedConfig.proxyList.isNotEmpty()
                cell.setSwitchVisible(hasProxies)
                cell.setChecked(SharedConfig.isProxyEnabled())
                cell.onSwitchToggled = onProxySwitchToggled
            }
        }
    }

    override fun getItemViewType(i: Int): Int {
        if (i == 0) return 0
        if (i == 1) return 1
        var idx = i - 2
        if (accountsShown) {
            if (idx < accountNumbers.size) return 4
            if (canAddAccount()) {
                if (idx == accountNumbers.size) return 5
                if (idx == accountNumbers.size + 1) return 2
            } else {
                if (idx == accountNumbers.size) return 2
            }
            idx -= getAccountRowsCount()
        }
        if (idx < 0 || idx >= items.size) return 2
        val id = items[idx]?.id ?: return 2
        if (id == ITEM_PROXY) return 7
        return 3
    }

    fun getId(position: Int): Int {
        var pos = position - 2
        if (accountsShown) pos -= getAccountRowsCount()
        if (pos < 0 || pos >= items.size) return -1
        return items[pos]?.id ?: -1
    }

    fun getAttachMenuBot(position: Int): TLRPC.TL_attachMenuBot? {
        var pos = position - 2
        if (accountsShown) pos -= getAccountRowsCount()
        if (pos < 0 || pos >= items.size) return null
        return items[pos]?.bot
    }

    fun swapAccounts(fromPosition: Int, toPosition: Int): Boolean {
        val from = fromPosition - 2
        val to = toPosition - 2
        if (from < 0 || to < 0 || from >= accountNumbers.size || to >= accountNumbers.size) return false
        val tmp = accountNumbers[from]
        accountNumbers[from] = accountNumbers[to]
        accountNumbers[to] = tmp
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    private fun resetItems() {
        accountNumbers.clear()
        for (a in 0 until UserConfig.MAX_ACCOUNT_COUNT) {
            if (UserConfig.getInstance(a).isClientActivated &&
                (a == UserConfig.selectedAccount || !PasscodeHelper.isAccountHidden(a))
            ) {
                accountNumbers.add(a)
            }
        }
        UserConfig.sortAccounts(accountNumbers)

        items.clear()
        if (!UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated) return

        items.add(Item(ITEM_MY_PROFILE, LocaleController.getString(R.string.MyProfile), R.drawable.left_status_profile))

        val menuBots = MediaDataController.getInstance(UserConfig.selectedAccount).getAttachMenuBots()
        if (menuBots?.bots != null) {
            for (bot in menuBots.bots) {
                if (bot.show_in_side_menu) items.add(Item(bot))
            }
        }

        addDivider()

        items.add(Item(ITEM_NEW_GROUP, LocaleController.getString(R.string.NewGroup), R.drawable.msg_groups))
        items.add(Item(ITEM_NEW_CHANNEL, LocaleController.getString(R.string.NewChannel), R.drawable.msg_channel))
        items.add(Item(ITEM_CONTACTS, LocaleController.getString(R.string.Contacts), R.drawable.msg_contacts))
        items.add(Item(ITEM_CALLS, LocaleController.getString(R.string.Calls), R.drawable.msg_calls))
        items.add(Item(ITEM_SAVED_MESSAGES, LocaleController.getString(R.string.SavedMessages), R.drawable.msg_saved))

        items.add(Item(ITEM_SETTINGS, LocaleController.getString(R.string.Settings), R.drawable.msg_settings_old))

        addDivider()

        items.add(Item(ITEM_PROXY, LocaleController.getString(R.string.ProxySettings), R.drawable.outline_shield_check))

        if (items.isNotEmpty() && items.last() == null) items.removeAt(items.lastIndex)
    }

    private fun addDivider() {
        if (items.isNotEmpty() && items.last() != null) items.add(null)
    }

    companion object {
        const val ITEM_MY_PROFILE = 16
        const val ITEM_NEW_GROUP = 2
        const val ITEM_NEW_MESSAGE = 17
        const val ITEM_CONTACTS = 6
        const val ITEM_CALLS = 10
        const val ITEM_SAVED_MESSAGES = 11
        const val ITEM_SETTINGS = 8
        const val ITEM_PROXY = 9
        const val ITEM_NEW_CHANNEL = 20
    }

    class Item private constructor(
        val id: Int,
        val text: CharSequence?,
        val icon: Int,
        val bot: TLRPC.TL_attachMenuBot?,
    ) {
        constructor(id: Int, text: CharSequence, icon: Int) : this(id, text, icon, null)
        constructor(bot: TLRPC.TL_attachMenuBot) : this((100 + (bot.bot_id shr 16)).toInt(), null, 0, bot)

        fun bind(actionCell: DrawerActionCell) {
            if (bot != null) actionCell.setBot(bot)
            else actionCell.setTextAndIcon(id, text!!, icon)
        }
    }
}
