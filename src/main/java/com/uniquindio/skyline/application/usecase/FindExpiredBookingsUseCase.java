package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.service.BookingExpirationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FindExpiredBookingsUseCase {

    private final BookingExpirationValidator bookingExpirationValidator;

    // Ids of the pending bookings whose payment deadline passed at the given time.
    public List<String> execute(LocalDateTime now) {
        return bookingExpirationValidator.findExpiredPendingBookings(now).stream()
                .map(Booking::getId)
                .toList();
    }
}