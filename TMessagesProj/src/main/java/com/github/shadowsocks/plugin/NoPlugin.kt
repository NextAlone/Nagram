package com.github.shadowsocks.plugin

import org.telegram.messenger.LocaleController
import org.telegram.messenger.R

object NoPlugin : Plugin() {
    override val id: String get() = ""
    override val label: CharSequence get() = LocaleController.getString("Disable", R.string.Disable)
}
