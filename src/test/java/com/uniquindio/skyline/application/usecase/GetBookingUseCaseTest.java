package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.application.dto.result.BookingDetail;
import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.BookingSeat;
import com.uniquindio.skyline.domain.repository.LegRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBookingUseCaseTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private LegRepository legRepository;

    @Mock
    private Booking booking;

    @InjectMocks
    private GetBookingUseCase useCase;

    @Test
    void shouldReturnBookingWithItsSeatsWhenItExists() {
        // Arrange
        List<BookingSeat> seats = List.of(
                new BookingSeat("seat-1", "12A", "1094", "leg-1"),
                new BookingSeat("seat-2", "14C", "1094", "leg-2"));
        when(booking.getId()).thenReturn("booking-1");
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(legRepository.findSeatsByBookingId("booking-1")).thenReturn(seats);

        // Act
        BookingDetail result = useCase.execute("booking-1");

        // Assert
        assertSame(booking, result.booking());
        assertEquals(seats, result.bookingSeats());
        verify(bookingRepository, never()).save(any());
        verify(legRepository, never()).save(any());
    }

    @Test
    void shouldReturnNoSeatsWhenPassengersHaveNotChosenThem() {
        // Arrange
        when(booking.getId()).thenReturn("booking-1");
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(legRepository.findSeatsByBookingId("booking-1")).thenReturn(List.of());

        // Act
        BookingDetail result = useCase.execute("booking-1");

        // Assert
        assertSame(booking, result.booking());
        assertEquals(List.of(), result.bookingSeats());
    }

    @Test
    void shouldThrowWhenBookingDoesNotExist() {
        // Arrange
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> useCase.execute("booking-1"));
        verify(legRepository, never()).findSeatsByBookingId(any());
    }
}
