package com.exteragram.messenger

import com.exteragram.messenger.preferences.*
import ua.itaysonlab.tgkit.TGKitSettingsFragment

object ExteraPreferencesNav {
    @JvmStatic
    fun createMainMenu() = TGKitSettingsFragment(MainPreferencesEntry())

    fun createAppearance() = TGKitSettingsFragment(AppearancePreferencesEntry())
    fun createChats() = TGKitSettingsFragment(ChatsPreferencesEntry())
}