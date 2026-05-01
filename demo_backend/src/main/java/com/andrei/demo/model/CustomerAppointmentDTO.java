package com.andrei.demo.model;

import java.util.UUID;

public record CustomerAppointmentDTO(
        UUID id,
        String appointmentDate,
        String status,
        Double totalCost,
        MotorcycleSummaryDTO motorcycle
) {}