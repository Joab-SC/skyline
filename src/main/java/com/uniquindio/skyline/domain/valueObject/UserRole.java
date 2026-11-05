package com.uniquindio.skyline.domain.valueObject;

// domain/valueObject/UserRole.java
public enum UserRole {
    ADMIN, AIRLINE_ADMIN, AIRLINE_SELLER, CUSTOMER;

    public boolean belongsToAirline() {
        return this == AIRLINE_ADMIN || this == AIRLINE_SELLER;
    }
}