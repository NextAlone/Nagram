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
        category("There's nothing here.") {
            textDetail {
                title = "But, it's not for long."
            }
        }
    }
}