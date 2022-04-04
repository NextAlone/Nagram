package top.qwq2333.nullgram.utils

import org.telegram.messenger.LocaleController
import org.telegram.messenger.MessageObject
import top.qwq2333.nullgram.config.ConfigManager

object Utils {

    @JvmStatic
    fun showForwardDate(obj: MessageObject, orig: CharSequence): String =
        if (ConfigManager.getBooleanOrFalse(Defines.dateOfForwardedMsg)) {
            "$orig • ${LocaleController.formatDate(obj.messageOwner.fwd_from.date.toLong())}"
        } else {
            orig.toString()
        }


}
