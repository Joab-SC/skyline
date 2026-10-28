package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddExtraServiceRequest(
        @NotBlank(message = "The extra service is required")
        String extraServiceId,

        @NotNull(message = "The quantity is required")
        @Positive(message = "The quantity must be greater than 0")
        Integer quantity
) {}