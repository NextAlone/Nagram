package xyz.nextalone.nagram.helper

import org.telegram.messenger.AndroidUtilities

object ColorOsHelper {
    val isColorOS: Boolean =
        AndroidUtilities.getSystemProperty("ro.build.version.oplusrom") != null
}
