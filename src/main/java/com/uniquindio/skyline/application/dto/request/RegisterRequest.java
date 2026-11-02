package com.uniquindio.skyline.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// A new customer account. The role is not sent: every registered user is a CUSTOMER.
public record RegisterRequest(
        @NotBlank(message = "The email is required")
        @Email(message = "The email is not valid")
        String email,

        @NotBlank(message = "The password is required")
        @Size(min = 8, message = "The password must have at least 8 characters")
        String password
) {}
