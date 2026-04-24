package com.andrei.demo.service;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Motorcycle;
import com.andrei.demo.model.MotorcycleCreateDTO;
import com.andrei.demo.model.Person;
import com.andrei.demo.repository.MotorcycleRepository;
import com.andrei.demo.repository.PersonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MotorcycleServiceTests {

    @Mock
    private MotorcycleRepository motorcycleRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private MotorcycleService motorcycleService;

    private AutoCloseable closeable;
    private UUID uuid;
    private UUID ownerId;
    private Motorcycle motorcycle;
    private Person owner;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        uuid = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        owner = new Person();
        owner.setId(ownerId);
        owner.setName("John Doe");

        motorcycle = new Motorcycle();
        motorcycle.setId(uuid);
        motorcycle.setBrand("Honda");
        motorcycle.setModel("CBR600");
        motorcycle.setLicensePlate("B123ABC");
        motorcycle.setManufactureYear("2020");
        motorcycle.setOwner(owner);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testGetMotorcycles() {
        when(motorcycleRepository.findAll()).thenReturn(List.of(motorcycle));
        List<Motorcycle> result = motorcycleService.getMotorcycles();
        assertEquals(1, result.size());
        verify(motorcycleRepository, times(1)).findAll();
    }

    @Test
    void testAddMotorcycle_Success() throws ValidationException {
        MotorcycleCreateDTO dto = new MotorcycleCreateDTO();
        dto.setBrand("Honda");
        dto.setModel("CBR600");
        dto.setLicensePlate("B123ABC");
        dto.setManufactureYear("2020");
        dto.setOwnerId(ownerId);

        when(motorcycleRepository.existsByLicensePlate(dto.getLicensePlate())).thenReturn(false);
        when(personRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(motorcycleRepository.save(any(Motorcycle.class))).thenReturn(motorcycle);

        Motorcycle result = motorcycleService.addMotorcycle(dto);

        assertNotNull(result);
        assertEquals("Honda", result.getBrand());
        verify(motorcycleRepository, times(1)).save(any(Motorcycle.class));
    }

    @Test
    void testAddMotorcycle_DuplicatePlate_ThrowsException() {
        MotorcycleCreateDTO dto = new MotorcycleCreateDTO();
        dto.setLicensePlate("B123ABC");

        when(motorcycleRepository.existsByLicensePlate(dto.getLicensePlate())).thenReturn(true);

        ValidationException ex = assertThrows(ValidationException.class, () -> motorcycleService.addMotorcycle(dto));
        assertTrue(ex.getMessage().contains("already registered"));
        verify(motorcycleRepository, never()).save(any());
    }

    @Test
    void testAddMotorcycle_FutureYear_ThrowsException() {
        MotorcycleCreateDTO dto = new MotorcycleCreateDTO();
        dto.setLicensePlate("B123ABC");
        dto.setManufactureYear(String.valueOf(Year.now().getValue() + 2));

        when(motorcycleRepository.existsByLicensePlate(dto.getLicensePlate())).thenReturn(false);

        ValidationException ex = assertThrows(ValidationException.class, () -> motorcycleService.addMotorcycle(dto));
        assertTrue(ex.getMessage().contains("future"));
    }

    @Test
    void testUpdateMotorcycle_Success() throws ValidationException {
        MotorcycleCreateDTO dto = new MotorcycleCreateDTO();
        dto.setBrand("Yamaha");
        dto.setModel("R1");
        dto.setLicensePlate("B123ABC");
        dto.setManufactureYear("2020");
        dto.setOwnerId(ownerId);

        Motorcycle updatedBike = new Motorcycle();
        updatedBike.setBrand("Yamaha");

        when(motorcycleRepository.findById(uuid)).thenReturn(Optional.of(motorcycle));
        when(personRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(motorcycleRepository.save(any(Motorcycle.class))).thenReturn(updatedBike);

        Motorcycle result = motorcycleService.updateMotorcycle(uuid, dto);
        assertNotNull(result);
        verify(motorcycleRepository, times(1)).save(any(Motorcycle.class));
    }

    @Test
    void testUpdateMotorcycle_NotFound_ThrowsException() {
        when(motorcycleRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> motorcycleService.updateMotorcycle(uuid, new MotorcycleCreateDTO()));
    }

    @Test
    void testGetMotorcycleById_NotFound() {
        when(motorcycleRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> motorcycleService.getMotorcycleById(uuid));
    }

    @Test
    void testDeleteMotorcycle() {
        doNothing().when(motorcycleRepository).deleteById(uuid);
        motorcycleService.deleteMotorcycle(uuid);
        verify(motorcycleRepository, times(1)).deleteById(uuid);
    }
}