package tw.nekomimi.nekogram.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import org.telegram.messenger.ApplicationLoader
import tw.nekomimi.nekogram.NekoConfig

object VibrateUtil {

    lateinit var vibrator: Vibrator

    fun initVibrator() {
        if (!::vibrator.isInitialized) {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    ApplicationLoader.applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator;
            } else {
                ApplicationLoader.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun vibrate(time: Long = 200L, effect: VibrationEffect? = null) {
        if (NekoConfig.disableVibration.Bool()) return

        initVibrator()

        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            runCatching {
                var vibe = effect
                if (vibe == null) {
                    vibe = VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                vibrator.cancel()
                vibrator.vibrate(vibe, null)
            }
        } else {
            runCatching {
                vibrator.cancel()
                vibrator.vibrate(time)
            }
        }
    }

    @JvmStatic
    fun disableHapticFeedback(view: View) {
        view.isHapticFeedbackEnabled = false
        (view as? ViewGroup)?.children?.forEach(::disableHapticFeedback)
    }

    @JvmStatic
    fun vibrate(longs: LongArray, repeat: Int) {
        if (NekoConfig.disableVibration.Bool()) return

        initVibrator()

        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            runCatching {
                val vibe = VibrationEffect.createWaveform(longs, repeat)
                vibrator.cancel()
                vibrator.vibrate(vibe, null)
            }
        } else {
            runCatching {
                vibrator.cancel()
                vibrator.vibrate(longs, repeat)
            }
        }
    }
}
