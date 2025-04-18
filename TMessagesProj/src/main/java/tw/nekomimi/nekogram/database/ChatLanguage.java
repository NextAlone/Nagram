package tw.nekomimi.nekogram.database;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class ChatLanguage {
    @Id(assignable = true)
    public Long chatId;
    public String language;

    public ChatLanguage() {
    }

    public ChatLanguage(Long chatId, String language) {
        this.chatId = chatId;
        this.language = language;
    }
}
