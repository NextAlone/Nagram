package com.exteragram.messenger.preferences

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import org.telegram.messenger.LocaleController
import org.telegram.ui.ActionBar.BaseFragment
import org.telegram.ui.LaunchActivity
import com.exteragram.messenger.ExteraPreferencesNav
import ua.itaysonlab.tgkit.ktx.category
import ua.itaysonlab.tgkit.ktx.textDetail
import ua.itaysonlab.tgkit.ktx.textIcon
import ua.itaysonlab.tgkit.ktx.tgKitScreen
import ua.itaysonlab.tgkit.preference.types.TGKitTextIconRow

import android.os.Build

import android.app.assist.AssistContent
import org.telegram.messenger.BuildConfig
import org.telegram.messenger.BuildVars
import org.telegram.messenger.R
import kotlin.String

class MainPreferencesEntry : BasePreferencesEntry {
    override fun getPreferences(bf: BaseFragment) = tgKitScreen(LocaleController.getString("Preferences", R.string.Preferences)) {
        category(LocaleController.getString("AboutExtera", R.string.AboutExtera)) {
            textDetail {
                if (BuildVars.isBetaApp()) title = "exteraGram β | v" + BuildConfig.VERSION_NAME
                    else title = "exteraGram | v" + BuildConfig.VERSION_NAME
                detail = LocaleController.getString("AboutExteraDescription", R.string.AboutExteraDescription)
            }

            textIcon {
                title = LocaleController.getString("Website", R.string.Website)
                listener = TGKitTextIconRow.TGTIListener {
                    goToWebsite(it)
                }
            }

            textIcon {
                title = LocaleController.getString("Github", R.string.Github)
                listener = TGKitTextIconRow.TGTIListener {
                    goToGithub(it)
                }
            }

            textIcon {
                title = LocaleController.getString("Channel", R.string.Channel)
                listener = TGKitTextIconRow.TGTIListener {
                    goToChannel(it)
                }
            }
            textIcon {
                title = LocaleController.getString("Chat", R.string.Chat)
                value = LocaleController.getString("languageRussian", R.string.languageRussian)
                listener = TGKitTextIconRow.TGTIListener {
                    goToRUChat(it)
                }
            }
            textIcon {
                title = LocaleController.getString("Chat", R.string.Chat)
                value = LocaleController.getString("languageEnglish", R.string.languageEnglish)
                listener = TGKitTextIconRow.TGTIListener {
                    goToENChat(it)
                }
            }
        }
        category(LocaleController.getString("Categories", R.string.Categories)) {
            textIcon {
                title = LocaleController.getString("Appearance", R.string.Appearance)
                icon = R.drawable.msg_theme
                listener = TGKitTextIconRow.TGTIListener {
                    it.presentFragment(ExteraPreferencesNav.createAppearance())
                }
            }
            textIcon {
                title = LocaleController.getString("Chats", R.string.Chats)
                icon = R.drawable.menu_chats
                listener = TGKitTextIconRow.TGTIListener {
                    it.presentFragment(ExteraPreferencesNav.createChats())
                }
            }
        }
    }

    companion object {
        private fun goToWebsite(bf: BaseFragment) {
            val openURL = Intent(android.content.Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://exterasquad.github.io/")
            bf.parentActivity.startActivity(openURL)
        }

        private fun goToGithub(bf: BaseFragment) {
            val openURL = Intent(android.content.Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://github.com/exteraSquad/exteraGram/")
            bf.parentActivity.startActivity(openURL)
        }

        private fun goToChannel(bf: BaseFragment) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/exteraGram"))
            val componentName = ComponentName(bf.parentActivity.packageName, LaunchActivity::class.java.name)
            intent.component = componentName
            intent.putExtra(Browser.EXTRA_CREATE_NEW_TAB, true)
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, bf.parentActivity.packageName)
            bf.parentActivity.startActivity(intent)
        }

        private fun goToRUChat(bf: BaseFragment) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/exteraChat"))
            val componentName = ComponentName(bf.parentActivity.packageName, LaunchActivity::class.java.name)
            intent.component = componentName
            intent.putExtra(Browser.EXTRA_CREATE_NEW_TAB, true)
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, bf.parentActivity.packageName)
            bf.parentActivity.startActivity(intent)
        }

        private fun goToENChat(bf: BaseFragment) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/en_exteraChat"))
            val componentName = ComponentName(bf.parentActivity.packageName, LaunchActivity::class.java.name)
            intent.component = componentName
            intent.putExtra(Browser.EXTRA_CREATE_NEW_TAB, true)
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, bf.parentActivity.packageName)
            bf.parentActivity.startActivity(intent)
        }

        fun onProvideAssistContent(outContent: AssistContent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                outContent.webUri = Uri.parse(
                    String.format(
                        "https://t.me/exteraGram"
                    )
                )
            }
        }
    }
}