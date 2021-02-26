package org.telegram.messenger.forkgram

import android.content.Context
import android.content.DialogInterface
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import org.telegram.messenger.*
import org.telegram.messenger.forkgram.ForkUtils.HasDocument
import org.telegram.messenger.forkgram.ForkUtils.HasPhoto
import org.telegram.tgnet.TLObject
import org.telegram.tgnet.TLRPC
import org.telegram.tgnet.TLRPC.*
import org.telegram.ui.ActionBar.*
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Components.AlertsCreator
import org.telegram.ui.Components.EditTextBoldCursor
import org.telegram.ui.Components.LayoutHelper
import kotlin.math.min

object ForkUtils {

@JvmStatic
fun HasPhotoOrDocument(messageObject: MessageObject): Boolean {
    return HasPhoto(messageObject) || HasDocument(messageObject);
}

@JvmStatic
fun HasPhoto(messageObject: MessageObject): Boolean {
    val media = messageObject.messageOwner.media;
    return (media != null)
        && (media.photo != null)
        && (media.photo is TLRPC.TL_photo);
}

@JvmStatic
fun HasDocument(messageObject: MessageObject): Boolean {
    val media = messageObject.messageOwner.media;
    return (media != null)
        && (media.document != null)
        && (media.document is TLRPC.TL_document);
}

@JvmStatic
public fun CreateVoiceCaptionAlert(
        context: Context,
        timestamps: ArrayList<String>,
        finish: (String) -> Unit) {
    val captionString = LocaleController.getString("Caption", R.string.Caption);

    val builder = AlertDialog.Builder(context);
    builder.setTitle(captionString);

    val textLayout = LinearLayout(context)
    textLayout.orientation = LinearLayout.HORIZONTAL

    val editText  = EditTextBoldCursor(context);
    editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f);
    editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
    editText.background = Theme.createEditTextDrawable(context, true);
    editText.isSingleLine = false;
    editText.isFocusable = true;
    editText.imeOptions = EditorInfo.IME_ACTION_DONE;
    editText.requestFocus();

    editText.setText(timestamps.foldIndexed("") { index, total, item ->
        total + "${index + 1}. $item \n";
    });

    val padding = AndroidUtilities.dp(0f);
    editText.setPadding(padding, 0, padding, 0);

    textLayout.addView(editText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 36))
    builder.setView(textLayout);
    builder.setPositiveButton(LocaleController.getString("Send", R.string.Send)) { _: DialogInterface?, _: Int ->
        finish(editText.text.toString());
    }
    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
    builder.show().setOnShowListener { dialog: DialogInterface? ->
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
    }

    val layoutParams = editText.layoutParams as MarginLayoutParams;
    if (layoutParams is FrameLayout.LayoutParams) {
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
    }
    layoutParams.leftMargin = AndroidUtilities.dp(24f);
    layoutParams.rightMargin = layoutParams.leftMargin;
    layoutParams.height = AndroidUtilities.dp(36f * 3);
    editText.layoutParams = layoutParams;
}

}

object AsCopy {

@JvmStatic
fun PerformForwardFromMyName(
        key: Long,
        text: String?,
        sendingMessageObjects: ArrayList<MessageObject>,
        currentAccount: Int,
        parentFragment: BaseFragment?,
        notify: Boolean) {

    val queue = ArrayList<() -> Unit>();
    val saveOriginalCaptions = (text == null)
    var replaceText = text
    val currentReplaceText = {
        val temp = replaceText;
        replaceText = "";
        if(saveOriginalCaptions) null else temp;
    };
    var groupedMsgs = ArrayList<MessageObject>()

    val deque = {
        if (queue.isNotEmpty()) {
            val copyLambda = queue[0]
            queue.removeAt(0)
            copyLambda()
        }
    }

    val sendAsAlbum = {
        val copyGrouped = ArrayList<MessageObject>(groupedMsgs);
        groupedMsgs = ArrayList()
        val copyText = currentReplaceText();
        queue.add {
            SendItemsAsAlbum(
                currentAccount,
                copyGrouped,
                key,
                parentFragment,
                copyText,
                notify,
                deque)
        }
    }

    for (msg in sendingMessageObjects) {
        if (msg.groupId != 0L) {
            if (groupedMsgs.isNotEmpty()) {
                if (groupedMsgs[0].groupId != msg.groupId) {
                    sendAsAlbum();
                }
            }
            groupedMsgs.add(msg)
            continue
        }
        if (groupedMsgs.isNotEmpty()) {
            sendAsAlbum();
        }
        val copyMsg = msg;
        val copyText = currentReplaceText();
        queue.add {
            val instance = SendMessagesHelper.getInstance(currentAccount);
            instance.processForwardFromMyName(copyMsg, key, copyText, notify)
            deque();
        }
    }
    if (groupedMsgs.isNotEmpty()) {
        sendAsAlbum();
    }
    deque();
}

@JvmStatic
fun GroupItemsIntoAlbum(
        key: Long,
        text: String?,
        sendingMessageObjects: ArrayList<MessageObject>,
        currentAccount: Int,
        parentFragment: BaseFragment?,
        notify: Boolean) {
    if (sendingMessageObjects.isEmpty()) {
        return;
    }

    val sub = { from: Int, to: Int ->
        ArrayList<MessageObject>(sendingMessageObjects.subList(from, to));
    };

    val objectsToSend = sub(0, min(10, sendingMessageObjects.size));
    val objectsToDelay = sub(objectsToSend.size, sendingMessageObjects.size);

    val finish = {
        GroupItemsIntoAlbum(key, text, objectsToDelay, currentAccount, parentFragment, notify)
    };

    SendItemsAsAlbum(
            currentAccount,
            objectsToSend,
            key,
            parentFragment,
            text,
            notify,
            finish)
}

fun inputMediaFromMessageObject(m: MessageObject): InputMedia {
    if (!(m.messageOwner.media != null
        && m.messageOwner.media !is TL_messageMediaEmpty
        && m.messageOwner.media !is TL_messageMediaWebPage
        && m.messageOwner.media !is TL_messageMediaGame
        && m.messageOwner.media !is TL_messageMediaInvoice)) {
        return TL_inputMediaEmpty()
    }
    if (HasDocument(m)) {
        val document = m.messageOwner.media.document
        val media = TL_inputMediaDocument()
        media.id = TL_inputDocument()
        media.id.id = document.id
        media.id.access_hash = document.access_hash
        media.id.file_reference = document.file_reference
        if (media.id.file_reference == null) {
            media.id.file_reference = ByteArray(0)
        }
        return media
    }
    if (HasPhoto(m)) {
        val photo = m.messageOwner.media.photo
        val media = TL_inputMediaPhoto()
        media.id = TL_inputPhoto()
        media.id.id = photo.id
        media.id.access_hash = photo.access_hash
        media.id.file_reference = photo.file_reference
        if (media.id.file_reference == null) {
            media.id.file_reference = ByteArray(0)
        }
        return media
    }
    return TL_inputMediaEmpty()
}

@JvmStatic
fun SendItemsAsAlbum(
        currentAccount: Int,
        messages: ArrayList<MessageObject>,
        peer: Long,
        fragment: BaseFragment?,
        replaceText: String?,
        notify: Boolean,
        finish: () -> Unit) {
    if (peer == 0L || messages.size > 10 || messages.isEmpty()) {
        return
    }
    val accountInstance = AccountInstance.getInstance(currentAccount)
    val lower_id = peer.toInt()
    val sendToPeer: InputPeer = 
        (if (lower_id != 0) accountInstance.messagesController.getInputPeer(lower_id)
            else null)
            ?: return
    val request = TL_messages_sendMultiMedia()
    request.peer = sendToPeer
    request.silent = !notify
    for (i in 0 until messages.size) {
        val m = messages[i]
        val media: InputMedia = inputMediaFromMessageObject(m)
        if (media is TL_inputMediaEmpty) {
            continue
        }
        val inputSingleMedia = TL_inputSingleMedia()
        inputSingleMedia.random_id = Utilities.random.nextLong()
        inputSingleMedia.media = media
        if (replaceText == null) {
            inputSingleMedia.message = m.messageOwner.message
            val entities = m.messageOwner.entities
            if (entities != null && entities.isNotEmpty()) {
                inputSingleMedia.entities = entities
                inputSingleMedia.flags = inputSingleMedia.flags or 1
            }
        } else {
            inputSingleMedia.message = if (request.multi_media.isEmpty()) replaceText else ""
        }
        request.multi_media.add(inputSingleMedia)
    }

    val showToast = { msg: String ->
        AndroidUtilities.runOnUIThread {
            Toast.makeText(
                ApplicationLoader.applicationContext,
                msg,
                Toast.LENGTH_LONG).show();
        }
    }

    val sendAlbum = sendRequest@{ response: TLObject?, error: TL_error? ->
        if (error == null) {
            accountInstance.messagesController.processUpdates(response as Updates, false)
            AndroidUtilities.runOnUIThread { finish(); }
            return@sendRequest
        }
        if (error != null) {
        }
        if (!FileRefController.isFileRefError(error.text)) {
            showToast("It seems that you want to group incompatible file types.");
            AndroidUtilities.runOnUIThread {
                AlertsCreator.processError(
                    currentAccount,
                    error,
                    fragment,
                    request)
            }
            return@sendRequest
        }
        // FileRefError.

        // Request messages, update file references and resend.
        val handleMessages = handleMessages@{ cloudMessages: ArrayList<TLRPC.Message>, msgErr: TL_error? ->
            if (cloudMessages.isEmpty()) {
                return@handleMessages
            }
            var atLeastOneFileRefUpdated = false;
            for (i in cloudMessages.indices) {
                val cloudMedia = cloudMessages[i].media;
                val localMedia = messages[i].messageOwner.media;
                if (cloudMedia == null) {
                    continue
                }
                if (cloudMedia.document != null && HasDocument(messages[i])) {
                    atLeastOneFileRefUpdated = true;
                    localMedia.document.file_reference = cloudMedia.document.file_reference
                }
                if (cloudMedia.photo != null && HasPhoto(messages[i])) {
                    atLeastOneFileRefUpdated = true;
                    localMedia.photo.file_reference = cloudMedia.photo.file_reference
                }
            }
            if (!atLeastOneFileRefUpdated) {
                showToast("Sorry, something went wrong.");
                return@handleMessages
            }
            SendItemsAsAlbum(currentAccount, messages, peer, fragment, replaceText, notify, finish)
        }

        val channelId: Int = messages[0].getChannelId()
        val api = accountInstance.connectionsManager;
        if (channelId != 0) {
            val req = TL_channels_getMessages()
            req.channel =
                accountInstance.messagesController.getInputChannel(channelId)
            for (i in 0 until messages.size) {
                req.id.add(messages[i].realId)
            }
            api.sendRequest(req) { msgResponse: TLObject?, msgErr: TL_error? ->
                val msgRes = msgResponse as messages_Messages
                handleMessages(msgRes.messages, msgErr)
            };
        } else {
            val req = TL_messages_getMessages()
            for (i in 0 until messages.size) {
                req.id.add(messages[i].realId)
            }
            api.sendRequest(req) { msgResponse: TLObject?, msgErr: TL_error? ->
                val msgRes = msgResponse as messages_Messages
                handleMessages(msgRes.messages, msgErr)
            };
        }

    };
    accountInstance.connectionsManager.sendRequest(request, sendAlbum)
}

}