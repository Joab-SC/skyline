package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SelectReturnFlightRequest(
        @NotBlank(message = "The return flight is required")
        String returnFlightId
) {}