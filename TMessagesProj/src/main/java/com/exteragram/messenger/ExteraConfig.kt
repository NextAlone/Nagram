package com.exteragram.messenger

import android.app.Activity
import android.content.SharedPreferences
import com.exteragram.messenger.preferences.ktx.boolean
import com.exteragram.messenger.preferences.ktx.int
import org.telegram.messenger.ApplicationLoader

object ExteraConfig {

    private val sharedPreferences: SharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE)

    // Appearance
    // Application
    // General
    // Drawer

    // Chats
    // Stickers
    // General
    var archiveOnPull by sharedPreferences.boolean("archiveOnPull", true)
    var dateOfForwardedMsg by sharedPreferences.boolean("dateOfForwardedMsg", false)
    // Media
}