package tw.nekomimi.nekogram.proxy;

import androidx.annotation.NonNull;

import org.dizitart.no2.Document;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.parts.ProxyLoadsKt;

@Index("id")
@SuppressWarnings("unchecked")
public class SubInfo implements Mappable {

    @Id
    public long id;
    public String name;
    public List<String> urls = new LinkedList<>();
    public List<String> proxies = new LinkedList<>();
    public Long lastFetch = -1L;
    public boolean enable = true;
    public boolean internal;

    public String displayName() {

        if (id == SubManager.publicProxySubID)
            return LocaleController.getString("PublicPrefix", R.string.PublicPrefix);

        if (name.length() < 10) return name;

        return name.substring(0, 10) + "...";
    }

    public List<String> reloadProxies() throws IOException {

        HashMap<String, Exception> exceptions = new HashMap<>();

        try {
            if (id == SubManager.publicProxySubID) {
                if (!NekoConfig.enablePublicProxy.Bool())
                    return new ArrayList<>();
                List<String> pubs = ProxyLoadsKt.loadProxiesPublic(urls, exceptions);
                if (!NekoConfig.enablePublicProxy.Bool())
                    return new ArrayList<>();
                else
                    return pubs;
            } else {
                return ProxyLoadsKt.loadProxies(urls, exceptions);
            }
//            return id == SubManager.publicProxySubID ?  :
        } catch (Exception ignored) {
        }

        throw new AllTriesFailed(exceptions);

    }

    public static class AllTriesFailed extends IOException {

        public AllTriesFailed(HashMap<String, Exception> exceptions) {
            this.exceptions = exceptions;
        }

        public HashMap<String, Exception> exceptions;

        @NonNull
        @Override
        public String toString() {

            StringBuilder errors = new StringBuilder();

            for (Map.Entry<String, Exception> e : exceptions.entrySet()) {

                errors.append(e.getKey()).append(": ");

                errors.append(e.getValue().getClass().getSimpleName());

                if (!StrUtil.isBlank(e.getValue().getMessage())) {

                    errors.append(" ( ");
                    errors.append(e.getValue().getMessage());
                    errors.append(" )");

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

        document.put("id", id);
        document.put("name", name);
        document.put("urls", urls);
        document.put("proxies", proxies);
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

        lastFetch = document.get("lastFetch", Long.class);
        enable = document.get("enable", Boolean.class);
        internal = document.get("internal", Boolean.class);

    }

}