package com.andrei.demo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetResponse(
        @NotBlank @Email String email,
        @NotBlank String code,
        @NotBlank @Size(min = 8) String newPassword,
        @NotBlank String confirmPassword
) {}