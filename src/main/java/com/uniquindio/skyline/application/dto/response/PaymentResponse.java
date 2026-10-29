package com.uniquindio.skyline.application.dto.response;

import java.time.LocalDateTime;

public record PaymentResponse(
        String id,
        double amount,
        String status,
        String transactionId,
        String message,
        LocalDateTime createdAt
) {}
