package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.service.BookingExpirationValidator;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindExpiredBookingsUseCaseTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 5, 10, 0);

    @Mock
    private BookingExpirationValidator bookingExpirationValidator;

    @InjectMocks
    private FindExpiredBookingsUseCase useCase;

    @Test
    void shouldReturnIdsOfExpiredPendingBookings() {
        // Arrange
        Booking first = Booking.createPendingBooking(1, 200, "flight-1", TripType.ONE_WAY);
        Booking second = Booking.createPendingBooking(2, 400, "flight-2", TripType.ONE_WAY);
        when(bookingExpirationValidator.findExpiredPendingBookings(NOW)).thenReturn(List.of(first, second));

        // Act
        List<String> result = useCase.execute(NOW);

        // Assert
        assertEquals(List.of(first.getId(), second.getId()), result);
    }

    @Test
    void shouldReturnEmptyListWhenNoBookingExpired() {
        // Arrange
        when(bookingExpirationValidator.findExpiredPendingBookings(NOW)).thenReturn(List.of());

        // Act
        List<String> result = useCase.execute(NOW);

        // Assert
        assertTrue(result.isEmpty());
    }
}