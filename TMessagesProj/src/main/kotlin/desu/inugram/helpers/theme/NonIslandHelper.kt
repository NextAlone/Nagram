package desu.inugram.helpers.theme

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import desu.inugram.ui.BlurBehindHelper
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.AndroidUtilities.dp
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.INavigationLayout
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Components.ChatActivityEnterView
import org.telegram.ui.Components.ChatActivityTopPanelLayout
import org.telegram.ui.Components.FilterTabsView
import org.telegram.ui.Components.FragmentSearchField
import org.telegram.ui.Components.MentionsContainerView
import org.telegram.ui.Components.RecyclerListView
import org.telegram.ui.Components.SizeNotifierFrameLayout
import org.telegram.ui.DialogsActivity
import org.telegram.ui.SearchTabsAndFiltersLayout
import xyz.nextalone.nagram.NaConfig
import kotlin.math.max

object NonIslandHelper {
    @JvmStatic
    fun foldersBar(): Boolean = NaConfig.nonIslandFoldersBar.Bool()

    @JvmStatic
    fun sharedMediaTabs(): Boolean = NaConfig.nonIslandSharedMediaTabs.Bool()

    @JvmStatic
    fun globalSearch(): Boolean = NaConfig.nonIslandGlobalSearch.Bool()

    @JvmStatic
    fun chatElements(): Boolean = NaConfig.nonIslandChatElements.Bool()

    @JvmStatic
    fun chatInputRowHeight(): Int = if (chatElements()) 48 else 44

    @JvmStatic
    fun chatSendIcon(): Int = if (chatElements()) R.drawable.ic_send else R.drawable.send_plane_24

    @JvmStatic
    fun createInputButtonSelector(color: Int): Drawable =
        if (chatElements()) Theme.createSelectorDrawable(color)
        else Theme.createInsetRoundRectDrawable(color, dp(19f).toFloat(), dp(1f), dp(3f))

    @JvmStatic
    fun applySendButtonRipple(button: View, frameWidthDp: Int, color: Int) {
        if (!chatElements()) return
        val box = dp(ChatActivityEnterView.DEFAULT_HEIGHT.toFloat())
        val inset = dp(3f)
        button.background = Theme.createInsetRoundRectDrawable(
            color,
            (box - inset * 2) / 2f,
            dp(frameWidthDp.toFloat()) - box + inset, inset, inset, inset,
        )
    }

    @JvmStatic
    fun syncChatInputRowHeight() {
        val height = chatInputRowHeight()
        val delta = (height - 44) / 2
        ChatActivityEnterView.DEFAULT_HEIGHT = height
        ChatActivityEnterView.inu_FIELD_PADDING_TOP = 9 + delta
        ChatActivityEnterView.inu_FIELD_PADDING_BOTTOM = 10 + delta
        ChatActivityEnterView.inu_ICON_PADDING = 7.5f + delta
    }

    // ChatActivity.java
    @JvmStatic
    fun needChatLightNavBar(
        inputBubbleHeight: Float,
        resourcesProvider: Theme.ResourcesProvider?,
    ): Boolean? {
        if (!chatElements()) return null
        if (inputBubbleHeight <= 0f) return null
        val color = Theme.getColor(Theme.key_chat_messagePanelBackground, resourcesProvider)
        return AndroidUtilities.computePerceivedBrightness(color) <= 0.9f
    }

    @JvmStatic
    fun needChatLightStatusBar(resourcesProvider: Theme.ResourcesProvider?): Boolean? {
        if (!chatElements()) return null
        val color = Theme.getColor(Theme.key_chat_topPanelBackground, resourcesProvider)
        return AndroidUtilities.computePerceivedBrightness(color) > 0.721f
    }

    // ChatAttachAlert.java
    const val ATTACH_TAB_SHADOW_DP = 3f

    @JvmStatic
    fun applyChatAttachTabBar(
        wrapper: FrameLayout,
        recyclerView: RecyclerListView,
    ) {
        if (!chatElements()) return
        wrapper.background = null
        wrapper.clipChildren = false
        recyclerView.clipToOutline = false
        recyclerView.outlineProvider = null
        val innerPaddingTop = dp(4f)
        val innerPadding = dp(6f)
        recyclerView.setPadding(innerPadding, innerPaddingTop, innerPadding, AndroidUtilities.navigationBarHeight)
        recyclerView.clipToPadding = false
        val recyclerLp = recyclerView.layoutParams as? FrameLayout.LayoutParams ?: return
        val shadowH = dp(ATTACH_TAB_SHADOW_DP)
        recyclerLp.topMargin = shadowH
        val lp = wrapper.layoutParams as? FrameLayout.LayoutParams ?: return
        lp.height = shadowH + dp(48f) + innerPaddingTop + AndroidUtilities.navigationBarHeight
        // container's onLayout already offsets BOTTOM children by navigationBarHeight,
        // so use negative bottomMargin to extend the wrapper into the nav bar area
        lp.bottomMargin = -AndroidUtilities.navigationBarHeight
    }

    // DialogsActivity.java
    const val FOLDERS_BAR_HEIGHT_DP = 44
    const val FOLDERS_BAR_OVERLAP_DP = 10
    const val FOLDERS_BAR_VISIBLE_HEIGHT_DP = FOLDERS_BAR_HEIGHT_DP - FOLDERS_BAR_OVERLAP_DP

    // the classic folders bar tucks under the classic search bar; with the island search
    // pill there is nothing to tuck under, so the overlap must not be applied
    @JvmStatic
    fun foldersBarOverlapDp(): Int = if (globalSearch()) FOLDERS_BAR_OVERLAP_DP else 0

    @JvmStatic
    fun foldersBarVisibleHeightDp(): Int = FOLDERS_BAR_HEIGHT_DP - foldersBarOverlapDp()

    @JvmStatic
    fun applyMd3TabsStyle(indicator: GradientDrawable, listView: RecyclerListView, selectorColor: Int) {
        val rad = AndroidUtilities.dpf2(3f)
        indicator.cornerRadii = floatArrayOf(rad, rad, rad, rad, 0f, 0f, 0f, 0f)
        listView.setSelectorType(42)
        listView.setSelectorRadius(16)
        listView.setSelectorDrawableColor(selectorColor)
    }

    @JvmStatic
    fun adjustMd3TabSelectorRect(rect: Rect) {
        val cy = rect.centerY()
        rect.top = cy - dp(16f)
        rect.bottom = cy + dp(16f)
        rect.inset(dp(2f), 0)
    }

    @JvmStatic
    fun setMd3TabIndicatorBounds(indicator: Drawable, indicatorX: Float, indicatorWidth: Float, height: Float, hideProgress: Float) {
        val centerX = indicatorX + indicatorWidth / 2f
        val width = max(dp(24f).toFloat(), indicatorWidth - dp(2f) * 2)
        val hideOffset = hideProgress * dp(3f)
        indicator.setBounds(
            (centerX - width / 2f).toInt(),
            (height - dp(3f) + hideOffset).toInt(),
            (centerX + width / 2f).toInt(),
            (height + hideOffset).toInt(),
        )
    }

    @JvmStatic
    fun applyFilterTabBar(tabsView: FilterTabsView, contentView: SizeNotifierFrameLayout) {
        if (!foldersBar()) return
        tabsView.setBlurredBackground(null)
        tabsView.background = null
        tabsView.inu_blurHelper = BlurBehindHelper(tabsView, contentView, Theme.key_windowBackgroundWhite, drawBottomDivider = true)
        tabsView.setPadding(0, 0, 0, 0)
        val lp = tabsView.layoutParams as? FrameLayout.LayoutParams ?: return
        lp.height = dp(FOLDERS_BAR_HEIGHT_DP.toFloat())
        lp.leftMargin = 0
        lp.rightMargin = 0
    }

    @JvmStatic
    fun applyGlobalSearchBar(field: FragmentSearchField, contentView: SizeNotifierFrameLayout) {
        if (!globalSearch()) return
        field.setupBlurredBackground(null)
        field.inu_blurHelper = BlurBehindHelper(field, contentView, Theme.key_windowBackgroundWhite)
        val lp = field.layoutParams as? FrameLayout.LayoutParams
        if (lp != null) {
            lp.leftMargin = 0
            lp.rightMargin = 0
        }
        updateGlobalSearchBarInsets(field)
    }

    // statusBarHeight is 0 at createView under the non-legacy inset system (only the live
    // window insets fill it in, after createView). The field's top padding pushes the input
    // below the status bar while the blur covers the area above it; the per-draw translationY
    // subtracts the same statusBarHeight. Baking a stale (0) value here leaves the bar
    // statusBarHeight too high once insets land, so re-apply on every inset change.
    @JvmStatic
    fun updateGlobalSearchBarInsets(field: FragmentSearchField) {
        if (!globalSearch()) return
        // extra padding to cover with blur the area above the search bar
        val extraTopPadding = AndroidUtilities.statusBarHeight + dp(8f)
        field.setPadding(0, extraTopPadding, 0, 0)
        val lp = field.layoutParams as? FrameLayout.LayoutParams ?: return
        val height = dp(DialogsActivity.SEARCH_FIELD_HEIGHT.toFloat()) + extraTopPadding
        if (lp.height != height) {
            lp.height = height
            field.requestLayout()
        }
    }

    @JvmStatic
    fun applyGlobalSearchTabs(layout: SearchTabsAndFiltersLayout, contentView: SizeNotifierFrameLayout) {
        if (!globalSearch()) return
        layout.setBlurredBackground(null)
        layout.background = null
        layout.inu_blurHelper = BlurBehindHelper(layout, contentView, Theme.key_windowBackgroundWhite, drawBottomDivider = true)
        layout.setPadding(0, 0, 0, 0)
        layout.translationY = -dp(FOLDERS_BAR_OVERLAP_DP.toFloat()).toFloat()
        val lp = layout.layoutParams as? FrameLayout.LayoutParams ?: return
        lp.height = dp(FOLDERS_BAR_HEIGHT_DP.toFloat())
        lp.leftMargin = 0
        lp.rightMargin = 0
    }

    // ChatActivity.java
    @JvmStatic
    fun applyChatTopPanelButton(view: TextView) {
        if (!chatElements()) return
        view.stateListAnimator = null // disable ScaleStateListAnimator in case there is one
        view.background = Theme.createSelectorDrawable(
            Theme.multAlpha(view.currentTextColor, 0.10f), Theme.RIPPLE_MASK_ALL
        )
    }

    @JvmStatic
    fun drawChatHeaderShadow(
        parentLayout: INavigationLayout,
        canvas: Canvas,
        topPanelLayout: ChatActivityTopPanelLayout?,
        mentionContainer: MentionsContainerView?,
        topicsTabsHeight: Float,
        actionBarBottom: Int,
    ) {
        if (!chatElements()) {
            // upstream no longer draws an action-bar header shadow in island mode
            return
        }

        val hasPanel = topPanelLayout != null && actionBarBottom > 0;
        val panelH = if (hasPanel) topPanelLayout.getAnimatedHeightWithPadding(0f).toInt() else 0;
        if (!(mentionContainer != null && mentionContainer.isVisible)) {
            // dont draw shadow if mention container is visible (todo: probably need a smoother way but im too lazy rn)
            parentLayout.drawHeaderShadow(canvas, actionBarBottom + topicsTabsHeight.toInt() + panelH);
        }
    }
}
