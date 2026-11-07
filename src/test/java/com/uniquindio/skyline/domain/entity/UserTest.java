package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// The person data is validated by Person (see PersonTest), so these tests only cover the User rules.
class UserTest {

    private static final String EMAIL = "laura@mail.com";
    private static final String PASSWORD_HASH = "hashed-password";

    private User customerWith(String email, String passwordHash) {
        return User.registerCustomer("1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Suarez", null, LocalDate.of(2000, 5, 10), Gender.FEMALE, email, passwordHash);
    }

    private User airlineAdminOf(String airlineId) {
        return User.registerAirlineAdmin(airlineId, "1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Suarez", null, LocalDate.of(2000, 5, 10), Gender.FEMALE, EMAIL, PASSWORD_HASH);
    }

    @Test
    void customerIsRegisteredWithoutAirline() {
        User user = customerWith(EMAIL, PASSWORD_HASH);

        assertNotNull(user.getId());
        assertEquals(EMAIL, user.getEmail());
        assertEquals(PASSWORD_HASH, user.getPasswordHash());
        assertEquals(UserRole.CUSTOMER, user.getRole());
        assertEquals(Optional.empty(), user.getAirlineId());
        assertEquals("1001", user.getPerson().getIdDocument());
    }

    @Test
    void airlineAdminIsRegisteredWithItsAirline() {
        User user = airlineAdminOf("airline-1");

        assertEquals(UserRole.AIRLINE_ADMIN, user.getRole());
        assertEquals(Optional.of("airline-1"), user.getAirlineId());
    }

    @Test
    void airlineAdminThrowsExceptionWhenAirlineIsNull() {
        assertThrows(DomainRuleException.class, () -> airlineAdminOf(null));
    }

    @Test
    void airlineAdminThrowsExceptionWhenAirlineIsBlank() {
        assertThrows(DomainRuleException.class, () -> airlineAdminOf("   "));
    }

    @Test
    void userThrowsExceptionWhenEmailIsBlank() {
        assertThrows(DomainRuleException.class, () -> customerWith("  ", PASSWORD_HASH));
    }

    @Test
    void userThrowsExceptionWhenEmailHasInvalidFormat() {
        assertThrows(DomainRuleException.class, () -> customerWith("laura.mail.com", PASSWORD_HASH));
    }

    @Test
    void userThrowsExceptionWhenPasswordHashIsBlank() {
        assertThrows(DomainRuleException.class, () -> customerWith(EMAIL, "  "));
    }

    @Test
    void strongPasswordIsAccepted() {
        assertDoesNotThrow(() -> User.validatePasswordStrength("Secret123"));
    }

    @Test
    void passwordThrowsExceptionWhenItIsNull() {
        assertThrows(DomainRuleException.class, () -> User.validatePasswordStrength(null));
    }

    @Test
    void passwordThrowsExceptionWhenItHasLessThan8Characters() {
        assertThrows(DomainRuleException.class, () -> User.validatePasswordStrength("Secr123"));
    }

    @Test
    void passwordThrowsExceptionWhenItHasNoUppercaseLetter() {
        assertThrows(DomainRuleException.class, () -> User.validatePasswordStrength("secret123"));
    }

    @Test
    void passwordThrowsExceptionWhenItHasNoNumber() {
        assertThrows(DomainRuleException.class, () -> User.validatePasswordStrength("SecretPass"));
    }
}
