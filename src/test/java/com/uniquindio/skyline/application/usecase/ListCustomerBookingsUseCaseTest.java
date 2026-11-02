package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListCustomerBookingsUseCaseTest {

    private static final String CUSTOMER_ID = "customer-1";

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ListCustomerBookingsUseCase listCustomerBookingsUseCase;

    @Test
    void shouldReturnTheBookingsOfTheCustomer() {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 200, "flight-1", TripType.ONE_WAY, CUSTOMER_ID);
        when(bookingRepository.findByIdCustomer(CUSTOMER_ID)).thenReturn(List.of(booking));

        // Act
        List<Booking> bookings = listCustomerBookingsUseCase.execute(CUSTOMER_ID);

        // Assert
        assertEquals(List.of(booking), bookings);
        verify(bookingRepository).findByIdCustomer(CUSTOMER_ID);
    }

    @Test
    void shouldReturnAnEmptyListWhenTheCustomerHasNoBookings() {
        // Arrange
        when(bookingRepository.findByIdCustomer(CUSTOMER_ID)).thenReturn(List.of());

        // Act
        List<Booking> bookings = listCustomerBookingsUseCase.execute(CUSTOMER_ID);

        // Assert
        assertTrue(bookings.isEmpty());
    }
}
