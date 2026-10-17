package com.uniquindio.skyline.domain.valueObject;

import java.time.LocalDateTime;

public record LegSummary(
        LocalDateTime departureTime, LocalDateTime arrivalTime, Airport originAirport,
        Airport destinationAirport, double price, String airlineId
) {
}