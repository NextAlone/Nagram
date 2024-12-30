/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package tw.nekomimi.nekogram.utils;

import android.util.Pair;

import com.radolyn.ayugram.utils.AyuState;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class AyuGhostUtils {
    public static void markReadOnServer(int accountId, int messageId, TLRPC.InputPeer peer) {
        var connectionsManager = ConnectionsManager.getInstance(accountId);

        TLObject req;
        if (peer instanceof TLRPC.TL_inputPeerChannel) {
            TLRPC.TL_channels_readHistory request = new TLRPC.TL_channels_readHistory();
            request.channel = MessagesController.getInputChannel(peer);
            request.max_id = messageId;
            req = request;
        } else {
            TLRPC.TL_messages_readHistory request = new TLRPC.TL_messages_readHistory();
            request.peer = peer;
            request.max_id = messageId;
            req = request;
        }

        AyuState.setAllowReadPacket(true, 1);
        connectionsManager.sendRequest(req, (response, error) -> {
            if (error == null) {
                if (response instanceof TLRPC.TL_messages_affectedMessages) {
                    TLRPC.TL_messages_affectedMessages res = (TLRPC.TL_messages_affectedMessages) response;
                    MessagesController.getInstance(accountId).processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                }
            }
        });
    }

    public static void markReadLocally(Integer accountId, long dialogId, int untilId, int unread) {
        var controller = MessagesController.getInstance(accountId);
        var storage = MessagesStorage.getInstance(accountId);

        var markAsReadMessagesInbox = new LongSparseIntArray();
        var stillUnreadMessagesCount = new LongSparseIntArray();

        markAsReadMessagesInbox.put(dialogId, untilId);
        stillUnreadMessagesCount.put(dialogId, unread);

        controller.dialogs_read_inbox_max.put(dialogId, untilId);
        storage.updateDialogsWithReadMessages(markAsReadMessagesInbox, null, null, stillUnreadMessagesCount, true);
        storage.markMessagesAsRead(markAsReadMessagesInbox, null, null, true);
    }

    public static Long getDialogId(TLRPC.InputPeer peer) {
        long dialogId;
        if (peer.chat_id != 0) {
            dialogId = -peer.chat_id;
        } else if (peer.channel_id != 0) {
            dialogId = -peer.channel_id;
        } else {
            dialogId = peer.user_id;
        }

        return dialogId;
    }

    public static Long getDialogId(TLRPC.InputChannel peer) {
        return -peer.channel_id;
    }

    public static Pair<Long, Integer> getDialogIdAndMessageIdFromRequest(TLObject req) {
        if (req instanceof TLRPC.TL_messages_readHistory) {
            var readHistory = (TLRPC.TL_messages_readHistory) req;
            var peer = readHistory.peer;
            var maxId = readHistory.max_id;

            var dialogId = getDialogId(peer);

            return new Pair<>(dialogId, maxId);
        } else if (req instanceof TLRPC.TL_messages_readDiscussion) {
            var readDiscussion = (TLRPC.TL_messages_readDiscussion) req;
            var peer = readDiscussion.peer;
            var maxId = readDiscussion.read_max_id;

            var dialogId = getDialogId(peer);

            return new Pair<>(dialogId, maxId);
        } else if (req instanceof TLRPC.TL_channels_readHistory) {
            var readHistory = (TLRPC.TL_channels_readHistory) req;
            var peer = readHistory.channel;
            var maxId = readHistory.max_id;

            var dialogId = getDialogId(peer);

            return new Pair<>(dialogId, maxId);
        }

        // not implemented:
        // - TL_messages_readEncryptedHistory
        // - TL_messages_readMessageContents
        // - TL_channels_readMessageContents

        return null;
    }
}
