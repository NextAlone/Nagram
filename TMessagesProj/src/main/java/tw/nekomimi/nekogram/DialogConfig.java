package tw.nekomimi.nekogram;

import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import xyz.nextalone.nagram.NaConfig;

public class DialogConfig {
    private static final SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekodialogconfig", Context.MODE_PRIVATE);

    public static String getAutoTranslateKey(long dialogId, long topicId) {
        return "autoTranslate_" + dialogId + (topicId != 0 ? "_" + topicId : "");
    }

    public static boolean isAutoTranslateEnable(long dialogId, long topicId) {
        return preferences.getBoolean(getAutoTranslateKey(dialogId, topicId), NaConfig.INSTANCE.getAutoTranslate().Bool());
    }

    public static boolean hasAutoTranslateConfig(long dialogId, long topicId) {
        return preferences.contains(getAutoTranslateKey(dialogId, topicId));
    }

    public static void setAutoTranslateEnable(long dialogId, long topicId, boolean enable) {
        preferences.edit().putBoolean(getAutoTranslateKey(dialogId, topicId), enable).apply();
    }

    public static void removeAutoTranslateConfig(long dialogId, long topicId) {
        preferences.edit().remove(getAutoTranslateKey(dialogId, topicId)).apply();
    }

    public static String getCustomForumTabsKey(long dialogId) {
        return "customForumTabs_" + dialogId;
    }

    public static boolean isCustomForumTabsEnable(long dialogId) {
        return preferences.getBoolean(getCustomForumTabsKey(dialogId), false);
    }

    public static boolean hasCustomForumTabsConfig(long dialogId) {
        return preferences.contains(getCustomForumTabsKey(dialogId));
    }

    public static void setCustomForumTabsEnable(long dialogId, boolean enable) {
        preferences.edit().putBoolean(getCustomForumTabsKey(dialogId), enable).apply();
    }

    public static void removeCustomForumTabsConfig(long dialogId) {
        preferences.edit().remove(getCustomForumTabsKey(dialogId)).apply();
    }

    public static String getShareTargetKey(long dialogId) {
        return "sharetarget_" + dialogId;
    }

    public static boolean isShareTargetEnable(long dialogId) {
        return preferences.getBoolean(getShareTargetKey(dialogId), false);
    }

    public static boolean hasShareTargetConfig(long dialogId) {
        return preferences.contains(getShareTargetKey(dialogId));
    }

    public static void setShareTargetEnable(long dialogId, boolean enable) {
        preferences.edit().putBoolean(getShareTargetKey(dialogId), enable).apply();
    }

    public static void removeShareTargetConfig(long dialogId) {
        preferences.edit().remove(getShareTargetKey(dialogId)).apply();
    }

    public static ArrayList<Long> getShareTargetAll(boolean enable) {
        ArrayList<Long> resultList = new ArrayList<>();

        Map<String, ?> allEntries = preferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.startsWith("sharetarget_") && value instanceof Boolean && ((Boolean) value == enable)) {
                try {
                    String numberPart = key.substring("sharetarget_".length());
                    long longValue = Long.parseLong(numberPart);
                    resultList.add(longValue);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultList;
    }

    public static ArrayList<TLRPC.TL_topPeer> getShareTargetUpdate() {
        ArrayList<Long> ids = getShareTargetAll(true);
        ArrayList<TLRPC.TL_topPeer> resultList = new ArrayList<>();

        for (Long peerId : ids) {
            if (!DialogObject.isUserDialog(peerId)) {
                continue;
            }
            TLRPC.TL_peerUser peer = new TLRPC.TL_peerUser();
            peer.user_id = peerId;
            TLRPC.TL_topPeer topPeer = new TLRPC.TL_topPeer();
            topPeer.peer = peer;
            topPeer.rating = 0;
            resultList.add(topPeer);
        }
        return resultList;
    }

    public static void modifyShareTarget(ArrayList<TLRPC.TL_topPeer> hintsFinal) {
        ArrayList<Long> toDelete = getShareTargetAll(false);
        ArrayList<TLRPC.TL_topPeer> toAdd = getShareTargetUpdate();

        if (!toDelete.isEmpty()) {
            Iterator<TLRPC.TL_topPeer> iterator = hintsFinal.iterator();
            while (iterator.hasNext()) {
                TLRPC.TL_topPeer peer = iterator.next();
                if (peer.peer != null && toDelete.contains(MessageObject.getPeerId(peer.peer))) {
                    iterator.remove();
                }
            }
        }

        if (!toAdd.isEmpty()) {
            hintsFinal.addAll(0, toAdd);
        }
    }
}
