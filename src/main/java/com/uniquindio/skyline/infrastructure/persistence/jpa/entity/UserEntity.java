package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// A user that can log in. It lives in infrastructure, not in the domain: authentication is not a business rule.
// The table is "users" because "user" is a reserved word in H2 and MariaDB.
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column(length = 36)
    private String id;

    // The email is the username used to log in.
    @Column(nullable = false, unique = true)
    private String email;

    // Always the hash produced by the PasswordEncoder ({bcrypt}$2a$...), never the plain password.
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SecurityRole role;

    // Airline the user works for. Required for AIRLINE, null for ADMIN and CUSTOMER.
    @Column(length = 36)
    private String airlineId;
}
