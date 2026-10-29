package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.service.BookingSeatsReleaser;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelBookingUseCaseTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingSeatsReleaser bookingSeatsReleaser;

    @InjectMocks
    private CancelBookingUseCase useCase;

    @Test
    void shouldCancelPendingBookingAndReleaseSeats() {
        // Arrange
        Booking booking = Booking.createPendingBooking(2, 500.0, "flight-1", TripType.ONE_WAY);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        // Act
        Booking result = useCase.execute(booking.getId());

        // Assert
        assertEquals(BookingStatus.CANCELLED, result.getBookingStatus());
        verify(bookingSeatsReleaser).releaseSeats(booking);
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldThrowExceptionWhenBookingDoesNotExist() {
        // Arrange
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> useCase.execute("booking-1"));
        verifyNoInteractions(bookingSeatsReleaser);
        verify(bookingRepository, never()).save(any(Booking.class));
    }


    @Test
    void shouldNotSaveBookingWhenReleasingSeatsFails() {
        // Arrange
        Booking booking = Booking.createPendingBooking(2, 500.0, "flight-1", TripType.ONE_WAY);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        doThrow(new DomainRuleException("The leg cannot be found"))
                .when(bookingSeatsReleaser).releaseSeats(booking);

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> useCase.execute(booking.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}