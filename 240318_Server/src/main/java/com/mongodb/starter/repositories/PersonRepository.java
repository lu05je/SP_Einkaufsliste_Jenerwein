package com.mongodb.starter.repositories;

import com.mongodb.starter.models.EinkaufszettelEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository {

    EinkaufszettelEntity save(EinkaufszettelEntity einkaufszettelEntity);

    List<EinkaufszettelEntity> saveAll(List<EinkaufszettelEntity> personEntities);

    List<EinkaufszettelEntity> findAll();

    List<EinkaufszettelEntity> findAll(List<String> ids);

    EinkaufszettelEntity findOne(String id);

    long count();

    long delete(String id);

    long delete(List<String> ids);

    long deleteAll();

    EinkaufszettelEntity update(EinkaufszettelEntity einkaufszettelEntity);

    long update(List<EinkaufszettelEntity> personEntities);

    double getAverageAge();

}
