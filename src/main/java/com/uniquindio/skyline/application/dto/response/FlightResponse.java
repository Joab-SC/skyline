package com.uniquindio.skyline.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record FlightResponse(
        String id,
        String airlineId,
        double price,
        String originAirport,
        String destinationAirport,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        String flightScope,
        List<String> legIds,
        List<LayoverResponse> layovers
) {}