package com.mongodb.starter.services;

import com.mongodb.starter.dtos.EinkaufszettelDTO;
import com.mongodb.starter.repositories.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public EinkaufszettelDTO save(EinkaufszettelDTO EinkaufszettelDTO) {
        return new EinkaufszettelDTO(personRepository.save(EinkaufszettelDTO.toPersonEntity()));
    }

    @Override
    public List<EinkaufszettelDTO> saveAll(List<EinkaufszettelDTO> personEntities) {
        return personEntities.stream()
                             .map(EinkaufszettelDTO::toPersonEntity)
                             .peek(personRepository::save)
                             .map(EinkaufszettelDTO::new)
                             .toList();
    }

    @Override
    public List<EinkaufszettelDTO> findAll() {
        return personRepository.findAll().stream().map(EinkaufszettelDTO::new).toList();
    }

    @Override
    public List<EinkaufszettelDTO> findAll(List<String> ids) {
        return personRepository.findAll(ids).stream().map(EinkaufszettelDTO::new).toList();
    }

    @Override
    public EinkaufszettelDTO findOne(String id) {
        return new EinkaufszettelDTO(personRepository.findOne(id));
    }

    @Override
    public long count() {
        return personRepository.count();
    }

    @Override
    public long delete(String id) {
        return personRepository.delete(id);
    }

    @Override
    public long delete(List<String> ids) {
        return personRepository.delete(ids);
    }

    @Override
    public long deleteAll() {
        return personRepository.deleteAll();
    }

    @Override
    public EinkaufszettelDTO update(EinkaufszettelDTO EinkaufszettelDTO) {
        return new EinkaufszettelDTO(personRepository.update(EinkaufszettelDTO.toPersonEntity()));
    }

    @Override
    public long update(List<EinkaufszettelDTO> personEntities) {
        return personRepository.update(personEntities.stream().map(EinkaufszettelDTO::toPersonEntity).toList());
    }

    @Override
    public double getAverageAge() {
        return personRepository.getAverageAge();
    }
}
