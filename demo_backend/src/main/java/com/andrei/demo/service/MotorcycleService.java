package com.andrei.demo.service;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Motorcycle;
import com.andrei.demo.model.MotorcycleCreateDTO;
import com.andrei.demo.model.Person;
import com.andrei.demo.model.Role;
import com.andrei.demo.repository.MotorcycleRepository;
import com.andrei.demo.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MotorcycleService {
    private final MotorcycleRepository motorcycleRepository;
    private final PersonRepository personRepository;

    public List<Motorcycle> getMotorcycles() {
        return motorcycleRepository.findAll();
    }

    public Motorcycle addMotorcycle(MotorcycleCreateDTO motorcycleCreateDTO) throws ValidationException {
        if (motorcycleRepository.existsByLicensePlate(motorcycleCreateDTO.getLicensePlate())) {
            throw new ValidationException("Motorcycle with license plate " + motorcycleCreateDTO.getLicensePlate() + " is already registered.");
        }

        int currentYear = Year.now().getValue();
        if (Integer.parseInt(motorcycleCreateDTO.getManufactureYear()) > currentYear) {
            throw new ValidationException("Manufacture year cannot be in the future.");
        }

        Person owner = personRepository.findById(motorcycleCreateDTO.getOwnerId())
                .orElseThrow(() -> new ValidationException("Owner with id " + motorcycleCreateDTO.getOwnerId() + " not found."));

        if (owner.getRole() == Role.ADMIN) {
            throw new ValidationException("Business Rule Violation: Admins cannot be assigned as motorcycle owners.");
        }

        Motorcycle motorcycle = new Motorcycle();
        motorcycle.setBrand(motorcycleCreateDTO.getBrand());
        motorcycle.setModel(motorcycleCreateDTO.getModel());
        motorcycle.setLicensePlate(motorcycleCreateDTO.getLicensePlate());
        motorcycle.setManufactureYear(motorcycleCreateDTO.getManufactureYear());
        motorcycle.setOwner(owner);

        return motorcycleRepository.save(motorcycle);
    }

    public void deleteMotorcycle(UUID id) {
        motorcycleRepository.deleteById(id);
    }

    public Motorcycle updateMotorcycle(UUID uuid, MotorcycleCreateDTO dto) throws ValidationException {
        Motorcycle existingMotorcycle = motorcycleRepository.findById(uuid)
                .orElseThrow(() -> new ValidationException("Motorcycle with id " + uuid + " not found"));

        Person owner = personRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new ValidationException("Owner with id " + dto.getOwnerId() + " not found."));

        existingMotorcycle.setManufactureYear(dto.getManufactureYear());
        existingMotorcycle.setBrand(dto.getBrand());
        existingMotorcycle.setLicensePlate(dto.getLicensePlate());
        existingMotorcycle.setModel(dto.getModel());
        existingMotorcycle.setOwner(owner);

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