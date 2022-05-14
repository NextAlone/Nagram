package top.qwq2333.nullgram.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup

import androidx.core.view.children

import org.telegram.messenger.ApplicationLoader
import top.qwq2333.nullgram.config.ConfigManager

object VibrationUtils {
    lateinit var vibrator: Vibrator

    @JvmStatic
    fun disableHapticFeedback(view: View) {
        view.isHapticFeedbackEnabled = false
        (view as? ViewGroup)?.children?.forEach(::disableHapticFeedback)
    }

    @JvmStatic
    @JvmOverloads
    fun vibrate(time: Long = 200L) {

        if (ConfigManager.getBooleanOrFalse(Defines.disableVibration)) return

        if (!vibrator.hasVibrator()) return

        if (!::vibrator.isInitialized) {
            vibrator =
                ApplicationLoader.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            runCatching {
                val effect = VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(effect, null)
            }
        } else {
            runCatching {
                vibrator.vibrate(time)
            }
        }
    }
}
