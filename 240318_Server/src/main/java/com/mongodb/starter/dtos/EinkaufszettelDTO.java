package com.mongodb.starter.dtos;

import com.mongodb.starter.models.EinkaufszettelEntity;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public record EinkaufszettelDTO(String id, Boolean isChecked, int anzahl, String produkt)
{
    public EinkaufszettelDTO(EinkaufszettelEntity p) {
        this(p.getId() == null ? new ObjectId().toHexString() : p.getId().toHexString(), p.getStatus(),
             p.getAnzahl(), p.getProdukt() );
    }

    public EinkaufszettelEntity toPersonEntity() {
        ObjectId _id = id == null ? new ObjectId() : new ObjectId(id);
        return new EinkaufszettelEntity(_id, isChecked, anzahl, produkt);
    }
}
