package com.mongodb.starter.services;

import java.util.List;

public interface CarService {
    CarDTO save(CarDTO CarDTO);
    List<CarDTO> saveAll(List<CarDTO> carEntities);
    List<CarDTO> findAll();
    List<CarDTO> findAll(List<String> ids);
    long count();
    CarDTO findOne(String id);
    long delete(String id);

    long delete(List<String> ids);
    long deleteAll();
    CarDTO update(CarDTO CarDTO);
    long update(List<CarDTO> carEntities);
}
