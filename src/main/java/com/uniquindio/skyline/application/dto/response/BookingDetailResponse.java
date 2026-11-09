package com.uniquindio.skyline.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record BookingDetailResponse(
        String id,
        int numPassengers,
        double price,
        String outboundFlightId,
        String returnFlightId,
        String tripType,
        String bookingStatus,
        LocalDateTime paymentDeadline,
        List<PassengerDetailResponse> passengers,
        List<BookingExtraServiceResponse> extraServices,
        List<PaymentResponse> payments
) {}