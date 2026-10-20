package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;

public class OutboundAndReturnFlightsValidator {

    public void validateOutboundAirportEqualsReturnFlight(Flight outboundFlight, Flight returnFlight) {
        if (!outboundFlight.getDestinationAirport().equals(returnFlight.getOriginAirport())) {
            throw new DomainRuleException("Return flight must depart from the outbound destination airport");
        }
    }
}
