package tw.nekomimi.nekogram.sub;

import androidx.annotation.NonNull;

import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.transform.ErrorListener;

import cn.hutool.core.util.StrUtil;
import tw.nekomimi.nekogram.utils.HttpUtil;
import tw.nekomimi.nekogram.utils.ProxyUtil;

@Index("id")
@SuppressWarnings("unchecked")
public class SubInfo implements Mappable {

    @Id
    public long id;
    public String name;
    public List<String> urls = new LinkedList<>();
    public List<String> proxies = new LinkedList<>();
    public long lastFetch = -1L;
    public boolean enable = true;
    public boolean internal;

    public String displayName() {

        if (id == 1) return LocaleController.getString("PublicPrefix", R.string.PublicPrefix);

        if (name.length() < 5) return name;

        return name.substring(0,5) + "...";

    }

    public List<String> reloadProxies() throws AllTriesFailed {

        HashMap<String,Exception> exceptions = new HashMap<>();

        if (id == 1) {

            try {

                List<String> legacyList = ProxyUtil.downloadLegacyProxyList();

                if (legacyList != null) {

                    exceptions.put("<Internal>", new IOException("Update Failed"));

                    return legacyList;

                }

            } catch (Exception e) {

                exceptions.put("<Internal>", e);

            }

        }

        for (String url : urls) {

            try {

                String source = HttpUtil.get(url);

                return ProxyUtil.parseProxies(source);

            } catch (Exception e) {

                exceptions.put(url,e);

            }

        }

        throw new AllTriesFailed(exceptions);

    }

    public static class AllTriesFailed extends IOException {

        public AllTriesFailed(HashMap<String,Exception> exceptions) {
            this.exceptions = exceptions;
        }

        public HashMap<String,Exception> exceptions;

        @NonNull @Override public String toString() {

            StringBuilder errors = new StringBuilder();

            for (Map.Entry<String, Exception> e : exceptions.entrySet()) {

                errors.append(e.getKey()).append(": ");

                if (StrUtil.isBlank(e.getValue().getMessage())) {

                    errors.append(e.getValue().getMessage());

                } else {

                    errors.append(e.getValue().getClass().getSimpleName());

                }

                errors.append("\n\n");

            }

            return errors.toString();

        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubInfo subInfo = (SubInfo) o;
        return id == subInfo.id;
    }
    @Override
    public Document write(NitriteMapper mapper) {

        Document document = new Document();

        if (id == 0) id = SubManager.getSubList().find().totalCount() + 10;

        document.put("id", id);
        document.put("name", name);
        document.put("urls", urls);
        document.put("proxies",proxies);

        document.put("lastFetch", lastFetch);
        document.put("enable", enable);
        document.put("internal", internal);

        return document;
    }

    @Override
    public void read(NitriteMapper mapper, Document document) {

        id = document.get("id", Long.class);
        name = document.get("name", String.class);
        urls = (List<String>) document.get("urls");
        proxies = (List<String>) document.get("proxies");

        lastFetch = document.get("lastFetch",Long.class);
        enable = document.get("enable",Boolean.class);
        internal = document.get("internal",Boolean.class);

    }



}
