package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.*;

public record CreateExtraServicerRequest(
        @NotBlank(message = "The airline is required")
        String airlineId,

        @NotBlank(message = "The name for the service is required")
        String nameService,

        String description,

        double nationalPrice,

        double internationalPrice
)
{}
