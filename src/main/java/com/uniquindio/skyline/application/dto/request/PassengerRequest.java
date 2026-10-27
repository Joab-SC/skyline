package com.uniquindio.skyline.application.dto.request;

import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record PassengerRequest(
        @NotBlank(message = "The document id is required")
        String idDocument,

        @NotNull(message = "The document type is required")
        DocumentType documentType,

        @NotBlank(message = "The first name is required")
        String firstName,

        String secondName,

        @NotBlank(message = "The first last name is required")
        String lastName1,

        String lastName2,

        @NotNull(message = "The birth date is required")
        @Past(message = "The birth date must be in the past")
        LocalDate birthDate,

        @NotNull(message = "The gender is required")
        Gender gender
) {}