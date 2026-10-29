package com.uniquindio.skyline.application.port;


public record PaymentRequest(
        String reference,
        double amount,
        String paymentToken,
        String customerEmail
) {}
