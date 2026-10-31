package com.uniquindio.skyline.application.dto.response;

public record TopAirlineResponse(
        String airlineId,
        String name,
        long aircraftCount
) {}