package com.exteragram.messenger.preferences

import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.BaseFragment
import com.exteragram.messenger.ExteraConfig
import ua.itaysonlab.tgkit.ktx.*
import ua.itaysonlab.tgkit.preference.types.TGKitSliderPreference.TGSLContract

class ChatsPreferencesEntry : BasePreferencesEntry {
    override fun getPreferences(bf: BaseFragment) = tgKitScreen(LocaleController.getString("Chats", R.string.Chats)) {
        category(LocaleController.getString("StickerSize", R.string.StickerSize)) {
            slider {
                contract = object : TGSLContract {
                    override fun setValue(value: Int) {
                        ExteraConfig.stickerSize = value
                    }

                    override fun getPreferenceValue(): Int {
                        return ExteraConfig.stickerSize
                    }

                    override fun getMin(): Int {
                        return 50
                    }

                    override fun getMax(): Int {
                        return 100
                    }
                }
            }
        }
        category(LocaleController.getString("General", R.string.General)) {
            switch {
                title = LocaleController.getString("HideKeyboardOnScroll", R.string.HideKeyboardOnScroll)

                contract({
                    return@contract ExteraConfig.hideKeyboardOnScroll
                }) {
                    ExteraConfig.hideKeyboardOnScroll = it
                }
            }
            switch {
                title = LocaleController.getString("ArchiveOnPull", R.string.ArchiveOnPull)

                contract({
                    return@contract ExteraConfig.archiveOnPull
                }) {
                    ExteraConfig.archiveOnPull = it
                }
            }
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