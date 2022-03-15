package com.exteragram.messenger.preferences

import android.graphics.Color
import android.os.Build
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.messenger.SharedConfig
import org.telegram.ui.ActionBar.BaseFragment
import org.telegram.ui.ActionBar.Theme
import com.exteragram.messenger.ExteraConfig
import ua.itaysonlab.tgkit.ktx.*

class AppearancePreferencesEntry : BasePreferencesEntry {
    override fun getPreferences(bf: BaseFragment) = tgKitScreen(LocaleController.getString("Appearance", R.string.Appearance)) {
        category(LocaleController.getString("Application", R.string.Application)) {
            switch {
                title = LocaleController.getString("TransparentStatusbar", R.string.TransparentStatusbar)

                contract({
                    return@contract SharedConfig.noStatusBar
                }) {
                    SharedConfig.toggleNoStatusBar()
                }
            }
            switch {
                title = LocaleController.getString("BlurForAllThemes", R.string.BlurForAllThemes)
                summary = LocaleController.getString("RestartRequired", R.string.RestartRequired)

                contract({
                    return@contract ExteraConfig.blurForAllThemes
                }) {
                    ExteraConfig.blurForAllThemes = it
                }
            }
        }

        category(LocaleController.getString("General", R.string.General)) {
            switch {
                title = LocaleController.getString("HideAllChats", R.string.HideAllChats)
                summary = LocaleController.getString("RestartRequired", R.string.RestartRequired)

                contract({
                    return@contract ExteraConfig.hideAllChats
                }) {
                    ExteraConfig.hideAllChats = it
                }
            }
            switch {
                title = LocaleController.getString("HideProxySponsor", R.string.HideProxySponsor)

                contract({
                    return@contract ExteraConfig.hideProxySponsor
                }) {
                    ExteraConfig.hideProxySponsor = it
                }
            }
            switch {
                title = LocaleController.getString("HidePhoneNumber", R.string.HidePhoneNumber)
                summary = LocaleController.getString("RestartRequired", R.string.RestartRequired)

                contract({
                    return@contract ExteraConfig.hidePhoneNumber
                }) {
                    ExteraConfig.hidePhoneNumber = it
                }
            }
            switch {
                title = LocaleController.getString("ShowID", R.string.ShowID)

                contract({
                    return@contract ExteraConfig.showID
                }) {
                    ExteraConfig.showID = it
                }
            }
            switch {
                title = LocaleController.getString("ChatsOnTitle", R.string.ChatsOnTitle)
                summary = LocaleController.getString("RestartRequired", R.string.RestartRequired)

                contract({
                    return@contract ExteraConfig.chatsOnTitle
                }) {
                    ExteraConfig.chatsOnTitle = it
                }
            }
            switch {
                title = LocaleController.getString("ForceTabletMode", R.string.ForceTabletMode)
                summary = LocaleController.getString("RestartRequired", R.string.RestartRequired)

                contract({
                    return@contract ExteraConfig.forceTabletMode
                }) {
                    ExteraConfig.forceTabletMode = it
                }
            }
        }
    }
}