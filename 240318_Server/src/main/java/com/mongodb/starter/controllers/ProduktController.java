package com.mongodb.starter.controllers;

import com.mongodb.starter.dtos.ProduktDTO;
import com.mongodb.starter.services.ProduktService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProduktController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProduktController.class);
    private final ProduktService produktService;

    public ProduktController(ProduktService produktService) {

        this.produktService = produktService;
    }

    @PostMapping("produkt")
    @ResponseStatus(HttpStatus.CREATED)
    public ProduktDTO postProdukt(@RequestBody ProduktDTO ProduktDTO) {
        return produktService.save(ProduktDTO);
    }

    @PostMapping("produkte")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ProduktDTO> postProdukte(@RequestBody List<ProduktDTO> personEntities) {
        return produktService.saveAll(personEntities);
    }

    @GetMapping("produkte")
    public List<ProduktDTO> getProdukte() {
        return produktService.findAll();
    }

    @GetMapping("produkt/{id}")
    public ResponseEntity<ProduktDTO> getProdukt(@PathVariable String id) {
        ProduktDTO ProduktDTO = produktService.findOne(id);
        if (ProduktDTO == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(ProduktDTO);
    }

    @GetMapping("produkte/{ids}")
    public List<ProduktDTO> getProdukte(@PathVariable String ids) {
        List<String> listIds = List.of(ids.split(","));
        return produktService.findAll(listIds);
    }

    @GetMapping("produkte/count")
    public Long getCount() {
        return produktService.count();
    }

    @DeleteMapping("produkt/{id}")
    public Long deleteProdukt(@PathVariable String id) {
        return produktService.delete(id);
    }

    @DeleteMapping("produkte/{ids}")
    public Long deleteProdukte(@PathVariable String ids) {
        List<String> listIds = List.of(ids.split(","));
        return produktService.delete(listIds);
    }

    @DeleteMapping("produkte")
    public Long deleteProdukte() {
        return produktService.deleteAll();
    }

    @PutMapping("produkt")
    public ProduktDTO putProdukt(@RequestBody ProduktDTO ProduktDTO) {
        return produktService.update(ProduktDTO);
    }

    @PutMapping("produkte")
    public Long putProdukt(@RequestBody List<ProduktDTO> personEntities) {
        return produktService.update(personEntities);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final Exception handleAllExceptions(RuntimeException e) {
        LOGGER.error("Internal server error.", e);
        return e;
    }
}
