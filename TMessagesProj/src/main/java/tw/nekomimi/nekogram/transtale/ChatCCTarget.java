package tw.nekomimi.nekogram.transtale;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;

@Index("chatId")
public class ChatCCTarget implements Mappable {

    @Id
    public int chatId;
    public String ccTarget;

    public ChatCCTarget() {
    }

    public ChatCCTarget(int chatId, String ccTarget) {
        this.chatId = chatId;
        this.ccTarget = ccTarget;
    }

    @Override
    public Document write(NitriteMapper mapper) {
        Document document = Document.createDocument();
        document.put("chatId", chatId);
        document.put("ccTarget", ccTarget);
        return document;
    }

    @Override
    public void read(NitriteMapper mapper, Document document) {
        chatId = ((int) document.get("chatId"));
        ccTarget = ((String) document.get("ccTarget"));
    }

}
