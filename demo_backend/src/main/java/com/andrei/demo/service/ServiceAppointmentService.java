package com.andrei.demo.service;

import com.andrei.demo.config.Status;
import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Motorcycle;
import com.andrei.demo.model.Part;
import com.andrei.demo.model.ServiceAppointment;
import com.andrei.demo.model.ServiceAppointmentCreateDTO;
import com.andrei.demo.repository.MotorcycleRepository;
import com.andrei.demo.repository.PartRepository;
import com.andrei.demo.repository.ServiceAppointmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ServiceAppointmentService {

    private final ServiceAppointmentRepository appointmentRepository;
    private final MotorcycleRepository motorcycleRepository;
    private final PartRepository partRepository;

    public List<ServiceAppointment> getAppointments() {
        return appointmentRepository.findAll();
    }

    public ServiceAppointment addAppointment(ServiceAppointmentCreateDTO createDTO) throws ValidationException {
        Motorcycle motorcycle = motorcycleRepository.findById(createDTO.getMotorcycleId())
                .orElseThrow(() -> new ValidationException("Motorcycle with id " + createDTO.getMotorcycleId() + " not found."));

        ServiceAppointment appointment = new ServiceAppointment();
        appointment.setScheduleDate(createDTO.getScheduleDate());
        appointment.setDescription(createDTO.getDescription());
        appointment.setTotalCost(createDTO.getTotalCost());
        appointment.setStatus(createDTO.getStatus());
        appointment.setMotorcycle(motorcycle);

        if (createDTO.getPartIds() != null && !createDTO.getPartIds().isEmpty()) {
            List<Part> parts = partRepository.findAllById(createDTO.getPartIds());
            appointment.setPartsUsed(parts);
        }

        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(UUID id) {
        appointmentRepository.deleteById(id);
    }

    public ServiceAppointment updateAppointment(UUID id, ServiceAppointmentCreateDTO dto) throws ValidationException {
        ServiceAppointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Appointment with id " + id + " not found"));

        Motorcycle motorcycle = motorcycleRepository.findById(dto.getMotorcycleId())
                .orElseThrow(() -> new ValidationException("Motorcycle with id " + dto.getMotorcycleId() + " not found."));

        existingAppointment.setScheduleDate(dto.getScheduleDate());
        existingAppointment.setDescription(dto.getDescription());
        existingAppointment.setTotalCost(dto.getTotalCost());
        existingAppointment.setStatus(dto.getStatus());
        existingAppointment.setMotorcycle(motorcycle); // <-- Update the motorcycle link

        // Update parts list
        if (dto.getPartIds() != null && !dto.getPartIds().isEmpty()) {
            List<Part> parts = partRepository.findAllById(dto.getPartIds());
            existingAppointment.setPartsUsed(parts);
        } else {
            existingAppointment.setPartsUsed(new ArrayList<>()); // Clear parts if none provided
        }

        return appointmentRepository.save(existingAppointment);
    }

    public ServiceAppointment getAppointmentById(UUID id) {
        return appointmentRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Appointment with id " + id + " not found"));
    }

    public ServiceAppointment updateAppointmentStatus(UUID id, String newStatus) {
        ServiceAppointment existingAppointment = getAppointmentById(id);
        existingAppointment.setStatus(Status.valueOf(newStatus.toUpperCase()));
        return appointmentRepository.save(existingAppointment);
    }
}