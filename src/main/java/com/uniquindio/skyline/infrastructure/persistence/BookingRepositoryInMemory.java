
package com.uniquindio.skyline.infrastructure.persistence;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
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

    // Stores or replaces a booking.
    @Override
    public void save(Booking booking) {
        bookings.put(booking.getId(), booking);
    }
}
