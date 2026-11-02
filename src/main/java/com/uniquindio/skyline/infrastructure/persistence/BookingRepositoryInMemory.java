
package com.uniquindio.skyline.infrastructure.persistence;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BookingRepositoryInMemory implements BookingRepository {

    private final Map<String, Booking> bookings = new HashMap<>();

    // Finds bookings whose status matches and payment deadline has passed.
    @Override
    public List<Booking> findByStatusAndPaymentDeadlineLessThanEqual(
            BookingStatus bookingStatus,
            LocalDateTime localDateTime) {

        return bookings.values().stream()
                .filter(booking -> booking.getBookingStatus() == bookingStatus)
                .filter(booking -> !booking.getPaymentDeadline().isAfter(localDateTime))
                .toList();
    }

    // Finds a booking by its id.
    @Override
    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(bookings.get(id));
    }

    // Finds the bookings of a customer, newest first.
    @Override
    public List<Booking> findByIdCustomer(String idCustomer) {
        return bookings.values().stream()
                .filter(booking -> booking.getIdCustomer().filter(idCustomer::equals).isPresent())
                .sorted(Comparator.comparing(Booking::getPaymentDeadline).reversed())
                .toList();
    }

    // Stores or replaces a booking.
    @Override
    public void save(Booking booking) {
        bookings.put(booking.getId(), booking);
    }
}
