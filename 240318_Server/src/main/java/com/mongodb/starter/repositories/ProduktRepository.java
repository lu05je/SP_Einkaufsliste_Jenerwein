package com.mongodb.starter.repositories;

import com.mongodb.starter.models.ProduktEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduktRepository {

    ProduktEntity save(ProduktEntity produktEntity);

    List<ProduktEntity> saveAll(List<ProduktEntity> personEntities);

    List<ProduktEntity> findAll();

    List<ProduktEntity> findAll(List<String> ids);

    ProduktEntity findOne(String id);

    long count();

    long delete(String id);

    long delete(List<String> ids);

    long deleteAll();

    ProduktEntity update(ProduktEntity produktEntity);

    long update(List<ProduktEntity> personEntities);

}
