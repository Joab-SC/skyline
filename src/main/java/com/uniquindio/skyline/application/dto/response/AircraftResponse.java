package com.uniquindio.skyline.application.dto.response;

import java.util.List;

public record AircraftResponse(
        String id,
        String aircraftModel,
        List<String> seats
) {}
