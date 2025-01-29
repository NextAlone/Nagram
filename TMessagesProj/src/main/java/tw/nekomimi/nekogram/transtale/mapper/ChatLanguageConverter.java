package tw.nekomimi.nekogram.transtale.mapper;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.common.mapper.EntityConverter;
import org.dizitart.no2.common.mapper.NitriteMapper;

import tw.nekomimi.nekogram.transtale.entity.ChatLanguage;

public class ChatLanguageConverter implements EntityConverter<ChatLanguage> {
    @Override
    public Class<ChatLanguage> getEntityType() {
        return ChatLanguage.class;
    }

    @Override
    public Document toDocument(ChatLanguage entity, NitriteMapper nitriteMapper) {
        return Document.createDocument()
                .put("chatId", entity.chatId)
                .put("language", entity.language);
    }

    @Override
    public ChatLanguage fromDocument(Document document, NitriteMapper nitriteMapper) {
        ChatLanguage entity = new ChatLanguage();
        entity.chatId = document.get("chatId", Long.class);
        entity.language = document.get("language", String.class);
        return entity;
    }
}
