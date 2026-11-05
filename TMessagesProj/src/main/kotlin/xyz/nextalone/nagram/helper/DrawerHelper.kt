package xyz.nextalone.nagram.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.AndroidUtilities.dp
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.DialogObject
import org.telegram.messenger.FileLoader
import org.telegram.messenger.ImageLoader
import org.telegram.messenger.LocaleController.getString
import org.telegram.messenger.MessagesController
import org.telegram.messenger.NotificationCenter
import org.telegram.messenger.R
import org.telegram.messenger.SharedConfig
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.TLRPC
import org.telegram.tgnet.tl.TL_stars
import org.telegram.ui.AccountFrozenAlert
import org.telegram.ui.ActionBar.DrawerLayoutContainer
import org.telegram.ui.ActionBar.INavigationLayout
import org.telegram.ui.ActionBar.MenuDrawable
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.CallLogActivity
import org.telegram.ui.ChannelCreateActivity
import org.telegram.ui.ChatActivity
import org.telegram.ui.Components.AnimatedEmojiDrawable
import org.telegram.ui.Components.LayoutHelper
import org.telegram.ui.Components.RecyclerListView
import org.telegram.ui.ContactsActivity
import org.telegram.ui.DialogsActivity
import org.telegram.ui.GroupCreateActivity
import org.telegram.ui.IUpdateLayout
import org.telegram.ui.LaunchActivity
import org.telegram.ui.LoginActivity
import org.telegram.ui.MainTabsActivity
import org.telegram.ui.ProfileActivity
import org.telegram.ui.ProxyListActivity
import org.telegram.ui.SelectAnimatedEmojiDialog
import org.telegram.ui.SettingsActivity
import org.telegram.ui.Stars.StarGiftSheet
import org.telegram.ui.Stars.StarsController
import org.telegram.ui.UpdateLayoutWrapper
import org.telegram.ui.ViewPagerActivity
import xyz.nextalone.nagram.NaConfig
import xyz.nextalone.nagram.ui.drawer.DrawerAddCell
import xyz.nextalone.nagram.ui.drawer.DrawerLayoutAdapter
import xyz.nextalone.nagram.ui.drawer.DrawerProfileCell
import xyz.nextalone.nagram.ui.drawer.DrawerProxyCell
import xyz.nextalone.nagram.ui.drawer.DrawerSwipeController
import xyz.nextalone.nagram.ui.drawer.DrawerUserCell
import xyz.nextalone.nagram.ui.drawer.SideMenultItemAnimator

@SuppressLint("StaticFieldLeak")
object DrawerHelper {

    private var adapter: DrawerLayoutAdapter? = null
    private var sideMenu: RecyclerListView? = null
    private var sideMenuContainer: FrameLayout? = null
    private var themeObserver: NotificationCenter.NotificationCenterDelegate? = null
    private var proxyObserver: NotificationCenter.NotificationCenterDelegate? = null
    private var updateLayout: IUpdateLayout? = null
    private var updateObserver: NotificationCenter.NotificationCenterDelegate? = null
    private var updateObserverAccount: Int = -1
    private var menuDrawableRef: MenuDrawable? = null
    private var statusPopup: SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow? = null
    var parentLayoutRef: INavigationLayout? = null

    /** Root fragment on startup: stock `addFragmentToStack` + navigation drawer wiring. */
    @JvmStatic
    fun setupMainFragment(activity: LaunchActivity, layout: INavigationLayout, dlc: DrawerLayoutContainer) {
        layout.addFragmentToStack(MainTabsActivity())
        if (NaConfig.classicDrawer.Bool()) setup(activity, dlc, layout)
    }

    /**
     * Wire the side drawer onto the activity's container once (idempotent), or
     * refresh its contents if already wired. Needed for login/relogin flows that
     * present the main fragment outside [setupMainFragment] — without this the
     * post-login main fragment has no drawer.
     */
    @JvmStatic
    fun ensureSetup(layout: INavigationLayout?) {
        if (!NaConfig.classicDrawer.Bool() || layout == null) return
        val dlc = layout.drawerLayoutContainer ?: return
        if (dlc.drawer == null) {
            setup(dlc.context, dlc, layout)
        } else {
            notifyDataChanged()
            rebindPerAccountObservers()
            refreshUpdateState()
        }
    }

    @JvmStatic
    fun teardown(layout: INavigationLayout?) {
        val dlc = layout?.drawerLayoutContainer ?: return
        val controller = dlc.drawer ?: return
        controller.detach()
        dlc.drawer = null
        adapter = null
        sideMenu = null
        sideMenuContainer = null
        updateLayout = null
        menuDrawableRef = null
        parentLayoutRef = null
    }

    fun refreshUpdateState() {
        if (!NaConfig.classicDrawer.Bool()) return
        updateLayout?.updateAppUpdateViews(UserConfig.selectedAccount, false)
        sideMenu?.let { applySideMenuBottomPadding(it) }
        refreshMenuButton(false)
    }

    @JvmStatic
    fun setup(
        context: Context,
        drawerLayoutContainer: DrawerLayoutContainer,
        actionBarLayout: INavigationLayout,
    ) {
        val sm = object : RecyclerListView(context) {
            override fun findChildViewUnder(x: Float, y: Float): View? {
                for (i in 0 until childCount) {
                    val child = getChildAt(i)
                    if (child is DrawerProfileCell &&
                        x >= child.left && x <= child.right &&
                        y >= child.top && y <= child.bottom
                    ) {
                        return child
                    }
                }
                return super.findChildViewUnder(x, y)
            }
        }
        sm.layoutManager = LinearLayoutManager(context)
        val itemAnimator = SideMenultItemAnimator(sm)
        val newAdapter = DrawerLayoutAdapter(context, itemAnimator, drawerLayoutContainer, ::applyProxyEnabled)
        adapter = newAdapter
        sideMenu = sm
        sm.itemAnimator = itemAnimator
        sm.adapter = newAdapter
        sm.isVerticalScrollBarEnabled = false
        sm.clipToPadding = false
        applySideMenuColors(sm)

        sm.setOnItemClickListener { view, position ->
            handleItemClick(position, view, drawerLayoutContainer, actionBarLayout, newAdapter)
        }

        attachAccountReorder(sm, newAdapter)

        val container = FrameLayout(context)
        sideMenuContainer = container
        container.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground))
        container.addView(
            sm, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        )

        val width = minOf(
            dp(320f),
            minOf(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - dp(56f)
        )

        val lp = FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT)
        parentLayoutRef = actionBarLayout
        val controller = DrawerSwipeController(drawerLayoutContainer, actionBarLayout)
        drawerLayoutContainer.drawer = controller
        controller.setDrawerLayout(container, sm, lp)
        controller.setAllowOpenDrawer(true, false)

        installThemeObserver()
        installProxyObserver()
        installUpdateLayout(context as? Activity, container, sm)
    }

    private fun attachAccountReorder(sm: RecyclerListView, adapter: DrawerLayoutAdapter) {
        val callback = object : ItemTouchHelper.Callback() {
            override fun isLongPressDragEnabled() = true

            override fun getMovementFlags(rv: RecyclerView, vh: RecyclerView.ViewHolder): Int {
                val dirs = if (vh.itemView is DrawerUserCell) ItemTouchHelper.UP or ItemTouchHelper.DOWN else 0
                return makeMovementFlags(dirs, 0)
            }

            override fun onMove(rv: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                if (target.itemView !is DrawerUserCell) return false
                return adapter.swapAccounts(source.adapterPosition, target.adapterPosition)
            }

            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {}

            override fun onSelectedChanged(vh: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(vh, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    sm.cancelClickRunnables(false)
                    vh?.itemView?.isPressed = true
                }
            }

            override fun clearView(rv: RecyclerView, vh: RecyclerView.ViewHolder) {
                super.clearView(rv, vh)
                vh.itemView.isPressed = false
                UserConfig.applyAccountsOrder(adapter.accountNumbers)
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(sm)
    }

    @Suppress("DEPRECATION")
    private fun installUpdateLayout(
        activity: Activity?,
        container: FrameLayout,
        sm: RecyclerListView,
    ) {
        if (activity == null) return
        val wrapper = UpdateLayoutWrapper(activity)
        container.addView(
            wrapper,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM,
            ),
        )
        ViewCompat.setOnApplyWindowInsetsListener(wrapper) { v, insets ->
            v.setPadding(0, 0, 0, insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)
            v.requestLayout()
            insets
        }
        wrapper.setPadding(0, 0, 0, AndroidUtilities.navigationBarHeight)

        val ul = ApplicationLoader.applicationLoaderInstance
            ?.takeUpdateLayout(activity, wrapper) ?: return
        updateLayout = ul
        applySideMenuBottomPadding(sm)
        ul.updateAppUpdateViews(UserConfig.selectedAccount, false)

        if (updateObserver == null) {
            val obs = NotificationCenter.NotificationCenterDelegate { id, _, args ->
                val current = updateLayout ?: return@NotificationCenterDelegate
                when (id) {
                    NotificationCenter.appUpdateAvailable -> {
                        val animated = args.getOrNull(0) as? Boolean ?: true
                        current.updateAppUpdateViews(UserConfig.selectedAccount, animated)
                        sideMenu?.let { applySideMenuBottomPadding(it) }
                        refreshMenuButton(animated)
                    }

                    NotificationCenter.appUpdateLoading -> {
                        current.updateFileProgress(null)
                        current.updateAppUpdateViews(UserConfig.selectedAccount, true)
                        refreshMenuButton(true)
                    }

                    NotificationCenter.fileLoadProgressChanged -> {
                        current.updateFileProgress(args)
                        refreshMenuButton(true)
                    }

                    NotificationCenter.fileLoaded, NotificationCenter.fileLoadFailed -> {
                        val name = args.getOrNull(0) as? String ?: return@NotificationCenterDelegate
                        val doc = SharedConfig.pendingAppUpdate?.document ?: return@NotificationCenterDelegate
                        if (name == FileLoader.getAttachFileName(doc)) {
                            current.updateAppUpdateViews(UserConfig.selectedAccount, true)
                            refreshMenuButton(true)
                        }
                    }
                }
            }
            updateObserver = obs
            val global = NotificationCenter.getGlobalInstance()
            global.addObserver(obs, NotificationCenter.appUpdateAvailable)
            global.addObserver(obs, NotificationCenter.appUpdateLoading)
        }
        rebindPerAccountObservers()
        refreshMenuButton(false)
    }

    @Suppress("DEPRECATION")
    private fun rebindPerAccountObservers() {
        val obs = updateObserver ?: return
        val newAccount = UserConfig.selectedAccount
        if (updateObserverAccount == newAccount) return
        if (updateObserverAccount != -1) {
            val prev = NotificationCenter.getInstance(updateObserverAccount)
            prev.removeObserver(obs, NotificationCenter.fileLoadProgressChanged)
            prev.removeObserver(obs, NotificationCenter.fileLoaded)
            prev.removeObserver(obs, NotificationCenter.fileLoadFailed)
        }
        updateObserverAccount = newAccount
        val acct = NotificationCenter.getInstance(newAccount)
        acct.addObserver(obs, NotificationCenter.fileLoadProgressChanged)
        acct.addObserver(obs, NotificationCenter.fileLoaded)
        acct.addObserver(obs, NotificationCenter.fileLoadFailed)
    }

    @JvmStatic
    fun refreshMenuButton(drawable: MenuDrawable?, animated: Boolean) {
        if (drawable != null) menuDrawableRef = drawable
        val d = drawable ?: menuDrawableRef ?: return
        val type: Int
        val downloadProgress: Float
        if (SharedConfig.isAppUpdateAvailable()) {
            val doc = SharedConfig.pendingAppUpdate.document
            val fileName = FileLoader.getAttachFileName(doc)
            if (FileLoader.getInstance(UserConfig.selectedAccount).isLoadingFile(fileName)) {
                type = MenuDrawable.TYPE_UDPATE_DOWNLOADING
                downloadProgress = ImageLoader.getInstance().getFileProgress(fileName) ?: 0f
            } else {
                type = MenuDrawable.TYPE_UDPATE_AVAILABLE
                downloadProgress = 0f
            }
        } else {
            type = MenuDrawable.TYPE_DEFAULT
            downloadProgress = 0f
        }
        d.setType(type, animated)
        d.setUpdateDownloadProgress(downloadProgress, animated)
    }

    private fun refreshMenuButton(animated: Boolean) {
        refreshMenuButton(null, animated)
    }

    @Suppress("DEPRECATION")
    private fun applySideMenuBottomPadding(sm: RecyclerListView) {
        val extra = if (SharedConfig.isAppUpdateAvailable()) {
            dp(44f) + AndroidUtilities.navigationBarHeight
        } else 0
        sm.setPadding(sm.paddingLeft, sm.paddingTop, sm.paddingRight, extra)
    }

    private fun applySideMenuColors(sm: RecyclerListView) {
        val bg = Theme.getColor(Theme.key_chats_menuBackground)
        sm.setBackgroundColor(bg)
        sm.setGlowColor(bg)
        sm.setListSelectorColor(Theme.getColor(Theme.key_listSelector))
    }

    @Suppress("DEPRECATION")
    private fun installThemeObserver() {
        if (themeObserver != null) return
        val obs = NotificationCenter.NotificationCenterDelegate { id, _, _ ->
            if (id == NotificationCenter.didSetNewTheme || id == NotificationCenter.reloadInterface) {
                refreshTheme()
            }
        }
        themeObserver = obs
        NotificationCenter.getGlobalInstance().addObserver(obs, NotificationCenter.didSetNewTheme)
        NotificationCenter.getGlobalInstance().addObserver(obs, NotificationCenter.reloadInterface)
    }

    @Suppress("DEPRECATION")
    private fun installProxyObserver() {
        if (proxyObserver != null) return
        val obs = NotificationCenter.NotificationCenterDelegate { id, _, _ ->
            if (id == NotificationCenter.proxySettingsChanged) {
                adapter?.notifyDataSetChanged()
            }
        }
        proxyObserver = obs
        NotificationCenter.getGlobalInstance().addObserver(obs, NotificationCenter.proxySettingsChanged)
    }

    private fun applyProxyEnabled(enabled: Boolean) {
        val proxy = if (enabled) SharedConfig.currentProxy else null
        MessagesController.getGlobalMainSettings().edit()
            .putBoolean("proxy_enabled", enabled && proxy != null)
            .apply()
        if (proxy != null) {
            ConnectionsManager.setProxySettings(true, proxy.settings)
        } else {
            ConnectionsManager.setProxySettings(false, null)
        }
        NotificationCenter.getGlobalInstance()
            .postNotificationName(NotificationCenter.proxySettingsChanged)
    }

    private fun refreshTheme() {
        sideMenuContainer?.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground))
        sideMenu?.let { applySideMenuColors(it) }
        adapter?.notifyDataSetChanged()
        adapter?.profileCell?.updateSunDrawable(Theme.isCurrentThemeDark())
    }

    fun dismissStatusPopup() {
        statusPopup?.dismiss()
        statusPopup = null
    }

    fun showSelectStatusDialog(cell: DrawerProfileCell, drawerLayoutContainer: DrawerLayoutContainer) {
        if (statusPopup != null || SharedConfig.appLocked) return
        val fragment = parentLayoutRef?.lastFragment ?: return
        val account = UserConfig.selectedAccount
        val user = UserConfig.getInstance(account).getCurrentUser() ?: return
        val scrimDrawable = cell.status
        scrimDrawable.play()
        cell.getEmojiStatusLocation(AndroidUtilities.rectTmp2)
        val yoff = -(cell.height - AndroidUtilities.rectTmp2.centerY()) - dp(16f)
        var xoff = AndroidUtilities.rectTmp2.centerX()
        (cell.context as? Activity)?.window?.decorView?.let { decorView ->
            ViewCompat.getRootWindowInsets(decorView)?.let { insets ->
                xoff -= insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars()).left
            }
        }
        val popupLayout = object : SelectAnimatedEmojiDialog(
            fragment,
            cell.context,
            true,
            xoff,
            SelectAnimatedEmojiDialog.TYPE_EMOJI_STATUS,
            null,
        ) {
            override fun onSettings() {
                drawerLayoutContainer.drawer?.closeDrawer(false)
            }

            override fun willApplyEmoji(
                view: View?,
                documentId: Long?,
                document: TLRPC.Document?,
                gift: TL_stars.TL_starGiftUnique?,
                until: Int?,
            ): Boolean {
                if (gift != null) {
                    val savedStarGift = StarsController.getInstance(account).findUserStarGift(gift.id)
                    return savedStarGift == null || MessagesController.getGlobalMainSettings().getInt("statusgiftpage", 0) >= 2
                }
                return true
            }

            override fun onEmojiSelected(
                emojiView: View?,
                documentId: Long?,
                document: TLRPC.Document?,
                gift: TL_stars.TL_starGiftUnique?,
                until: Int?,
            ) {
                val emojiStatus: TLRPC.EmojiStatus
                if (documentId == null) {
                    emojiStatus = TLRPC.TL_emojiStatusEmpty()
                } else if (gift != null) {
                    val savedStarGift = StarsController.getInstance(account).findUserStarGift(gift.id)
                    if (savedStarGift != null && MessagesController.getGlobalMainSettings().getInt("statusgiftpage", 0) < 2) {
                        MessagesController.getGlobalMainSettings().edit()
                            .putInt("statusgiftpage", MessagesController.getGlobalMainSettings().getInt("statusgiftpage", 0) + 1)
                            .apply()
                        StarGiftSheet(cell.context, account, UserConfig.getInstance(account).getClientUserId(), null)
                            .set(savedStarGift, null)
                            .setupWearPage()
                            .show()
                        dismissStatusPopup()
                        return
                    }
                    val status = TLRPC.TL_inputEmojiStatusCollectible()
                    status.collectible_id = gift.id
                    if (until != null) {
                        status.flags = status.flags or 1
                        status.until = until
                    }
                    emojiStatus = status
                } else {
                    val status = TLRPC.TL_emojiStatus()
                    status.document_id = documentId
                    if (until != null) {
                        status.flags = status.flags or 1
                        status.until = until
                    }
                    emojiStatus = status
                }
                MessagesController.getInstance(account).updateEmojiStatus(emojiStatus, gift)
                dismissStatusPopup()
            }
        }
        if (DialogObject.getEmojiStatusUntil(user.emoji_status) > 0) {
            popupLayout.setExpireDateHint(DialogObject.getEmojiStatusUntil(user.emoji_status))
        }
        val giftId = (user.emoji_status as? TLRPC.TL_emojiStatusCollectible)?.collectible_id
        popupLayout.setSelected(giftId ?: (scrimDrawable.drawable as? AnimatedEmojiDrawable)?.documentId)
        popupLayout.setSaveState(2)
        popupLayout.setScrimDrawable(scrimDrawable, cell.getEmojiStatusDrawableParent())
        val popup = object : SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow(
            popupLayout,
            LayoutHelper.WRAP_CONTENT,
            LayoutHelper.WRAP_CONTENT,
        ) {
            override fun dismiss() {
                super.dismiss()
                statusPopup = null
            }
        }
        statusPopup = popup
        popup.showAsDropDown(cell, 0, yoff, Gravity.TOP)
        popup.dimBehind()
    }

    @JvmStatic
    fun handleItemClick(
        position: Int,
        view: View,
        drawerLayoutContainer: DrawerLayoutContainer,
        nav: INavigationLayout,
        adapter: DrawerLayoutAdapter,
    ) {
        val account = UserConfig.selectedAccount
        val close = { drawerLayoutContainer.drawer?.closeDrawer(false) }

        if (position == 0) {
            return
        }

        if (view is DrawerUserCell) {
            LaunchActivity.instance?.switchToAccount(view.accountNumber, true)
            close()
            return
        }

        if (view is DrawerAddCell) {
            val availableAccount = (UserConfig.MAX_ACCOUNT_COUNT - 1 downTo 0)
                .firstOrNull { !UserConfig.getInstance(it).isClientActivated }
            if (availableAccount != null) {
                nav.presentFragment(LoginActivity(availableAccount))
                close()
            }
            return
        }

        adapter.getAttachMenuBot(position)?.let { bot ->
            val activity = LaunchActivity.instance ?: return
            LaunchActivity.showAttachMenuBot(activity, account, bot, null, true)
            close()
            return
        }

        val itemId = adapter.getId(position)

        when (itemId) {
            DrawerLayoutAdapter.ITEM_MY_PROFILE -> {
                openMyProfile(drawerLayoutContainer)
            }

            DrawerLayoutAdapter.ITEM_NEW_GROUP -> {
                if (MessagesController.getInstance(account).isFrozen) {
                    AccountFrozenAlert.show(account)
                } else {
                    nav.presentFragment(GroupCreateActivity(Bundle()))
                    close()
                }
            }

            DrawerLayoutAdapter.ITEM_NEW_CHANNEL -> {
                if (MessagesController.getInstance(account).isFrozen) {
                    AccountFrozenAlert.show(account)
                } else {
                    val preferences = MessagesController.getGlobalMainSettings()
                    nav.presentFragment(ChannelCreateActivity(Bundle().apply { putInt("step", 0) }))
                    close()
                }
            }

            DrawerLayoutAdapter.ITEM_NEW_MESSAGE -> {
                val args = Bundle().apply { putBoolean("destroyAfterSelect", true) }
                nav.presentFragment(ContactsActivity(args))
                close()
            }

            DrawerLayoutAdapter.ITEM_CONTACTS -> {
                val args = Bundle()
                args.putBoolean("needPhonebook", true)
                nav.presentFragment(ContactsActivity(args))
                close()
            }

            DrawerLayoutAdapter.ITEM_CALLS -> {
                nav.presentFragment(CallLogActivity())
                close()
            }

            DrawerLayoutAdapter.ITEM_SAVED_MESSAGES -> {
                val args = Bundle()
                args.putLong("user_id", UserConfig.getInstance(account).getClientUserId())
                nav.presentFragment(ChatActivity(args))
                close()
            }

            DrawerLayoutAdapter.ITEM_SETTINGS -> {
                nav.presentFragment(SettingsActivity())
                close()
            }

            DrawerLayoutAdapter.ITEM_PROXY -> {
                nav.presentFragment(ProxyListActivity())
                close()
            }

            else -> close()
        }
    }

    fun openMyProfile(drawerLayoutContainer: DrawerLayoutContainer) {
        val args = Bundle()
        args.putLong("user_id", UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId())
        args.putBoolean("my_profile", true)
        parentLayoutRef?.presentFragment(ProfileActivity(args))
        drawerLayoutContainer.drawer?.closeDrawer(false)
    }

    @JvmStatic
    fun notifyDataChanged() {
        adapter?.notifyDataSetChanged()
    }

    @JvmStatic
    fun toggleDrawer(parentLayout: INavigationLayout?): Boolean {
        val controller = parentLayout?.drawerLayoutContainer?.drawer ?: return false
        if (controller.isDrawerOpened) controller.closeDrawer(false) else controller.openDrawer(false)
        return true
    }
}
