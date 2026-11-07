package com.uniquindio.skyline.application.dto.request;


import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

// A new customer account. The role is not sent: every registered user is a CUSTOMER.
public record RegisterUserRequest(
        @NotBlank(message = "The email is required")
        @Email(message = "The email is not valid")
        String email,

        @NotBlank(message = "The password is required")
        String password,

        @NotBlank(message = "The document is required")
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