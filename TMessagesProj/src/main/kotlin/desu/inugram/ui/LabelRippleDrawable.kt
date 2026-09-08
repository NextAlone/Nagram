package desu.inugram.ui

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.widget.TextView
import kotlin.math.ceil
import kotlin.math.min

class LabelRippleDrawable(
    private val label: TextView,
    private val ripple: Drawable,
    private val horizontalPadding: Int,
    private val verticalInset: Int,
) : Drawable(), Drawable.Callback {
    init {
        ripple.callback = this
    }

    private fun layoutRipple() {
        val b = bounds
        val layout = label.layout
        val textWidth = if (layout != null && layout.lineCount > 0) {
            ceil(layout.getLineWidth(0).toDouble()).toInt()
        } else {
            b.width()
        }
        val pillWidth = min(b.width(), textWidth + horizontalPadding * 2)
        val cx = b.centerX()
        ripple.setBounds(cx - pillWidth / 2, b.top + verticalInset, cx + pillWidth / 2, b.bottom - verticalInset)
    }

    override fun onBoundsChange(bounds: Rect) = layoutRipple()

    override fun draw(canvas: Canvas) {
        layoutRipple()
        ripple.draw(canvas)
    }

    override fun isStateful(): Boolean = ripple.isStateful

    override fun onStateChange(state: IntArray): Boolean = ripple.setState(state)

    override fun setHotspot(x: Float, y: Float) = ripple.setHotspot(x, y)

    override fun jumpToCurrentState() = ripple.jumpToCurrentState()

    override fun setAlpha(alpha: Int) {
        ripple.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        ripple.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java", ReplaceWith("android.graphics.PixelFormat.TRANSLUCENT"))
    override fun getOpacity(): Int = ripple.opacity

    override fun invalidateDrawable(who: Drawable) = invalidateSelf()

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) = scheduleSelf(what, `when`)

    override fun unscheduleDrawable(who: Drawable, what: Runnable) = unscheduleSelf(what)
}
