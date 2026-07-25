package tw.nekomimi.nekogram.helpers.remote;

import android.content.ClipboardManager;
import android.content.Context;

import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;

import java.util.ArrayList;
import java.util.regex.Pattern;

import xyz.nextalone.nagram.NaConfig;

public class ExtendedHelper extends BaseRemoteHelper {
    private static final String EXTENDED_TAG = "extended";
    private static final Pattern MD5_PATTERN = Pattern.compile("[a-fA-F0-9]{64}");
    private static final long TTL = 15 * 60 * 1000L;
    private static volatile ExtendedHelper Instance;

    private String data;
    private boolean loading;

    public static ExtendedHelper getInstance() {
        ExtendedHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (ExtendedHelper.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new ExtendedHelper();
                }
            }
        }
        return localInstance;
    }

    @Override
    protected void onError(String text, Delegate delegate) {
    }

    @Override
    protected String getTag() {
        return EXTENDED_TAG;
    }

    @Override
    protected void onLoadSuccess(ArrayList<JSONObject> responses, Delegate delegate) {
        JSONObject json = responses.isEmpty() ? null : responses.get(0);
        if (json == null) {
            return;
        }

        String value = json.optString("data", null);
        if (!isValidData(value)) {
            return;
        }

        data = value;
        super.onLoadSuccess(responses, delegate);
    }

    public void checkExtended() {
        if (loading) {
            return;
        }
        loading = true;
        loadExtendedInfo();
        if (needUpdate()) {
            load();
        }
        loading = false;
    }

    public String getData() {
        if (data == null) {
            loadExtendedInfo();
        }
        return data;
    }

    private String getClip() {
        String text = null;
        try {
            ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE);
            text = clipboardManager.getPrimaryClip().getItemAt(0).coerceToText(ApplicationLoader.applicationContext).toString();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return text;
    }

    private boolean hasExtended(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        String data = getData();
        if (data == null || data.isEmpty()) {
            return false;
        }
        String tokenMd5 = Utilities.SHA256(token);
        return tokenMd5 != null && data.equalsIgnoreCase(tokenMd5);
    }

    public boolean hasExtended() {
        String token = NaConfig.INSTANCE.getExtendedFeatureUnlockedToken().String();
        if (hasExtended(token)) {
            return true;
        }
        token = getClip();
        if (hasExtended(token)) {
            NaConfig.INSTANCE.getExtendedFeatureUnlockedToken().setConfigString(token);
            return true;
        }
        return false;
    }

    private void loadExtendedInfo() {
        JSONObject json = getJSON();
        if (json == null) {
            return;
        }

        String value = json.optString("data", null);
        if (isValidData(value)) {
            data = value;
        }
    }

    private boolean needUpdate() {
        long oldTime = preferences.getLong(getTag() + "_update_time", 0L);
        return oldTime + TTL <= System.currentTimeMillis();
    }

    private static boolean isValidData(String value) {
        return value != null && MD5_PATTERN.matcher(value).matches();
    }
}
