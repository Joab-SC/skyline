package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

// Airports are sent as their IATA code.
// The airline is not sent: it is the airline of the logged-in user (JWT).
public record CreateLegRequest(
        // IATA code, e.g. "BOG".
        @NotBlank(message = "The origin airport code is required")
        String originAirport,

        // IATA code, e.g. "MDE".
        @NotBlank(message = "The destination airport code is required")
        String destinationAirport,

        @NotNull(message = "The departure time is required")
        LocalDateTime departureTime,

        @NotNull(message = "The arrival time is required")
        LocalDateTime arrivalTime,

        @NotBlank(message = "The aircraft is required")
        String aircraftId,

        @PositiveOrZero(message = "The luggage price cannot be negative")
        double luggagePrice,

        @Positive(message = "The price must be greater than 0")
        double price
) {}