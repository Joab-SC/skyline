package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.application.port.PaymentGateway;
import com.uniquindio.skyline.application.port.PaymentRequest;
import com.uniquindio.skyline.application.port.PaymentResult;
import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Payment;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.PassengerData;
import com.uniquindio.skyline.domain.valueObject.PaymentStatus;
import com.uniquindio.skyline.domain.valueObject.PhonePrefix;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayBookingTest {

    private static final String PAYMENT_TOKEN = "tok_test";
    private static final String HOLDER_DOCUMENT = "1001";
    private static final String HOLDER_EMAIL = "laura@mail.com";

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private PayBooking useCase;

    // ---------- Payment processed by the gateway ----------

    @Test
    void shouldSaveAttemptChargeAndSaveResultWhenPaymentIsApproved() {
        // Arrange
        Booking booking = createPayableBooking();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(paymentGateway.charge(any())).thenReturn(new PaymentResult(PaymentStatus.APPROVED, "TX-1", null));

        // Act
        Booking result = useCase.execute(PAYMENT_TOKEN, booking.getId());

        // Assert: the attempt is saved before charging, and the result after
        assertEquals(BookingStatus.CONFIRMED, result.getBookingStatus());
        InOrder inOrder = inOrder(bookingRepository, paymentGateway);
        inOrder.verify(bookingRepository).findById(booking.getId());
        inOrder.verify(bookingRepository).save(booking);
        inOrder.verify(paymentGateway).charge(any());
        inOrder.verify(bookingRepository).save(booking);
        verifyNoMoreInteractions(bookingRepository, paymentGateway);
    }

    @Test
    void shouldSendBookingPaymentDataToGateway() {
        // Arrange
        Booking booking = createPayableBooking();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(paymentGateway.charge(any())).thenReturn(new PaymentResult(PaymentStatus.APPROVED, "TX-1", null));
        ArgumentCaptor<PaymentRequest> captor = ArgumentCaptor.forClass(PaymentRequest.class);

        // Act
        useCase.execute(PAYMENT_TOKEN, booking.getId());

        // Assert
        verify(paymentGateway).charge(captor.capture());
        PaymentRequest request = captor.getValue();
        assertEquals(booking.getPayments().getFirst().getId(), request.reference());
        assertEquals(booking.getPrice(), request.amount(), 0.001);
        assertEquals(PAYMENT_TOKEN, request.paymentToken());
        assertEquals(HOLDER_EMAIL, request.customerEmail());
    }

    @Test
    void shouldSaveAttemptAndThrowWhenPaymentIsDeclined() {
        // Arrange
        Booking booking = createPayableBooking();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(paymentGateway.charge(any()))
                .thenReturn(new PaymentResult(PaymentStatus.DECLINED, null, "Insufficient funds"));

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> useCase.execute(PAYMENT_TOKEN, booking.getId()));
        verify(paymentGateway).charge(any());
        verify(bookingRepository, times(2)).save(booking);
    }

    @Test
    void shouldSaveAttemptAndThrowWhenGatewayAnswersError() {
        // Arrange
        Booking booking = createPayableBooking();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(paymentGateway.charge(any()))
                .thenReturn(new PaymentResult(PaymentStatus.ERROR, null, "Provider unavailable"));

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> useCase.execute(PAYMENT_TOKEN, booking.getId()));
        verify(paymentGateway).charge(any());
        verify(bookingRepository, times(2)).save(booking);
    }

    @Test
    void shouldSaveAttemptAndThrowWhenGatewayIsStillProcessing() {
        // Arrange
        Booking booking = createPayableBooking();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(paymentGateway.charge(any())).thenReturn(new PaymentResult(PaymentStatus.PENDING, null, null));

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> useCase.execute(PAYMENT_TOKEN, booking.getId()));
        verify(paymentGateway).charge(any());
        verify(bookingRepository, times(2)).save(booking);
    }

    @Test
    void shouldSaveOnlyThePendingAttemptWhenGatewayCallFails() {
        // Arrange: the call to the provider breaks, so there is no result to save
        Booking booking = createPayableBooking();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(paymentGateway.charge(any())).thenThrow(new RuntimeException("Connection timed out"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> useCase.execute(PAYMENT_TOKEN, booking.getId()));
        verify(paymentGateway).charge(any());
        verify(bookingRepository, times(1)).save(booking);
    }

    // ---------- Retries ----------

    @Test
    void shouldChargePendingAttemptWithItsSameReference() {
        // Arrange: a previous attempt was left pending
        Booking booking = createPayableBooking();
        Payment pending = booking.startPayment();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(paymentGateway.charge(any())).thenReturn(new PaymentResult(PaymentStatus.APPROVED, "TX-1", null));
        ArgumentCaptor<PaymentRequest> captor = ArgumentCaptor.forClass(PaymentRequest.class);

        // Act
        useCase.execute(PAYMENT_TOKEN, booking.getId());

        // Assert
        verify(paymentGateway).charge(captor.capture());
        assertEquals(pending.getId(), captor.getValue().reference());
    }

    @Test
    void shouldChargeNewAttemptWithNewReferenceAfterADeclinedOne() {
        // Arrange
        Booking booking = createPayableBooking();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(paymentGateway.charge(any()))
                .thenReturn(new PaymentResult(PaymentStatus.DECLINED, null, "Insufficient funds"))
                .thenReturn(new PaymentResult(PaymentStatus.APPROVED, "TX-2", null));
        assertThrows(DomainRuleException.class, () -> useCase.execute("tok_declined", booking.getId()));
        ArgumentCaptor<PaymentRequest> captor = ArgumentCaptor.forClass(PaymentRequest.class);

        // Act
        useCase.execute(PAYMENT_TOKEN, booking.getId());

        // Assert
        verify(paymentGateway, times(2)).charge(captor.capture());
        List<PaymentRequest> requests = captor.getAllValues();
        assertNotEquals(requests.get(0).reference(), requests.get(1).reference());
    }

    // ---------- Booking that cannot be paid: the gateway is never called ----------

    @Test
    void shouldNotChargeWhenBookingDoesNotExist() {
        // Arrange
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> useCase.execute(PAYMENT_TOKEN, "booking-1"));
        verify(paymentGateway, never()).charge(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldNotChargeBookingWithoutHolder() {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight-1", TripType.ONE_WAY);
        booking.addPassengers(List.of(passengerData()));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> useCase.execute(PAYMENT_TOKEN, booking.getId()));
        verify(paymentGateway, never()).charge(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldNotChargeBookingAlreadyConfirmed() {
        // Arrange
        Booking booking = createPayableBooking();
        Payment payment = booking.startPayment();
        booking.registerPaymentResult(payment.getId(), PaymentStatus.APPROVED, "TX-1", null);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> useCase.execute(PAYMENT_TOKEN, booking.getId()));
        verify(paymentGateway, never()).charge(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldNotChargeCancelledBooking() {
        // Arrange
        Booking booking = createPayableBooking();
        booking.cancelBooking();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> useCase.execute(PAYMENT_TOKEN, booking.getId()));
        verify(paymentGateway, never()).charge(any());
        verify(bookingRepository, never()).save(any());
    }

    // Pending booking with one passenger who is also the holder.
    private Booking createPayableBooking() {
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight-1", TripType.ONE_WAY);
        booking.addPassengers(List.of(passengerData()));
        booking.choseHolder(HOLDER_DOCUMENT, HOLDER_EMAIL, "3001234567", PhonePrefix.COLOMBIA);
        return booking;
    }

    private PassengerData passengerData() {
        return new PassengerData(HOLDER_DOCUMENT, DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE);
    }
}
