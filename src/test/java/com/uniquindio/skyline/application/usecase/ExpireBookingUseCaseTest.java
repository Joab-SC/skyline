package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.service.BookingExpirationValidator;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpireBookingUseCaseTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingExpirationValidator bookingExpirationValidator;

    @InjectMocks
    private ExpireBookingUseCase useCase;

    @Test
    void shouldExpireTheLoadedBooking() {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight-1", TripType.ONE_WAY);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        // Act
        useCase.execute(booking.getId());

        // Assert
        verify(bookingExpirationValidator).expire(booking);
    }

    @Test
    void shouldThrowExceptionWhenBookingDoesNotExist() {
        // Arrange
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> useCase.execute("booking-1"));
        verifyNoInteractions(bookingExpirationValidator);
    }
}