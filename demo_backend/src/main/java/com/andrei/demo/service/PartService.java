package com.andrei.demo.service;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Part;
import com.andrei.demo.model.PartCreateDTO;
import com.andrei.demo.repository.PartRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PartService {

    private final PartRepository partRepository;

    public List<Part> getParts() {
        return partRepository.findAll();
    }

    public Part addPart(PartCreateDTO partCreateDTO) {
        Part part = new Part();
        part.setName(partCreateDTO.getName());
        part.setPrice(partCreateDTO.getPrice());

        return partRepository.save(part);
    }

    public void deletePart(UUID id) {
        partRepository.deleteById(id);
    }

    public Part updatePart(UUID id, Part part) throws ValidationException {
        Optional<Part> partOptional = partRepository.findById(id);

        if(partOptional.isEmpty()) {
            throw new ValidationException("Part with id " + id + " not found");
        }

        Part existingPart = partOptional.get();
        existingPart.setName(part.getName());
        existingPart.setPrice(part.getPrice());

        return partRepository.save(existingPart);
    }

    public Part getPartById(UUID uuid) {
        return partRepository.findById(uuid).orElseThrow(
                () -> new IllegalStateException("Part with id " + uuid + " not found"));
    }

    public Part updatePartPrice(UUID uuid, Double newPrice) {
        Part existingPart = getPartById(uuid);
        existingPart.setPrice(newPrice);
        return partRepository.save(existingPart);
    }
}