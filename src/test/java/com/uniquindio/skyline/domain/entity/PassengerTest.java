package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.PhonePrefix;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PassengerTest {

    @Test
    void passengerIsCreatedAsNonHolderWithoutContactInfo() {
        Passenger passenger = createPassenger();

        assertFalse(passenger.isHolder());
        assertEquals(Optional.empty(), passenger.getEmail());
        assertEquals(Optional.empty(), passenger.getPhoneNumber());
        assertEquals(Optional.empty(), passenger.getPhonePrefix());
        assertEquals(Optional.empty(), passenger.getLuggageDetail());
        assertEquals("1001", passenger.getPerson().getIdDocument());
    }

    @Test
    void passengerThrowsExceptionWhenPersonDataIsInvalid() {
        assertThrows(DomainRuleException.class, () ->
                Passenger.createPassenger(null, DocumentType.CITIZENSHIP_ID, "Laura", null,
                        "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE)
        );
    }

    //Tests add info holder

    @Test
    void shouldAddInfoHolderAndMarkAsHolder() {
        // Arrange
        Passenger passenger = createPassenger();

        // Act
        passenger.addInfoHolder("laura@mail.com", "3001234567", PhonePrefix.COLOMBIA);

        // Assert
        assertTrue(passenger.isHolder());
        assertEquals(Optional.of("laura@mail.com"), passenger.getEmail());
        assertEquals(Optional.of("3001234567"), passenger.getPhoneNumber());
        assertEquals(Optional.of(PhonePrefix.COLOMBIA), passenger.getPhonePrefix());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        Passenger passenger = createPassenger();

        assertThrows(DomainRuleException.class,
                () -> passenger.addInfoHolder("  ", "3001234567", PhonePrefix.COLOMBIA));
        assertFalse(passenger.isHolder());
    }

    @Test
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        Passenger passenger = createPassenger();

        assertThrows(DomainRuleException.class,
                () -> passenger.addInfoHolder("laura.mail.com", "3001234567", PhonePrefix.COLOMBIA));
        assertFalse(passenger.isHolder());
    }

    @Test
    void shouldThrowExceptionWhenPhoneNumberIsBlank() {
        Passenger passenger = createPassenger();

        assertThrows(DomainRuleException.class,
                () -> passenger.addInfoHolder("laura@mail.com", "  ", PhonePrefix.COLOMBIA));
        assertFalse(passenger.isHolder());
    }

    @Test
    void shouldThrowExceptionWhenPhonePrefixIsNull() {
        Passenger passenger = createPassenger();

        assertThrows(DomainRuleException.class,
                () -> passenger.addInfoHolder("laura@mail.com", "3001234567", null));
        assertFalse(passenger.isHolder());
    }

    private Passenger createPassenger() {
        return Passenger.createPassenger("1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE);
    }
}
