package tw.nekomimi.nekogram;

import android.content.Context;
import android.util.SparseArray;

import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteException;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BaseController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import tw.nekomimi.nekogram.utils.AlertUtil;

import static tw.nekomimi.nekogram.utils.LangsKt.uDismiss;
import static tw.nekomimi.nekogram.utils.LangsKt.uUpdate;

public class MessageHelper extends BaseController {

    private static SparseArray<MessageHelper> Instance = new SparseArray<>();
    private int lastReqId;

    public MessageHelper(int num) {
        super(num);
    }

    public void resetMessageContent(long dialog_id, MessageObject messageObject) {
        TLRPC.Message message = messageObject.messageOwner;

        MessageObject obj = new MessageObject(currentAccount, message, true, true);

        ArrayList<MessageObject> arrayList = new ArrayList<>();
        arrayList.add(obj);
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, dialog_id, arrayList, false);
    }

    public static MessageHelper getInstance(int num) {
        MessageHelper localInstance = Instance.get(num);
        if (localInstance == null) {
            synchronized (MessageHelper.class) {
                localInstance = Instance.get(num);
                if (localInstance == null) {
                    Instance.put(num, localInstance = new MessageHelper(num));

                }
            }
        }
        return localInstance;
    }

    public void processForwardFromMyName(ArrayList<MessageObject> messages, long did, boolean notify, int scheduleDate) {
        HashMap<Long, Long> map = new HashMap<>();
        for (int i = 0; i < messages.size(); i++) {
            MessageObject messageObject = messages.get(i);
            ArrayList<TLRPC.MessageEntity> entities;
            if (messageObject.messageOwner.entities != null && !messageObject.messageOwner.entities.isEmpty()) {
                entities = new ArrayList<>();
                for (int a = 0; a < messageObject.messageOwner.entities.size(); a++) {
                    TLRPC.MessageEntity entity = messageObject.messageOwner.entities.get(a);
                    if (entity instanceof TLRPC.TL_messageEntityBold ||
                            entity instanceof TLRPC.TL_messageEntityItalic ||
                            entity instanceof TLRPC.TL_messageEntityPre ||
                            entity instanceof TLRPC.TL_messageEntityCode ||
                            entity instanceof TLRPC.TL_messageEntityTextUrl ||
                            entity instanceof TLRPC.TL_messageEntityStrike ||
                            entity instanceof TLRPC.TL_messageEntityUnderline) {
                        entities.add(entity);
                    }
                    if (entity instanceof TLRPC.TL_messageEntityMentionName) {
                        TLRPC.TL_inputMessageEntityMentionName mention = new TLRPC.TL_inputMessageEntityMentionName();
                        mention.length = entity.length;
                        mention.offset = entity.offset;
                        mention.user_id = getMessagesController().getInputUser(((TLRPC.TL_messageEntityMentionName) entity).user_id);
                        entities.add(mention);
                    }
                }
            } else {
                entities = null;
            }
            if (messageObject.messageOwner.media != null && !(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty) && !(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && !(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame) && !(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) {
                HashMap<String, String> params = null;
                if ((int) did == 0 && messageObject.messageOwner.peer_id != null && (messageObject.messageOwner.media.photo instanceof TLRPC.TL_photo || messageObject.messageOwner.media.document instanceof TLRPC.TL_document)) {
                    params = new HashMap<>();
                    params.put("parentObject", "sent_" + messageObject.messageOwner.peer_id.channel_id + "_" + messageObject.getId());
                }
                long oldGroupId = messageObject.messageOwner.grouped_id;
                if (oldGroupId != 0) {
                    if (params == null) {
                        params = new HashMap<>();
                    }
                    Long groupId;
                    if (map.containsKey(oldGroupId)) {
                        groupId = map.get(oldGroupId);
                    } else {
                        groupId = Utilities.random.nextLong();
                        map.put(oldGroupId, groupId);
                    }
                    params.put("groupId", String.valueOf(groupId));
                    if (i == messages.size() - 1) {
                        params.put("final", "true");
                    } else {
                        long nextOldGroupId = messages.get(i + 1).messageOwner.grouped_id;
                        if (nextOldGroupId != oldGroupId) {
                            params.put("final", "true");
                        }
                    }
                }
                if (messageObject.messageOwner.media.photo instanceof TLRPC.TL_photo) {
                    getSendMessagesHelper().sendMessage((TLRPC.TL_photo) messageObject.messageOwner.media.photo, null, did, null, null, messageObject.messageOwner.message, entities, null, params, notify, scheduleDate, 0, messageObject);
                } else if (messageObject.messageOwner.media.document instanceof TLRPC.TL_document) {
                    getSendMessagesHelper().sendMessage((TLRPC.TL_document) messageObject.messageOwner.media.document, null, messageObject.messageOwner.attachPath, did, null, null, messageObject.messageOwner.message, entities, null, params, notify, scheduleDate, 0, messageObject);
                } else if (messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue || messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo) {
                    getSendMessagesHelper().sendMessage(messageObject.messageOwner.media, did, null, null, null, null, notify, scheduleDate);
                } else if (messageObject.messageOwner.media.phone_number != null) {
                    TLRPC.User user = new TLRPC.TL_userContact_old2();
                    user.phone = messageObject.messageOwner.media.phone_number;
                    user.first_name = messageObject.messageOwner.media.first_name;
                    user.last_name = messageObject.messageOwner.media.last_name;
                    user.id = messageObject.messageOwner.media.user_id;
                    getSendMessagesHelper().sendMessage(user, did, null, null, null, null, notify, scheduleDate);
                } else if ((int) did != 0) {
                    ArrayList<MessageObject> arrayList = new ArrayList<>();
                    arrayList.add(messageObject);
                    getSendMessagesHelper().sendMessage(arrayList, did, notify, scheduleDate);
                }
            } else if (messageObject.messageOwner.message != null) {
                TLRPC.WebPage webPage = null;
                if (messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) {
                    webPage = messageObject.messageOwner.media.webpage;
                }
                getSendMessagesHelper().sendMessage(messageObject.messageOwner.message, did, null, null, webPage, webPage != null, entities, null, null, notify, scheduleDate);
            } else if ((int) did != 0) {
                ArrayList<MessageObject> arrayList = new ArrayList<>();
                arrayList.add(messageObject);
                getSendMessagesHelper().sendMessage(arrayList, did, notify, scheduleDate);
            }
        }
    }

    public void deleteUserChannelHistoryWithSearch(Context ctx, final long dialog_id, final TLRPC.User user) {
        AlertDialog progress = null;
        if (ctx != null) {
            progress = AlertUtil.showProgress(ctx);
            progress.show();
        }
        deleteUserChannelHistoryWithSearch(progress, dialog_id, user, 0, 0);
    }

    public void deleteUserChannelHistoryWithSearch(AlertDialog progress, final long dialog_id, final TLRPC.User user, final int offset_id, int index) {
        final TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
        req.peer = getMessagesController().getInputPeer((int) dialog_id);
        if (req.peer == null) {
            if (progress != null) uDismiss(progress);
            return;
        }
        req.limit = 100;
        req.q = "";
        req.offset_id = offset_id;
        if (user != null) {
            req.from_id = MessagesController.getInputPeer(user);
            req.flags |= 1;
        }
        req.filter = new TLRPC.TL_inputMessagesFilterEmpty();
        getConnectionsManager().sendRequest(req, (response, error) -> {
            if (error == null) {
                int lastMessageId = offset_id;
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                ArrayList<Integer> ids = new ArrayList<>();
                ArrayList<Long> random_ids = new ArrayList<>();
                int channelId = 0;
                int indey = index;
                for (int a = 0; a < res.messages.size(); a++) {
                    TLRPC.Message message = res.messages.get(a);
                    if (!message.out || message instanceof TLRPC.TL_messageService) {
                        continue;
                    }
                    ids.add(message.id);
                    if (message.random_id != 0) {
                        random_ids.add(message.random_id);
                    }
                    if (message.peer_id.channel_id != 0) {
                        channelId = message.peer_id.channel_id;
                    }
                    if (message.id > lastMessageId) {
                        lastMessageId = message.id;
                    }
                    indey++;
                }
                if (ids.size() == 0) {
                    if (progress != null) uDismiss(progress);
                    return;
                }
                int finalChannelId = channelId;
                AndroidUtilities.runOnUIThread(() -> getMessagesController().deleteMessages(ids, random_ids, null, dialog_id, finalChannelId, true, false));
                if (progress != null) uUpdate(progress, ">> " + indey);
                deleteUserChannelHistoryWithSearch(progress, dialog_id, user, lastMessageId, indey);
            } else {
                if (progress != null) uDismiss(progress);
                AlertUtil.showToast(error);
            }
        }, ConnectionsManager.RequestFlagFailOnServerErrors);
    }

    public void deleteChannelHistory(final long dialog_id, TLRPC.Chat chat, final int offset_id) {

        final TLRPC.TL_messages_getHistory req = new TLRPC.TL_messages_getHistory();
        req.peer = getMessagesController().getInputPeer((int) dialog_id);
        if (req.peer == null) {
            return;
        }
        req.limit = 100;
        req.offset_id = offset_id;
        final int currentReqId = ++lastReqId;
        getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            if (error == null) {
                int lastMessageId = offset_id;
                if (currentReqId == lastReqId) {
                    if (response != null) {
                        TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                        int size = res.messages.size();
                        if (size == 0) {
                            return;
                        }
                        /*
                        ArrayList<Integer> ids = new ArrayList<>();
                        ArrayList<Long> random_ids = new ArrayList<>();
                        int channelId = 0;
                        for (int a = 0; a < res.messages.size(); a++) {
                            TLRPC.Message message = res.messages.get(a);
                            ids.add(message.id);
                            if (message.random_id != 0) {
                                random_ids.add(message.random_id);
                            }
                            if (message.to_id.channel_id != 0) {
                                channelId = message.to_id.channel_id;
                            }
                            if (message.id > lastMessageId) {
                                lastMessageId = message.id;
                            }
                        }
                        getMessagesController().deleteMessages(ids, random_ids, null, dialog_id, channelId, true, false);
                         */
                        HashSet<Integer> ids = new HashSet<>();
                        ArrayList<Integer> msgIds = new ArrayList<>();
                        ArrayList<Long> random_ids = new ArrayList<>();
                        int channelId = 0;
                        for (int a = 0; a < res.messages.size(); a++) {
                            TLRPC.Message message = res.messages.get(a);
                            ids.add(message.id);
                            if (message.from_id.user_id > 0) {
                                ids.add(message.peer_id.user_id);
                            } else {
                                msgIds.add(message.id);
                                if (message.random_id != 0) {
                                    random_ids.add(message.random_id);
                                }
                            }
                            if (message.id > lastMessageId) {
                                lastMessageId = message.id;
                            }
                        }
                        for (int userId : ids) {
                            deleteUserChannelHistory(chat, userId, 0);
                        }
                        if (!msgIds.isEmpty()) {
                            getMessagesController().deleteMessages(msgIds, random_ids, null, dialog_id, channelId, true, false);
                        }
                        deleteChannelHistory(dialog_id, chat, lastMessageId);

                    }
                }
            } else {
                AlertUtil.showToast(error.code + ": " + error.text);
            }
        }), ConnectionsManager.RequestFlagFailOnServerErrors);
    }

    public void deleteUserChannelHistory(final TLRPC.Chat chat, int userId, int offset) {
        if (offset == 0) {
            getMessagesStorage().deleteUserChatHistory(chat.id, chat.id, userId);
        }
        TLRPC.TL_channels_deleteUserHistory req = new TLRPC.TL_channels_deleteUserHistory();
        req.channel = getMessagesController().getInputChannel(chat.id);
        req.user_id = getMessagesController().getInputUser(userId);
        getConnectionsManager().sendRequest(req, (response, error) -> {
            if (error == null) {
                TLRPC.TL_messages_affectedHistory res = (TLRPC.TL_messages_affectedHistory) response;
                if (res.offset > 0) {
                    deleteUserChannelHistory(chat, userId, res.offset);
                }
                getMessagesController().processNewChannelDifferenceParams(res.pts, res.pts_count, chat.id);
            }
        });
    }

    public MessageObject getLastMessageFromUnblock(long dialogId) {
        SQLiteCursor cursor;
        MessageObject ret = null;
        try {
            cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data,send_state,mid,date FROM messages WHERE uid = %d ORDER BY date DESC LIMIT %d,%d", dialogId, 0, 10));
            while (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data == null)
                    continue;
                TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                data.reuse();
                if (getMessagesController().blockePeers.indexOfKey(message.from_id.user_id) < 0) {
                    // valid message
                    ret = new MessageObject(currentAccount, message, true, true);
                    message.send_state = cursor.intValue(1);
                    message.id = cursor.intValue(2);
                    message.date = cursor.intValue(3);
                    message.dialog_id = dialogId;
                    // Fix username show
                    if (getMessagesController().getUser(ret.getSenderId()) == null) {
                        TLRPC.User user = getMessagesStorage().getUser(ret.getSenderId());
                        if (user != null)
                            getMessagesController().putUser(user, true);
                    }
                    break;
                }
            }
            cursor.dispose();
        } catch (SQLiteException sqLiteException) {
            FileLog.e("NekoX, ignoreBlocked, SQLiteException when read last message from unblocked user", sqLiteException);
            return null;
        }
        return ret;
    }

}
