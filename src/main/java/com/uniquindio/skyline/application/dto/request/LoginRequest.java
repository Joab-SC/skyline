package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.NotBlank;

// username is the user's email.
public record LoginRequest(
        @NotBlank(message = "The username is required")
        String username,

        @NotBlank(message = "The password is required")
        String password
) {}
