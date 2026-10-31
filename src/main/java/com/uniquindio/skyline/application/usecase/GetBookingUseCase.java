package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetBookingUseCase {

    private final BookingRepository bookingRepository;

    // Returns the booking with its flights, passengers, luggage and extra services.
    @Transactional(readOnly = true)
    public Booking execute(String bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow();
    }
}