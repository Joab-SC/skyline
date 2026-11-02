package com.uniquindio.skyline.application.dto.request;

import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import jakarta.validation.constraints.NotNull;

// The airline is not sent: it is the airline of the logged-in user (JWT).
public record CreateAircraftRequest(
        @NotNull(message = "The aircraft model is required")
        AircraftModel aircraftModel
) {}
