package com.uniquindio.skyline.application.dto.response;

public record BookingExtraServiceResponse(
        String extraServiceId,
        int quantity,
        double subtotal
) {}