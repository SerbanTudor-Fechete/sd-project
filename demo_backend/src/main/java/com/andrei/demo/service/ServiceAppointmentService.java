package com.andrei.demo.service;

import com.andrei.demo.config.Status;
import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.ServiceAppointment;
import com.andrei.demo.model.ServiceAppointmentCreateDTO;
import com.andrei.demo.repository.ServiceAppointmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ServiceAppointmentService {

    private final ServiceAppointmentRepository appointmentRepository;

    public List<ServiceAppointment> getAppointments() {
        return appointmentRepository.findAll();
    }

    public ServiceAppointment addAppointment(ServiceAppointmentCreateDTO createDTO) {
        ServiceAppointment appointment = new ServiceAppointment();
        appointment.setScheduleDate(createDTO.getScheduleDate());
        appointment.setDescription(createDTO.getDescription());
        appointment.setTotalCost(createDTO.getTotalCost());
        appointment.setStatus(createDTO.getStatus());

        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(UUID id) {
        appointmentRepository.deleteById(id);
    }

    public ServiceAppointment updateAppointment(UUID id, ServiceAppointment appointment) throws ValidationException {
        Optional<ServiceAppointment> appointmentOptional = appointmentRepository.findById(id);

        if(appointmentOptional.isEmpty()) {
            throw new ValidationException("Appointment with id " + id + " not found");
        }

        ServiceAppointment existingAppointment = appointmentOptional.get();
        existingAppointment.setScheduleDate(appointment.getScheduleDate());
        existingAppointment.setDescription(appointment.getDescription());
        existingAppointment.setTotalCost(appointment.getTotalCost());
        existingAppointment.setStatus(appointment.getStatus());

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