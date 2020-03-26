package tw.nekomimi.nekogram.utils

import android.content.Context
import android.widget.Toast
import com.google.android.exoplayer2.drm.DecryptionResource
import org.telegram.messenger.ApplicationLoader

object AlertUtil {

    @JvmStatic
    fun showToast(text: String) = Toast.makeText(ApplicationLoader.applicationContext, text.takeIf { it.isNotBlank() } ?: "喵 !", Toast.LENGTH_LONG).show()

}