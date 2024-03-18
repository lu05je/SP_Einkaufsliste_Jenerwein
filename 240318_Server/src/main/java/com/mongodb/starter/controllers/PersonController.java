package com.mongodb.starter.controllers;

import com.mongodb.starter.dtos.EinkaufszettelDTO;
import com.mongodb.starter.services.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PersonController {

    private final static Logger LOGGER = LoggerFactory.getLogger(PersonController.class);
    private final PersonService personService;

    public PersonController(PersonService personService) {

        this.personService = personService;
    }

    @PostMapping("person")
    @ResponseStatus(HttpStatus.CREATED)
    public EinkaufszettelDTO postPerson(@RequestBody EinkaufszettelDTO EinkaufszettelDTO) {
        return personService.save(EinkaufszettelDTO);
    }

    @PostMapping("persons")
    @ResponseStatus(HttpStatus.CREATED)
    public List<EinkaufszettelDTO> postPersons(@RequestBody List<EinkaufszettelDTO> personEntities) {
        return personService.saveAll(personEntities);
    }

    @GetMapping("persons")
    public List<EinkaufszettelDTO> getPersons() {
        return personService.findAll();
    }

    @GetMapping("person/{id}")
    public ResponseEntity<EinkaufszettelDTO> getPerson(@PathVariable String id) {
        EinkaufszettelDTO EinkaufszettelDTO = personService.findOne(id);
        if (EinkaufszettelDTO == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(EinkaufszettelDTO);
    }

    @GetMapping("persons/{ids}")
    public List<EinkaufszettelDTO> getPersons(@PathVariable String ids) {
        List<String> listIds = List.of(ids.split(","));
        return personService.findAll(listIds);
    }

    @GetMapping("persons/count")
    public Long getCount() {
        return personService.count();
    }

    @DeleteMapping("person/{id}")
    public Long deletePerson(@PathVariable String id) {
        return personService.delete(id);
    }

    @DeleteMapping("persons/{ids}")
    public Long deletePersons(@PathVariable String ids) {
        List<String> listIds = List.of(ids.split(","));
        return personService.delete(listIds);
    }

    @DeleteMapping("persons")
    public Long deletePersons() {
        return personService.deleteAll();
    }

    @PutMapping("person")
    public EinkaufszettelDTO putPerson(@RequestBody EinkaufszettelDTO EinkaufszettelDTO) {
        return personService.update(EinkaufszettelDTO);
    }

    @PutMapping("persons")
    public Long putPerson(@RequestBody List<EinkaufszettelDTO> personEntities) {
        return personService.update(personEntities);
    }

    @GetMapping("persons/averageAge")
    public Double averageAge() {
        return personService.getAverageAge();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final Exception handleAllExceptions(RuntimeException e) {
        LOGGER.error("Internal server error.", e);
        return e;
    }
}
