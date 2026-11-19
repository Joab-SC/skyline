package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AddExtraServiceRequest(
        @NotBlank(message = "The extra service is required")
        String extraServiceId,

        @NotNull(message = "The quantity is required")
        // 0 removes what was chosen
        @PositiveOrZero(message = "The quantity cannot be negative")
        Integer quantity
) {}