package tw.nekomimi.nekogram.transtale;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;

@Index(value = "text")
public class TransItem implements Mappable {

    @Id
    public String text;
    public String trans;

    public TransItem() {
    }

    public TransItem(String text, String trans) {
        this.text = text;
        this.trans = trans;
    }

    @Override
    public Document write(NitriteMapper mapper) {
        Document document = Document.createDocument();
        document.put("text",text);
        document.put("trans", trans);
        return document;
    }

    @Override
    public void read(NitriteMapper mapper, Document document) {
        text = (String) document.get("text");
        trans = (String) document.get("trans");
    }

}
