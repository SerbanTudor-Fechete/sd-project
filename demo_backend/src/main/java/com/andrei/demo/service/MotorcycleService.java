package com.andrei.demo.service;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Motorcycle;
import com.andrei.demo.model.MotorcycleCreateDTO;
import com.andrei.demo.repository.MotorcycleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@AllArgsConstructor
public class MotorcycleService {
    private final MotorcycleRepository motorcycleRepository;

    public List<Motorcycle> getMotorcycles() {
        return motorcycleRepository.findAll();
    }

    public Motorcycle addMotorcycle (MotorcycleCreateDTO motorcycleCreateDTO) throws ValidationException {
        if (motorcycleRepository.existsByLicensePlate(motorcycleCreateDTO.getLicensePlate())) {
            throw new ValidationException("Motorcycle with license plate " + motorcycleCreateDTO.getLicensePlate() + " is already registered.");
        }

        int currentYear = Year.now().getValue();
        if (Integer.parseInt(motorcycleCreateDTO.getManufactureYear()) > currentYear) {
            throw new ValidationException("Manufacture year cannot be in the future.");
        }

        Motorcycle motorcycle = new Motorcycle();
        motorcycle.setBrand(motorcycleCreateDTO.getBrand());
        motorcycle.setModel(motorcycleCreateDTO.getModel());
        motorcycle.setLicensePlate(motorcycleCreateDTO.getLicensePlate());
        motorcycle.setManufactureYear(motorcycleCreateDTO.getManufactureYear());

        return motorcycleRepository.save(motorcycle);
    }

    public void deleteMotorcycle(UUID id) { motorcycleRepository.deleteById(id); }

    public Motorcycle updateMotorcycle(UUID uuid, Motorcycle motorcycle) throws ValidationException {
        Optional<Motorcycle> motorcycleOptional = motorcycleRepository.findById(uuid);

        if(motorcycleOptional.isEmpty()) {
            throw new ValidationException("Motorcycle with id " + uuid + " not found");
        }
        Motorcycle existingMotorcycle =  motorcycleOptional.get();
        existingMotorcycle.setManufactureYear(motorcycle.getManufactureYear());
        existingMotorcycle.setBrand(motorcycle.getBrand());
        existingMotorcycle.setLicensePlate(motorcycle.getLicensePlate());
        existingMotorcycle.setModel(motorcycle.getModel());

        return motorcycleRepository.save(existingMotorcycle);
    }

    public Motorcycle getMotorcycleById(UUID uuid) {
        return motorcycleRepository.findById(uuid).orElseThrow(
                () -> new IllegalStateException("Motorcycle with id " + uuid + " not found"));
    }

    public Motorcycle updateMotorcycleLicense(UUID uuid, String newLicense){
        Motorcycle existingMotorcycle = getMotorcycleById(uuid);
        existingMotorcycle.setLicensePlate(newLicense);
        return motorcycleRepository.save(existingMotorcycle);
    }
}
