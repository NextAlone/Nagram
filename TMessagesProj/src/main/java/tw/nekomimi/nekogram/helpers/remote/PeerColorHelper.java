package tw.nekomimi.nekogram.helpers.remote;

import android.text.TextUtils;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.HashMap;

public class PeerColorHelper extends BaseRemoteHelper {
    private static final String PEERCOLOR_TAG = "peercolor";
    private static volatile PeerColorHelper Instance;
    private final ArrayList<PeerColorInfo> peerColorInfo = new ArrayList<>();
    private final HashMap<Long, PeerColorInfo> peerColorInfoMap = new HashMap<>();
    private boolean loading = false;

    public static PeerColorHelper getInstance() {
        PeerColorHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (PeerColorHelper.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new PeerColorHelper();
                }
                return localInstance;
            }
        }
        return localInstance;
    }

    @Override
    protected void onError(String text, Delegate delegate) {

    }

    @Override
    protected String getTag() {
        return PEERCOLOR_TAG;
    }

    @Override
    protected void onLoadSuccess(ArrayList<JSONObject> responses, Delegate delegate) {
        var tag = getTag();
        var json = responses.size() > 0 ? responses.get(0) : null;
        if (json == null) {
            preferences.edit()
                    .remove(tag + "_update_time")
                    .remove(tag)
                    .apply();
            return;
        }

        try {
            ArrayList<PeerColorInfo> peerColors = new ArrayList<>();
            var array = json.getJSONArray("peerColors");

            for (int i = 0; i < array.length(); i++) {
                var obj = array.getJSONObject(i);
                var info = new PeerColorInfo(
                        obj.getLong("chat_id"),
                        obj.getInt("version")
                );
                info.setValue(obj);
                if (peerColorInfoMap.containsKey(info.chatId)) {
                    var oldInfo = peerColorInfoMap.get(info.chatId);
                    if (oldInfo != null && oldInfo.version == info.version) {
                        peerColors.add(oldInfo);
                    } else {
                        peerColors.add(info);
                    }
                } else {
                    peerColors.add(info);
                }
            }

            peerColorInfo.clear();
            peerColorInfo.addAll(peerColors);
            peerColorInfoMap.clear();
            for (PeerColorInfo info : peerColorInfo) {
                peerColorInfoMap.put(info.chatId, info);
            }
            savePeerColorInfo();
        } catch (JSONException e) {
            FileLog.e(e);
            delegate.onTLResponse(null, e.getLocalizedMessage());
        }
    }

    public void loadPeerColorInfo() {
        var tag = getTag();
        String list = preferences.getString(tag, "");
        peerColorInfo.clear();
        peerColorInfoMap.clear();
        if (!TextUtils.isEmpty(list)) {
            byte[] bytes = Base64.decode(list, Base64.DEFAULT);
            SerializedData data = new SerializedData(bytes);
            int count = data.readInt32(false);
            for (int a = 0; a < count; a++) {
                PeerColorInfo info = PeerColorInfo.deserialize(data);
                peerColorInfo.add(info);
                peerColorInfoMap.put(info.chatId, info);
            }
            data.cleanup();
        }
    }

    public void savePeerColorInfo() {
        var tag = getTag();
        SerializedData serializedData = new SerializedData();
        serializedData.writeInt32(peerColorInfo.size());
        for (PeerColorInfo info : peerColorInfo) {
            info.serializeToStream(serializedData);
        }
        preferences.edit()
                .putLong(tag + "_update_time", System.currentTimeMillis())
                .putString(tag, Base64.encodeToString(serializedData.toByteArray(), Base64.NO_WRAP | Base64.NO_PADDING))
                .apply();
        serializedData.cleanup();
    }

    public boolean needUpdate() {
        var tag = getTag();
        long oldTime = preferences.getLong(tag + "_update_time", 0L);
        long nowTime = System.currentTimeMillis();
        int TTL = 15 * 60;
        return oldTime + TTL <= nowTime;
    }

    public void checkPeerColor() {
        if (loading) {
            return;
        }
        loading = true;
        loadPeerColorInfo();
        if (needUpdate()) {
            load();
        }
        loading = false;
    }

    public Integer getColorId(long chatId) {
        if (!peerColorInfoMap.containsKey(chatId)) {
            return null;
        }
        var info = peerColorInfoMap.get(chatId);
        if (info != null && (info.flags & 1) != 0) {
            return info.colorId;
        }
        return null;
    }

    public Integer getColorId(TLRPC.User user) {
        return getColorId(user.id);
    }

    public Integer getColorId(TLRPC.Chat chat) {
        return getColorId(-chat.id);
    }

    public Long getEmojiId(long chatId) {
        if (!peerColorInfoMap.containsKey(chatId)) {
            return null;
        }
        var info = peerColorInfoMap.get(chatId);
        if (info != null && (info.flags & 2) != 0) {
            return info.emojiId;
        }
        return null;
    }

    public Long getEmojiId(TLRPC.User user) {
        if (user == null) return null;
        return getEmojiId(user.id);
    }

    public Long getEmojiId(TLRPC.Chat chat) {
        if (chat == null) return null;
        return getEmojiId(-chat.id);
    }

    public Integer getProfileColorId(long chatId) {
        if (!peerColorInfoMap.containsKey(chatId)) {
            return null;
        }
        var info = peerColorInfoMap.get(chatId);
        if (info != null && (info.flags & 4) != 0) {
            return info.profileColorId;
        }
        return null;
    }

    public Integer getProfileColorId(TLRPC.User user) {
        return getProfileColorId(user.id);
    }

    public Integer getProfileColorId(TLRPC.Chat chat) {
        return getProfileColorId(-chat.id);
    }

    public Long getProfileEmojiId(long chatId) {
        if (!peerColorInfoMap.containsKey(chatId)) {
            return null;
        }
        var info = peerColorInfoMap.get(chatId);
        if (info != null && (info.flags & 8) != 0) {
            return info.profileEmojiId;
        }
        return null;
    }

    public Long getProfileEmojiId(TLRPC.User user) {
        if (user == null) return null;
        return getProfileEmojiId(user.id);
    }

    public Long getProfileEmojiId(TLRPC.Chat chat) {
        if (chat == null) return null;
        return getEmojiId(-chat.id);
    }

    public static class PeerColorInfo {
        private int flags;
        public long chatId;
        public int version;
        public int colorId;
        public long emojiId;
        public int profileColorId;
        public long profileEmojiId;

        public PeerColorInfo(long chatId, int version) {
            this.chatId = chatId;
            this.version = version;
        }

        public PeerColorInfo() {
        }

        public void setValue(JSONObject obj) {
            this.setColorId(obj);
            this.setEmojiId(obj);
            this.setProfileColorId(obj);
            this.setProfileEmojiId(obj);
        }

        public void setColorId(JSONObject obj) {
            try {
                this.colorId = obj.getInt("colorId");
                this.flags |= 1;
            } catch (Exception ignored) {
            }
        }

        public void setEmojiId(JSONObject obj) {
            try {
                this.emojiId = obj.getLong("emojiId");
                this.flags |= 2;
            } catch (Exception ignored) {
            }
        }

        public void setProfileColorId(JSONObject obj) {
            try {
                this.profileColorId = obj.getInt("profileColorId");
                this.flags |= 4;
            } catch (Exception ignored) {
            }
        }

        public void setProfileEmojiId(JSONObject obj) {
            try {
                this.profileEmojiId = obj.getLong("profileEmojiId");
                this.flags |= 8;
            } catch (Exception ignored) {
            }
        }

        public static PeerColorInfo deserialize(AbstractSerializedData stream) {
            PeerColorInfo peerColorInfo = new PeerColorInfo();
            peerColorInfo.flags = stream.readInt32(false);
            peerColorInfo.chatId = stream.readInt64(false);
            peerColorInfo.version = stream.readInt32(false);
            if ((peerColorInfo.flags & 1) != 0) {
                peerColorInfo.colorId = stream.readInt32(false);
            }
            if ((peerColorInfo.flags & 2) != 0) {
                peerColorInfo.emojiId = stream.readInt64(false);
            }
            if ((peerColorInfo.flags & 4) != 0) {
                peerColorInfo.profileColorId = stream.readInt32(false);
            }
            if ((peerColorInfo.flags & 8) != 0) {
                peerColorInfo.profileEmojiId = stream.readInt64(false);
            }
            return peerColorInfo;
        }

        public void serializeToStream(AbstractSerializedData serializedData) {
            serializedData.writeInt32(flags);
            serializedData.writeInt64(chatId);
            serializedData.writeInt32(version);
            if ((flags & 1) != 0) {
                serializedData.writeInt32(colorId);
            }
            if ((flags & 2) != 0) {
                serializedData.writeInt64(emojiId);
            }
            if ((flags & 4) != 0) {
                serializedData.writeInt32(profileColorId);
            }
            if ((flags & 8) != 0) {
                serializedData.writeInt64(profileEmojiId);
            }
        }
    }
}
