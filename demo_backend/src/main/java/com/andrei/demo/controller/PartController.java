package com.andrei.demo.controller;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Part;
import com.andrei.demo.model.PartCreateDTO;
import com.andrei.demo.service.PartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
@PreAuthorize("hasAuthority('ADMIN')")
public class PartController {

    private final PartService partService;

    @GetMapping("/part")
    public List<Part> getParts() {
        return partService.getParts();
    }

    @GetMapping("/part/{uuid}")
    public Part getPartById(@PathVariable UUID uuid) {
        return partService.getPartById(uuid);
    }

    @PostMapping("/part")
    public Part addPart(@Valid @RequestBody PartCreateDTO partCreateDTO) {
        return partService.addPart(partCreateDTO);
    }

    @PutMapping("/part/{uuid}")
    public Part updatePart(@PathVariable UUID uuid, @RequestBody Part part) throws ValidationException {
        return partService.updatePart(uuid, part);
    }

    @DeleteMapping("/part/{uuid}")
    public void deletePart(@PathVariable UUID uuid) {
        partService.deletePart(uuid);
    }

    @PatchMapping("/part/{uuid}/price")
    public Part updatePartPrice(@PathVariable UUID uuid, @RequestBody Double newPrice) {
        return partService.updatePartPrice(uuid, newPrice);
    }
}