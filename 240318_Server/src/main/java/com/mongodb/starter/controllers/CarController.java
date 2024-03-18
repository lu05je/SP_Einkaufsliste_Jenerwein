package com.mongodb.starter.controllers;

import com.mongodb.starter.dtos.CarDTO;
import com.mongodb.starter.dtos.PersonDTO;
import com.mongodb.starter.services.CarService;
import com.mongodb.starter.services.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {
    private final static Logger LOGGER = LoggerFactory.getLogger(PersonController.class);
    private final CarService carService;

    public CarController(CarService carService) {

        this.carService = carService;
    }

    @PostMapping("car")
    @ResponseStatus(HttpStatus.CREATED)
    public CarDTO postCar(@RequestBody CarDTO CarDTO) {
        return carService.save(CarDTO);
    }

    @PostMapping("cars")
    @ResponseStatus(HttpStatus.CREATED)
    public List<CarDTO> postCars(@RequestBody List<CarDTO> carEntities) {
        return carService.saveAll(carEntities);
    }
    @GetMapping("cars")
    public List<CarDTO> getCars() {
        return carService.findAll();
    }

    @GetMapping("car/{id}")
    public ResponseEntity<CarDTO> getCar(@PathVariable String id) {
        CarDTO carDTO = carService.findOne(id);
        if (carDTO == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(carDTO);
    }

    @GetMapping("cars/{ids}")
    public List<CarDTO> getCars(@PathVariable String ids) {
        List<String> listIds = List.of(ids.split(","));
        return carService.findAll(listIds);
    }

    @GetMapping("cars/count")
    public Long getCount() {
        return carService.count();
    }

    @DeleteMapping("car/{id}")
    public Long deleteCar(@PathVariable String id) {
        return carService.delete(id);
    }

    @DeleteMapping("cars/{ids}")
    public Long deleteCars(@PathVariable String ids) {
        List<String> listIds = List.of(ids.split(","));
        return carService.delete(listIds);
    }

    @DeleteMapping("cars")
    public Long deleteCars() {
        return carService.deleteAll();
    }


    @PutMapping("car")
    public CarDTO putCar(@RequestBody CarDTO carDTO) {
        return carService.update(carDTO);
    }

    @PutMapping("cars")
    public Long putCars(@RequestBody List<CarDTO> carDTOs) {
        return carService.update(carDTOs);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final Exception handleAllExceptions(RuntimeException e) {
        LOGGER.error("Internal server error.", e);
        return e;
    }

}
