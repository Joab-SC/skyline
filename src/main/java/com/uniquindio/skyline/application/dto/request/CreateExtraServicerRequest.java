package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.*;

// The airline is not sent: it is the airline of the logged-in user (JWT).
public record CreateExtraServicerRequest(
        @NotBlank(message = "The name for the service is required")
        String nameService,

        String description,

        double nationalPrice,

        double internationalPrice
)
{}
