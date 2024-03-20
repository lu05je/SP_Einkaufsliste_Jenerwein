package com.mongodb.starter.services;

import com.mongodb.starter.dtos.ProduktDTO;
import com.mongodb.starter.repositories.ProduktRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProduktServiceImpl implements ProduktService {

    private final ProduktRepository produktRepository;

    public ProduktServiceImpl(ProduktRepository produktRepository) {
        this.produktRepository = produktRepository;
    }

    @Override
    public ProduktDTO save(ProduktDTO ProduktDTO) {
        return new ProduktDTO(produktRepository.save(ProduktDTO.toProduktEntity()));
    }

    @Override
    public List<ProduktDTO> saveAll(List<ProduktDTO> personEntities) {
        return personEntities.stream()
                             .map(ProduktDTO::toProduktEntity)
                             .peek(produktRepository::save)
                             .map(ProduktDTO::new)
                             .toList();
    }

    @Override
    public List<ProduktDTO> findAll() {
        return produktRepository.findAll().stream().map(ProduktDTO::new).toList();
    }

    @Override
    public List<ProduktDTO> findAll(List<String> ids) {
        return produktRepository.findAll(ids).stream().map(ProduktDTO::new).toList();
    }

    @Override
    public ProduktDTO findOne(String id) {
        return new ProduktDTO(produktRepository.findOne(id));
    }

    @Override
    public long count() {
        return produktRepository.count();
    }

    @Override
    public long delete(String id) {
        return produktRepository.delete(id);
    }

    @Override
    public long delete(List<String> ids) {
        return produktRepository.delete(ids);
    }

    @Override
    public long deleteAll() {
        return produktRepository.deleteAll();
    }

    @Override
    public ProduktDTO update(ProduktDTO ProduktDTO) {
        return new ProduktDTO(produktRepository.update(ProduktDTO.toProduktEntity()));
    }

    @Override
    public long update(List<ProduktDTO> personEntities) {
        return produktRepository.update(personEntities.stream().map(ProduktDTO::toProduktEntity).toList());
    }

}
