package com.exteragram.messenger.extras

import org.telegram.messenger.LocaleController
import org.telegram.messenger.MessageObject

import com.exteragram.messenger.ExteraConfig

object dateOfForwardedMsg {
    @JvmStatic
    fun showForwardDate(obj: MessageObject, orig: CharSequence): String {
        if (!ExteraConfig.dateOfForwardedMsg) {
            return orig.toString()
        } else return "$orig • ${LocaleController.formatDate(obj.messageOwner.fwd_from.date.toLong())}"
    }
}