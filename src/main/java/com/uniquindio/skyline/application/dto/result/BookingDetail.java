package com.uniquindio.skyline.application.dto.result;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingSeat;

import java.util.List;

// What GetBooking returns: the booking and the seats its passengers hold on the legs.
public record BookingDetail(Booking booking, List<BookingSeat> bookingSeats) {
}
