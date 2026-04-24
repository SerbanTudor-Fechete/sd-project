package com.andrei.demo.model;

import com.andrei.demo.config.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ServiceAppointmentCreateDTO {

    @NotBlank(message = "Schedule Date is required")
    private String scheduleDate;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Total Cost is required")
    private Double totalCost;

    @NotNull(message = "Status is required")
    private Status status;

    @NotNull(message = "Motorcycle ID is required")
    private UUID motorcycleId;

    private List<UUID> partIds = new ArrayList<>();

}