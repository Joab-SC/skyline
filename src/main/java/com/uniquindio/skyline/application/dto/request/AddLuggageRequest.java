package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddLuggageRequest(
        @NotBlank(message = "The passenger is required")
        String passengerId,

        @NotNull(message = "The luggage quantity is required")
        @Positive(message = "The luggage quantity must be greater than 0")
        Integer quantity
) {}