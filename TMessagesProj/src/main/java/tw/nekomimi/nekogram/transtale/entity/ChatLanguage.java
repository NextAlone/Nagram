package tw.nekomimi.nekogram.transtale.entity;

import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;

@Index(fields = "chatId")
public class ChatLanguage {
    @Id
    public Long chatId;
    public String language;

    public ChatLanguage() {
    }

    public ChatLanguage(Long chatId, String language) {
        this.chatId = chatId;
        this.language = language;
    }
}
