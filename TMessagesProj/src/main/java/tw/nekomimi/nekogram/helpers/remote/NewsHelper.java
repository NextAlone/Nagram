package tw.nekomimi.nekogram.helpers.remote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

import java.util.ArrayList;

import tw.nekomimi.nekogram.NekoConfig;

public class NewsHelper extends BaseRemoteHelper {
    private static final String NEWS_TAG = "newsv2";

    private static volatile NewsHelper Instance;
    private static final ArrayList<NewsItem> DEFAULT_LIST = new ArrayList<>();

    static {
        if (NekoConfig.isChineseUser) {
            DEFAULT_LIST.add(new NewsItem(
                    0,
                    LocaleController.getString("YahagiTitle", R.string.YahagiTitle),
                    LocaleController.getString("YahagiSummary", R.string.YahagiSummary),
                    LocaleController.getString("YahagiLink", R.string.YahagiLink)
            ));
        }
    }

    public static NewsHelper getInstance() {
        NewsHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (NewsHelper.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new NewsHelper();
                }
                return localInstance;
            }
        }
        return localInstance;
    }

    public static ArrayList<NewsItem> getNews() {
        JSONObject jsonObject = getInstance().getJSON();
        if (jsonObject == null) {
            return DEFAULT_LIST;
        }
        ArrayList<NewsItem> newsItems = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("news");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getBoolean("chineseOnly") && !NekoConfig.isChineseUser) {
                    continue;
                }
                if (!object.getString("language").equals("ALL") && !object.getString("language").equals(LocaleController.getString("OfficialChannelUsername", R.string.OfficialChannelUsername))) {
                    continue;
                }
                newsItems.add(new NewsItem(
                        object.getInt("type"),
                        object.getString("title"),
                        object.getString("summary"),
                        object.getString("url")
                ));
            }
            return newsItems;
        } catch (JSONException e) {
            FileLog.e(e);
            getInstance().load();
            return DEFAULT_LIST;
        }
    }

    @Override
    protected void onError(String text, Delegate delegate) {
        FileLog.e("NewsHelper error = " + text);
    }

    @Override
    protected String getTag() {
        return "#" + NEWS_TAG;
    }

    public static class NewsItem {
        public int type;
        public String title;
        public String summary;
        public String url;

        public NewsItem(int type, String title, String summary, String url) {
            this.type = type;
            this.title = title;
            this.summary = summary;
            this.url = url;
        }
    }
}
