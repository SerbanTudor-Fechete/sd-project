package com.andrei.demo.controller;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.CustomerAppointmentDTO;
import com.andrei.demo.model.ServiceAppointment;
import com.andrei.demo.model.ServiceAppointmentCreateDTO;
import com.andrei.demo.service.ServiceAppointmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
public class ServiceAppointmentController {

    private final ServiceAppointmentService appointmentService;

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/appointment/my-appointments")
    public List<CustomerAppointmentDTO> getMyAppointments(Principal principal) {
        return appointmentService.getAppointmentsForUser(principal.getName());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/appointment")
    public List<ServiceAppointment> getAppointments() {
        return appointmentService.getAppointments();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/appointment/{uuid}")
    public ServiceAppointment getAppointmentById(@PathVariable UUID uuid) {
        return appointmentService.getAppointmentById(uuid);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/appointment")
    public ServiceAppointment addAppointment(@Valid @RequestBody ServiceAppointmentCreateDTO appointmentCreateDTO) throws ValidationException {
        return appointmentService.addAppointment(appointmentCreateDTO);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/appointment/{uuid}")
    public ServiceAppointment updateAppointment(@PathVariable UUID uuid, @RequestBody ServiceAppointmentCreateDTO appointmentCreateDTO) throws ValidationException {
        return appointmentService.updateAppointment(uuid, appointmentCreateDTO);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/appointment/{uuid}")
    public void deleteAppointment(@PathVariable UUID uuid) {
        appointmentService.deleteAppointment(uuid);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/appointment/{uuid}/status")
    public ServiceAppointment updateAppointmentStatus(@PathVariable UUID uuid, @RequestBody String newStatus) {
        return appointmentService.updateAppointmentStatus(uuid, newStatus);
    }
}