package com.mongodb.starter.dtos;

import com.mongodb.starter.models.CarEntity;
import org.bson.types.ObjectId;

public record CarDTO(String id, String brand, String model, Float maxSpeedKmH) {

    public CarDTO(CarEntity c) {
        this(c.getId() == null ? new ObjectId().toHexString() : c.getId().toHexString(), c.getBrand(), c.getModel(), c.getMaxSpeedKmH());
    }

    public CarEntity toCarEntity() {
        ObjectId _id = id == null ? new ObjectId() : new ObjectId(id);
        return new CarEntity(_id, brand, model, maxSpeedKmH);
    }
}