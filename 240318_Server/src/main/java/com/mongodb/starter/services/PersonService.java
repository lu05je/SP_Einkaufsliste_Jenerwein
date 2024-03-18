package com.mongodb.starter.services;


import com.mongodb.starter.dtos.EinkaufszettelDTO;

import java.util.List;

public interface PersonService {

    EinkaufszettelDTO save(EinkaufszettelDTO EinkaufszettelDTO);

    List<EinkaufszettelDTO> saveAll(List<EinkaufszettelDTO> personEntities);

    List<EinkaufszettelDTO> findAll();

    List<EinkaufszettelDTO> findAll(List<String> ids);

    EinkaufszettelDTO findOne(String id);

    long count();

    long delete(String id);

    long delete(List<String> ids);

    long deleteAll();

    EinkaufszettelDTO update(EinkaufszettelDTO EinkaufszettelDTO);

    long update(List<EinkaufszettelDTO> personEntities);

    double getAverageAge();
    
}
