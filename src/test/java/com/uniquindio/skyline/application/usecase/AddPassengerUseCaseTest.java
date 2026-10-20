package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.PassengerData;
import com.uniquindio.skyline.domain.valueObject.PhonePrefix;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddPassengerUseCaseTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AddPassengerUseCase useCase;

    @Test
    void shouldAddPassengersChooseHolderAndSaveBooking() {
        // Arrange
        Booking booking = Booking.createPendingBooking(2, 500.0, "outbound-1", TripType.ONE_WAY);

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        // Act
        useCase.execute(booking.getId(), List.of(passengerData("1001"), passengerData("1002")),
                "1001", "laura@mail.com", "3001234567", PhonePrefix.COLOMBIA);

        // Assert
        assertEquals(2, booking.getPassengerList().size());
        assertTrue(booking.getPassengerList().get(0).isHolder());
        assertFalse(booking.getPassengerList().get(1).isHolder());

        verify(bookingRepository).findById(booking.getId());
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldThrowExceptionWhenBookingDoesNotExist() {
        // Arrange
        when(bookingRepository.findById("booking-1"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> useCase.execute("booking-1", List.of(passengerData("1001")),
                        "1001", "laura@mail.com", "3001234567", PhonePrefix.COLOMBIA)
        );

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldNotSaveBookingWhenPassengersCannotBeAdded() {
        // Arrange
        Booking booking = Booking.createPendingBooking(2, 500.0, "outbound-1", TripType.ONE_WAY);

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> useCase.execute(booking.getId(), List.of(passengerData("1001")),
                        "1001", "laura@mail.com", "3001234567", PhonePrefix.COLOMBIA)
        );

        assertTrue(booking.getPassengerList().isEmpty());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldNotSaveBookingWhenHolderCannotBeChosen() {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 500.0, "outbound-1", TripType.ONE_WAY);

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> useCase.execute(booking.getId(), List.of(passengerData("1001")),
                        "9999", "laura@mail.com", "3001234567", PhonePrefix.COLOMBIA)
        );

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    private PassengerData passengerData(String idDocument) {
        return new PassengerData(idDocument, DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE);
    }
}
