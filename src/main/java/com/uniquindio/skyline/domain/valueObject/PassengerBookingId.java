package com.uniquindio.skyline.domain.valueObject;

import com.uniquindio.skyline.domain.exception.DomainRuleException;

// Who holds a leg seat: a passenger inside a specific booking.
// The same person can be in two bookings, so the document alone is not enough.
public record PassengerBookingId(String bookingId, String passengerDocument) {

    public PassengerBookingId {
        if (bookingId == null || bookingId.isBlank()) {
            throw new DomainRuleException("The booking id is required");
        }
        if (passengerDocument == null || passengerDocument.isBlank()) {
            throw new DomainRuleException("The passenger document is required");
        }
    }

    public boolean belongsToBooking(String bookingId) {
        return this.bookingId.equals(bookingId);
    }
}