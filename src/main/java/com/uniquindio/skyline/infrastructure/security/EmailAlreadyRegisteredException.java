package com.uniquindio.skyline.infrastructure.security;

// Not a DomainRuleException: users are not part of the domain. GlobalExceptionHandler answers it with 409.
public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException() {
        super("The email is already registered");
    }
}
