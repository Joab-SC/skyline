package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AddLuggageRequest(
        @NotBlank(message = "The passenger is required")
        String passengerId,

        @NotNull(message = "The luggage quantity is required")
        // 0 removes what was chosen
        @PositiveOrZero(message = "The luggage quantity cannot be negative")
        Integer quantity
) {}