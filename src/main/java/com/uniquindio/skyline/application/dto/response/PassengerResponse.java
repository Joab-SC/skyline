package com.uniquindio.skyline.application.dto.response;

import java.time.LocalDate;

public record PassengerResponse(
        String idDocument,
        String documentType,
        String firstName,
        String secondName,
        String lastName1,
        String lastName2,
        LocalDate birthDate,
        String gender,
        boolean holder,
        String email,
        String phonePrefix,
        String phoneNumber,
        LuggageDetailResponse luggage
) {}