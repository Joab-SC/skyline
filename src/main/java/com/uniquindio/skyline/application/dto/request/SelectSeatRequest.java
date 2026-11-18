package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SelectSeatRequest(
        @NotBlank(message = "The leg is required")
        String legId,

        // null releases the passenger's seat on the leg; a blank value is invalid
        @Pattern(regexp = ".*\\S.*", message = "The seat cannot be blank")
        String legSeatId,

        @NotBlank(message = "The passenger is required")
        String passengerId
) {}