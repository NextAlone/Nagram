package tw.nekomimi.nekogram.transtale;

import org.dizitart.no2.Document;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;

@Index("chatId")
public class ChatLanguage implements Mappable {

    @Id
    public int chatId;

    public String language;

    public ChatLanguage() {
    }

    public ChatLanguage(int chatId, String language) {
        this.chatId = chatId;
        this.language = language;
    }

    @Override public Document write(NitriteMapper mapper) {
        Document document = new Document();
        document.put("chatId",chatId);
        document.put("language",language);
        return document;
    }

    @Override public void read(NitriteMapper mapper, Document document) {
        chatId = ((int) document.get("chatId"));
        language = ((String) document.get("language"));
    }

}
