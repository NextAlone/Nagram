package com.exteragram.messenger

import android.app.Activity
import android.content.SharedPreferences
import com.exteragram.messenger.preferences.ktx.boolean
import com.exteragram.messenger.preferences.ktx.int
import org.telegram.messenger.ApplicationLoader

object ExteraConfig {

    private val sharedPreferences: SharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE)

    // Chats
    // General
    var dateOfForwardedMsg by sharedPreferences.boolean("dateOfForwardedMsg", false)

}