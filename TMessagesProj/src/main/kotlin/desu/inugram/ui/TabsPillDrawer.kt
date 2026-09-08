package desu.inugram.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.Gravity
import org.telegram.messenger.AndroidUtilities.dp
import org.telegram.messenger.AndroidUtilities.lerp
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Components.AnimatedFloat
import org.telegram.ui.Components.CubicBezierInterpolator
import org.telegram.ui.Components.LayoutHelper
import org.telegram.ui.Components.ScrollSlidingTextTabStrip
import org.telegram.ui.Components.SizeNotifierFrameLayout

class TabsPillDrawer(
    private val strip: ScrollSlidingTextTabStrip,
    private val blurBehindHelper: BlurBehindHelper,
) {
    private var docked = false
    private val dockedT = AnimatedFloat(strip, 320L, CubicBezierInterpolator.EASE_OUT_QUINT)
    private val bgRect = RectF()
    private val bgPath = Path()
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun setDocked(docked: Boolean) {
        if (this.docked != docked) {
            this.docked = docked
            strip.invalidate()
        }
    }

    fun getDockedProgress(): Float = dockedT.get()

    fun drawBackgroundAndClip(canvas: Canvas, color: Int) {
        val t = dockedT.set(docked)
        val w = strip.measuredWidth.toFloat()
        val h = strip.measuredHeight.toFloat()
        val cw = strip.tabsContainer.measuredWidth.toFloat()
        val margin = dp(12f).toFloat()
        var cx = (w - cw) / 2f
        if (cx < margin) cx = 0f
        strip.tabsContainer.translationX = lerp(cx, 0f, t)
        bgRect.set(
            lerp(maxOf(margin, cx), 0f, t),
            strip.paddingTop.toFloat(),
            lerp(minOf(w - margin, cx + cw), w, t),
            h - strip.paddingBottom + t * dp(4f),
        )
        val r = lerp(bgRect.height() / 2f, 0f, t)
        canvas.save()
        canvas.translate(strip.scrollX.toFloat(), 0f)
        bgPath.rewind()
        bgPath.addRoundRect(bgRect, r, r, Path.Direction.CW)
        bgPaint.color = color
        canvas.drawPath(bgPath, bgPaint)
        canvas.clipPath(bgPath)
        if (t > 0f) {
            blurBehindHelper.draw(canvas, bgRect.bottom.toInt(), (0xFF * t).toInt())
        }
        canvas.translate(-strip.scrollX.toFloat(), 0f)
    }

    companion object {
        @JvmStatic
        fun attach(strip: ScrollSlidingTextTabStrip, contentView: SizeNotifierFrameLayout) {
            strip.layoutParams = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 50, Gravity.LEFT or Gravity.TOP)
            strip.setPadding(0, dp(7f), 0, dp(7f))
            strip.clipToPadding = false
            strip.tabsContainer.setPadding(dp(12f), 0, dp(12f), 0)
            strip.inu_pill = TabsPillDrawer(strip, BlurBehindHelper.create(strip, contentView, Theme.key_windowBackgroundWhite))
        }
    }
}
