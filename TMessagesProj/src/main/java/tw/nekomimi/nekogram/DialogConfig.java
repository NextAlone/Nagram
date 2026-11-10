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

    public static final int FORUM_LAYOUT_DEFAULT = 0;
    public static final int FORUM_LAYOUT_TABS = 1;
    public static final int FORUM_LAYOUT_LIST = 2;
    public static final int FORUM_LAYOUT_SIDEBAR = 3;

    public static String getCustomForumTabsKey(long dialogId) {
        return "customForumTabs_" + dialogId;
    }

    private static String getCustomForumTabsModeKey(long dialogId) {
        return "customForumTabsMode_" + dialogId;
    }

    public static int getCustomForumTabsMode(long dialogId) {
        final String key = getCustomForumTabsModeKey(dialogId);
        if (preferences.contains(key)) {
            return preferences.getInt(key, FORUM_LAYOUT_DEFAULT);
        }
        // migrate the old boolean config: true meant tabs, false meant list
        final String oldKey = getCustomForumTabsKey(dialogId);
        if (preferences.contains(oldKey)) {
            final int mode = preferences.getBoolean(oldKey, false) ? FORUM_LAYOUT_TABS : FORUM_LAYOUT_LIST;
            preferences.edit().putInt(key, mode).remove(oldKey).apply();
            return mode;
        }
        return FORUM_LAYOUT_DEFAULT;
    }

    public static void setCustomForumTabsMode(long dialogId, int mode) {
        final SharedPreferences.Editor editor = preferences.edit().remove(getCustomForumTabsKey(dialogId));
        if (mode == FORUM_LAYOUT_DEFAULT) {
            editor.remove(getCustomForumTabsModeKey(dialogId));
        } else {
            editor.putInt(getCustomForumTabsModeKey(dialogId), mode);
        }
        editor.apply();
    }

    public static void removeCustomForumTabsConfig(long dialogId) {
        preferences.edit().remove(getCustomForumTabsModeKey(dialogId)).remove(getCustomForumTabsKey(dialogId)).apply();
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
