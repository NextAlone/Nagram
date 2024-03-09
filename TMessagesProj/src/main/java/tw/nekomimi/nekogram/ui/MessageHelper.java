package tw.nekomimi.nekogram.ui;

import static tw.nekomimi.nekogram.utils.LangsKt.uDismiss;
import static tw.nekomimi.nekogram.utils.LangsKt.uUpdate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteException;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BaseController;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.LayoutHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import tw.nekomimi.nekogram.utils.AlertUtil;

public class MessageHelper extends BaseController {

    private static final CharsetDecoder utf8Decoder = StandardCharsets.UTF_8.newDecoder();

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

    public void resetMessageContent(long dialog_id, ArrayList<MessageObject> messageObjects) {
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        for (MessageObject messageObject : messageObjects) {
            MessageObject obj = new MessageObject(currentAccount, messageObject.messageOwner, true, true);
            arrayList.add(obj);
        }
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
                    getSendMessagesHelper().sendMessage(SendMessagesHelper.SendMessageParams.of((TLRPC.TL_photo) messageObject.messageOwner.media.photo, null, did, null, null, messageObject.messageOwner.message, entities, null, params, notify, scheduleDate, 0, messageObject, false));
                } else if (messageObject.messageOwner.media.document instanceof TLRPC.TL_document) {
                    getSendMessagesHelper().sendMessage(SendMessagesHelper.SendMessageParams.of((TLRPC.TL_document) messageObject.messageOwner.media.document, null, messageObject.messageOwner.attachPath, did, null, null, messageObject.messageOwner.message, entities, null, params, notify, scheduleDate, 0, messageObject, null, false));
                } else if (messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue || messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo) {
                    getSendMessagesHelper().sendMessage(SendMessagesHelper.SendMessageParams.of(messageObject.messageOwner.media, did, null, null, null, null, notify, scheduleDate));
                } else if (messageObject.messageOwner.media.phone_number != null) {
                    TLRPC.User user = new TLRPC.TL_userContact_old2();
                    user.phone = messageObject.messageOwner.media.phone_number;
                    user.first_name = messageObject.messageOwner.media.first_name;
                    user.last_name = messageObject.messageOwner.media.last_name;
                    user.id = messageObject.messageOwner.media.user_id;
                    getSendMessagesHelper().sendMessage(SendMessagesHelper.SendMessageParams.of(user, did, null, null, null, null, notify, scheduleDate));
                } else if ((int) did != 0) {
                    ArrayList<MessageObject> arrayList = new ArrayList<>();
                    arrayList.add(messageObject);
                    getSendMessagesHelper().sendMessage(arrayList, did, false, false, notify, scheduleDate);
                }
            } else if (messageObject.messageOwner.message != null) {
                TLRPC.WebPage webPage = null;
                if (messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) {
                    webPage = messageObject.messageOwner.media.webpage;
                }
                getSendMessagesHelper().sendMessage(SendMessagesHelper.SendMessageParams.of(messageObject.messageOwner.message, did, null, null, webPage, webPage != null, entities, null, null, notify, scheduleDate, null, false));
            } else if ((int) did != 0) {
                ArrayList<MessageObject> arrayList = new ArrayList<>();
                arrayList.add(messageObject);
                getSendMessagesHelper().sendMessage(arrayList, did, true, false, notify, scheduleDate);
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
                long channelId = 0;
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
                AndroidUtilities.runOnUIThread(() -> getMessagesController().deleteMessages(ids, random_ids, null, dialog_id, 0, true, 0));
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
                        HashSet<Long> ids = new HashSet<>();
                        ArrayList<Integer> msgIds = new ArrayList<>();
                        ArrayList<Long> random_ids = new ArrayList<>();
                        for (int a = 0; a < res.messages.size(); a++) {
                            TLRPC.Message message = res.messages.get(a);
//                            ids.add(message.id);
                            msgIds.add(message.id);
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
                        for (long userId : ids) {
                            deleteUserChannelHistory(chat, userId, 0);
                        }
                        if (!msgIds.isEmpty()) {
                            getMessagesController().deleteMessages(msgIds, random_ids, null, dialog_id, 0, true, 0);
                        }
                        deleteChannelHistory(dialog_id, chat, lastMessageId);

                    }
                }
            } else {
                AlertUtil.showToast(error.code + ": " + error.text);
            }
        }), ConnectionsManager.RequestFlagFailOnServerErrors);
    }

    public void deleteUserChannelHistory(final TLRPC.Chat chat, long userId, int offset) {
        if (offset == 0) {
            getMessagesStorage().deleteUserChatHistory(chat.id, userId);
        }
        TLRPC.TL_channels_deleteParticipantHistory req = new TLRPC.TL_channels_deleteParticipantHistory();
        req.channel = getMessagesController().getInputChannel(chat.id);
        req.participant = getMessagesController().getInputPeer(userId);
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

    public void saveStickerToGallery(Context context, MessageObject messageObject) {
        if (messageObject.isAnimatedSticker()) return;
        // Animated Sticker is not supported.

        String path = messageObject.messageOwner.attachPath;
        if (!TextUtils.isEmpty(path)) {
            File temp = new File(path);
            if (!temp.exists()) {
                path = null;
            }
        }
        if (TextUtils.isEmpty(path)) {
            path = FileLoader.getInstance(currentAccount).getPathToMessage(messageObject.messageOwner).toString();
            File temp = new File(path);
            if (!temp.exists()) {
                path = null;
            }
        }
        if (TextUtils.isEmpty(path)) {
            path = FileLoader.getInstance(currentAccount).getPathToAttach(messageObject.getDocument(), true).toString();
        }
        if (!TextUtils.isEmpty(path)) {
            if (messageObject.isVideoSticker()) {
                MediaController.saveFile(path, context, 1, null, null);
            } else {
                try {
                    Bitmap image = BitmapFactory.decodeFile(path);
                    FileOutputStream stream = new FileOutputStream(path + ".png");
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();
                    MediaController.saveFile(path + ".png", context, 0, null, null);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    public void saveStickerToGallery(Context context, TLRPC.Document document) {
        String path = FileLoader.getInstance(currentAccount).getPathToAttach(document, true).toString();

        if (!TextUtils.isEmpty(path)) {
            if (MessageObject.isVideoSticker(document)) {
                MediaController.saveFile(path, context, 1, null, document.mime_type);
            } else {
                try {
                    Bitmap image = BitmapFactory.decodeFile(path);
                    FileOutputStream stream = new FileOutputStream(path + ".png");
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();
                    MediaController.saveFile(path + ".png", context, 0, null, null);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    public MessageObject getMessageForRepeat(MessageObject selectedObject, MessageObject.GroupedMessages selectedObjectGroup) {
        MessageObject messageObject = null;
        if (selectedObjectGroup != null && !selectedObjectGroup.isDocuments) {
            messageObject = getTargetMessageObjectFromGroup(selectedObjectGroup);
        } else if (!TextUtils.isEmpty(selectedObject.messageOwner.message) || selectedObject.isAnyKindOfSticker()) {
            messageObject = selectedObject;
        }
        return messageObject;
    }

    private MessageObject getTargetMessageObjectFromGroup(MessageObject.GroupedMessages selectedObjectGroup) {
        MessageObject messageObject = null;
        for (MessageObject object : selectedObjectGroup.messages) {
            if (!TextUtils.isEmpty(object.messageOwner.message)) {
                if (messageObject != null) {
                    messageObject = null;
                    break;
                } else {
                    messageObject = object;
                }
            }
        }
        return messageObject;
    }

    public void createDeleteHistoryAlert(BaseFragment fragment, TLRPC.Chat chat, TLRPC.TL_forumTopic forumTopic, long mergeDialogId, Theme.ResourcesProvider resourcesProvider) {
        createDeleteHistoryAlert(fragment, chat, forumTopic, mergeDialogId, -1, resourcesProvider);
    }

    private void createDeleteHistoryAlert(BaseFragment fragment, TLRPC.Chat chat, TLRPC.TL_forumTopic forumTopic, long mergeDialogId, int before, Theme.ResourcesProvider resourcesProvider) {
        if (fragment == null || fragment.getParentActivity() == null || chat == null) {
            return;
        }

        Context context = fragment.getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);

        CheckBoxCell cell = before == -1 && forumTopic == null && ChatObject.isChannel(chat) && ChatObject.canUserDoAction(chat, ChatObject.ACTION_DELETE_MESSAGES) ? new CheckBoxCell(context, 1, resourcesProvider) : null;

        TextView messageTextView = new TextView(context);
        messageTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        messageTextView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);

        FrameLayout frameLayout = new FrameLayout(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (cell != null) {
                    setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + cell.getMeasuredHeight() + AndroidUtilities.dp(7));
                }
            }
        };
        builder.setView(frameLayout);

        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(12));
        avatarDrawable.setInfo(chat);

        BackupImageView imageView = new BackupImageView(context);
        imageView.setRoundRadius(AndroidUtilities.dp(20));
        if (forumTopic != null) {
            if (forumTopic.id == 1) {
                imageView.setImageDrawable(ForumUtilities.createGeneralTopicDrawable(context, 0.75f, Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider), false));
            } else {
                ForumUtilities.setTopicIcon(imageView, forumTopic, false, true, resourcesProvider);
            }
        } else {
            imageView.setForUserOrChat(chat, avatarDrawable);
        }
        frameLayout.addView(imageView, LayoutHelper.createFrame(40, 40, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 22, 5, 22, 0));

        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(LocaleController.getString("DeleteAllFromSelf", R.string.DeleteAllFromSelf));

        frameLayout.addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, (LocaleController.isRTL ? 21 : 76), 11, (LocaleController.isRTL ? 76 : 21), 0));
        frameLayout.addView(messageTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 24, 57, 24, 9));

        if (cell != null) {
            boolean sendAs = ChatObject.getSendAsPeerId(chat, getMessagesController().getChatFull(chat.id), true) != getUserConfig().getClientUserId();
            cell.setBackground(Theme.getSelectorDrawable(false));
            cell.setText(LocaleController.getString("DeleteAllFromSelfAdmin", R.string.DeleteAllFromSelfAdmin), "", !ChatObject.shouldSendAnonymously(chat) && !sendAs, false);
            cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16) : AndroidUtilities.dp(8), 0, LocaleController.isRTL ? AndroidUtilities.dp(8) : AndroidUtilities.dp(16), 0);
            frameLayout.addView(cell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.BOTTOM | Gravity.LEFT, 0, 0, 0, 0));
            cell.setOnClickListener(v -> {
                CheckBoxCell cell1 = (CheckBoxCell) v;
                cell1.setChecked(!cell1.isChecked(), true);
            });
        }

        if (before > 0) {
            messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("DeleteAllFromSelfAlertBefore", R.string.DeleteAllFromSelfAlertBefore, LocaleController.formatDateForBan(before))));
        } else {
            messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("DeleteAllFromSelfAlert", R.string.DeleteAllFromSelfAlert)));
        }

        builder.setNeutralButton(LocaleController.getString("DeleteAllFromSelfBefore", R.string.DeleteAllFromSelfBefore), (dialog, which) -> showBeforeDatePickerAlert(fragment, before1 -> createDeleteHistoryAlert(fragment, chat, forumTopic, mergeDialogId, before1, resourcesProvider)));
        builder.setPositiveButton(LocaleController.getString("DeleteAll", R.string.DeleteAll), (dialogInterface, i) -> {
            if (cell != null && cell.isChecked()) {
                showDeleteHistoryBulletin(fragment, 0, false, () -> getMessagesController().deleteUserChannelHistory(chat, getUserConfig().getCurrentUser(), null, 0), resourcesProvider);
            } else {
                deleteUserHistoryWithSearch(fragment, -chat.id, forumTopic != null ? forumTopic.id : 0, mergeDialogId, before == -1 ? getConnectionsManager().getCurrentTime() : before, (count, deleteAction) -> showDeleteHistoryBulletin(fragment, count, true, deleteAction, resourcesProvider));
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog alertDialog = builder.create();
        fragment.showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_text_RedBold, resourcesProvider));
        }
    }

    private void showBeforeDatePickerAlert(BaseFragment fragment, Utilities.Callback<Integer> callback) {
        Context context = fragment.getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("DeleteAllFromSelfBefore", R.string.DeleteAllFromSelfBefore));
        builder.setItems(new CharSequence[]{
                LocaleController.formatPluralString("Days", 1),
                LocaleController.formatPluralString("Weeks", 1),
                LocaleController.formatPluralString("Months", 1),
                LocaleController.getString("UserRestrictionsCustom", R.string.UserRestrictionsCustom)
        }, (dialog1, which) -> {
            switch (which) {
                case 0:
                    callback.run(getConnectionsManager().getCurrentTime() - 60 * 60 * 24);
                    break;
                case 1:
                    callback.run(getConnectionsManager().getCurrentTime() - 60 * 60 * 24 * 7);
                    break;
                case 2:
                    callback.run(getConnectionsManager().getCurrentTime() - 60 * 60 * 24 * 30);
                    break;
                case 3: {
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog dateDialog = new DatePickerDialog(context, (view1, year1, month, dayOfMonth1) -> {
                        TimePickerDialog timeDialog = new TimePickerDialog(context, (view11, hourOfDay, minute) -> {
                            calendar.set(year1, month, dayOfMonth1, hourOfDay, minute);
                            callback.run((int) (calendar.getTimeInMillis() / 1000));
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                        timeDialog.setButton(DialogInterface.BUTTON_POSITIVE, LocaleController.getString("Set", R.string.Set), timeDialog);
                        timeDialog.setButton(DialogInterface.BUTTON_NEGATIVE, LocaleController.getString("Cancel", R.string.Cancel), (dialog3, which3) -> {
                        });
                        fragment.showDialog(timeDialog);
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                    final DatePicker datePicker = dateDialog.getDatePicker();

                    datePicker.setMinDate(1375315200000L);
                    datePicker.setMaxDate(System.currentTimeMillis());

                    dateDialog.setButton(DialogInterface.BUTTON_POSITIVE, LocaleController.getString("Set", R.string.Set), dateDialog);
                    dateDialog.setButton(DialogInterface.BUTTON_NEGATIVE, LocaleController.getString("Cancel", R.string.Cancel), (dialog2, which2) -> {
                    });
                    dateDialog.setOnShowListener(dialog12 -> {
                        int count = datePicker.getChildCount();
                        for (int b = 0; b < count; b++) {
                            View child = datePicker.getChildAt(b);
                            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
                            layoutParams.width = LayoutHelper.MATCH_PARENT;
                            child.setLayoutParams(layoutParams);
                        }
                    });
                    fragment.showDialog(dateDialog);
                    break;
                }
            }
            builder.getDismissRunnable().run();
        });
        fragment.showDialog(builder.create());
    }

    public static void showDeleteHistoryBulletin(BaseFragment fragment, int count, boolean search, Runnable delayedAction, Theme.ResourcesProvider resourcesProvider) {
        if (fragment.getParentActivity() == null) {
            if (delayedAction != null) {
                delayedAction.run();
            }
            return;
        }
        Bulletin.ButtonLayout buttonLayout;
        if (search) {
            final Bulletin.TwoLineLottieLayout layout = new Bulletin.TwoLineLottieLayout(fragment.getParentActivity(), resourcesProvider);
            layout.titleTextView.setText(LocaleController.getString("DeleteAllFromSelfDone", R.string.DeleteAllFromSelfDone));
            layout.subtitleTextView.setText(LocaleController.formatPluralString("MessagesDeletedHint", count));
            layout.setTimer();
            buttonLayout = layout;
        } else {
            final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment.getParentActivity(), resourcesProvider);
            layout.textView.setText(LocaleController.getString("DeleteAllFromSelfDone", R.string.DeleteAllFromSelfDone));
            layout.setTimer();
            buttonLayout = layout;
        }
        buttonLayout.setButton(new Bulletin.UndoButton(fragment.getParentActivity(), true, resourcesProvider).setDelayedAction(delayedAction));
        Bulletin.make(fragment, buttonLayout, Bulletin.DURATION_PROLONG).show();
    }

    private void deleteUserHistoryWithSearch(BaseFragment fragment, final long dialogId, int replyMessageId, final long mergeDialogId, int before, SearchMessagesResultCallback callback) {
        Utilities.globalQueue.postRunnable(() -> {
            ArrayList<Integer> messageIds = new ArrayList<>();
            var latch = new CountDownLatch(1);
            var peer = getMessagesController().getInputPeer(dialogId);
            var fromId = MessagesController.getInputPeer(getUserConfig().getCurrentUser());
            doSearchMessages(fragment, latch, messageIds, peer, replyMessageId, fromId, before, Integer.MAX_VALUE, 0);
            try {
                latch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!messageIds.isEmpty()) {
                ArrayList<ArrayList<Integer>> lists = new ArrayList<>();
                final int N = messageIds.size();
                for (int i = 0; i < N; i += 100) {
                    lists.add(new ArrayList<>(messageIds.subList(i, Math.min(N, i + 100))));
                }
                Runnable deleteAction = () -> {
                    for (ArrayList<Integer> list : lists) {
                        getMessagesController().deleteMessages(list, null, null, dialogId, 0, true, 0);
                    }
                };
                AndroidUtilities.runOnUIThread(callback != null ? () -> callback.run(messageIds.size(), deleteAction) : deleteAction);
            }
            if (mergeDialogId != 0) {
                deleteUserHistoryWithSearch(fragment, mergeDialogId, 0, 0, before, null);
            }
        });
    }

    private interface SearchMessagesResultCallback {
        void run(int count, Runnable deleteAction);
    }

    private void doSearchMessages(BaseFragment fragment, CountDownLatch latch, ArrayList<Integer> messageIds, TLRPC.InputPeer peer, int replyMessageId, TLRPC.InputPeer fromId, int before, int offsetId, long hash) {
        var req = new TLRPC.TL_messages_search();
        req.peer = peer;
        req.limit = 100;
        req.q = "";
        req.offset_id = offsetId;
        req.from_id = fromId;
        req.flags |= 1;
        req.filter = new TLRPC.TL_inputMessagesFilterEmpty();
        if (replyMessageId != 0) {
            req.top_msg_id = replyMessageId;
            req.flags |= 2;
        }
        req.hash = hash;
        getConnectionsManager().sendRequest(req, (response, error) -> {
            if (response instanceof TLRPC.messages_Messages) {
                var res = (TLRPC.messages_Messages) response;
                if (response instanceof TLRPC.TL_messages_messagesNotModified || res.messages.isEmpty()) {
                    latch.countDown();
                    return;
                }
                var newOffsetId = offsetId;
                for (TLRPC.Message message : res.messages) {
                    newOffsetId = Math.min(newOffsetId, message.id);
                    if (!message.out || message.post || message.date >= before) {
                        continue;
                    }
                    messageIds.add(message.id);
                }
                doSearchMessages(fragment, latch, messageIds, peer, replyMessageId, fromId, before, newOffsetId, calcMessagesHash(res.messages));
            } else {
                if (error != null) {
                    AndroidUtilities.runOnUIThread(() -> AlertsCreator.showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text));
                }
                latch.countDown();
            }
        }, ConnectionsManager.RequestFlagFailOnServerErrors);
    }

    private long calcMessagesHash(ArrayList<TLRPC.Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return 0;
        }
        long acc = 0;
        for (TLRPC.Message message : messages) {
            acc = MediaDataController.calcHash(acc, message.id);
        }
        return acc;
    }

    public static String getTextOrBase64(byte[] data) {
        try {
            return utf8Decoder.decode(ByteBuffer.wrap(data)).toString();
        } catch (CharacterCodingException e) {
            return Base64.encodeToString(data, Base64.NO_PADDING | Base64.NO_WRAP);
        }
    }
}
