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

public class WallpaperHelper extends BaseRemoteHelper {
    private static final String WALLPAPER_TAG = "wallpaper";
    private static volatile WallpaperHelper Instance;
    private final ArrayList<WallPaperInfo> wallPaperInfo = new ArrayList<>();
    private final HashMap<Long, WallPaperInfo> wallpaperInfoMap = new HashMap<>();
    private boolean loading = false;

    public static WallpaperHelper getInstance() {
        WallpaperHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (WallpaperHelper.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new WallpaperHelper();
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
        return WALLPAPER_TAG;
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
            ArrayList<WallPaperInfo> wallpapers = new ArrayList<>();
            var array = json.getJSONArray("wallpapers");

            for (int i = 0; i < array.length(); i++) {
                var obj = array.getJSONObject(i);
                var info = new WallPaperInfo(
                        obj.getLong("chat_id"),
                        obj.getInt("version"),
                        obj.getString("slug"),
                        obj.getString("emoticon"),
                        obj.getString("mode"));
                if (wallpaperInfoMap.containsKey(info.chatId)) {
                    var oldInfo = wallpaperInfoMap.get(info.chatId);
                    if (oldInfo != null && oldInfo.version == info.version) {
                        wallpapers.add(oldInfo);
                    } else {
                        wallpapers.add(info);
                    }
                } else {
                    wallpapers.add(info);
                }
            }

            wallPaperInfo.clear();
            wallPaperInfo.addAll(wallpapers);
            wallpaperInfoMap.clear();
            for (WallPaperInfo info: wallPaperInfo) {
                wallpaperInfoMap.put(info.chatId, info);
            }
            loadWallPaperFromServer();
        } catch (JSONException e) {
            FileLog.e(e);
            delegate.onTLResponse(null, e.getLocalizedMessage());
        }
    }

    public void loadWallPaperInfo() {
        var tag = getTag();
        String list = preferences.getString(tag, "");
        wallPaperInfo.clear();
        wallpaperInfoMap.clear();
        if (!TextUtils.isEmpty(list)) {
            byte[] bytes = Base64.decode(list, Base64.DEFAULT);
            SerializedData data = new SerializedData(bytes);
            int count = data.readInt32(false);
            for (int a = 0; a < count; a++) {
                WallPaperInfo info = WallPaperInfo.deserialize(data);
                wallPaperInfo.add(info);
                wallpaperInfoMap.put(info.chatId, info);
            }
            data.cleanup();
        }
    }

    public void saveWallPaperInfo() {
        var tag = getTag();
        SerializedData serializedData = new SerializedData();
        serializedData.writeInt32(wallPaperInfo.size());
        for (WallPaperInfo info: wallPaperInfo) {
            info.serializeToStream(serializedData);
        }
        preferences.edit()
                .putLong(tag + "_update_time", System.currentTimeMillis())
                .putString(tag, Base64.encodeToString(serializedData.toByteArray(), Base64.NO_WRAP | Base64.NO_PADDING))
                .apply();
        serializedData.cleanup();
    }

    private void loadWallPaperFromServer() {
        for (WallPaperInfo info : wallPaperInfo) {
            if (info.wallPaper != null) {
                continue;
            }
            if (!info.emoticon.isEmpty()) {
                TLRPC.TL_wallPaperNoFile wallpaper = new TLRPC.TL_wallPaperNoFile();
                wallpaper.id = 0;
                wallpaper.isDefault = false;
                wallpaper.dark = false;
                wallpaper.settings = info.parseSettings();
                info.flags |= 1;
                info.wallPaper = wallpaper;
            }
        }
        for (WallPaperInfo info: wallPaperInfo) {
            if (info.wallPaper != null) {
                continue;
            }
            if (info.slug != null && !info.slug.isEmpty()) {
                TLRPC.TL_account_getWallPaper req = new TLRPC.TL_account_getWallPaper();
                TLRPC.TL_inputWallPaperSlug inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
                inputWallPaperSlug.slug = info.slug;
                req.wallpaper = inputWallPaperSlug;
                getConnectionsManager().sendRequest(req, (response, error1) -> {
                    if (error1 == null) {
                        if (response instanceof TLRPC.TL_wallPaper) {
                            info.flags |= 1;
                            info.wallPaper = (TLRPC.WallPaper) response;
                            info.wallPaper.settings = info.parseSettings();
                            saveWallPaperInfo();
                        }
                    }
                });
            }
        }
        saveWallPaperInfo();
    }

    public TLRPC.WallPaper getDialogWallpaper(long dialogId) {
        if (!wallpaperInfoMap.containsKey(dialogId)) {
            return null;
        }
        var info = wallpaperInfoMap.get(dialogId);
        if (info != null) {
            return info.wallPaper;
        }
        return null;
    }

    public boolean needUpdate() {
        var tag = getTag();
        long oldTime = preferences.getLong(tag + "_update_time", 0L);
        long nowTime = System.currentTimeMillis();
        int TTL = 15 * 60;
        return oldTime + TTL <= nowTime;
    }

    public void checkWallPaper() {
        if (loading) {
            return;
        }
        loading = true;
        loadWallPaperInfo();
        if (needUpdate()) {
            load();
        }
        loading = false;
    }

    public static class WallPaperInfo {
        private int flags;
        public long chatId;
        public int version;
        public String slug;
        public String emoticon;
        public String mode;
        public TLRPC.WallPaper wallPaper;

        public WallPaperInfo(long chatId, int version, String slug, String emoticon, String mode) {
            this.chatId = chatId;
            this.version = version;
            this.slug = slug;
            this.emoticon = emoticon;
            this.mode = mode;
        }

        public WallPaperInfo() {}

        public TLRPC.WallPaperSettings parseSettings() {
            TLRPC.WallPaperSettings settings = new TLRPC.TL_wallPaperSettings();
            if (emoticon != null && !emoticon.isBlank()) {
                settings.emoticon = emoticon;
            }
            if (mode != null && !mode.isEmpty()) {
                mode = mode.toLowerCase();
                String[] modes = mode.split(" ");
                for (String s : modes) {
                    if ("blur".equals(s)) {
                        settings.blur = true;
                    } else if ("motion".equals(s)) {
                        settings.motion = true;
                    } else if (s.startsWith("intensity_")) {
                        try {
                            settings.intensity = Integer.parseInt(s.replace("intensity_", ""));
                        } catch (Exception ignore) {
                        }
                    }
                }
            }
            return settings;
        }

        public static WallpaperHelper.WallPaperInfo deserialize(AbstractSerializedData stream) {
            WallpaperHelper.WallPaperInfo wallPaperInfo = new WallpaperHelper.WallPaperInfo();
            wallPaperInfo.flags = stream.readInt32(false);
            wallPaperInfo.chatId = stream.readInt64(false);
            wallPaperInfo.version = stream.readInt32(false);
            wallPaperInfo.slug = stream.readString(false);
            wallPaperInfo.emoticon = stream.readString(false);
            wallPaperInfo.mode = stream.readString(false);

            if ((wallPaperInfo.flags & 1) != 0) {
                wallPaperInfo.wallPaper = TLRPC.WallPaper.TLdeserialize(stream, stream.readInt32(false), false);
            }

            return wallPaperInfo;
        }

        public void serializeToStream(AbstractSerializedData serializedData) {
            serializedData.writeInt32(flags);
            serializedData.writeInt64(chatId);
            serializedData.writeInt32(version);
            serializedData.writeString(slug);
            serializedData.writeString(emoticon);
            serializedData.writeString(mode);

            if ((flags & 1) != 0) {
                wallPaper.serializeToStream(serializedData);
            }
        }
    }
}
