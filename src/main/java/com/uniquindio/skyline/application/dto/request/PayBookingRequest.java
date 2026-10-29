package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PayBookingRequest(
        @NotBlank(message = "The payment token is required")
        String paymentToken
) {}
