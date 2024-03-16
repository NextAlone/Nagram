package tw.nekomimi.nekogram.helpers.remote;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.nekomimi.nekogram.NekoXConfig;

public class UpdateHelper extends BaseRemoteHelper {
    public static final String UPDATE_TAG = NekoXConfig.autoUpdateReleaseChannel == 2 ? "updatetest" : "updatev1";

    private static final class InstanceHolder {
        private static final UpdateHelper instance = new UpdateHelper();
    }

    public static UpdateHelper getInstance() {
        return InstanceHolder.instance;
    }

    private boolean updateAlways = false;

    @Override
    protected void onError(String text, Delegate delegate) {
        delegate.onTLResponse(null, text);
    }

    @Override
    protected String getTag() {
        return UPDATE_TAG;
    }

    @SuppressWarnings("ConstantConditions")
    private int getPreferredAbiFile(Map<String, Integer> files) {
        for (String abi : Build.SUPPORTED_ABIS) {
            if (files.containsKey(abi)) {
                return files.get(abi);
            }
        }
        return files.get("arm64-v8a");
    }

    private Map<String, Integer> jsonToMap(JSONObject obj) {
        Map<String, Integer> map = new HashMap<>();
        List<String> abis = new ArrayList<>();
        abis.add("armeabi-v7a");
        abis.add("arm64-v8a");

        try {
            for(var abi: abis) {
                map.put(abi, obj.getInt(abi));
            }
        } catch (JSONException ignored) {}
        return map;
    }

    private Update getShouldUpdateVersion(List<JSONObject> responses) {
        long maxVersion = BuildConfig.VERSION_CODE;
        Update ref = null;
        for (var string : responses) {
            try {
                int version_code = string.getInt("version_code");
                if (version_code > maxVersion || updateAlways) {
                    if (updateAlways) {
                        updateAlways = false;
                    }
                    maxVersion = version_code;
                    ref = new Update(
                            string.getBoolean("can_not_skip"),
                            string.getString("version"),
                            string.getInt("version_code"),
                            string.getInt("sticker"),
                            string.getInt("message"),
                            jsonToMap(string.getJSONObject("gcm")),
                            jsonToMap(string.getJSONObject("nogcm")),
                            string.getString("url")
                    );
                    break;
                }
            } catch (JSONException ignored) {}
        }
        return ref;
    }

    private void getNewVersionMessagesCallback(Delegate delegate, Update json,
                                               HashMap<String, Integer> ids, TLObject response) {
        var update = new TLRPC.TL_help_appUpdate();
        update.version = json.version;
        update.can_not_skip = json.canNotSkip;
        if (json.url != null) {
            update.url = json.url;
            update.flags |= 4;
        }
        if (response != null) {
            var res = (TLRPC.messages_Messages) response;
            getMessagesController().removeDeletedMessagesFromArray(CHANNEL_METADATA_ID, res.messages);
            var messages = new HashMap<Integer, TLRPC.Message>();
            for (var message : res.messages) {
                messages.put(message.id, message);
            }

            if (ids.containsKey("file")) {
                var file = messages.get(ids.get("file"));
                if (file != null && file.media != null) {
                    update.document = file.media.document;
                    update.flags |= 2;
                }
            }
            if (ids.containsKey("message")) {
                var message = messages.get(ids.get("message"));
                if (message != null) {
                    update.text = message.message;
                    update.entities = message.entities;
                }
            }
            if (ids.containsKey("sticker")) {
                var sticker = messages.get(ids.get("sticker"));
                if (sticker != null && sticker.media != null) {
                    update.sticker = sticker.media.document;
                    update.flags |= 8;
                }
            }
        }
        delegate.onTLResponse(update, null);
    }

    @Override
    protected void onLoadSuccess(ArrayList<JSONObject> responses, Delegate delegate) {
        var update = getShouldUpdateVersion(responses);
        if (update == null) {
            delegate.onTLResponse(null, null);
            return;
        }

        var ids = new HashMap<String, Integer>();
        if (update.message != null) {
            ids.put("message", update.message);
        }
        if (update.sticker != null) {
            ids.put("sticker", update.sticker);
        }
        if (update.gcm != null) {
            ids.put("file", getPreferredAbiFile(update.gcm));
        }

        if (ids.isEmpty()) {
            getNewVersionMessagesCallback(delegate, update, null, null);
        } else {
            var req = new TLRPC.TL_channels_getMessages();
            req.channel = getMessagesController().getInputChannel(CHANNEL_METADATA_ID);
            req.id = new ArrayList<>(ids.values());
            getConnectionsManager().sendRequest(req, (response1, error1) -> {
                if (error1 == null) {
                    getNewVersionMessagesCallback(delegate, update, ids, response1);
                } else {
                    delegate.onTLResponse(null, error1.text);
                }
            });
        }
    }

    public void checkNewVersionAvailable(Delegate delegate) {
        checkNewVersionAvailable(delegate, false);
    }

    public void checkNewVersionAvailable(Delegate delegate, boolean updateAlways_) {
        if (NekoXConfig.autoUpdateReleaseChannel == 0) {
            delegate.onTLResponse(null, null);
            return;
        }
        updateAlways = updateAlways_;
        load(delegate);
    }

    public static class Update {
        public Boolean canNotSkip;
        public String version;
        public Integer versionCode;
        public Integer sticker;
        public Integer message;
        public Map<String, Integer> gcm;
        public Map<String, Integer> nogcm;
        public String url;

        public Update(Boolean canNotSkip, String version, int versionCode, int sticker, int message, Map<String, Integer> gcm, Map<String, Integer> nogcm, String url) {
            this.canNotSkip = canNotSkip;
            this.version = version;
            this.versionCode = versionCode;
            this.sticker = sticker;
            this.message = message;
            this.gcm = gcm;
            this.nogcm = nogcm;
            this.url = url;
        }
    }
}
