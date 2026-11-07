package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.valueObject.Person;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.UserRole;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter
public class User {

    private static final String EMAIL_PATTERN = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final String id;
    private Person person;
    private String email;
    private String passwordHash;
    private final UserRole role;
    private final Optional<String> airlineId;

    private User(String id, Person person, String email, String passwordHash,
                 UserRole role, Optional<String> airlineId) {
        this.id = id;
        this.person = person;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.airlineId = airlineId;
    }


    public static User registerCustomer(String idDocument, DocumentType documentType, String firstName,
                                        String secondName, String lastName1, String lastName2, LocalDate birthDate,
                                        Gender gender, String email, String passwordHash) {
        return create(
                Person.createPerson(idDocument, documentType, firstName,
                        secondName, lastName1, lastName2, birthDate, gender),
                email, passwordHash, UserRole.CUSTOMER, null);
    }


    public static User registerAirlineAdmin(String airlineId, String idDocument, DocumentType documentType, String firstName,
                                            String secondName, String lastName1, String lastName2, LocalDate birthDate,
                                            Gender gender, String email, String passwordHash) {
        return create(
                Person.createPerson(idDocument, documentType, firstName,
                        secondName, lastName1, lastName2, birthDate, gender),
                email, passwordHash, UserRole.AIRLINE_ADMIN, airlineId);
    }


    public static User registerAirlineSeller(String airlineId, String idDocument, DocumentType documentType, String firstName,
                                             String secondName, String lastName1, String lastName2, LocalDate birthDate,
                                             Gender gender, String email, String passwordHash) {
        return create(
                Person.createPerson(idDocument, documentType, firstName,
                        secondName, lastName1, lastName2, birthDate, gender),
                email, passwordHash, UserRole.AIRLINE_SELLER, airlineId);
    }



    // Only for the persistence mapper: the data was already validated when the user was created.
    public static User reconstruct(String id, Person person, String email, String passwordHash,
                                   UserRole role, String airlineId) {
        return new User(id, person, email, passwordHash, role, Optional.ofNullable(airlineId));
    }

    // The use case calls it with the plain password BEFORE hashing it.
    public static void validatePasswordStrength(String plainPassword) {
        if (plainPassword == null || plainPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new DomainRuleException(
                    "The password must have at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        if (plainPassword.chars().noneMatch(Character::isUpperCase)) {
            throw new DomainRuleException("The password must have an uppercase letter");
        }
        if (plainPassword.chars().noneMatch(Character::isDigit)) {
            throw new DomainRuleException("The password must have a number");
        }
    }


    private static User create(Person person, String email, String passwordHash,
                               UserRole role, String airlineId) {
        if (person == null) {
            throw new DomainRuleException("The person is required");
        }
        validateEmail(email);
        validatePasswordHash(passwordHash);
        if (role == null) {
            throw new DomainRuleException("The role is required");
        }
        boolean hasAirline = airlineId != null && !airlineId.isBlank();
        if (role.belongsToAirline() && !hasAirline) {
            throw new DomainRuleException("An airline user must have an airline");
        }
        if (!role.belongsToAirline() && airlineId != null) {
            throw new DomainRuleException("Only an airline user can have an airline");
        }
        return new User(UUID.randomUUID().toString(), person, email, passwordHash,
                role, Optional.ofNullable(airlineId));
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new DomainRuleException("Email cannot be empty");
        }
        if (!email.matches(EMAIL_PATTERN)) {
            throw new DomainRuleException("Email format is invalid");
        }
    }

    private static void validatePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new DomainRuleException("The password hash is required");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}