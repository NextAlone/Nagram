package tw.nekomimi.nekogram.parts

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import org.telegram.ui.ActionBar.Theme

fun getDrawable(ctx: Context, resId: Int): Drawable {
    val drawable = ContextCompat.getDrawable(ctx, resId)!!.mutate()
    drawable.colorFilter = PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.SRC_IN)
    return drawable
}