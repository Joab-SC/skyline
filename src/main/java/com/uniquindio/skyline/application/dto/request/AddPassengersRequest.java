package com.uniquindio.skyline.application.dto.request;

import com.uniquindio.skyline.domain.valueObject.PhonePrefix;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddPassengersRequest(
        @NotEmpty(message = "At least one passenger is required")
        @Valid
        List<PassengerRequest> passengers,

        @NotBlank(message = "The holder document id is required")
        String holderIdDocument,

        @NotBlank(message = "The holder email is required")
        @Email(message = "The holder email is not valid")
        String email,

        @NotBlank(message = "The holder phone number is required")
        String phoneNumber,

        @NotNull(message = "The holder phone prefix is required")
        PhonePrefix phonePrefix
) {}