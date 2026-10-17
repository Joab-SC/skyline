package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.valueObject.TripType;

import java.util.List;

public class SelectOutboundFlight {

    private final BookingRepository bookingRepository;

    public SelectOutboundFlight(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking execute(int numPassengers, double price, String idOutboundFlight,
                           TripType tripType){
        Booking booking = Booking.createPendingBooking(numPassengers, price, idOutboundFlight, tripType);
        bookingRepository.save(booking);

        return booking;
    }
}
