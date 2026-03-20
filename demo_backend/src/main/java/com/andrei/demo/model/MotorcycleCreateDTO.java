package com.andrei.demo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MotorcycleCreateDTO {

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Manufacture year is required")
    private String manufactureYear;

    @NotBlank(message = "License plate is required")
    @Size(min = 7, max = 8, message = "License plate number is invalid")
    private String licensePlate;
}
