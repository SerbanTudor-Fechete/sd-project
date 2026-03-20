package com.andrei.demo.controller;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Motorcycle;
import com.andrei.demo.model.MotorcycleCreateDTO;
import com.andrei.demo.service.MotorcycleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
public class MotorcycleController {
    private final MotorcycleService motorcycleService;

    @GetMapping("/motorcycle")
    public List<Motorcycle> getMotorcycles() {
        return motorcycleService.getMotorcycles();
    }

    @GetMapping("/motorcycle/{uuid}")
    public Motorcycle getMotorcycleById(@PathVariable UUID uuid) {
        return motorcycleService.getMotorcycleById(uuid);
    }

    @PostMapping("/motorcycle")
    public Motorcycle addMotorcycle(@Valid @RequestBody MotorcycleCreateDTO motorcycleCreateDTO) throws ValidationException{
        return motorcycleService.addMotorcycle(motorcycleCreateDTO);
    }
    @PutMapping("/motorcycle/{uuid}")
    public Motorcycle updateMotorcycle(@PathVariable UUID uuid, @RequestBody Motorcycle motorcycle)
        throws ValidationException {
        return motorcycleService.updateMotorcycle(uuid, motorcycle);
    }
    @DeleteMapping("/motorcycle/{uuid}")
    public void deleteMotorcycle(@PathVariable UUID uuid) {
        motorcycleService.deleteMotorcycle(uuid);
    }

    @PatchMapping("/motorcycle/{uuid}/license")
    public Motorcycle updateMotorcycleLicence(@PathVariable UUID uuid, @RequestBody String newLicense){
        return motorcycleService.updateMotorcycleLicense(uuid, newLicense);
    }

}
