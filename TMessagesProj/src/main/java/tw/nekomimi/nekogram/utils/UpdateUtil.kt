package tw.nekomimi.nekogram.utils

import android.content.Context
import org.telegram.messenger.*
import org.telegram.messenger.browser.Browser
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.TLObject
import org.telegram.tgnet.TLRPC
import org.telegram.ui.ActionBar.AlertDialog

object UpdateUtil {

    const val channelUsername = "nagram_channel"
    const val channelUsernameTips = "NagramTips"

    @JvmStatic
    fun postCheckFollowChannel(ctx: Context, currentAccount: Int) = UIUtil.runOnIoDispatcher {

        if (MessagesController.getMainSettings(currentAccount).getBoolean("update_channel_skip", false)) return@runOnIoDispatcher

        val messagesCollector = MessagesController.getInstance(currentAccount)
        val connectionsManager = ConnectionsManager.getInstance(currentAccount)
        val messagesStorage = MessagesStorage.getInstance(currentAccount)
        val updateChannel = messagesCollector.getUserOrChat(channelUsername)

        if (updateChannel is TLRPC.Chat) checkFollowChannel(ctx, currentAccount, updateChannel) else {
            connectionsManager.sendRequest(TLRPC.TL_contacts_resolveUsername().apply {
                username = channelUsername
            }) { response: TLObject?, error: TLRPC.TL_error? ->
                if (error == null) {
                    val res = response as TLRPC.TL_contacts_resolvedPeer
                    val chat = res.chats.find { it.username == channelUsername } ?: return@sendRequest
                    messagesCollector.putChats(res.chats, false)
                    messagesStorage.putUsersAndChats(res.users, res.chats, false, true)
                    checkFollowChannel(ctx, currentAccount, chat)
                }
            }
        }

    }

    private fun checkFollowChannel(ctx: Context, currentAccount: Int, channel: TLRPC.Chat) {

        if (!channel.left || channel.kicked) {

            //   MessagesController.getMainSettings(currentAccount).edit().putBoolean("update_channel_skip", true).apply()

            return

        }

        UIUtil.runOnUIThread {

            val messagesCollector = MessagesController.getInstance(currentAccount)
            val userConfig = UserConfig.getInstance(currentAccount)

            val builder = AlertDialog.Builder(ctx)

            builder.setTitle(LocaleController.getString(R.string.FCTitle))
            builder.setMessage(LocaleController.getString(R.string.FCInfo))

            builder.setPositiveButton(LocaleController.getString(R.string.ChannelJoin)) { _, _ ->
                messagesCollector.addUserToChat(channel.id, userConfig.currentUser, 0, null, null, null)
                Browser.openUrl(ctx, "https://t.me/$channelUsername")
            }

            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null)

            builder.setNeutralButton(LocaleController.getString(R.string.DoNotRemindAgain)) { _, _ ->
                MessagesController.getMainSettings(currentAccount).edit().putBoolean("update_channel_skip", true).apply()
            }

            try {
                builder.show()
            } catch (ignored: Exception) {}

        }

    }

    @JvmStatic
    fun postCheckFollowTipsChannel(ctx: Context, currentAccount: Int) = UIUtil.runOnIoDispatcher {

        if (MessagesController.getMainSettings(currentAccount).getBoolean("update_channel_tip_skip", false)) return@runOnIoDispatcher

        val messagesCollector = MessagesController.getInstance(currentAccount)
        val connectionsManager = ConnectionsManager.getInstance(currentAccount)
        val messagesStorage = MessagesStorage.getInstance(currentAccount)
        val updateChannel = messagesCollector.getUserOrChat(channelUsernameTips)

        if (updateChannel is TLRPC.Chat) checkFollowTipsChannel(ctx, currentAccount, updateChannel) else {
            connectionsManager.sendRequest(TLRPC.TL_contacts_resolveUsername().apply {
                username = channelUsernameTips
            }) { response: TLObject?, error: TLRPC.TL_error? ->
                if (error == null) {
                    val res = response as TLRPC.TL_contacts_resolvedPeer
                    val chat = res.chats.find { it.username == channelUsernameTips } ?: return@sendRequest
                    messagesCollector.putChats(res.chats, false)
                    messagesStorage.putUsersAndChats(res.users, res.chats, false, true)
                    checkFollowTipsChannel(ctx, currentAccount, chat)
                }
            }
        }

    }

    private fun checkFollowTipsChannel(ctx: Context, currentAccount: Int, channel: TLRPC.Chat) {
        if (!channel.left || channel.kicked) {
            return
        }

        UIUtil.runOnUIThread {

            val messagesCollector = MessagesController.getInstance(currentAccount)
            val userConfig = UserConfig.getInstance(currentAccount)

            val builder = AlertDialog.Builder(ctx)

            builder.setTitle(LocaleController.getString(R.string.FCTitle))
            builder.setMessage(LocaleController.getString(R.string.TipsInfo))

            builder.setPositiveButton(LocaleController.getString(R.string.ChannelJoin)) { _, _ ->
                messagesCollector.addUserToChat(channel.id, userConfig.currentUser, 0, null, null, null)
                Browser.openUrl(ctx, "https://t.me/$channelUsernameTips")
            }

            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null)

            builder.setNeutralButton(LocaleController.getString(R.string.DoNotRemindAgain)) { _, _ ->
                MessagesController.getMainSettings(currentAccount).edit().putBoolean("update_channel_tip_skip", true).apply()
            }

            try {
                builder.show()
            } catch (ignored: Exception) {}

        }

    }

}
