package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SelectSeatRequest(
        @NotBlank(message = "The leg is required")
        String legId,

        @NotBlank(message = "The seat is required")
        String legSeatId,

        @NotBlank(message = "The passenger is required")
        String passengerId
) {}