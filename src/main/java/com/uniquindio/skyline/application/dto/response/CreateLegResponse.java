package com.uniquindio.skyline.application.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateLegResponse(
        String legId,
        String originAirportId,
        String destinationAirportId,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        String airlineId,
        String aircraftId,
        double luggagePrice,
        double price
) {
}
