package com.uniquindio.skyline.domain.repository;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    List<Booking> findByStatusAndPaymentDeadlineLessThanEqual(BookingStatus bookingStatus, LocalDateTime localDateTime);
    Optional<Booking> findById(String id);
    void save( Booking booking);
}
