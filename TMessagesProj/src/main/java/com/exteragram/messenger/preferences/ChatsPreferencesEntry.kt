package com.exteragram.messenger.preferences

import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.BaseFragment
import com.exteragram.messenger.ExteraConfig
import ua.itaysonlab.tgkit.ktx.*
import ua.itaysonlab.tgkit.preference.types.TGKitSliderPreference.TGSLContract

class ChatsPreferencesEntry : BasePreferencesEntry {
    override fun getPreferences(bf: BaseFragment) = tgKitScreen(LocaleController.getString("Chats", R.string.Chats)) {
        category(LocaleController.getString("General", R.string.General)) {
            switch {
                title = LocaleController.getString("DateOfForwardedMsg", R.string.DateOfForwardedMsg)

                contract({
                    return@contract ExteraConfig.dateOfForwardedMsg
                }) {
                    ExteraConfig.dateOfForwardedMsg = it
                }
            }
        }
    }
}