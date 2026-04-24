package com.andrei.demo.service;

import com.andrei.demo.config.Status;
import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Motorcycle;
import com.andrei.demo.model.ServiceAppointment;
import com.andrei.demo.model.ServiceAppointmentCreateDTO;
import com.andrei.demo.repository.MotorcycleRepository;
import com.andrei.demo.repository.PartRepository;
import com.andrei.demo.repository.ServiceAppointmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServiceAppointmentServiceTests {

    @Mock
    private ServiceAppointmentRepository appointmentRepository;

    @Mock
    private MotorcycleRepository motorcycleRepository;

    @Mock
    private PartRepository partRepository;

    @InjectMocks
    private ServiceAppointmentService appointmentService;

    private AutoCloseable closeable;
    private UUID uuid;
    private UUID motorcycleId;
    private ServiceAppointment appointment;
    private Motorcycle motorcycle;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        uuid = UUID.randomUUID();
        motorcycleId = UUID.randomUUID();

        motorcycle = new Motorcycle();
        motorcycle.setId(motorcycleId);
        motorcycle.setBrand("Honda");

        appointment = new ServiceAppointment();
        appointment.setId(uuid);
        appointment.setScheduleDate("2024-05-10");
        appointment.setDescription("Oil change and brake check");
        appointment.setTotalCost(120.50);
        appointment.setStatus(Status.PENDING);
        appointment.setMotorcycle(motorcycle);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testGetAppointments() {
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

        List<ServiceAppointment> result = appointmentService.getAppointments();

        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findAll();
    }

    @Test
    void testAddAppointment() throws ValidationException {
        ServiceAppointmentCreateDTO dto = new ServiceAppointmentCreateDTO();
        dto.setScheduleDate("2024-05-10");
        dto.setDescription("Oil change and brake check");
        dto.setTotalCost(120.50);
        dto.setStatus(Status.PENDING);
        dto.setMotorcycleId(motorcycleId);
        dto.setPartIds(new ArrayList<>());

        when(motorcycleRepository.findById(motorcycleId)).thenReturn(Optional.of(motorcycle));
        when(appointmentRepository.save(any(ServiceAppointment.class))).thenReturn(appointment);

        ServiceAppointment result = appointmentService.addAppointment(dto);

        assertNotNull(result);
        assertEquals("Oil change and brake check", result.getDescription());
        verify(appointmentRepository, times(1)).save(any(ServiceAppointment.class));
    }

    @Test
    void testUpdateAppointment_Success() throws ValidationException {
        ServiceAppointmentCreateDTO dto = new ServiceAppointmentCreateDTO();
        dto.setScheduleDate("2024-05-15");
        dto.setDescription("Tire replacement");
        dto.setTotalCost(300.00);
        dto.setStatus(Status.COMPLETED);
        dto.setMotorcycleId(motorcycleId);
        dto.setPartIds(new ArrayList<>());

        ServiceAppointment updatedAppointment = new ServiceAppointment();
        updatedAppointment.setScheduleDate("2024-05-15");
        updatedAppointment.setDescription("Tire replacement");
        updatedAppointment.setTotalCost(300.00);
        updatedAppointment.setStatus(Status.COMPLETED);

        when(appointmentRepository.findById(uuid)).thenReturn(Optional.of(appointment));
        when(motorcycleRepository.findById(motorcycleId)).thenReturn(Optional.of(motorcycle));
        when(appointmentRepository.save(any(ServiceAppointment.class))).thenReturn(updatedAppointment);

        ServiceAppointment result = appointmentService.updateAppointment(uuid, dto);

        assertEquals("Tire replacement", result.getDescription());
        assertEquals(300.00, result.getTotalCost());
        verify(appointmentRepository, times(1)).save(any(ServiceAppointment.class));
    }

    @Test
    void testUpdateAppointment_NotFound_ThrowsValidationException() {
        when(appointmentRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () ->
                appointmentService.updateAppointment(uuid, new ServiceAppointmentCreateDTO())
        );

        verify(appointmentRepository, never()).save(any(ServiceAppointment.class));
    }

    @Test
    void testGetAppointmentById_Success() {
        when(appointmentRepository.findById(uuid)).thenReturn(Optional.of(appointment));

        ServiceAppointment result = appointmentService.getAppointmentById(uuid);

        assertNotNull(result);
        assertEquals(uuid, result.getId());
    }

    @Test
    void testGetAppointmentById_NotFound_ThrowsIllegalStateException() {
        when(appointmentRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                appointmentService.getAppointmentById(uuid)
        );
    }

    @Test
    void testUpdateAppointmentStatus_Success() {
        when(appointmentRepository.findById(uuid)).thenReturn(Optional.of(appointment));

        ServiceAppointment completedAppointment = new ServiceAppointment();
        completedAppointment.setStatus(Status.COMPLETED);

        when(appointmentRepository.save(any(ServiceAppointment.class))).thenReturn(completedAppointment);

        ServiceAppointment result = appointmentService.updateAppointmentStatus(uuid, "COMPLETED");

        assertEquals(Status.COMPLETED, result.getStatus());
        verify(appointmentRepository, times(1)).save(any(ServiceAppointment.class));
    }

    @Test
    void testDeleteAppointment() {
        doNothing().when(appointmentRepository).deleteById(uuid);

        appointmentService.deleteAppointment(uuid);

        verify(appointmentRepository, times(1)).deleteById(uuid);
    }
}