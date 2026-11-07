package com.uniquindio.skyline.domain.valueObject;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PassengerBookingIdTest {

    @Test
    void isCreatedWithBookingAndPassenger() {
        PassengerBookingId passengerBookingId = new PassengerBookingId("booking-1", "1001");

        assertEquals("booking-1", passengerBookingId.bookingId());
        assertEquals("1001", passengerBookingId.passengerDocument());
    }

    @Test
    void throwsExceptionWhenBookingIdIsNull() {
        assertThrows(DomainRuleException.class, () -> new PassengerBookingId(null, "1001"));
    }

    @Test
    void throwsExceptionWhenBookingIdIsBlank() {
        assertThrows(DomainRuleException.class, () -> new PassengerBookingId("  ", "1001"));
    }

    @Test
    void throwsExceptionWhenPassengerDocumentIsNull() {
        assertThrows(DomainRuleException.class, () -> new PassengerBookingId("booking-1", null));
    }

    @Test
    void throwsExceptionWhenPassengerDocumentIsBlank() {
        assertThrows(DomainRuleException.class, () -> new PassengerBookingId("booking-1", "  "));
    }

    @Test
    void twoIdsWithTheSameBookingAndPassengerAreEqual() {
        assertEquals(new PassengerBookingId("booking-1", "1001"), new PassengerBookingId("booking-1", "1001"));
    }

    @Test
    void sameDocumentInAnotherBookingIsNotEqual() {
        assertNotEquals(new PassengerBookingId("booking-1", "1001"), new PassengerBookingId("booking-2", "1001"));
    }

    @Test
    void belongsOnlyToItsBooking() {
        PassengerBookingId passengerBookingId = new PassengerBookingId("booking-1", "1001");

        assertTrue(passengerBookingId.belongsToBooking("booking-1"));
        assertFalse(passengerBookingId.belongsToBooking("booking-2"));
    }
}
