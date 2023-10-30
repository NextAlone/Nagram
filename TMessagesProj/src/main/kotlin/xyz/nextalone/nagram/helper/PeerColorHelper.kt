package xyz.nextalone.nagram.helper

import xyz.nextalone.nagram.NaConfig


object PeerColorHelper {

    @JvmStatic
    fun replaceColor(old: Int?): Int? {
        if (NaConfig.useLocalQuoteColor.Bool()) {
            return NaConfig.useLocalQuoteColorColor.Int()
        }
        return old
    }
}
