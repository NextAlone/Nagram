package desu.inugram.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.View
import org.telegram.messenger.AndroidUtilities
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Components.SizeNotifierFrameLayout

/**
 * Attachable blur-behind + shadow drawer for views that can't extend BlurredFrameLayout.
 *
 * Used primarily to visually match the legacy blur pipeline for tab bars in non-island mode.
 * Ideally we shouldn't need this at all and just use blur3 throughout, but that requires tweaking a bunch of stuff
 * and seems to be a lot more prone to break in future releases, so for the time being we use this crutch.
 */
class BlurBehindHelper(
    private val view: View,
    private val contentView: SizeNotifierFrameLayout,
    private val colorKey: Int = Theme.key_windowBackgroundWhite,
    private val isTop: Boolean = true,
    private val topShadowDp: Float = 0f,
    private val bottomShadowDp: Float = 0f,
    private val drawBottomDivider: Boolean = false,
) {
    private val rect = Rect()
    private val paint = Paint()
    private var topShadow: GradientDrawable? = null
    private var bottomShadow: GradientDrawable? = null

    init {
        contentView.blurBehindViews.add(view)
        if (topShadowDp > 0f) {
            topShadow = GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                intArrayOf(Theme.getColor(Theme.key_dialogShadowLine), 0)
            )
        }
        if (bottomShadowDp > 0f) {
            bottomShadow = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(Theme.getColor(Theme.key_dialogShadowLine), 0)
            )
        }
    }

    @JvmOverloads
    fun draw(canvas: Canvas, heightOverride: Int = -1, alphaOverride: Int = -1) {
        val w = view.measuredWidth
        val h = if (heightOverride >= 0) heightOverride else view.measuredHeight
        val topShadowPx = AndroidUtilities.dp(topShadowDp)
        val bottomShadowPx = AndroidUtilities.dp(bottomShadowDp)

        topShadow?.let {
            it.setBounds(0, 0, w, topShadowPx)
            it.draw(canvas)
        }
        bottomShadow?.let {
            it.setBounds(0, h - bottomShadowPx, w, h)
            it.draw(canvas)
        }

        if (h <= topShadowPx + bottomShadowPx) return
        rect.set(0, topShadowPx, w, h - bottomShadowPx)
        paint.color = Theme.getColor(colorKey)
        paint.alpha = if (alphaOverride >= 0) alphaOverride else 255
        val y = computeY()
        if (y != null) {
            contentView.drawBlurRect(canvas, y, rect, paint, isTop)
        }
        if (drawBottomDivider) {
            canvas.drawRect(0f, (h - 1).toFloat(), w.toFloat(), h.toFloat(), Theme.dividerPaint)
        }
    }

    private fun computeY(): Float? {
        var y = 0f
        var cur: View = view
        while (cur !== contentView) {
            y += cur.y
            val parent = cur.parent
            if (parent is View) {
                cur = parent
            } else {
                return null
            }
        }
        return y
    }

    companion object {
        @JvmStatic
        @JvmOverloads
        fun create(
            view: View,
            contentView: SizeNotifierFrameLayout,
            colorKey: Int = Theme.key_actionBarDefault,
            isTop: Boolean = true,
            topShadowDp: Float = 0f,
            bottomShadowDp: Float = 0f,
        ): BlurBehindHelper {
            return BlurBehindHelper(view, contentView, colorKey, isTop, topShadowDp, bottomShadowDp)
        }
    }
}
