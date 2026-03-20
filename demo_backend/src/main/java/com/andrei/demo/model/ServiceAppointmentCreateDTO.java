package com.andrei.demo.model;

import com.andrei.demo.config.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceAppointmentCreateDTO {

    @NotBlank(message = "Schedule Date is required")
    private String scheduleDate;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Total Cost is required")
    private Double totalCost;

    @NotBlank(message = "Status is required")
    private Status status;
}