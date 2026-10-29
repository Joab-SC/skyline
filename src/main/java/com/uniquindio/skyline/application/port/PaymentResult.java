package com.uniquindio.skyline.application.port;

import com.uniquindio.skyline.domain.valueObject.PaymentStatus;

public record PaymentResult(
        PaymentStatus status,
        String transactionId,
        String message
) {}
