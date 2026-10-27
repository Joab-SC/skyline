package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record SearchFlightsRequest(
        @NotBlank(message = "The origin airport code is required")
        String origin,

        @NotBlank(message = "The destination airport code is required")
        String destination,

        @NotNull(message = "The departure day is required")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate departureDay,

        @NotNull(message = "The number of passengers is required")
        @Positive(message = "The number of passengers must be greater than 0")
        Integer passengers
) {}