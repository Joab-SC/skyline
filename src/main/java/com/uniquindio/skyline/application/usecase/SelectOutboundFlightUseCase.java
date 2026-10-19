package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.service.SeatsAvailableReducer;
import com.uniquindio.skyline.domain.valueObject.TripType;

public class SelectOutboundFlightUseCase {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final SeatsAvailableReducer seatsAvailableReducer;

    public SelectOutboundFlightUseCase(BookingRepository bookingRepository, FlightRepository flightRepository, SeatsAvailableReducer seatsAvailableReducer) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.seatsAvailableReducer = seatsAvailableReducer;
    }

    public Booking execute(int numPassengers, double price, String idOutboundFlight,
                           TripType tripType){
        flightRepository.findById(idOutboundFlight).orElseThrow(()-> new DomainRuleException("OutboundFlight cannot be found"));
        Booking booking = Booking.createPendingBooking(numPassengers, price, idOutboundFlight, tripType);
        seatsAvailableReducer.reduceAvailableSeats(idOutboundFlight,numPassengers);
        bookingRepository.save(booking);

        return booking;
    }
}
