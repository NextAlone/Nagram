package desu.inugram.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.widget.LinearLayout
import androidx.core.graphics.ColorUtils
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Components.AnimatedFloat
import org.telegram.ui.Components.CubicBezierInterpolator
import org.telegram.ui.Components.SizeNotifierFrameLayout

/**
 * Shared media selection bar for non-island mode, filling the band of the tab strip it covers.
 *
 * Owns its own background, so [setBackgroundColor] is deliberately inert — stock drives it from the
 * action mode's theme descriptions and would otherwise fight the docked crossfade.
 */
@SuppressLint("ViewConstructor")
open class MediaActionModeLayout(
    context: Context,
    contentView: SizeNotifierFrameLayout?,
    private val dockable: Boolean,
    private val resourcesProvider: Theme.ResourcesProvider?,
) : LinearLayout(context) {

    private val blurBehindHelper = contentView?.let {
        BlurBehindHelper.create(this, it, Theme.key_windowBackgroundWhite)
    }
    private var docked = false
    private val dockedT = AnimatedFloat(this, 320L, CubicBezierInterpolator.EASE_OUT_QUINT)

    fun setDocked(docked: Boolean) {
        if (this.docked != docked) {
            this.docked = docked
            invalidate()
        }
    }

    open fun processColor(color: Int): Int = color

    override fun setBackgroundColor(color: Int) {}

    override fun dispatchDraw(canvas: Canvas) {
        val t = if (dockable) dockedT.set(docked) else 1f
        if (t < 1f) {
            canvas.drawColor(getThemedColor(Theme.key_windowBackgroundGray))
        }
        if (t > 0f) {
            drawDockedBackground(canvas, t)
        }
        super.dispatchDraw(canvas)
    }

    private fun drawDockedBackground(canvas: Canvas, t: Float) {
        val alpha = (0xFF * t).toInt()
        if (blurBehindHelper != null) {
            blurBehindHelper.draw(canvas, alphaOverride = alpha)
        } else {
            canvas.drawColor(ColorUtils.setAlphaComponent(getThemedColor(Theme.key_windowBackgroundWhite), alpha))
        }
    }

    private fun getThemedColor(key: Int): Int = processColor(Theme.getColor(key, resourcesProvider))
}
