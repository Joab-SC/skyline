package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.application.port.PaymentGateway;
import com.uniquindio.skyline.application.port.PaymentRequest;
import com.uniquindio.skyline.application.port.PaymentResult;
import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Payment;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayBookingUseCase {

    private final BookingRepository bookingRepository;
    private final PaymentGateway paymentGateway;


    public Booking execute(String paymentToken, String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        Payment payment = booking.startPayment();
        bookingRepository.save(booking);

        PaymentResult result = paymentGateway.charge(new PaymentRequest(
                payment.getId(),
                payment.getAmount(),
                paymentToken,
                booking.getHolderEmail()));

        booking.registerPaymentResult(payment.getId(), result.status(), result.transactionId(), result.message());
        bookingRepository.save(booking);

        payment.validateApproved();
        return booking;
    }
}
