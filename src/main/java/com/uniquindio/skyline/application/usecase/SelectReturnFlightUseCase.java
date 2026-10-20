package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.FlightSearchCache;
import com.uniquindio.skyline.domain.service.OutboundAndReturnFlightsValidator;
import com.uniquindio.skyline.domain.service.SeatsAvailableReducer;

public class SelectReturnFlightUseCase {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final OutboundAndReturnFlightsValidator outboundAndReturnFlightsValidator;
    private final SeatsAvailableReducer seatsAvailableReducer;
    private final FlightSearchCache flightSearchCache;


    public SelectReturnFlightUseCase(BookingRepository bookingRepository, FlightRepository flightRepository, OutboundAndReturnFlightsValidator outboundAndReturnFlightsValidator,
                                     SeatsAvailableReducer seatsAvailableReducer, FlightSearchCache flightSearchCache) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.outboundAndReturnFlightsValidator = outboundAndReturnFlightsValidator;
        this.seatsAvailableReducer = seatsAvailableReducer;
        this.flightSearchCache = flightSearchCache;
    }

    public Booking execute(String idBooking, String idReturnFlight){
        Booking booking = bookingRepository.findById(idBooking).orElseThrow(()-> new DomainRuleException(""));

        Flight returnFlight = flightSearchCache.findById(idReturnFlight).orElseThrow(()-> new DomainRuleException(""));
        Flight outboundFlight = flightRepository.findById(booking.getIdOutboundFlight()).orElseThrow(()-> new DomainRuleException(""));
        outboundAndReturnFlightsValidator.validateOutboundAirportEqualsReturnFlight(outboundFlight,  returnFlight);

        seatsAvailableReducer.reduceAvailableSeats(returnFlight,booking.getNumPassengers());
        booking.addReturnFlight(returnFlight.getId(), returnFlight.getPrice());


        flightRepository.save(returnFlight);
        bookingRepository.save(booking);
        return booking;
    }
}
