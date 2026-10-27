package com.uniquindio.skyline.application.dto.request;

import com.uniquindio.skyline.domain.valueObject.TripType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SelectOutboundFlightRequest(
        @NotBlank(message = "The outbound flight is required")
        String outboundFlightId,

        @NotNull(message = "The number of passengers is required")
        @Positive(message = "The number of passengers must be greater than 0")
        Integer numPassengers,

        @NotNull(message = "The trip type is required")
        TripType tripType
) {}