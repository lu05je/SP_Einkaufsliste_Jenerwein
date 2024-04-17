package com.mongodb.starter.dtos;

import com.mongodb.starter.models.ProduktEntity;
import org.bson.types.ObjectId;

public record ProduktDTO(String id, Boolean isChecked, String produkt)
{
    public ProduktDTO(ProduktEntity p) {
        this(p.getId() == null ? new ObjectId().toHexString() : p.getId().toHexString(), p.getStatus(), p.getProdukt() );
    }

    public ProduktEntity toProduktEntity() {
        ObjectId _id = id == null ? new ObjectId() : new ObjectId(id);
        return new ProduktEntity(_id, produkt);
    }
}
