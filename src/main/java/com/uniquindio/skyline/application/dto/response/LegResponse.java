package com.uniquindio.skyline.application.dto.response;

import java.time.LocalDateTime;

public record LegResponse(
        String id,
        String originAirport,
        String destinationAirport,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        String airlineId,
        String aircraftId,
        double luggagePrice,
        double price,
        int availableSeats
) {}
