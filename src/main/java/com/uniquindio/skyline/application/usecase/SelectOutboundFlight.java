package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.valueObject.TripType;

import java.util.List;

public class SelectOutboundFlight {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;

    public SelectOutboundFlight(BookingRepository bookingRepository, FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
    }

    public Booking execute(int numPassengers, double price, String idOutboundFlight,
                           TripType tripType){
        flightRepository.findById(idOutboundFlight).orElseThrow(()-> new DomainRuleException("OutboundFlight cannot be found"));
        Booking booking = Booking.createPendingBooking(numPassengers, price, idOutboundFlight, tripType);
        bookingRepository.save(booking);

        return booking;
    }
}
