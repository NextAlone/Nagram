package tw.nekomimi.nekogram;

import android.os.Build;

import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.webrtc.EglBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import tw.nekomimi.nekogram.utils.FileUtil;

//TODO use UpdateAppAlertDialog / BlockingUpdateView?

public class InternalUpdater {

    static final int UPDATE_METADATA_START_FROM = 0;
    static final int MAX_READ_COUNT = 20;
    static final long CHANNEL_METADATA_ID = 1793245134;
    static final String CHANNEL_METADATA_NAME = "nagram_metadata";
    static final long CHANNEL_APKS_ID = 1645699549;
    static final String CHANNEL_APKS_NAME = "nagram_channel";

    static void retrieveUpdateMetadata(retrieveUpdateMetadataCallback callback) {
        final int localVersionCode = BuildVars.BUILD_VERSION;
        AccountInstance accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
        TLRPC.TL_messages_getHistory req = new TLRPC.TL_messages_getHistory();
        req.peer = accountInstance.getMessagesController().getInputPeer(-CHANNEL_METADATA_ID);
        req.offset_id = 0;
        req.limit = MAX_READ_COUNT;
        Runnable sendReq = () -> accountInstance.getConnectionsManager().sendRequest(req, (response, error) -> {
            if (error != null) {
                FileLog.e("Error when retrieving update metadata from channel " + error);
                callback.apply(null, true);
                return;
            }
            try {
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                List<UpdateMetadata> metas = new ArrayList<>();
                for (TLRPC.Message message : res.messages) {
                    if (!(message instanceof TLRPC.TL_message)) continue;
                    if (!message.message.startsWith("v")) continue;
                    String[] split = message.message.split(",");
                    if (split.length < 4) continue;
                    UpdateMetadata metaData = new UpdateMetadata(message.id, split);
                    metas.add(metaData);
                }
                Collections.sort(metas, (o1, o2) -> o2.versionCode - o1.versionCode); // versionCode Desc
                UpdateMetadata found = null;
                for (UpdateMetadata metaData : metas) {
                    if (metaData.versionCode <= localVersionCode) break;
                    if (NekoXConfig.autoUpdateReleaseChannel < 3 && metaData.versionName.contains("preview"))
                        continue;
                    if (NekoXConfig.autoUpdateReleaseChannel < 2 && metaData.versionName.contains("rc"))
                        continue;
                    found = metaData;
                    break;
                }
                if (found != null) {
                    for (TLRPC.Message message : res.messages) {
                        if (!(message instanceof TLRPC.TL_message)) continue;
                        if (message.id == found.UpdateLogMessageID) {
                            found.updateLog = message.message;
                            found.updateLogEntities = message.entities;
                            break;
                        }
                    }
                }
                if (found == null) {
                    FileLog.d("Cannot find Update Metadata");
                    callback.apply(null, false);
                    return;
                }
                FileLog.w("Found Update Metadata " + found.versionName + " " + found.versionCode);
                callback.apply(found, false);
            } catch (Exception e) {
                FileLog.e(e);
                callback.apply(null, true);
            }
        });
        if (req.peer.access_hash != 0) sendReq.run();
        else {
            TLRPC.TL_contacts_resolveUsername resolve = new TLRPC.TL_contacts_resolveUsername();
            resolve.username = CHANNEL_METADATA_NAME;
            accountInstance.getConnectionsManager().sendRequest(resolve, (response1, error1) -> {
                if (error1 != null) {
                    FileLog.e("Error when checking update, unable to resolve metadata channel " + error1.text);
                    callback.apply(null, true);
                    return;
                }
                if (!(response1 instanceof TLRPC.TL_contacts_resolvedPeer)) {
                    FileLog.e("Error when checking update, unable to resolve metadata channel, unexpected responseType " + response1.getClass().getName());
                    callback.apply(null, true);
                    return;
                }
                TLRPC.TL_contacts_resolvedPeer resolvedPeer = (TLRPC.TL_contacts_resolvedPeer) response1;
                accountInstance.getMessagesController().putUsers(resolvedPeer.users, false);
                accountInstance.getMessagesController().putChats(resolvedPeer.chats, false);
                accountInstance.getMessagesStorage().putUsersAndChats(resolvedPeer.users, resolvedPeer.chats, false, true);
                if ((resolvedPeer.chats == null || resolvedPeer.chats.size() == 0)) {
                    FileLog.e("Error when checking update, unable to resolve metadata channel, unexpected resolvedChat ");
                    callback.apply(null, true);
                    return;
                }
                req.peer = new TLRPC.TL_inputPeerChannel();
                req.peer.channel_id = resolvedPeer.chats.get(0).id;
                req.peer.access_hash = resolvedPeer.chats.get(0).access_hash;
                sendReq.run();
            });
        }
    }

    public static void checkUpdate(checkUpdateCallback callback) {
        AccountInstance accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
        retrieveUpdateMetadata((metadata, err) -> {
            if (metadata == null) {
                callback.apply(null, err);
                return;
            }

            TLRPC.TL_messages_getHistory req = new TLRPC.TL_messages_getHistory();
            req.peer = accountInstance.getMessagesController().getInputPeer(-CHANNEL_APKS_ID);
            req.min_id = metadata.apkChannelMessageID;
            req.limit = MAX_READ_COUNT;

            Runnable sendReq = () -> accountInstance.getConnectionsManager().sendRequest(req, (response, error) -> {
                if (error != null) {
                    FileLog.e("Error when getting update document " + error.text);
                    callback.apply(null, true);
                    return;
                }
                try {
                    TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                    FileLog.d("Retrieve update messages, size:" + res.messages.size());
                    final String target = metadata.versionName + "(" + metadata.versionCode + ")" + "-" + Build.SUPPORTED_ABIS[0].toLowerCase(Locale.ROOT) + ".apk";
                    for (int i = 0; i < res.messages.size(); i++) {
                        if (res.messages.get(i).media == null) continue;

                        TLRPC.Document apkDocument = res.messages.get(i).media.document;
                        if (apkDocument.attributes == null) continue;
                        String fileName = apkDocument.attributes.size() == 0 ? "" : apkDocument.attributes.get(0).file_name;
                        if (!fileName.contains(target))
                            continue;
                        TLRPC.TL_help_appUpdate update = new TLRPC.TL_help_appUpdate();
                        update.version = metadata.versionName;
                        update.document = apkDocument;
                        update.can_not_skip = false;
                        update.flags |= 2;
                        if (metadata.updateLog != null) {
                            update.text = metadata.updateLog;
                            update.entities = metadata.updateLogEntities;
                        }
                        callback.apply(update, false);
                        return;
                    }
                    callback.apply(null, false);
                } catch (Exception e) {
                    FileLog.e(e);
                    callback.apply(null, true);
                }
            });
            if (req.peer.access_hash != 0) sendReq.run();
            else {
                TLRPC.TL_contacts_resolveUsername resolve = new TLRPC.TL_contacts_resolveUsername();
                resolve.username = CHANNEL_APKS_NAME;
                accountInstance.getConnectionsManager().sendRequest(resolve, (response1, error1) -> {
                    if (error1 != null) {
                        FileLog.e("Error when checking update, unable to resolve metadata channel " + error1);
                        callback.apply(null, true);
                        return;
                    }
                    if (!(response1 instanceof TLRPC.TL_contacts_resolvedPeer)) {
                        FileLog.e("Error when checking update, unable to resolve metadata channel, unexpected responseType " + response1.getClass().getName());
                        callback.apply(null, true);
                        return;
                    }
                    TLRPC.TL_contacts_resolvedPeer resolvedPeer = (TLRPC.TL_contacts_resolvedPeer) response1;
                    accountInstance.getMessagesController().putUsers(resolvedPeer.users, false);
                    accountInstance.getMessagesController().putChats(resolvedPeer.chats, false);
                    accountInstance.getMessagesStorage().putUsersAndChats(resolvedPeer.users, resolvedPeer.chats, false, true);
                    if ((resolvedPeer.chats == null || resolvedPeer.chats.size() == 0)) {
                        FileLog.e("Error when checking update, unable to resolve metadata channel, unexpected resolvedChat ");
                        callback.apply(null, true);
                        return;
                    }
                    req.peer = new TLRPC.TL_inputPeerChannel();
                    req.peer.channel_id = resolvedPeer.chats.get(0).id;
                    req.peer.access_hash = resolvedPeer.chats.get(0).access_hash;
                    sendReq.run();
                });
            }
        });


    }

    public interface retrieveUpdateMetadataCallback {
        void apply(UpdateMetadata metadata, boolean error);
    }

    public interface checkUpdateCallback {
        void apply(TLRPC.TL_help_appUpdate resp, boolean error);
    }

    static class UpdateMetadata {
        int messageID;
        String versionName;
        int versionCode;
        int apkChannelMessageID;
        int UpdateLogMessageID;
        String updateLog = null;
        ArrayList<TLRPC.MessageEntity> updateLogEntities = null;

        UpdateMetadata(int messageID, String[] split) {
            this.messageID = messageID;
            versionName = split[0];
            versionCode = Integer.parseInt(split[1]);
            apkChannelMessageID = Integer.parseInt(split[2]);
            UpdateLogMessageID = Integer.parseInt(split[3]);
        }
    }

}
