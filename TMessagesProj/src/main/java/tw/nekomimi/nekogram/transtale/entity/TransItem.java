package tw.nekomimi.nekogram.transtale.entity;

import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;

@Index(fields = "text")
public class TransItem {
    @Id
    public String text;
    public String trans;

    public TransItem() {
    }

    public TransItem(String text, String trans) {
        this.text = text;
        this.trans = trans;
    }
}
