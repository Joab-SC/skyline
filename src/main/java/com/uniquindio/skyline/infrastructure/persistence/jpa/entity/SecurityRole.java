package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

// Roles of the users that can log in. The name of each value travels inside the JWT ("roles" claim)
// and is the authority checked in SecurityConfig (hasAuthority("ADMIN")).
public enum SecurityRole {
    ADMIN,
    AIRLINE,
    CUSTOMER
}
