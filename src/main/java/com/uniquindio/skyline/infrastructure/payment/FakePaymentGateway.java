package com.uniquindio.skyline.infrastructure.payment;

import com.uniquindio.skyline.application.port.PaymentGateway;
import com.uniquindio.skyline.application.port.PaymentRequest;
import com.uniquindio.skyline.application.port.PaymentResult;
import com.uniquindio.skyline.domain.valueObject.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Simulates a payment provider. Approves every token except the two test tokens,
// like the test cards of a real sandbox.
@Component
public class FakePaymentGateway implements PaymentGateway {

    private static final String DECLINED_TOKEN = "tok_declined";
    private static final String ERROR_TOKEN = "tok_error";

    @Override
    public PaymentResult charge(PaymentRequest request) {
        return switch (request.paymentToken()) {
            case DECLINED_TOKEN -> new PaymentResult(PaymentStatus.DECLINED, null, "Insufficient funds");
            case ERROR_TOKEN -> new PaymentResult(PaymentStatus.ERROR, null, "Payment provider unavailable");
            default -> new PaymentResult(PaymentStatus.APPROVED, "FAKE-" + UUID.randomUUID(), null);
        };
    }
}
