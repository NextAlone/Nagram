package tw.nekomimi.nekogram.translator;

import org.dizitart.no2.Document;
import org.dizitart.no2.IndexType;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.objects.Indices;

@Indices({
        @Index(value = "text", type = IndexType.Unique)
})
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
        Document document = new Document();
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
