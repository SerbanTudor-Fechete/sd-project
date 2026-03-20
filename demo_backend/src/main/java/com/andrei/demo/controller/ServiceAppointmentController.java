package com.andrei.demo.controller;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.ServiceAppointment;
import com.andrei.demo.model.ServiceAppointmentCreateDTO;
import com.andrei.demo.service.ServiceAppointmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
public class ServiceAppointmentController {

    private final ServiceAppointmentService appointmentService;

    @GetMapping("/appointment")
    public List<ServiceAppointment> getAppointments() {
        return appointmentService.getAppointments();
    }

    @GetMapping("/appointment/{uuid}")
    public ServiceAppointment getAppointmentById(@PathVariable UUID uuid) {
        return appointmentService.getAppointmentById(uuid);
    }

    @PostMapping("/appointment")
    public ServiceAppointment addAppointment(@Valid @RequestBody ServiceAppointmentCreateDTO appointmentCreateDTO) {
        return appointmentService.addAppointment(appointmentCreateDTO);
    }

    @PutMapping("/appointment/{uuid}")
    public ServiceAppointment updateAppointment(@PathVariable UUID uuid, @RequestBody ServiceAppointment appointment) throws ValidationException {
        return appointmentService.updateAppointment(uuid, appointment);
    }

    @DeleteMapping("/appointment/{uuid}")
    public void deleteAppointment(@PathVariable UUID uuid) {
        appointmentService.deleteAppointment(uuid);
    }

    @PatchMapping("/appointment/{uuid}/status")
    public ServiceAppointment updateAppointmentStatus(@PathVariable UUID uuid, @RequestBody String newStatus) {
        return appointmentService.updateAppointmentStatus(uuid, newStatus);
    }
}