@file:JvmName("SharedMediaLayoutPatch")

package xyz.nextalone.nagram.ui

import androidx.recyclerview.widget.RecyclerView
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ChatObject
import org.telegram.messenger.MessageObject
import org.telegram.messenger.MessagesController
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.TLRPC
import java.util.WeakHashMap

private val requestedSelfParticipants = WeakHashMap<TLRPC.ChatFull, Boolean>()

/**
 * 群组开启 participants_hidden 后，服务器返回的成员列表可能不包含自己，
 * 这里主动拉取一次自己的参与者信息并插入到列表最前面。
 */
fun ensureSelfParticipant(
    currentChat: TLRPC.Chat?,
    chatInfo: TLRPC.ChatFull?,
    sortedUsers: ArrayList<Int>?,
    account: Int,
    adapter: RecyclerView.Adapter<*>?
) {
    if (chatInfo == null || !chatInfo.participants_hidden) return
    if (currentChat == null || !ChatObject.isMegagroup(currentChat) || ChatObject.isNotInChat(currentChat)) return
    val participants = chatInfo.participants?.participants ?: return
    val self = UserConfig.getInstance(account).currentUser ?: return
    val selfId = self.id
    if (participants.any { it.user_id == selfId }) return

    synchronized(requestedSelfParticipants) {
        if (requestedSelfParticipants[chatInfo] == true) return
        requestedSelfParticipants[chatInfo] = true
    }

    MessagesController.getInstance(account).getChannelParticipant(currentChat, self) { participant ->
        val channelParticipant = participant ?: return@getChannelParticipant
        AndroidUtilities.runOnUIThread {
            addSelfParticipant(chatInfo, sortedUsers, selfId, channelParticipant, adapter)
        }
    }
}

private fun addSelfParticipant(
    chatInfo: TLRPC.ChatFull,
    sortedUsers: ArrayList<Int>?,
    selfId: Long,
    participant: TLRPC.ChannelParticipant,
    adapter: RecyclerView.Adapter<*>?
) {
    val participants = chatInfo.participants?.participants ?: return
    if (participants.any { it.user_id == selfId }) return
    if (sortedUsers == null) return

    val chatParticipant = TLRPC.TL_chatChannelParticipant()
    chatParticipant.channelParticipant = participant
    chatParticipant.user_id = if (participant.user_id != 0L) participant.user_id else MessageObject.getPeerId(participant.peer)
    chatParticipant.inviter_id = participant.inviter_id
    chatParticipant.date = participant.date
    chatParticipant.rank = participant.rank
    participants.add(0, chatParticipant)

    if (sortedUsers.isNotEmpty()) {
        for (i in sortedUsers.indices) {
            sortedUsers[i] = sortedUsers[i] + 1
        }
        sortedUsers.add(0, 0)
    }
    adapter?.notifyDataSetChanged()
}
