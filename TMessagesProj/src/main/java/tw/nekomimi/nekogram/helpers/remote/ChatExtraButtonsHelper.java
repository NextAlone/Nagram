package tw.nekomimi.nekogram.helpers.remote;

import android.text.TextUtils;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.SerializedData;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatExtraButtonsHelper extends BaseRemoteHelper {
    private static final String CHAT_EXTRA_BUTTONS_TAG = "chatButtonsV1";
    private static volatile ChatExtraButtonsHelper Instance;
    private final ArrayList<ChatExtraButtonInfo> buttons = new ArrayList<>();
    private final HashMap<Long, ArrayList<ChatExtraButtonInfo>> buttonsMap = new HashMap<>();
    private boolean loading = false;

    public static ChatExtraButtonsHelper getInstance() {
        ChatExtraButtonsHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (ChatExtraButtonsHelper.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new ChatExtraButtonsHelper();
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
        return CHAT_EXTRA_BUTTONS_TAG;
    }

    @Override
    protected void onLoadSuccess(ArrayList<JSONObject> responses, Delegate delegate) {
        var tag = getTag();
        var json = !responses.isEmpty() ? responses.get(0) : null;
        if (json == null) {
            preferences.edit()
                    .remove(tag + "_update_time")
                    .remove(tag)
                    .apply();
            return;
        }

        try {
            ArrayList<ChatExtraButtonInfo> buttonInfo = new ArrayList<>();
            var array = json.getJSONArray("buttons");

            for (int i = 0; i < array.length(); i++) {
                var obj = array.getJSONObject(i);
                int type = obj.getInt("type");
                long chatId = obj.getLong("chat_id");
                String name = obj.getString("name");
                String url = obj.getString("url");
                var info = new ChatExtraButtonInfo(
                        type,
                        chatId,
                        name,
                        url
                );
                buttonInfo.add(info);
            }

            buttons.clear();
            buttons.addAll(buttonInfo);
            buttonsMap.clear();
            for (ChatExtraButtonInfo info : buttons) {
                putToMap(info);
            }
            saveChatExtraButtons();
        } catch (JSONException e) {
            FileLog.e(e);
        }
    }

    private void putToMap(ChatExtraButtonInfo info) {
        ArrayList<ChatExtraButtonInfo> arrayList = buttonsMap.get(info.chatId);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        arrayList.add(info);
        buttonsMap.put(info.chatId, arrayList);
    }

    public ArrayList<ChatExtraButtonInfo> getChatExtraButtons(long chatId) {
        return buttonsMap.get(chatId);
    }

    public void loadPagePreviewRules() {
        var tag = getTag();
        String list = preferences.getString(tag, "");
        buttons.clear();
        buttonsMap.clear();
        if (!TextUtils.isEmpty(list)) {
            byte[] bytes = Base64.decode(list, Base64.DEFAULT);
            SerializedData data = new SerializedData(bytes);
            int count = data.readInt32(false);
            for (int a = 0; a < count; a++) {
                ChatExtraButtonInfo info = ChatExtraButtonInfo.deserialize(data);
                buttons.add(info);
                putToMap(info);
            }
            data.cleanup();
        }
    }

    public void saveChatExtraButtons() {
        var tag = getTag();
        SerializedData serializedData = new SerializedData();
        serializedData.writeInt32(buttons.size());
        for (ChatExtraButtonInfo info : buttons) {
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

    public void checkChatExtraButtons() {
        if (loading) {
            return;
        }
        loading = true;
        loadPagePreviewRules();
        if (needUpdate()) {
            load();
        }
        loading = false;
    }

    public static final int CHAT_BUTTON_TYPE_LINK = 1;
    public static final int CHAT_BUTTON_TYPE_SEARCH = 2;

    public static class ChatExtraButtonInfo {
        public int type;
        public long chatId;
        public String name;
        public String url;

        public ChatExtraButtonInfo() {}

        public ChatExtraButtonInfo(int type, long chatId, String name, String url) {
            this.type = type;
            this.chatId = chatId;
            this.name = name;
            this.url = url;
        }

        public static ChatExtraButtonInfo deserialize(AbstractSerializedData stream) {
            ChatExtraButtonInfo chatExtraButtonInfo = new ChatExtraButtonInfo();
            chatExtraButtonInfo.type = stream.readInt32(false);
            chatExtraButtonInfo.chatId = stream.readInt64(false);
            chatExtraButtonInfo.name = stream.readString(false);
            chatExtraButtonInfo.url = stream.readString(false);
            return chatExtraButtonInfo;
        }

        public void serializeToStream(AbstractSerializedData serializedData) {
            serializedData.writeInt32(type);
            serializedData.writeInt64(chatId);
            serializedData.writeString(name);
            serializedData.writeString(url);
        }
    }
}
