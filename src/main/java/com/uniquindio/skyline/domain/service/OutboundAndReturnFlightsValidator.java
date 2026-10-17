package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;

public class OutboundAndReturnFlightsValidator {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;

    public OutboundAndReturnFlightsValidator(BookingRepository bookingRepository, FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
    }

    public void validateOutboundAirportEqualsReturnFlight(String idBooking){
        Booking booking = bookingRepository.findById(idBooking).orElseThrow(()-> new DomainRuleException("Booking not found"));
        Flight outboundFlight = flightRepository.findById(booking.getIdOutboundFlight()).orElseThrow(()-> new DomainRuleException("Flight not found"));
        Flight returnFlight = flightRepository.findById(booking.getIdReturnFlight().orElseThrow(()-> new DomainRuleException("Booking must return outbound flight"))).orElseThrow(()-> new DomainRuleException("Flight not found"));
        if (!outboundFlight.getDestinationAirport().equals(returnFlight.getOriginAirport())) {
            throw new DomainRuleException("Return flight must depart from the outbound destination airport");
        }
    }
}
