package xyz.nextalone.nagram.helper

import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ChatObject
import org.telegram.messenger.LocaleController
import org.telegram.messenger.MessagesController
import org.telegram.messenger.R
import org.telegram.messenger.UserConfig
import org.telegram.messenger.Utilities
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.TLObject
import org.telegram.tgnet.TLRPC
import org.telegram.ui.ActionBar.AlertDialog
import org.telegram.ui.ActionBar.BaseFragment
import org.telegram.ui.Components.BulletinFactory
import org.telegram.ui.Components.DeleteMessagesBottomSheet

/**
 * Admin actions on a group member that do not need one of the member's messages:
 * "Change permissions" and "Ban or delete messages" (the upstream [DeleteMessagesBottomSheet]
 * opened with no selected messages).
 *
 * The member's participant record is always fetched on demand with `channels.getParticipant`,
 * because `ChatFull.participants` is only a partial list for supergroups.
 */
object MemberAdminActions {

    /** Supergroup (not a broadcast channel, not a monoforum) where we can ban, and a target that is another user. */
    @JvmStatic
    fun canManageMember(currentAccount: Int, chat: TLRPC.Chat?, user: TLRPC.User?): Boolean {
        if (chat == null || user == null) return false
        if (!ChatObject.isMegagroup(chat) || ChatObject.isMonoForum(chat)) return false
        if (!ChatObject.canBlockUsers(chat)) return false
        return user.id != UserConfig.getInstance(currentAccount).getClientUserId()
    }

    @JvmStatic
    fun canChangePermissions(currentAccount: Int, chat: TLRPC.Chat?, user: TLRPC.User?): Boolean {
        return canManageMember(currentAccount, chat, user) && !chat!!.gigagroup
    }

    @JvmStatic
    fun canBanOrDelete(currentAccount: Int, chat: TLRPC.Chat?, user: TLRPC.User?): Boolean {
        return canManageMember(currentAccount, chat, user) &&
                ChatObject.canUserDoAdminAction(chat, ChatObject.ACTION_DELETE_MESSAGES)
    }

    /**
     * Whether removing [participant] from the member list can go through the ban-or-delete panel.
     * The panel only offers a ban for admins when we are the creator, so other admins keep the
     * upstream removal.
     */
    @JvmStatic
    fun canRemoveWithPanel(currentAccount: Int, chat: TLRPC.Chat?, user: TLRPC.User?, participant: TLRPC.ChannelParticipant?): Boolean {
        if (!canBanOrDelete(currentAccount, chat, user)) return false
        if (participant is TLRPC.TL_channelParticipantCreator) return false
        if (participant is TLRPC.TL_channelParticipantAdmin && !chat!!.creator) return false
        return true
    }

    /** The creator, and admins we cannot edit, cannot be restricted. */
    @JvmStatic
    fun canEditPermissionsOf(participant: TLRPC.ChannelParticipant?): Boolean {
        if (participant is TLRPC.TL_channelParticipantCreator) return false
        if (participant is TLRPC.TL_channelParticipantAdmin && !participant.can_edit) return false
        return true
    }

    @JvmStatic
    fun showCannotEditPermissions(fragment: BaseFragment) {
        BulletinFactory.of(fragment).createErrorBulletin(LocaleController.getString(R.string.EditAdminCantEdit)).show()
    }

    /**
     * Wraps a channel participant the way the chat participant list does, so it can be handed to
     * the existing rights editor helpers. A user that is not a member gets a plain member record.
     */
    @JvmStatic
    fun wrapParticipant(currentAccount: Int, user: TLRPC.User, participant: TLRPC.ChannelParticipant?): TLRPC.TL_chatChannelParticipant {
        val channelParticipant = participant ?: TLRPC.TL_channelParticipant().apply {
            peer = MessagesController.getInstance(currentAccount).getPeer(user.id)
        }
        return TLRPC.TL_chatChannelParticipant().apply {
            this.channelParticipant = channelParticipant
            user_id = user.id
            inviter_id = channelParticipant.inviter_id
            date = channelParticipant.date
        }
    }

    /**
     * Loads the member's participant record with a cancellable progress dialog. [callback] runs on
     * the UI thread with null when the user is not a member of the chat; any other failure is shown
     * to the user and [callback] is not called.
     */
    @JvmStatic
    fun loadParticipant(fragment: BaseFragment, chat: TLRPC.Chat, user: TLRPC.User, callback: Utilities.Callback<TLRPC.ChannelParticipant?>) {
        val context = fragment.parentActivity ?: return
        val currentAccount = fragment.getCurrentAccount()
        val progressDialog = AlertDialog(context, AlertDialog.ALERT_TYPE_SPINNER)
        val req = TLRPC.TL_channels_getParticipant().apply {
            channel = MessagesController.getInputChannel(chat)
            participant = MessagesController.getInputPeer(user)
        }
        var cancelled = false
        val requestId = ConnectionsManager.getInstance(currentAccount).sendRequest(req) { response: TLObject?, error: TLRPC.TL_error? ->
            AndroidUtilities.runOnUIThread {
                if (cancelled) return@runOnUIThread
                try {
                    progressDialog.dismiss()
                } catch (ignore: Exception) {
                }
                if (response is TLRPC.TL_channels_channelParticipant) {
                    val messagesController = MessagesController.getInstance(currentAccount)
                    messagesController.putUsers(response.users, false)
                    messagesController.putChats(response.chats, false)
                    callback.run(response.participant)
                } else if (error != null && error.text == "USER_NOT_PARTICIPANT") {
                    callback.run(null)
                } else {
                    BulletinFactory.of(fragment).showForError(error)
                }
            }
        }
        progressDialog.setOnCancelListener {
            cancelled = true
            ConnectionsManager.getInstance(currentAccount).cancelRequest(requestId, true)
        }
        fragment.showDialog(progressDialog)
    }

    /**
     * Opens the ban-or-delete panel for [user]. When [participant] is already known it is used as
     * is, otherwise it is fetched. [preselectBan] checks the ban option (used when the panel replaces
     * a member removal), and [onBanned] runs after the admin applied a ban.
     */
    @JvmStatic
    @JvmOverloads
    fun openBanOrDelete(
        fragment: BaseFragment,
        chat: TLRPC.Chat,
        user: TLRPC.User,
        participant: TLRPC.ChannelParticipant? = null,
        preselectBan: Boolean = false,
        onBanned: Runnable? = null
    ) {
        if (participant != null) {
            showBanOrDelete(fragment, chat, user, participant, preselectBan, onBanned)
        } else {
            loadParticipant(fragment, chat, user) {
                showBanOrDelete(fragment, chat, user, it, preselectBan, onBanned)
            }
        }
    }

    private fun showBanOrDelete(
        fragment: BaseFragment,
        chat: TLRPC.Chat,
        user: TLRPC.User,
        participant: TLRPC.ChannelParticipant?,
        preselectBan: Boolean,
        onBanned: Runnable?
    ) {
        if (fragment.parentActivity == null) return
        val sheet = DeleteMessagesBottomSheet(
            fragment, chat, ArrayList(), arrayListOf<TLObject>(user), arrayOf(participant),
            0, 0, 0, false, null
        )
        if (preselectBan) {
            sheet.toggleDefaultChecks(booleanArrayOf(true, false, false, false, false))
        }
        sheet.setNaOnBanned(onBanned)
        sheet.show()
    }
}
