package com.uniquindio.skyline.application.port;

// Port to the payment provider. The adapter decides which provider is used.
public interface PaymentGateway {

    PaymentResult charge(PaymentRequest request);
}
