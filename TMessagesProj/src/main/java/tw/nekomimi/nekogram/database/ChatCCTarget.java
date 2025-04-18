package tw.nekomimi.nekogram.database;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class ChatCCTarget {
    @Id(assignable = true)
    public long chatId;
    public String ccTarget;

    public ChatCCTarget() {
    }

    public ChatCCTarget(Long chatId, String ccTarget) {
        this.chatId = chatId;
        this.ccTarget = ccTarget;
    }
}
