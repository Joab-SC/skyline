package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import com.uniquindio.skyline.domain.valueObject.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {

    @Id
    @Column(length = 36)
    private String id;

    // The email is the username used to log in.
    @Column(nullable = false, unique = true)
    private String email;

    // Always the hash ({bcrypt}$2a$...), never the plain password.
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    // Required for AIRLINE_ADMIN and AIRLINE_SELLER, null for the others.
    @Column(length = 36)
    private String airlineId;

    // The person data are plain columns of the users table (id_document, first_name, ...).
    @Embedded
    private PersonEmbeddable person;
}
