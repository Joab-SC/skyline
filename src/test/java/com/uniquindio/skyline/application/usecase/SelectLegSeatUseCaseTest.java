package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.entity.Passenger;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.service.SeatAssigner;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.PassengerData;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SelectLegSeatUseCaseTest {

    @Mock
    private LegRepository legRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SeatAssigner seatAssigner;

    @InjectMocks
    private SelectLegSeatUseCase useCase;

    @Test
    void shouldSelectSeatAndSaveLegAndBooking() {
        // Arrange
        Booking booking = createBooking();
        Leg leg = createLeg();
        Passenger passenger = booking.getPassenger("1001");

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(legRepository.findById(leg.getId()))
                .thenReturn(Optional.of(leg));

        // Act
        Leg result = useCase.execute(booking.getId(), "seat-1", "1001", leg.getId());

        // Assert
        assertEquals(leg, result);
        verify(seatAssigner).assignSeat(booking, "seat-1", passenger, leg);
        verify(legRepository).save(leg);
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldThrowExceptionWhenBookingDoesNotExist() {
        // Arrange
        when(bookingRepository.findById("booking-1"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                NoSuchElementException.class,
                () -> useCase.execute("booking-1", "seat-1", "1001", "leg-1")
        );

        verifyNoInteractions(legRepository);
        verifyNoInteractions(seatAssigner);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionWhenPassengerIsNotInBooking() {
        // Arrange
        Booking booking = createBooking();

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> useCase.execute(booking.getId(), "seat-1", "9999", "leg-1")
        );

        verifyNoInteractions(legRepository);
        verifyNoInteractions(seatAssigner);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionWhenLegDoesNotExist() {
        // Arrange
        Booking booking = createBooking();

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(legRepository.findById("leg-1"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                NoSuchElementException.class,
                () -> useCase.execute(booking.getId(), "seat-1", "1001", "leg-1")
        );

        verifyNoInteractions(seatAssigner);
        verify(legRepository, never()).save(any(Leg.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldNotSaveWhenSeatCannotBeAssigned() {
        // Arrange
        Booking booking = createBooking();
        Leg leg = createLeg();
        Passenger passenger = booking.getPassenger("1001");

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(legRepository.findById(leg.getId()))
                .thenReturn(Optional.of(leg));
        doThrow(new DomainRuleException("The seat is already occupied"))
                .when(seatAssigner).assignSeat(booking, "seat-1", passenger, leg);

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> useCase.execute(booking.getId(), "seat-1", "1001", leg.getId())
        );

        verify(legRepository, never()).save(any(Leg.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    private Booking createBooking() {
        Booking booking = Booking.createPendingBooking(1, 500.0, "outbound-1", TripType.ONE_WAY);
        booking.addPassengers(List.of(new PassengerData("1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE)));
        return booking;
    }

    private Leg createLeg() {
        return Leg.createLeg(Airport.EL_EDEN, Airport.EL_DORADO, LocalDateTime.of(2026, 11, 5, 10, 0),
                LocalDateTime.of(2026, 11, 5, 12, 0), "AIR-001", "airline-1",
                List.of(new Seat("001")), 100, 500);
    }
}
