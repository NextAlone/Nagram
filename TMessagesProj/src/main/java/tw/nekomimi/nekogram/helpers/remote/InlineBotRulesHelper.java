package tw.nekomimi.nekogram.helpers.remote;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.FileLog;

import java.net.IDN;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.nextalone.nagram.NaConfig;

public class InlineBotRulesHelper {
    private static final String HOST_PATTERN_PREFIX = "(^|[\\s(\\[{<])(?:https?://)?([\\w-]+\\.)*";
    private static final String HOST_PATTERN_SUFFIX = "(?=[:/?#\\s)\\]}>]|$)";

    private static volatile InlineBotRulesHelper Instance;
    private final ArrayList<InlineBotRule> inlineBotRules = new ArrayList<>();
    private String loadedRules;

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

    private void loadInlineBotRules() {
        String list = NaConfig.INSTANCE.getFixUrlAutoInlineBotRules().String();
        if (TextUtils.equals(loadedRules, list)) {
            return;
        }
        loadedRules = list;
        ArrayList<InlineBotRule> tmpInlineBotRules = parseInlineBotRules(list, true);
        inlineBotRules.clear();
        inlineBotRules.addAll(tmpInlineBotRules);
    }

    public String doRegex(String textToCheck) {
        if (!NaConfig.INSTANCE.getFixUrlAutoInlineBot().Bool()) {
            return null;
        }
        if (textToCheck == null || textToCheck.isEmpty()) {
            return null;
        }
        loadInlineBotRules();
        for (InlineBotRule rule : inlineBotRules) {
            Matcher matcher = rule.regexPattern.matcher(textToCheck);
            if (matcher.find()) {
                return rule.username;
            }
        }
        return null;
    }

    public static ArrayList<InlineBotRule> parseInlineBotRules(String list, boolean compilePattern) {
        ArrayList<InlineBotRule> rules = new ArrayList<>();
        if (TextUtils.isEmpty(list)) {
            return rules;
        }
        try {
            JSONArray array = new JSONArray(list.trim());
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                try {
                    String host = object.optString("host", "");
                    String pattern = object.optString("pattern", object.optString("rule", ""));
                    if (TextUtils.isEmpty(pattern) && !TextUtils.isEmpty(host)) {
                        pattern = buildHostPattern(host);
                    }
                    addInlineBotRule(
                            rules,
                            object.optString("bot", object.optString("username", "")),
                            pattern,
                            host,
                            compilePattern
                    );
                } catch (RuntimeException e) {
                    FileLog.e(e);
                }
            }
        } catch (JSONException e) {
            FileLog.e(e);
        }
        return rules;
    }

    public static String normalizeInlineBotUsername(String username) {
        username = username == null ? "" : username.trim();
        if (username.startsWith("@")) {
            return username.substring(1);
        }
        return username;
    }

    public static String normalizeHostInput(String host) {
        host = host == null ? "" : host.trim();
        if (host.isEmpty()) {
            return "";
        }
        host = host.replaceFirst("^[a-zA-Z][a-zA-Z0-9+.-]*://", "");
        int endIndex = host.length();
        for (char separator : new char[]{'/', '?', '#', ' '}) {
            int index = host.indexOf(separator);
            if (index >= 0 && index < endIndex) {
                endIndex = index;
            }
        }
        host = host.substring(0, endIndex).trim();
        if (host.startsWith("*.")) {
            host = host.substring(2);
        }
        while (host.endsWith(".")) {
            host = host.substring(0, host.length() - 1);
        }
        if (host.isEmpty()) {
            return "";
        }
        try {
            host = IDN.toASCII(host).toLowerCase(Locale.US);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid domain name");
        }
        if (host.length() > 253) {
            throw new IllegalArgumentException("host is too long");
        }
        String[] labels = host.split("\\.");
        for (String label : labels) {
            if (label.isEmpty()) {
                throw new IllegalArgumentException("empty host label");
            }
            if (label.length() > 63) {
                throw new IllegalArgumentException("host label is too long");
            }
            if (label.startsWith("-") || label.endsWith("-")) {
                throw new IllegalArgumentException("host label cannot start or end with '-'");
            }
            for (int i = 0; i < label.length(); i++) {
                char c = label.charAt(i);
                if (!(c >= 'a' && c <= 'z') && !(c >= '0' && c <= '9') && c != '-') {
                    throw new IllegalArgumentException("host contains invalid characters");
                }
            }
        }
        return host;
    }

    public static String normalizeSimpleHostInput(String host) {
        host = host == null ? "" : host.trim();
        if (host.isEmpty()) {
            return "";
        }
        if (host.startsWith("*.") || host.matches(".*[\\s:/?#].*")) {
            throw new IllegalArgumentException("enter host only");
        }
        return normalizeHostInput(host);
    }

    public static String buildHostPattern(String host) {
        host = normalizeHostInput(host);
        if (TextUtils.isEmpty(host)) {
            throw new IllegalArgumentException("host is empty");
        }
        return HOST_PATTERN_PREFIX + Pattern.quote(host) + HOST_PATTERN_SUFFIX;
    }

    public static String getHostForRule(String rule, String host) {
        try {
            host = normalizeHostInput(host);
            if (!TextUtils.isEmpty(host)) {
                return host;
            }
        } catch (IllegalArgumentException ignored) {
        }
        String extractedHost = extractHostFromPattern(rule);
        return extractedHost == null ? "" : extractedHost;
    }

    public static String extractHostFromPattern(String rule) {
        if (TextUtils.isEmpty(rule) || !rule.startsWith(HOST_PATTERN_PREFIX) || !rule.endsWith(HOST_PATTERN_SUFFIX)) {
            return null;
        }
        String hostPattern = rule.substring(HOST_PATTERN_PREFIX.length(), rule.length() - HOST_PATTERN_SUFFIX.length());
        if (hostPattern.startsWith("\\Q") && hostPattern.endsWith("\\E")) {
            return normalizeHostInput(hostPattern.substring(2, hostPattern.length() - 2));
        }
        StringBuilder host = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < hostPattern.length(); i++) {
            char c = hostPattern.charAt(i);
            if (escaped) {
                if (c != '.') {
                    return null;
                }
                host.append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '-' || c == '.') {
                host.append(c);
            } else {
                return null;
            }
        }
        if (escaped) {
            return null;
        }
        return normalizeHostInput(host.toString());
    }

    private static void addInlineBotRule(ArrayList<InlineBotRule> rules, String username, String rule, String host, boolean compilePattern) {
        username = normalizeInlineBotUsername(username);
        if (TextUtils.isEmpty(rule) || TextUtils.isEmpty(username)) {
            return;
        }
        try {
            rules.add(new InlineBotRule(username, rule, host, compilePattern));
        } catch (RuntimeException e) {
            FileLog.e(e);
        }
    }

    public static String serializeInlineBotRules(ArrayList<InlineBotRule> rules) {
        JSONArray array = new JSONArray();
        for (InlineBotRule rule : rules) {
            if (rule == null || TextUtils.isEmpty(rule.rule) || TextUtils.isEmpty(rule.username)) {
                continue;
            }
            JSONObject object = new JSONObject();
            try {
                object.put("pattern", rule.rule);
                if (!TextUtils.isEmpty(rule.host)) {
                    object.put("host", normalizeHostInput(rule.host));
                }
                object.put("bot", normalizeInlineBotUsername(rule.username));
                array.put(object);
            } catch (JSONException e) {
                FileLog.e(e);
            }
        }
        return array.toString();
    }

    public static class InlineBotRule {
        public String username;
        public String rule;
        public String host;
        public Pattern regexPattern;

        public InlineBotRule() {}

        public InlineBotRule(String username, String rule) {
            this(username, rule, true);
        }

        public InlineBotRule(String username, String rule, boolean compilePattern) {
            this(username, rule, "", compilePattern);
        }

        public InlineBotRule(String username, String rule, String host, boolean compilePattern) {
            this.username = username;
            this.rule = rule;
            this.host = host;
            if (compilePattern) {
                this.buildRegexPattern();
            }
        }

        public void buildRegexPattern() {
            this.regexPattern = Pattern.compile(rule, Pattern.CASE_INSENSITIVE);
        }
    }
}
