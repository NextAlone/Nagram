package xyz.nextalone.nagram.helper

import android.text.TextUtils
import org.telegram.messenger.AndroidUtilities

object ColorOsHelper {
    val isColorOS: Boolean = !TextUtils.isEmpty(AndroidUtilities.getSystemProperty("ro.build.version.oplusrom"))
}
