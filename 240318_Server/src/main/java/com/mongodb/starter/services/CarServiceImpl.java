package com.mongodb.starter.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public CarDTO save(CarDTO carDTO) {
        return new CarDTO(carRepository.save(carDTO.toCarEntity()));
    }

    @Override
    public List<CarDTO> saveAll(List<CarDTO> carDTOs) {
        return carDTOs.stream()
                .map(CarDTO::toCarEntity)
                .peek(carRepository::save)
                .map(CarDTO::new)
                .toList();
    }

    @Override
    public List<CarDTO> findAll() {
        return carRepository.findAll().stream().map(CarDTO::new).toList();
    }

    @Override
    public List<CarDTO> findAll(List<String> ids) {
        return carRepository.findAll(ids).stream().map(CarDTO::new).toList();
    }

    @Override
    public CarDTO findOne(String id) {
        return new CarDTO(carRepository.findOne(id));
    }

    @Override
    public long count() {
        return carRepository.count();
    }

    @Override
    public long delete(String id) {
        return carRepository.delete(id);
    }

    @Override
    public long delete(List<String> ids) {
        return carRepository.delete(ids);
    }

    @Override
    public long deleteAll() {
        return carRepository.deleteAll();
    }

    @Override
    public CarDTO update(CarDTO carDTO) {
        return new CarDTO(carRepository.update(carDTO.toCarEntity()));
    }

    @Override
    public long update(List<CarDTO> carDTOs) {
        return carRepository.update(carDTOs.stream().map(CarDTO::toCarEntity).toList());
    }

}
