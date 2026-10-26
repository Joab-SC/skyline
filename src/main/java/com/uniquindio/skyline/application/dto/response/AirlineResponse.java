package com.uniquindio.skyline.application.dto.response;

public record AirlineResponse(
        String id,
        String name,
        int maximumLuggageAmount,
        double priceSeats
) {}