package tw.nekomimi.nekogram.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import org.telegram.messenger.ApplicationLoader
import tw.nekomimi.nekogram.NekoConfig

object VibrateUtil {

    lateinit var vibrator: Vibrator

    @JvmStatic
    @JvmOverloads
    fun vibrate(time: Long = 200L) {

        if (NekoConfig.disableVibration) return

        if (!::vibrator.isInitialized) {

            vibrator = ApplicationLoader.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        }

        if (!vibrator.hasVibrator()) return

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

    @JvmStatic
    fun disableHapticFeedback(view: View) {

        view.isHapticFeedbackEnabled = false

        (view as? ViewGroup)?.children?.forEach(::disableHapticFeedback)

    }

}