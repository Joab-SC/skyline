package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.service.BookingExpirationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpireBookingUseCase {

    private final BookingRepository bookingRepository;
    private final BookingExpirationValidator bookingExpirationValidator;

    // Expires one booking in its own transaction: all of it is saved, or nothing.
    @Transactional
    public void execute(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        bookingExpirationValidator.expire(booking);
    }
}