package com.uniquindio.skyline.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        String id,
        int numPassengers,
        double price,
        String outboundFlightId,
        String returnFlightId,
        String tripType,
        String bookingStatus,
        LocalDateTime paymentDeadline,
        List<PassengerResponse> passengers,
        List<BookingExtraServiceResponse> extraServices
) {}