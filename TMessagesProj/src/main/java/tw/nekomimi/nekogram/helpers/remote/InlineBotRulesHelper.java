package tw.nekomimi.nekogram.helpers.remote;

import android.text.TextUtils;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.SerializedData;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.nextalone.nagram.NaConfig;

public class InlineBotRulesHelper extends BaseRemoteHelper {
    private static final String INLINE_BOT_TAG = "inlinebot";
    private static volatile InlineBotRulesHelper Instance;
    private final ArrayList<InlineBotRule> inlineBotRules = new ArrayList<>();
    private boolean loading = false;

    public static InlineBotRulesHelper getInstance() {
        InlineBotRulesHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (InlineBotRulesHelper.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new InlineBotRulesHelper();
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
        return INLINE_BOT_TAG;
    }

    private void parseInlineBotRules(ArrayList<InlineBotRule> tmpInlineBotRules, JSONObject json) {
        try {
            var array = json.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                var obj = array.getJSONObject(i);
                String username = obj.getString("username");
                var rules = obj.getJSONArray("rules");
                ArrayList<String> ruleList = new ArrayList<>();
                for (int j = 0; j < rules.length(); j++) {
                    String rule = rules.getString(j);
                    ruleList.add(rule);
                }
                var rule = new InlineBotRule(
                        username,
                        ruleList
                );
                tmpInlineBotRules.add(rule);
            }
        } catch (JSONException e) {
            FileLog.e(e);
        }
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

        ArrayList<InlineBotRule> tmpInlineBotRules = new ArrayList<>();
        for (int i = 0; i < responses.size(); i++) {
            parseInlineBotRules(tmpInlineBotRules, responses.get(i));
        }

        inlineBotRules.clear();
        inlineBotRules.addAll(tmpInlineBotRules);
        saveInlineBotRules();
    }

    public void loadInlineBotRules() {
        var tag = getTag();
        String list = preferences.getString(tag, "");
        ArrayList<InlineBotRule> tmpInlineBotRules = new ArrayList<>();
        if (!TextUtils.isEmpty(list)) {
            byte[] bytes = Base64.decode(list, Base64.DEFAULT);
            SerializedData data = new SerializedData(bytes);
            int count = data.readInt32(false);
            try {
                for (int a = 0; a < count; a++) {
                    InlineBotRule info = InlineBotRule.deserialize(data);
                    tmpInlineBotRules.add(info);
                }
            } catch (RuntimeException ignored) {}
            data.cleanup();
        }
        inlineBotRules.clear();
        inlineBotRules.addAll(tmpInlineBotRules);
    }

    public void saveInlineBotRules() {
        var tag = getTag();
        SerializedData serializedData = new SerializedData();
        serializedData.writeInt32(inlineBotRules.size());
        for (InlineBotRule info : inlineBotRules) {
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

    public void checkInlineBotRules() {
        if (loading) {
            return;
        }
        loading = true;
        loadInlineBotRules();
        if (needUpdate()) {
            load();
        }
        loading = false;
    }

    public String doRegex(String textToCheck) {
        if (!NaConfig.INSTANCE.getFixUrlAutoInlineBot().Bool()) {
            return null;
        }
        if (textToCheck == null || textToCheck.isEmpty()) {
            return null;
        }
        for (InlineBotRule rule : inlineBotRules) {
            for (Pattern pattern : rule.regexPattern) {
                Matcher matcher = pattern.matcher(textToCheck);
                if (matcher.find()) {
                    return rule.username;
                }
            }
        }
        return null;
    }

    public static class InlineBotRule {
        public String username;
        public ArrayList<String> rules;
        public ArrayList<Pattern> regexPattern;

        public InlineBotRule() {}

        public InlineBotRule(String username, ArrayList<String> rules) {
            this.username = username;
            this.rules = rules;
            this.buildRegexPattern();
        }

        public void buildRegexPattern() {
            this.regexPattern = new ArrayList<>();
            for (String rule : rules) {
                this.regexPattern.add(Pattern.compile(rule));
            }
        }

        public static InlineBotRule deserialize(AbstractSerializedData stream) {
            InlineBotRule inlineBotRule = new InlineBotRule();
            inlineBotRule.username = stream.readString(false);
            int count = stream.readInt32(false);
            ArrayList<String> rules = new ArrayList<>();
            for (int a = 0; a < count; a++) {
                String rule = stream.readString(false);
                rules.add(rule);
            }
            inlineBotRule.rules = rules;
            inlineBotRule.buildRegexPattern();
            return inlineBotRule;
        }

        public void serializeToStream(AbstractSerializedData serializedData) {
            serializedData.writeString(username);
            serializedData.writeInt32(rules.size());
            for (String rule : rules) {
                serializedData.writeString(rule);
            }
        }
    }
}
