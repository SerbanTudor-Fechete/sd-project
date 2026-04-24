package com.andrei.demo.service;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Part;
import com.andrei.demo.model.PartCreateDTO;
import com.andrei.demo.repository.PartRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PartServiceTests {

    @Mock
    private PartRepository partRepository;

    @InjectMocks
    private PartService partService;

    private AutoCloseable closeable;
    private UUID uuid;
    private Part part;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        uuid = UUID.randomUUID();
        part = new Part();
        part.setId(uuid);
        part.setName("Brake Pads");
        part.setPrice(150.0);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testGetParts() {
        when(partRepository.findAll()).thenReturn(List.of(part));
        List<Part> result = partService.getParts();
        assertEquals(1, result.size());
        verify(partRepository, times(1)).findAll();
    }

    @Test
    void testAddPart() {
        PartCreateDTO dto = new PartCreateDTO();
        dto.setName("Brake Pads");
        dto.setPrice(150.0);

        when(partRepository.save(any(Part.class))).thenReturn(part);
        Part result = partService.addPart(dto);

        assertNotNull(result);
        assertEquals(150.0, result.getPrice());
        verify(partRepository, times(1)).save(any(Part.class));
    }

    @Test
    void testUpdatePart_Success() throws ValidationException {
        Part updatedPart = new Part();
        updatedPart.setName("Engine Oil");
        updatedPart.setPrice(45.0);

        when(partRepository.findById(uuid)).thenReturn(Optional.of(part));
        when(partRepository.save(any(Part.class))).thenReturn(updatedPart);

        Part result = partService.updatePart(uuid, updatedPart);
        assertEquals("Engine Oil", result.getName());
    }

    @Test
    void testUpdatePart_NotFound() {
        when(partRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> partService.updatePart(uuid, new Part()));
    }
}