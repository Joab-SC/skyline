package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Passenger;
import com.uniquindio.skyline.domain.entity.Person;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.service.LuggageDetailCreator;
import com.uniquindio.skyline.domain.valueObject.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddLuggageUseCaseTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private LuggageDetailCreator luggageDetailCreator;

    @InjectMocks
    private AddLuggageUseCase addLuggageUseCase;


    @Test
    void shouldRegisterCreatedDetailAndSaveBooking() {
        // Arrange
        Booking booking = createBookingWithPassenger();
        LuggageDetail luggageDetail = new LuggageDetail(List.of("luggage-1"), 2, 100.0, 3);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(luggageDetailCreator.createDetail(booking, 2)).thenReturn(luggageDetail);
        // Act
        Booking result = addLuggageUseCase.execute("123", booking.getId(), 2);

        // Assert
        assertEquals(luggageDetail, result.getPassengerList().getFirst().getLuggageDetail().get());
        assertEquals(600.0, result.getPrice(), 0.001);
        InOrder inOrder = inOrder(luggageDetailCreator, bookingRepository);
        inOrder.verify(luggageDetailCreator).createDetail(booking, 2);
        inOrder.verify(bookingRepository).save(booking);
    }

    @Test
    void shouldThrowExceptionWhenBookingIsNotFound() {
        // Arrange
        when(bookingRepository.findById("missing")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> addLuggageUseCase.execute("123", "missing", 1));
        verify(luggageDetailCreator, never()).createDetail(any(), anyInt());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldNotSaveBookingWhenDetailCannotBeCreated() {
        // Arrange
        Booking booking = createBookingWithPassenger();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(luggageDetailCreator.createDetail(booking, 4))
                .thenThrow(new DomainRuleException("The luggage quantity cannot exceed the maximum quantity"));

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> addLuggageUseCase.execute("123", booking.getId(), 4));
        assertEquals(500.0, booking.getPrice(), 0.001);
        verify(bookingRepository, never()).save(any());
    }

    private PassengerData passengerData(String idDocument) {
        return new PassengerData(idDocument, DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE);
    }

    private Booking createBookingWithPassenger() {
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight-outbound", TripType.ONE_WAY);
        booking.addPassengers(List.of(passengerData("123")));
        return booking;
    }
}
