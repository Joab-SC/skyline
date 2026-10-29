package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

// Expires pending bookings whose payment deadline passed and gives their seats back.
public class BookingExpirationValidator {

    private final BookingRepository bookingRepository;
    private final BookingSeatsReleaser bookingSeatsReleaser;

    public BookingExpirationValidator(BookingRepository bookingRepository,
                                      BookingSeatsReleaser bookingSeatsReleaser) {
        this.bookingRepository = bookingRepository;
        this.bookingSeatsReleaser = bookingSeatsReleaser;
    }

    public List<Booking> findExpiredPendingBookings(LocalDateTime now) {
        return bookingRepository.findByStatusAndPaymentDeadlineLessThanEqual(BookingStatus.PENDING, now);
    }

    public void expire(Booking booking) {
        booking.expireBooking();
        bookingSeatsReleaser.releaseSeats(booking);
        bookingRepository.save(booking);
    }
}