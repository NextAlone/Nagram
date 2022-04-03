package com.exteragram.messenger.extras

import org.telegram.messenger.LocaleController
import org.telegram.messenger.MessageObject

import com.exteragram.messenger.ExteraConfig

object DateOfForwardedMsg {
    @JvmStatic
    fun showForwardDate(obj: MessageObject, orig: CharSequence): String {
        return if (!ExteraConfig.dateOfForwardedMsg) orig.toString()
               else "$orig • ${LocaleController.formatDate(obj.messageOwner.fwd_from.date.toLong())}"
    }
}