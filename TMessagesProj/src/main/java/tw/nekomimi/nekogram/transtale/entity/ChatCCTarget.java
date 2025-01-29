package tw.nekomimi.nekogram.transtale.entity;

import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;

@Index(fields = "chatId")
public class ChatCCTarget {
    @Id
    public Long chatId;
    public String ccTarget;

    public ChatCCTarget() {
    }

    public ChatCCTarget(Long chatId, String ccTarget) {
        this.chatId = chatId;
        this.ccTarget = ccTarget;
    }
}
