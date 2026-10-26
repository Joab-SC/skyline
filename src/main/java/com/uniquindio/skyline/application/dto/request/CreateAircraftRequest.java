package com.uniquindio.skyline.application.dto.request;

import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAircraftRequest(
        @NotBlank(message = "The airline is required")
        String airlineId,

        @NotNull(message = "The aircraft model is required")
        AircraftModel aircraftModel
) {}
