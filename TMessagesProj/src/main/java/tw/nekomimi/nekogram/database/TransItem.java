package tw.nekomimi.nekogram.database;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class TransItem {
    @Id
    public long id;
    @Index
    public String code;
    @Index
    public String text;
    public String trans;

    public TransItem() {
    }

    public TransItem(String code, String text, String trans) {
        this.code = code;
        this.text = text;
        this.trans = trans;
    }
}
