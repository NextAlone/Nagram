package tw.nekomimi.nekogram.utils;


import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.BaseCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BulletinFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TelegramUtil {

    public static String getFileNameWithoutEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    // 消息是否为文件
    public static boolean messageObjectIsFile(int type, MessageObject messageObject) {
        boolean cansave = (type == 4 || type == 5 || type == 6 || type == 10);
        boolean downloading = messageObject.loadedFileSize > 0;

        //图片的问题
        if (type == 4 && messageObject.getDocument() == null) {
            return false;
        }
        return cansave || downloading;
    }

    // 当文件有过加载过程，loadedFileSize > 0 ，所以不能用loadedFileSize判断是否正在下载
    public static boolean messageObjectIsDownloading(int type) {
        boolean cansave = (type == 4 || type == 5 || type == 6 || type == 10);
        return !cansave;
    }

    private static List<TLRPC.Document> messageDocuments(MessageObject message) {
        if (message == null) {
            return Collections.emptyList();
        }
        List<TLRPC.Document> out = new ArrayList<>(2);
        TLRPC.Document doc = message.getDocument();
        if (doc != null) {
            out.add(doc);
        }
        if (message.messageOwner != null && message.messageOwner.media != null && message.messageOwner.media.alt_documents != null) {
            out.addAll(message.messageOwner.media.alt_documents);
        }
        return out;
    }

    private static List<File> partialDownloadFiles(TLRPC.Document doc) {
        File cacheDir = FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE);
        if (cacheDir == null || doc == null) {
            return Collections.emptyList();
        }
        String base = doc.dc_id + "_" + doc.id;
        return Arrays.asList(
                new File(cacheDir, base + ".temp"),
                new File(cacheDir, base + ".temp.enc"),
                new File(cacheDir, base + "_64.pt"),
                new File(cacheDir, base + "_64.preload"),
                new File(cacheDir, base + "_64.iv"),
                new File(cacheDir, base + "_64.iv.enc")
        );
    }

    private static boolean hasPartialDownload(MessageObject message) {
        File cacheDir = FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE);
        if (cacheDir == null) {
            return false;
        }
        for (TLRPC.Document doc : messageDocuments(message)) {
            String base = doc.dc_id + "_" + doc.id;
            if (new File(cacheDir, base + ".temp").exists() || new File(cacheDir, base + ".temp.enc").exists()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasCachedFile(MessageObject selected, MessageObject.GroupedMessages group) {
        if (selected == null && group == null) {
            return false;
        }
        List<MessageObject> list = group != null ? group.messages : Collections.singletonList(selected);
        for (MessageObject msg : list) {
            if (msg != null && (msg.mediaExists || msg.attachPathExists || hasPartialDownload(msg))) {
                return true;
            }
        }
        return false;
    }

    private static void addIfManaged(List<File> out, File file, String telegramPath) {
        if (file != null && file.getAbsolutePath().startsWith(telegramPath)) {
            out.add(file);
        }
    }

    private static List<File> cachedFilesForMessage(int currentAccount, MessageObject message) {
        if (message == null || message.messageOwner == null) {
            return Collections.emptyList();
        }
        TLRPC.Message owner = message.messageOwner;
        FileLoader loader = FileLoader.getInstance(currentAccount);
        String telegramPath = EnvUtil.getTelegramPath().getAbsolutePath();
        List<File> out = new ArrayList<>(8);
        if (owner.attachPath != null && !owner.attachPath.isEmpty()) {
            addIfManaged(out, new File(owner.attachPath), telegramPath);
        }
        addIfManaged(out, loader.getPathToMessage(owner), telegramPath);
        for (TLRPC.Document doc : messageDocuments(message)) {
            addIfManaged(out, loader.getPathToAttach(doc, false), telegramPath);
            addIfManaged(out, loader.getPathToAttach(doc, true), telegramPath);
            for (File partial : partialDownloadFiles(doc)) {
                addIfManaged(out, partial, telegramPath);
            }
        }
        return out;
    }

    public static void clearMessageCaches(ChatActivity activity, List<MessageObject> messages) {
        if (activity == null || messages == null || messages.isEmpty()) {
            return;
        }
        int account = activity.getCurrentAccount();
        FileLoader loader = FileLoader.getInstance(account);
        for (MessageObject msg : messages) {
            if (msg == null) {
                continue;
            }
            TLRPC.Document doc = msg.getDocument();
            if (doc != null) {
                loader.cancelLoadFile(doc, true);
            }
            TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(msg.photoThumbs, AndroidUtilities.getPhotoSize(true));
            if (photoSize != null) {
                loader.cancelLoadFile(photoSize, true);
            }
        }
        Utilities.globalQueue.postRunnable(() -> {
            for (MessageObject msg : messages) {
                if (msg == null) {
                    continue;
                }
                for (File file : cachedFilesForMessage(account, msg)) {
                    try {
                        if (file.exists() && !file.delete()) {
                            file.deleteOnExit();
                        }
                    } catch (Exception ignore) {
                    }
                }
                msg.checkMediaExistance();
            }
            AndroidUtilities.runOnUIThread(() -> {
                for (MessageObject msg : messages) {
                    if (msg == null) {
                        continue;
                    }
                    msg.loadingCancelled = true;
                    BaseCell cell = activity.findMessageCell(msg.getId(), false);
                    if (cell instanceof ChatMessageCell) {
                        ((ChatMessageCell) cell).updateButtonState(false, true, false);
                    }
                }
                BulletinFactory.of(activity)
                        .createSimpleBulletin(R.raw.ic_delete, LocaleController.getString(R.string.DeleteDownloadedFileSucceed))
                        .show();
            });
        });
    }

}