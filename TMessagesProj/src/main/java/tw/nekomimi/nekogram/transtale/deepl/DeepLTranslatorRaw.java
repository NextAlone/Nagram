package tw.nekomimi.nekogram.transtale.deepl;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.annotation.SuppressLint;
import android.os.SystemClock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.SharedConfig;

public class DeepLTranslatorRaw {
    private int id = (new Random()).nextInt(10000) * 10000 + 1;
    private JSONArray sentence_counter;
    private JSONArray linefeed_counter;
    private JSONArray sections;
    private JSONArray sentences;
    private int i_counter;
    private int pos = 0;
    private String detected_language;
    private String cookie;
    private final Pattern iPattern = Pattern.compile("[i]");

    public String translate(String query_texts, String source_language, String target_language) throws IOException, JSONException {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        query_texts = Pattern.compile("^\\s+").matcher(query_texts).replaceFirst("");
        this.statistics_sections_sentences(query_texts);
        this.statistics_linefeeds(query_texts);
        if (source_language.contentEquals("auto")) {
            this.LMT_split_into_sentences(source_language);
        } else {
            this.detected_language = source_language;
        }

        return this.joint(this.get_raw_result(target_language));
    }

    private void LMT_split_into_sentences(String lang_user_selected) throws IOException, JSONException {
        ++this.id;
        JSONObject body = new JSONObject();
        JSONObject params = new JSONObject();
        JSONObject lang = new JSONObject();
        lang.put("lang_user_selected", lang_user_selected);
        lang.put("user_preferred_langs", new JSONArray());
        params.put("texts", this.sections);
        params.put("lang", lang);
        body.put("jsonrpc", "2.0");
        body.put("method", "LMT_split_into_sentences");
        body.put("params", params);
        body.put("id", this.id);
        String body_ = (this.id + 3) % 13 != 0 && (this.id + 5) / 29 != 0 ? body.toString().replace("hod\":\"", "hod\": \"") : body.toString().replace("hod\":\"", "hod\" : \"");
        String response = this.request("https://www2.deepl.com/jsonrpc", body_);
        this.detected_language = lang_user_selected.contentEquals("auto") ? (String)(new JSONObject(response)).getJSONObject("result").get("lang") : lang_user_selected;
    }

    private String LMT_handle_jobs(String source_language, String target_language) throws IOException, JSONException {
        ++this.id;
        JSONObject body = new JSONObject();
        JSONObject params = new JSONObject();
        JSONObject lang = new JSONObject();
        JSONArray user_preferred_langs = new JSONArray();
        JSONArray jobs = new JSONArray();
        new JSONArray();
        JSONArray raw_en_context_after = null;
        user_preferred_langs.put(target_language);
        user_preferred_langs.put(this.detected_language);
        lang.put("user_preferred_langs", user_preferred_langs);
        lang.put("source_lang_computed", source_language);
        lang.put("target_lang", target_language);
        params.put("jobs", jobs);
        params.put("lang", lang);
        params.put("priority", 1);
        params.put("commonJobParams", (new JSONObject()).put("formality", JSONObject.NULL));
        body.put("jsonrpc", "2.0");
        body.put("method", "LMT_handle_jobs");
        body.put("params", params);
        body.put("id", this.id);
        int character_number = ((this.id + 3) % 13 != 0 && (this.id + 5) % 29 != 0 ? body.toString().replace("hod\":\"", "hod\": \"") : body.toString().replace("hod\":\"", "hod\" : \"")).length() + 26;

        for(this.i_counter = 1; this.pos <= this.sentences.length() - 1 && character_number < 10000; ++this.pos) {
            JSONObject job = new JSONObject();
            job.put("kind", "default");
            if (this.sentences.length() != 0) {
                job.put("raw_en_sentence", this.sentences.get(this.pos));
            }

            for(Matcher iMatcher = this.iPattern.matcher((String)this.sentences.get(this.pos)); iMatcher.find(); ++this.i_counter) {
            }

            if (this.pos == 0) {
                job.put("raw_en_context_before", new JSONArray());
            } else {
                JSONArray raw_en_context_before = new JSONArray();

                for(int i = this.pos - 5 < 0 ? 0 : this.pos - 5; i <= this.pos - 1; ++i) {
                    if (this.sentences.get(i) != null) {
                        raw_en_context_before.put(this.sentences.get(i));
                    }
                }

                job.put("raw_en_context_before", raw_en_context_before);
            }

            if (this.pos + 1 > this.sentences.length() - 1) {
                job.put("raw_en_context_after", new JSONArray());
            } else {
                raw_en_context_after = (new JSONArray()).put(this.sentences.get(this.pos + 1));
                job.put("raw_en_context_after", raw_en_context_after);
            }

            job.put("preferred_num_beams", 1);
            jobs.put(job);
            character_number += job.toString().length();
        }

        params.put("timestamp", this.getTimestamp(this.i_counter));
        String body_ = (this.id + 3) % 13 != 0 && (this.id + 5) % 29 != 0 ? body.toString().replace("hod\":\"", "hod\": \"") : body.toString().replace("hod\":\"", "hod\" : \"");
        return this.request("https://www2.deepl.com/jsonrpc", body_);
    }

    private JSONArray get_raw_result(String to_language) throws IOException, JSONException {
        JSONArray raw_result = new JSONArray();
        JSONArray translations;

        while(this.pos <= this.sentences.length() - 1) {
            translations = (new JSONObject(this.LMT_handle_jobs(this.detected_language, to_language))).getJSONObject("result").getJSONArray("translations");

            try {
                for(int i = 0; i <= translations.length() - 1; ++i) {
                    raw_result.put(((JSONObject)((JSONObject)translations.get(i)).getJSONArray("beams").get(0)).get("postprocessed_sentence"));
                }
            } catch (JSONException var5) {
                raw_result.put("...");
            }
        }

        this.pos = 0;
        return raw_result;
    }

    private String joint(JSONArray raw_result) throws IOException {
        String Result_ = "";
        int p = 0;

        try {
            for(int i = 0; i <= this.sentence_counter.length() - 1; ++i) {
                int k;
                for(k = 0; k <= this.sentence_counter.getInt(i) - 1; ++k) {
                    Result_ = Result_ + raw_result.get(p + k);
                }

                p += this.sentence_counter.getInt(i);

                for(k = 0; k < this.linefeed_counter.getInt(i); ++k) {
                    Result_ = Result_ + "\n";
                }
            }
        } catch (JSONException var6) {
        }

        return Result_;
    }

    @SuppressLint("NewApi")
    private String request(String url, String body) throws IOException {
        InputStream httpConnectionStream = null;
        URL downloadUrl = new URL(url);
        HttpURLConnection httpConnection;
//        final Proxy proxy = SharedConfig.getActiveSocks5Proxy();
//        if (proxy != null)
//            httpConnection = (HttpURLConnection) downloadUrl.openConnection(proxy);
//        else
        httpConnection = (HttpURLConnection) downloadUrl.openConnection();
        httpConnection.addRequestProperty("Connection", "keep-alive");
        httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4147.105 Safari/537.36");
        httpConnection.addRequestProperty("Content-Type", "application/json");
        httpConnection.addRequestProperty("Accept", "*/*");
        httpConnection.addRequestProperty("Origin", "https://www.deepl.com");
        httpConnection.addRequestProperty("Sec-Fetch-Site", "same-site");
        httpConnection.addRequestProperty("Sec-Fetch-Mode", "cors");
        httpConnection.addRequestProperty("Sec-Fetch-Dest", "empty");
        httpConnection.addRequestProperty("Referer", "https://www.deepl.com/");
        httpConnection.addRequestProperty("Accept-Encoding", "gzip, deflate, br");
        if (this.cookie != null) {
            httpConnection.addRequestProperty("Cookie", this.cookie);
        }

        httpConnection.setRequestMethod("POST");
        httpConnection.setDoOutput(true);
        httpConnection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        httpConnection.getOutputStream().flush();
        httpConnection.getOutputStream().close();
        httpConnection.setConnectTimeout(2000);
        httpConnection.setReadTimeout(2000);
        int var8 = 3;

        boolean k;
        do {
            k = false;

            try {
                httpConnection.connect();
                httpConnectionStream = httpConnection.getInputStream();
            } catch (IOException var12) {
                k = true;
                if (var8-- <= 0) {
                    throw var12;
                }
            }
        } while(k);

        Map<String, List<String>> map = httpConnection.getHeaderFields();
        if (map.get("Set-Cookie") != null) {
            this.cookie = (String)((List)map.get("Set-Cookie")).get(0);
            this.cookie = this.cookie.substring(0, this.cookie.indexOf(";"));
        }

        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        byte[] data = new byte['耀'];

        while(true) {
            int read = httpConnectionStream.read(data);
            if (read <= 0) {
                if (read == -1) {
                }

                String result = new String(outbuf.toByteArray());
                httpConnectionStream.close();
                outbuf.close();
                return result;
            }

            outbuf.write(data, 0, read);
        }
    }

    private void statistics_sections_sentences(String query_texts) throws JSONException {
        Matcher section = Pattern.compile(".+$", 8).matcher(query_texts);
        this.sentence_counter = new JSONArray();
        this.sections = new JSONArray();

        int section_count;
        for(section_count = 0; section.find(); ++section_count) {
            this.sections.put(section.group(0));
        }

        Pattern sentence_ = Pattern.compile(".+?([.!?](?=\\s+)|$|\\n)");
        this.sentences = new JSONArray();

        for(int i = 0; i <= section_count - 1; ++i) {
            Matcher sentence = sentence_.matcher(this.sections.getString(i));

            int sentence_count;
            for(sentence_count = 0; sentence.find(); ++sentence_count) {
                this.sentences.put(sentence.group(0));
            }

            this.sentence_counter.put(sentence_count);
        }

    }

    private void statistics_linefeeds(String query_texts) {
        Matcher linefeed = Pattern.compile("\n++").matcher(query_texts);
        this.linefeed_counter = new JSONArray();

        while(linefeed.find()) {
            this.linefeed_counter.put(linefeed.group(0).length());
        }

    }

    private Long getTimestamp(int i_number) {
        long now = System.currentTimeMillis();
        now = now + (long)i_number - now % (long)i_number;
        return now;
    }
}