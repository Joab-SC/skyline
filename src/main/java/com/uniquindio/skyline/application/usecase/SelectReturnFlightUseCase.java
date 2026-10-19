package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.service.OutboundAndReturnFlightsValidator;
import com.uniquindio.skyline.domain.service.SeatsAvailableReducer;

public class SelectReturnFlightUseCase {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final OutboundAndReturnFlightsValidator outboundAndReturnFlightsValidator;
    private final SeatsAvailableReducer seatsAvailableReducer;


    public SelectReturnFlightUseCase(BookingRepository bookingRepository, FlightRepository flightRepository, OutboundAndReturnFlightsValidator outboundAndReturnFlightsValidator, SeatsAvailableReducer seatsAvailableReducer) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.outboundAndReturnFlightsValidator = outboundAndReturnFlightsValidator;
        this.seatsAvailableReducer = seatsAvailableReducer;
    }

    public Booking execute(String idBooking, String idReturnFlight){
        Booking booking = bookingRepository.findById(idBooking).orElseThrow(()-> new DomainRuleException(""));
        Flight flight = flightRepository.findById(idReturnFlight).orElseThrow(()-> new DomainRuleException(""));
        booking.addReturnFlight(flight.getId(), flight.getPrice());
        outboundAndReturnFlightsValidator.validateOutboundAirportEqualsReturnFlight(idBooking);
        seatsAvailableReducer.reduceAvailableSeats(idReturnFlight,booking.getNumPassengers());
        return booking;
    }
}
