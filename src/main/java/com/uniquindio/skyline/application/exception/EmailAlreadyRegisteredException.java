package com.uniquindio.skyline.application.exception;

// Not a DomainRuleException: a repeated email is a conflict (409), not a broken rule (422).
public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException() {
        super("The email is already registered");
    }
}
