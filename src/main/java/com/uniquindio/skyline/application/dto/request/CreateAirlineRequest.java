package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.*;

public record CreateAirlineRequest(
        @NotBlank(message = "The airline name is required")
        String name,

        @Positive(message = "The maximum luggage amount must be greater than 0")
        int maximumLuggageAmount,

        @PositiveOrZero(message = "The seat price cannot be negative")
        double seatPrice
) {}