package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CreateLegRequest(
        @NotBlank String originAirportId,
        @NotBlank String destinationAirportId,
        @NotNull LocalDateTime departureTime,
        @NotNull LocalDateTime arrivalTime,
        @NotBlank String airlineId,
        @NotBlank String aircraftId,
        @Positive double luggagePrice,
        @Positive double price
) {
}
