package top.qwq2333.nullgram.utils

import android.media.AudioRecord
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import top.qwq2333.nullgram.config.ConfigManager

object AudioUtils {
    var automaticGainControl: AutomaticGainControl? =
        null
    var acousticEchoCanceler: AcousticEchoCanceler? =
        null
    var noiseSuppressor: NoiseSuppressor? =
        null

    @JvmStatic
    fun initVoiceEnhance(
        audioRecord: AudioRecord
    ) {
        if (!ConfigManager.getBooleanOrFalse(Defines.enchantAudio)) return
        if (AutomaticGainControl.isAvailable()) {
            automaticGainControl =
                AutomaticGainControl.create(
                    audioRecord.audioSessionId
                )
            automaticGainControl?.enabled =
                true
        }
        if (AcousticEchoCanceler.isAvailable()) {
            acousticEchoCanceler =
                AcousticEchoCanceler.create(
                    audioRecord.audioSessionId
                )
            acousticEchoCanceler?.enabled =
                true
        }
        if (NoiseSuppressor.isAvailable()) {
            noiseSuppressor =
                NoiseSuppressor.create(
                    audioRecord.audioSessionId
                )
            noiseSuppressor?.enabled =
                true
        }
    }

    @JvmStatic
    fun releaseVoiceEnhance() {
        if (automaticGainControl != null) {
            automaticGainControl?.release()
            automaticGainControl =
                null
        }
        if (acousticEchoCanceler != null) {
            acousticEchoCanceler?.release()
            acousticEchoCanceler =
                null
        }
        if (noiseSuppressor != null) {
            noiseSuppressor?.release()
            noiseSuppressor =
                null
        }
    }

    @JvmStatic
    fun isAvailable(): Boolean {
        return AutomaticGainControl.isAvailable() || NoiseSuppressor.isAvailable() || AcousticEchoCanceler.isAvailable()
    }
}
