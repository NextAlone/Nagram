package tw.nekomimi.nekogram.transtale.mapper;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.common.mapper.EntityConverter;
import org.dizitart.no2.common.mapper.NitriteMapper;

import tw.nekomimi.nekogram.transtale.entity.ChatCCTarget;

public class ChatCCTargetConverter implements EntityConverter<ChatCCTarget> {
    @Override
    public Class<ChatCCTarget> getEntityType() {
        return ChatCCTarget.class;
    }

    @Override
    public Document toDocument(ChatCCTarget entity, NitriteMapper nitriteMapper) {
        return Document.createDocument()
                .put("chatId", entity.chatId)
                .put("ccTarget", entity.ccTarget);
    }

    @Override
    public ChatCCTarget fromDocument(Document document, NitriteMapper nitriteMapper) {
        ChatCCTarget entity = new ChatCCTarget();
        entity.chatId = document.get("chatId", Long.class);
        entity.ccTarget = document.get("ccTarget", String.class);
        return entity;
    }
}
