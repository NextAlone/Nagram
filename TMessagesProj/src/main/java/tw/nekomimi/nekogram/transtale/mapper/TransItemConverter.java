package tw.nekomimi.nekogram.transtale.mapper;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.common.mapper.EntityConverter;
import org.dizitart.no2.common.mapper.NitriteMapper;

import tw.nekomimi.nekogram.transtale.entity.TransItem;

public class TransItemConverter implements EntityConverter<TransItem> {
    @Override
    public Class<TransItem> getEntityType() {
        return TransItem.class;
    }

    @Override
    public Document toDocument(TransItem entity, NitriteMapper nitriteMapper) {
        return Document.createDocument()
                .put("text", entity.text)
                .put("trans", entity.trans);
    }

    @Override
    public TransItem fromDocument(Document document, NitriteMapper nitriteMapper) {
        TransItem entity = new TransItem();
        entity.text = document.get("text", String.class);
        entity.trans = document.get("trans", String.class);
        return entity;
    }
}
