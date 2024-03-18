package com.mongodb.starter.repositories;

import com.mongodb.starter.models.CarEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository {
    CarEntity save(CarEntity carEntity);
    List<CarEntity> saveAll(List<CarEntity> carEntities);
    List<CarEntity> findAll();
    List<CarEntity> findAll(List<String> ids);
    CarEntity findOne(String id);
    long count();
    long delete(String id);

    long delete(List<String> ids);
    long deleteAll();
    CarEntity update(CarEntity carEntity);
    long update(List<CarEntity> carEntities);
}