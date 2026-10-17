package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SelectOutboundFlightTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private SelectOutboundFlight selectOutboundFlight;

    @Test
    void shouldCreatePendingBookingAndSaveIt() {
        // Arrange
        int numPassengers = 2;
        double price = 500.0;
        String idOutboundFlight = "flight-1";
        TripType tripType = TripType.ONE_WAY;

        // Act
        Booking booking = selectOutboundFlight.execute(
                numPassengers,
                price,
                idOutboundFlight,
                tripType
        );

        // Assert
        assertNotNull(booking);
        assertEquals(numPassengers, booking.getNumPassengers());
        assertEquals(price, booking.getPrice());
        assertEquals(idOutboundFlight, booking.getIdOutboundFlight());
        assertEquals(tripType, booking.getTripType());
        assertEquals(BookingStatus.PENDING, booking.getBookingStatus());

        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldGenerateUniqueBookingIds() {
        // Arrange
        int numPassengers = 1;
        double price = 300.0;
        String idOutboundFlight = "flight-1";
        TripType tripType = TripType.ONE_WAY;

        // Act
        Booking firstBooking = selectOutboundFlight.execute(
                numPassengers, price, idOutboundFlight, tripType
        );

        Booking secondBooking = selectOutboundFlight.execute(
                numPassengers, price, idOutboundFlight, tripType
        );

        // Assert
        assertNotEquals(firstBooking.getId(), secondBooking.getId());

        verify(bookingRepository, times(2)).save(any(Booking.class));
    }

    @Test
    void shouldNotSaveBookingWhenCreationFails() {
        // Arrange
        int invalidNumPassengers = 0;

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> selectOutboundFlight.execute(
                        invalidNumPassengers,
                        500.0,
                        "flight-1",
                        TripType.ONE_WAY
                )
        );

        verify(bookingRepository, never()).save(any(Booking.class));
    }
}

