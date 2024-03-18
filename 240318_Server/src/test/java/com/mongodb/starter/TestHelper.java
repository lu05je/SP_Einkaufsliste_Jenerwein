package com.mongodb.starter;

import com.mongodb.starter.dtos.EinkaufszettelDTO;
import com.mongodb.starter.models.EinkaufszettelEntity;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class TestHelper {

    EinkaufszettelEntity getMaxEntity() {
        return new EinkaufszettelEntity().setFirstName("Maxime")
                                 .setLastName("Beugnet")
                                 .setAddress(new AddressEntity().setCity("Paris")
                                                                .setCountry("France")
                                                                .setNumber(1)
                                                                .setPostcode("12345")
                                                                .setStreet("The Best Street"))
                                 .setAge(31)
                                 .setInsurance(true)
                                 .setCars(List.of(new CarEntity().setBrand("Ferrari")
                                                                 .setMaxSpeedKmH(339f)
                                                                 .setModel("SF90 Stradale")));
    }

    EinkaufszettelEntity getAlexEntity() {
        return new EinkaufszettelEntity().setFirstName("Alex")
                                 .setLastName("Beugnet")
                                 .setAddress(new AddressEntity().setCity("Toulouse")
                                                                .setCountry("France")
                                                                .setNumber(2)
                                                                .setPostcode("54321")
                                                                .setStreet("Another Street"))
                                 .setAge(27)
                                 .setInsurance(false)
                                 .setCars(List.of(new CarEntity().setBrand("Mercedes")
                                                                 .setMaxSpeedKmH(355f)
                                                                 .setModel("Project One")));
    }

    EinkaufszettelDTO getMaxDTO() {
        return new EinkaufszettelDTO(getMaxEntity());
    }

    public EinkaufszettelDTO getMaxDTOWithId(ObjectId id) {
        return new EinkaufszettelDTO(getMaxEntity().setId(id));
    }

    EinkaufszettelDTO getAlexDTO() {
        return new EinkaufszettelDTO(getAlexEntity());
    }

    EinkaufszettelDTO getAlexDTOWithId(ObjectId id) {
        return new EinkaufszettelDTO(getAlexEntity().setId(id));
    }

    List<EinkaufszettelEntity> getListMaxAlexEntity() {
        return List.of(getMaxEntity(), getAlexEntity());
    }

    List<EinkaufszettelDTO> getListMaxAlexDTO() {
        return List.of(getMaxDTO(), getAlexDTO());
    }
}
