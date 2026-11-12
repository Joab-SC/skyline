package com.uniquindio.skyline.application.dto.response;

// The code (IATA, e.g. "BOG") is the airport's identifier in every request and response.
public record AirportResponse(
        String code,
        String name,
        String city,
        String country
) {}
