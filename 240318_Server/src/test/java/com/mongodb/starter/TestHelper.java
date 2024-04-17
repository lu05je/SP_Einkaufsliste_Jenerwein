package com.mongodb.starter;

import com.mongodb.starter.dtos.ProduktDTO;
import com.mongodb.starter.models.ProduktEntity;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class TestHelper {

    ProduktEntity getJogurtEntity() {
        return new ProduktEntity().setProdukt("Jogurt");
    }

    ProduktEntity getTomateEntity() {
        return new ProduktEntity().setProdukt("Tomate");
    }

    ProduktDTO getJogurtDTO() {
        return new ProduktDTO(getJogurtEntity());
    }

    public ProduktDTO getJogurtDTOWithId(ObjectId id) {
        return new ProduktDTO(getJogurtEntity().setId(id));
    }

    ProduktDTO getTomateDTO() {
        return new ProduktDTO(getTomateEntity());
    }

    ProduktDTO getTomateDTOWithId(ObjectId id) {
        return new ProduktDTO(getTomateEntity().setId(id));
    }

    List<ProduktEntity> getListJogurtTomateEntity() {
        return List.of(getJogurtEntity(), getTomateEntity());
    }

    List<ProduktDTO> getListJogurtTomateDTO() {
        return List.of(getJogurtDTO(), getTomateDTO());
    }
}
