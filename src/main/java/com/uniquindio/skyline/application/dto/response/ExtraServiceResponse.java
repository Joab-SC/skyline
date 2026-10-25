package com.uniquindio.skyline.application.dto.response;

public record ExtraServiceResponse(
        String id,
        String nameService,
        String description,
        double nationalPrice,
        double internationalPrice
)
{}
