package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.service.BookingSeatsReleaser;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelBookingUseCase {

    private final BookingRepository bookingRepository;
    private final BookingSeatsReleaser bookingSeatsReleaser;

    // Cancels a pending booking and gives its seats back to every leg of its flights.
    @Transactional
    public Booking execute(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        booking.cancelBooking();
        bookingSeatsReleaser.releaseSeats(booking);

        bookingRepository.save(booking);
        return booking;
    }
}
