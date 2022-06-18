/*
 * Copyright (C) 2019-2022 qwq233 <qwq233@qwq2333.top>
 * https://github.com/qwq233/Nullgram
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this software.
 *  If not, see
 * <https://www.gnu.org/licenses/>
 */

package top.qwq2333.nullgram.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BaseController;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;


public class MessageUtils extends BaseController {

    public static final ArrayList<DatacenterInfo> datacenterInfos = new ArrayList<>(5);

    static {
        for (int a = 1; a <= 5; a++) {
            datacenterInfos.add(new DatacenterInfo(a));
        }
    }

    private static final MessageUtils[] Instance = new MessageUtils[UserConfig.MAX_ACCOUNT_COUNT];

    public MessageUtils(int num) {
        super(num);
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

    public MessageObject getMessageForRepeat(MessageObject selectedObject, MessageObject.GroupedMessages selectedObjectGroup) {
        MessageObject messageObject = null;
        if (selectedObjectGroup != null && !selectedObjectGroup.isDocuments) {
            messageObject = getTargetMessageObjectFromGroup(selectedObjectGroup);
        } else if (!TextUtils.isEmpty(selectedObject.messageOwner.message) || selectedObject.isAnyKindOfSticker()) {
            messageObject = selectedObject;
        }
        return messageObject;
    }

    public static MessageUtils getInstance(int num) {
        MessageUtils localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (MessageUtils.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    Instance[num] = localInstance = new MessageUtils(num);
                }
            }
        }
        return localInstance;
    }

    public void createDeleteHistoryAlert(BaseFragment fragment, TLRPC.Chat chat, long mergeDialogId, Theme.ResourcesProvider resourcesProvider) {
        if (fragment == null || fragment.getParentActivity() == null || chat == null) {
            return;
        }

        Context context = fragment.getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);

        CheckBoxCell cell = ChatObject.isChannel(chat) && ChatObject.canUserDoAction(chat, ChatObject.ACTION_DELETE_MESSAGES) ? new CheckBoxCell(context, 1, resourcesProvider) : null;

        TextView messageTextView = new TextView(context);
        messageTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
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
        imageView.setForUserOrChat(chat, avatarDrawable);
        frameLayout.addView(imageView, LayoutHelper.createFrame(40, 40, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 22, 5, 22, 0));

        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
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

        messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("DeleteAllFromSelfAlert", R.string.DeleteAllFromSelfAlert)));

        builder.setPositiveButton(LocaleController.getString("DeleteAll", R.string.DeleteAll), (dialogInterface, i) -> {
            if (cell != null && cell.isChecked()) {
                getMessagesController().deleteUserChannelHistory(chat, getUserConfig().getCurrentUser(), null, 0);
            } else {
                deleteUserHistoryWithSearch(fragment, -chat.id, mergeDialogId, count -> {
                    if (BulletinFactory.canShowBulletin(fragment)) {
                        BulletinFactory.createDeleteMessagesBulletin(fragment, count, resourcesProvider).show();
                    }
                });
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog alertDialog = builder.create();
        fragment.showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
        }
    }


    public void deleteUserHistoryWithSearch(BaseFragment fragment, final long dialogId, final long mergeDialogId, MessagesStorage.IntCallback callback) {
        Utilities.globalQueue.postRunnable(() -> {
            ArrayList<Integer> messageIds = new ArrayList<>();
            var latch = new CountDownLatch(1);
            var peer = getMessagesController().getInputPeer(dialogId);
            var fromId = MessagesController.getInputPeer(getUserConfig().getCurrentUser());
            doSearchMessages(fragment, latch, messageIds, peer, fromId, Integer.MAX_VALUE, 0);
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
                AndroidUtilities.runOnUIThread(() -> {
                    for (ArrayList<Integer> list : lists) {
                        getMessagesController().deleteMessages(list, null, null, dialogId, true, false);
                    }
                    if (callback != null) {
                        callback.run(messageIds.size());
                    }
                });
            }
            if (mergeDialogId != 0) {
                deleteUserHistoryWithSearch(fragment, mergeDialogId, 0, null);
            }
        });
    }

    public void doSearchMessages(BaseFragment fragment, CountDownLatch latch, ArrayList<Integer> messageIds, TLRPC.InputPeer peer, TLRPC.InputPeer fromId, int offsetId, long hash) {
        var req = new TLRPC.TL_messages_search();
        req.peer = peer;
        req.limit = 100;
        req.q = "";
        req.offset_id = offsetId;
        req.from_id = fromId;
        req.flags |= 1;
        req.filter = new TLRPC.TL_inputMessagesFilterEmpty();
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
                    if (!message.out || message.post) {
                        continue;
                    }
                    messageIds.add(message.id);
                }
                doSearchMessages(fragment, latch, messageIds, peer, fromId, newOffsetId, calcMessagesHash(res.messages));
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
            layout.setAnimation(R.raw.ic_delete, "Envelope", "Cover", "Bucket");
            layout.titleTextView.setText(LocaleController.getString("DeleteAllFromSelfDone", R.string.DeleteAllFromSelfDone));
            layout.subtitleTextView.setText(LocaleController.formatPluralString("MessagesDeletedHint", count));
            buttonLayout = layout;
        } else {
            final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment.getParentActivity(), resourcesProvider);
            layout.setAnimation(R.raw.ic_delete, "Envelope", "Cover", "Bucket");
            layout.textView.setText(LocaleController.getString("DeleteAllFromSelfDone", R.string.DeleteAllFromSelfDone));
            buttonLayout = layout;
        }
        buttonLayout.setButton(new Bulletin.UndoButton(fragment.getParentActivity(), true, resourcesProvider).setDelayedAction(delayedAction));
        Bulletin.make(fragment, buttonLayout, 5000).show();
    }

    private interface SearchMessagesResultCallback {
        void run(int count, Runnable deleteAction);
    }


    public void saveStickerToGallery(Activity activity, MessageObject messageObject, Runnable callback) {
        String path = messageObject.messageOwner.attachPath;
        if (!TextUtils.isEmpty(path)) {
            File temp = new File(path);
            if (!temp.exists()) {
                path = null;
            }
        }
        if (TextUtils.isEmpty(path)) {
            path = FileLoader.getPathToMessage(messageObject.messageOwner).toString();
            File temp = new File(path);
            if (!temp.exists()) {
                path = null;
            }
        }
        if (TextUtils.isEmpty(path)) {
            path = FileLoader.getPathToAttach(messageObject.getDocument(), true).toString();
            File temp = new File(path);
            if (!temp.exists()) {
                return;
            }
        }
        saveStickerToGallery(activity, path, callback);
    }

    public static void saveStickerToGallery(Activity activity, TLRPC.Document document, Runnable callback) {
        String path = FileLoader.getPathToAttach(document, true).toString();
        File temp = new File(path);
        if (!temp.exists()) {
            return;
        }
        saveStickerToGallery(activity, path, callback);
    }

    private static void saveStickerToGallery(Activity activity, String path, Runnable callback) {
        Utilities.globalQueue.postRunnable(() -> {
            try {
                Bitmap image = BitmapFactory.decodeFile(path);
                if (image != null) {
                    File file = new File(path.replace(".webp", ".png"));
                    FileOutputStream stream = new FileOutputStream(file);
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();
                    MediaController.saveFile(file.toString(), activity, 0, null, null);
                    AndroidUtilities.runOnUIThread(callback);
                }
            } catch (Exception e) {
                Log.e(e);
            }
        });
    }

    public void addMessageToClipboard(MessageObject selectedObject, Runnable callback) {
        var path = getPathToMessage(selectedObject);
        if (!TextUtils.isEmpty(path)) {
            addFileToClipboard(new File(path), callback);
        }
    }

    public static void addFileToClipboard(File file, Runnable callback) {
        try {
            var context = ApplicationLoader.applicationContext;
            var clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            var uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            var clip = ClipData.newUri(context.getContentResolver(), "label", uri);
            clipboard.setPrimaryClip(clip);
            callback.run();
        } catch (Exception e) {
            Log.e(e);
        }
    }

    public String getPathToMessage(MessageObject messageObject) {
        String path = messageObject.messageOwner.attachPath;
        if (!TextUtils.isEmpty(path)) {
            File temp = new File(path);
            if (!temp.exists()) {
                path = null;
            }
        }
        if (TextUtils.isEmpty(path)) {
            path = FileLoader.getPathToMessage(messageObject.messageOwner).toString();
            File temp = new File(path);
            if (!temp.exists()) {
                path = null;
            }
        }
        if (TextUtils.isEmpty(path)) {
            path = FileLoader.getPathToAttach(messageObject.getDocument(), true).toString();
            File temp = new File(path);
            if (!temp.exists()) {
                return null;
            }
        }
        return path;
    }

    public static String getDCLocation(int dc) {
        switch (dc) {
            case 1:
            case 3:
                return "Miami";
            case 2:
            case 4:
                return "Amsterdam";
            case 5:
                return "Singapore";
            default:
                return "Unknown";
        }
    }

    public static String getDCName(int dc) {
        switch (dc) {
            case 1:
                return "Pluto";
            case 2:
                return "Venus";
            case 3:
                return "Aurora";
            case 4:
                return "Vesta";
            case 5:
                return "Flora";
            default:
                return "Unknown";
        }
    }


    public static class DatacenterInfo {

        public int id;

        public long pingId;
        public long ping;
        public boolean checking;
        public boolean available;
        public long availableCheckTime;

        public DatacenterInfo(int i) {
            id = i;
        }
    }


    private static final CharsetDecoder utf8Decoder = StandardCharsets.UTF_8.newDecoder();

    @SuppressLint("SetTextI18n")
    public void showSendCallbackDialog(ChatActivity fragment, Theme.ResourcesProvider resourcesProvider, byte[] originalData, MessageObject messageObject) {
        Context context = fragment.getParentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);
        builder.setTitle(LocaleController.getString("SendCallback", R.string.SendCallback));

        final EditTextBoldCursor editText = new EditTextBoldCursor(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64), MeasureSpec.EXACTLY));
            }
        };
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        try {
            editText.setText(utf8Decoder.decode(ByteBuffer.wrap(originalData)).toString());
        } catch (CharacterCodingException ignore) {
        }
        editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        editText.setHintText(LocaleController.getString("CallbackData", R.string.CallbackData));
        editText.setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader, resourcesProvider));
        editText.setSingleLine(true);
        editText.setFocusable(true);
        editText.setTransformHintToHeader(true);
        editText.setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField, resourcesProvider), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated, resourcesProvider), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3, resourcesProvider));
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setBackgroundDrawable(null);
        editText.requestFocus();
        editText.setPadding(0, 0, 0, 0);
        builder.setView(editText);

        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (dialogInterface, i) -> {
            var button = new TLRPC.TL_keyboardButtonCallback();
            button.data = editText.getText().toString().getBytes(StandardCharsets.UTF_8);
            getSendMessagesHelper().sendCallback(true, messageObject, button, fragment);
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.show().setOnShowListener(dialog -> {
            editText.requestFocus();
            AndroidUtilities.showKeyboard(editText);
        });
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) editText.getLayoutParams();
        if (layoutParams != null) {
            if (layoutParams instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) layoutParams).gravity = Gravity.CENTER_HORIZONTAL;
            }
            layoutParams.rightMargin = layoutParams.leftMargin = AndroidUtilities.dp(24);
            layoutParams.height = AndroidUtilities.dp(36);
            editText.setLayoutParams(layoutParams);
        }
        editText.setSelection(0, editText.getText().length());
    }

    public String getTextOrBase64(byte[] data) {
        try {
            return utf8Decoder.decode(ByteBuffer.wrap(data)).toString();
        } catch (CharacterCodingException e) {
            return Base64.encodeToString(data, Base64.NO_PADDING | Base64.NO_WRAP);
        }
    }

    public Bitmap createQR(String key) {
        try {
            HashMap<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 0);
            QRCodeWriter writer = new QRCodeWriter();
            return writer.encode(key, 768, 768, hints, null);
        } catch (Exception e) {
            Log.e(e);
        }
        return null;
    }

}
