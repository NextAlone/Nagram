package xyz.nextalone.nagram.ui.drawer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Property
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewTreeObserver
import android.view.accessibility.AccessibilityEvent
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.BaseFragment
import org.telegram.ui.ActionBar.DrawerLayoutContainer
import org.telegram.ui.ActionBar.INavigationLayout
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.DialogsActivity
import org.telegram.ui.LaunchActivity
import org.telegram.ui.MainTabsActivity
import org.telegram.ui.ViewPagerActivity
import xyz.nextalone.nagram.NaConfig
import xyz.nextalone.nagram.helper.DrawerHelper

class DrawerSwipeController(private val host: DrawerLayoutContainer, private val parentLayout: INavigationLayout) {

    private var drawerLayout: FrameLayout? = null
    private var drawerListView: View? = null
    private var drawerPosition: Float = 0f
    private var drawerOpened: Boolean = false
    private var allowOpenDrawer: Boolean = false
    private var maybeStartTracking: Boolean = false
    private var startedTracking: Boolean = false
    private var startedTrackingX: Int = 0
    private var startedTrackingY: Int = 0
    private var startedTrackingPointerId: Int = 0
    private var velocityTracker: VelocityTracker? = null
    private var beginTrackingSent: Boolean = false
    private var currentAnimation: AnimatorSet? = null
    private var scrimOpacity: Float = 0f
    private val scrimPaint = Paint()
    private var shadowLeft: Drawable? = null
    private val exclusionRect = Rect()
    private val exclusionRects = listOf(exclusionRect)

    private val preDrawListener = ViewTreeObserver.OnPreDrawListener {
        updateGestureExclusion()
        true
    }

    private val attachListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            v.viewTreeObserver.addOnPreDrawListener(preDrawListener)
        }

        override fun onViewDetachedFromWindow(v: View) {
            val observer = v.viewTreeObserver
            if (observer.isAlive) observer.removeOnPreDrawListener(preDrawListener)
        }
    }

    val isDrawerOpened: Boolean get() = drawerOpened

    val isDrawerVisible: Boolean get() = drawerPosition > 0f

    val drawerWidth: Int get() = drawerLayout?.measuredWidth ?: 0

    val canOpenFromBackGesture: Boolean
        get() = allowOpenDrawer && !drawerOpened && drawerPosition == 0f &&
            drawerWidth > 0 && canTrackGesture() &&
            (getVisibleFragment() as? DialogsActivity)?.actionBar?.isActionModeShowed != true

    fun dragDrawerTo(value: Float) {
        cancelCurrentAnimation()
        setDrawerPosition(value)
    }

    fun setDrawerLayout(layout: FrameLayout, listView: View, lp: FrameLayout.LayoutParams) {
        drawerLayout = layout
        drawerListView = listView
        host.addView(drawerLayout, lp)
        drawerLayout!!.visibility = View.INVISIBLE
        if (shadowLeft == null) {
            try {
                shadowLeft = ContextCompat.getDrawable(host.context, R.drawable.header_shadow)
            } catch (_: Exception) {
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            host.addOnAttachStateChangeListener(attachListener)
            if (host.isAttachedToWindow) host.viewTreeObserver.addOnPreDrawListener(preDrawListener)
        }
    }

    fun detach() {
        cancelCurrentAnimation()
        setAllowOpenDrawer(false, false)
        host.removeView(drawerLayout)
        drawerLayout = null
        drawerListView = null
        velocityTracker?.recycle()
        velocityTracker = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            host.removeOnAttachStateChangeListener(attachListener)
            host.viewTreeObserver.removeOnPreDrawListener(preDrawListener)
        }
    }

    fun isDrawerChild(child: View?): Boolean = child != null && child == drawerLayout

    fun setAllowOpenDrawer(value: Boolean, animated: Boolean) {
        allowOpenDrawer = value
        if (!allowOpenDrawer && drawerPosition != 0f) {
            if (!animated) {
                DrawerHelper.dismissStatusPopup()
                setDrawerPosition(0f)
                onDrawerAnimationEnd(false)
            } else closeDrawer(true)
        }
        updateGestureExclusion()
    }

    @Keep
    fun setDrawerPosition(value: Float) {
        val layout = drawerLayout ?: return
        if (drawerPosition == 0f && value > 0f) DrawerHelper.notifyDataChanged()
        drawerPosition = maxOf(0f, minOf(value, layout.measuredWidth.toFloat()))
        layout.translationX = drawerPosition
        if (drawerPosition > 0 && drawerListView != null && drawerListView!!.visibility != View.VISIBLE) {
            drawerListView!!.visibility = View.VISIBLE
        }
        layout.visibility = if (drawerPosition > 0) View.VISIBLE else View.INVISIBLE
        scrimOpacity = drawerPosition / layout.measuredWidth.toFloat()
        host.invalidate()
    }

    fun openDrawer(fast: Boolean) {
        val layout = drawerLayout ?: return
        if (!allowOpenDrawer) return
        cancelCurrentAnimation()
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, DRAWER_POSITION, layout.measuredWidth.toFloat()))
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.duration = if (fast) maxOf((200f / layout.measuredWidth * (layout.measuredWidth - drawerPosition)).toInt(), 50).toLong() else 250L
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(a: Animator) {
                onDrawerAnimationEnd(true)
            }
        })
        animatorSet.start()
        currentAnimation = animatorSet
    }

    fun closeDrawer(fast: Boolean) {
        val layout = drawerLayout ?: return
        DrawerHelper.dismissStatusPopup()
        cancelCurrentAnimation()
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, DRAWER_POSITION, 0f))
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.duration = if (fast) maxOf((200f / layout.measuredWidth * drawerPosition).toInt(), 50).toLong() else 250L
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(a: Animator) {
                onDrawerAnimationEnd(false)
            }
        })
        animatorSet.start()
        currentAnimation = animatorSet
    }

    private fun cancelCurrentAnimation() {
        currentAnimation?.cancel()
        currentAnimation = null
    }

    private fun onDrawerAnimationEnd(opened: Boolean) {
        startedTracking = false
        currentAnimation = null
        drawerOpened = opened
        host.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
        syncStatusBar(opened)
        updateGestureExclusion()
    }

    private fun syncStatusBar(opened: Boolean) {
        val activity = host.context as? LaunchActivity ?: return
        if (opened) {
            val bgKey = if (Theme.hasThemeKey(Theme.key_chats_menuTopBackground) &&
                Theme.getColor(Theme.key_chats_menuTopBackground) != 0
            ) Theme.key_chats_menuTopBackground else Theme.key_chats_menuTopBackgroundCats
            AndroidUtilities.setLightStatusBar(
                activity.window,
                ColorUtils.calculateLuminance(Theme.getColor(bgKey)) > 0.7,
            )
        } else {
            activity.checkSystemBarColors(false, true, false)
        }
    }

    private fun canTrackGesture(): Boolean {
        if (drawerOpened || drawerPosition > 0) return true
        if (NaConfig.sidebarSettingsActivity.Bool()) return false
        if (parentLayout.fragmentStack.size != 1) return false
        val top = getVisibleFragment()
        if (top?.actionBar?.isSearchFieldVisible == true) return false
        val dialogs = top as? DialogsActivity ?: return false
        if (!dialogs.isAllChatsTab) return false
        return true
    }

    private fun getVisibleFragment(): BaseFragment? {
        val top = parentLayout.lastFragment
        return if (top is ViewPagerActivity) top.currentVisibleFragment else top
    }

    private fun tabsOwnHorizontalSwipe(): Boolean {
        if (NaConfig.sidebarSettingsActivity.Bool()) return true
        val mainTabs = parentLayout.lastFragment as? MainTabsActivity
        if (mainTabs != null && mainTabs.currentVisibleFragment !is DialogsActivity) return true
        val top = getVisibleFragment() as? DialogsActivity ?: return false
        return !top.isAllChatsTab
    }

    @Suppress("DEPRECATION")
    private fun updateGestureExclusion() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        val height = host.height
        if (!allowOpenDrawer || height <= 0 || !canTrackGesture()) {
            host.systemGestureExclusionRects = emptyList()
            return
        }
        val bottom = maxOf(0, height - AndroidUtilities.navigationBarHeight)
        exclusionRect.set(
            0,
            maxOf(0, bottom - AndroidUtilities.dp(EXCLUSION_HEIGHT_DP.toFloat())),
            AndroidUtilities.dp(EDGE_SAFE_ZONE_DP.toFloat()),
            bottom,
        )
        host.systemGestureExclusionRects = exclusionRects
    }

    fun onTouchEvent(ev: MotionEvent?): Boolean {
        val layout = drawerLayout
        if (layout == null || parentLayout.checkTransitionAnimation()) {
            if (startedTracking || maybeStartTracking) {
                startedTracking = false
                maybeStartTracking = false
                velocityTracker?.recycle()
                velocityTracker = null
            }
            return false
        }
        if (ev != null && ev.action == MotionEvent.ACTION_DOWN && !startedTracking && maybeStartTracking) {
            maybeStartTracking = false
            velocityTracker?.recycle()
            velocityTracker = null
        }
        if (drawerOpened && ev != null && ev.x > drawerPosition && !startedTracking) {
            if (ev.action == MotionEvent.ACTION_UP) closeDrawer(false)
            return true
        }
        if (allowOpenDrawer && canTrackGesture()) {
            if (ev != null && (ev.action == MotionEvent.ACTION_DOWN || ev.action == MotionEvent.ACTION_MOVE)
                && !startedTracking && !maybeStartTracking
            ) {
                startedTrackingX = ev.x.toInt()
                startedTrackingY = ev.y.toInt()
                startedTrackingPointerId = ev.getPointerId(0)
                maybeStartTracking = true
                cancelCurrentAnimation()
                if (velocityTracker == null) velocityTracker = VelocityTracker.obtain()
                else velocityTracker!!.clear()
                velocityTracker!!.addMovement(ev)
            } else if (ev != null && ev.action == MotionEvent.ACTION_MOVE
                && ev.getPointerId(0) == startedTrackingPointerId
            ) {
                if (velocityTracker == null) velocityTracker = VelocityTracker.obtain()
                velocityTracker!!.addMovement(ev)
                val dx = ev.x - startedTrackingX
                val dy = Math.abs(ev.y - startedTrackingY)
                val inEdgeZone = startedTrackingX <= AndroidUtilities.dp(EDGE_SAFE_ZONE_DP.toFloat())
                val openAngleOk = inEdgeZone || dx / 3f > dy
                val openSwipe = dx > 0 && openAngleOk && Math.abs(dx) >= AndroidUtilities.getPixelsInCM(0.2f, true)
                    && !tabsOwnHorizontalSwipe()
                val closeSwipe = drawerOpened && dx < 0 && Math.abs(dx) >= dy && Math.abs(dx) >= AndroidUtilities.getPixelsInCM(0.4f, true)
                if (maybeStartTracking && !startedTracking && (openSwipe || closeSwipe)) {
                    maybeStartTracking = false
                    startedTracking = true
                    beginTrackingSent = false
                    setDrawerPosition(drawerPosition + dx)
                    startedTrackingX = ev.x.toInt()
                    host.requestDisallowInterceptTouchEvent(true)
                } else if (startedTracking) {
                    if (!beginTrackingSent) {
                        beginTrackingSent = true
                    }
                    setDrawerPosition(drawerPosition + dx)
                    startedTrackingX = ev.x.toInt()
                }
            } else if (ev == null || ev.getPointerId(0) == startedTrackingPointerId
                && (ev.action == MotionEvent.ACTION_CANCEL
                    || ev.action == MotionEvent.ACTION_UP
                    || ev.action == MotionEvent.ACTION_POINTER_UP)
            ) {
                if (velocityTracker == null) velocityTracker = VelocityTracker.obtain()
                if (ev != null) velocityTracker!!.addMovement(ev)
                velocityTracker!!.computeCurrentVelocity(1000)
                if (startedTracking || (drawerPosition != 0f && drawerPosition != layout.measuredWidth.toFloat())) {
                    val velX = velocityTracker!!.xVelocity
                    val velY = velocityTracker!!.yVelocity
                    val back = drawerPosition < layout.measuredWidth / 2f
                        && (velX < 3500 || Math.abs(velX) < Math.abs(velY))
                        || velX < 0 && Math.abs(velX) >= 3500
                    if (!back) openDrawer(!drawerOpened && Math.abs(velX) >= 3500)
                    else closeDrawer(drawerOpened && Math.abs(velX) >= 3500)
                }
                startedTracking = false
                maybeStartTracking = false
                velocityTracker?.recycle()
                velocityTracker = null
            }
        } else {
            if (ev == null || ev.getPointerId(0) == startedTrackingPointerId
                && (ev.action == MotionEvent.ACTION_CANCEL
                    || ev.action == MotionEvent.ACTION_UP
                    || ev.action == MotionEvent.ACTION_POINTER_UP)
            ) {
                startedTracking = false
                maybeStartTracking = false
                velocityTracker?.recycle()
                velocityTracker = null
            }
        }
        return startedTracking
    }

    fun onParentDisallowIntercept() {
        if (startedTracking) return
        onTouchEvent(null)
    }

    fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        if (drawerLayout == null) {
            return host.superDrawChild(canvas, child, drawingTime)
        }
        val height = host.height
        val drawingContent = child != drawerLayout
        var lastVisibleChild = 0
        var clipLeft = 0
        val clipRight = host.width

        val restoreCount = canvas.save()
        if (drawingContent) {
            val childCount = host.childCount
            for (i in 0 until childCount) {
                val v = host.getChildAt(i)
                if (v.visibility == View.VISIBLE && v != drawerLayout) {
                    lastVisibleChild = i
                }
                if (v == child || v.visibility != View.VISIBLE || v != drawerLayout || v.height < height) {
                    continue
                }
                val vright = Math.ceil(v.x.toDouble()).toInt() + v.measuredWidth
                if (vright > clipLeft) clipLeft = vright
            }
            if (clipLeft != 0) {
                canvas.clipRect(clipLeft - AndroidUtilities.dp(1f), 0, clipRight, host.height)
            }
        }
        val result = host.superDrawChild(canvas, child, drawingTime)
        canvas.restoreToCount(restoreCount)

        if (scrimOpacity > 0 && drawingContent) {
            if (host.indexOfChild(child) == lastVisibleChild) {
                scrimPaint.color = (0x99 * scrimOpacity).toInt() shl 24
                canvas.drawRect(clipLeft.toFloat(), 0f, clipRight.toFloat(), host.height.toFloat(), scrimPaint)
            }
        } else if (shadowLeft != null && drawerPosition > 0) {
            val alpha = maxOf(0f, minOf(drawerPosition / AndroidUtilities.dp(20f).toFloat(), 1f))
            if (alpha != 0f) {
                shadowLeft!!.setBounds(drawerPosition.toInt(), child.top, drawerPosition.toInt() + shadowLeft!!.intrinsicWidth, child.bottom)
                shadowLeft!!.alpha = (0xff * alpha).toInt()
                shadowLeft!!.draw(canvas)
            }
        }
        return result
    }

    companion object {
        private const val EDGE_SAFE_ZONE_DP = 25
        private const val EXCLUSION_HEIGHT_DP = 200

        @JvmField
        val DRAWER_POSITION: Property<DrawerSwipeController, Float> =
            object : Property<DrawerSwipeController, Float>(Float::class.java, "drawerPosition") {
                override fun get(o: DrawerSwipeController): Float = o.drawerPosition
                override fun set(o: DrawerSwipeController, v: Float) {
                    o.setDrawerPosition(v)
                }
            }
    }
}
