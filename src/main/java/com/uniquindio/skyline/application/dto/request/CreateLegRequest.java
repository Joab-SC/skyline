package com.uniquindio.skyline.application.dto.request;

import com.uniquindio.skyline.domain.valueObject.Airport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record CreateLegRequest(
        @NotNull(message = "The origin airport is required")
        Airport originAirport,

        @NotNull(message = "The destination airport is required")
        Airport destinationAirport,

        @NotNull(message = "The departure time is required")
        LocalDateTime departureTime,

        @NotNull(message = "The arrival time is required")
        LocalDateTime arrivalTime,

        @NotBlank(message = "The airline is required")
        String airlineId,

        @NotBlank(message = "The aircraft is required")
        String aircraftId,

        @PositiveOrZero(message = "The luggage price cannot be negative")
        double luggagePrice,

        @Positive(message = "The price must be greater than 0")
        double price
) {}