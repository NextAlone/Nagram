package top.qwq2333.nullgram.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import org.telegram.messenger.AndroidUtilities
import org.telegram.ui.Cells.DrawerProfileCell


class DrawerProfilePreviewCell(context: Context?) :
    DrawerProfileCell(context) {
    init {
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(148f))
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    override fun dispatchSetPressed(pressed: Boolean) {}

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}

