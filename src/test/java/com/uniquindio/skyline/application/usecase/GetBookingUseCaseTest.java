package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

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
    private Booking booking;

    @InjectMocks
    private GetBookingUseCase useCase;

    @Test
    void shouldReturnBookingWhenItExists() {
        // Arrange
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));

        // Act
        Booking result = useCase.execute("booking-1");

        // Assert
        assertSame(booking, result);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenBookingDoesNotExist() {
        // Arrange
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> useCase.execute("booking-1"));
    }
}