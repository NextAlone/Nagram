package tw.nekomimi.nekogram.helpers.remote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.FileLog;

import java.util.ArrayList;

public class VerifyHelper extends BaseRemoteHelper {
    private static final String VERIFY_TAG = "verify";

    private static volatile VerifyHelper Instance;
    private static final ArrayList<Long> DEFAULT_LIST = new ArrayList<>();

    static {
        DEFAULT_LIST.add(1302242053L);
        DEFAULT_LIST.add(1406090861L);
        DEFAULT_LIST.add(1221673407L);
        DEFAULT_LIST.add(1339737452L);
        DEFAULT_LIST.add(1349472891L);
        DEFAULT_LIST.add(1676383632L);
    }

    public static VerifyHelper getInstance() {
        VerifyHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (VerifyHelper.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new VerifyHelper();
                }
                return localInstance;
            }
        }
        return localInstance;
    }

    public static ArrayList<Long> getVerify() {
        JSONObject jsonObject = getInstance().getJSON();
        if (jsonObject == null) {
            return DEFAULT_LIST;
        }
        ArrayList<Long> verifyItems = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("verify");
            for (int i = 0; i < jsonArray.length(); i++) {
                verifyItems.add(jsonArray.getLong(i));
            }
            return verifyItems;
        } catch (JSONException e) {
            FileLog.e(e);
            getInstance().load();
            return DEFAULT_LIST;
        }
    }

    @Override
    protected void onError(String text, Delegate delegate) {
        FileLog.e("VerifyHelper error = " + text);
    }

    @Override
    protected String getTag() {
        return "#" + VERIFY_TAG;
    }
}
