package com.mongodb.starter.services;


import com.mongodb.starter.dtos.ProduktDTO;

import java.util.List;

public interface ProduktService {

    ProduktDTO save(ProduktDTO ProduktDTO);

    List<ProduktDTO> saveAll(List<ProduktDTO> personEntities);

    List<ProduktDTO> findAll();

    List<ProduktDTO> findAll(List<String> ids);

    ProduktDTO findOne(String id);

    long count();

    long delete(String id);

    long delete(List<String> ids);

    long deleteAll();

    ProduktDTO update(ProduktDTO ProduktDTO);

    long update(List<ProduktDTO> personEntities);

    
}
