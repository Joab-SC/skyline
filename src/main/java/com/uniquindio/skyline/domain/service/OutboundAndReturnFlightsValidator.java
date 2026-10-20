package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.exception.DomainRuleException;

public class OutboundAndReturnFlightsValidator {

    public void validateOutboundAirportEqualsReturnFlight(Flight outboundFlight, Flight returnFlight) {
        if (!outboundFlight.getAirlineId().equals(returnFlight.getAirlineId())) {
            throw new DomainRuleException("Outbound and return flights must be from the same airline");
        }
        if (!outboundFlight.getDestinationAirport().equals(returnFlight.getOriginAirport())) {
            throw new DomainRuleException("Return flight must depart from the outbound destination airport");
        }
        if (!returnFlight.getDestinationAirport().equals(outboundFlight.getOriginAirport())) {
            throw new DomainRuleException("Return flight must arrive at the outbound origin airport");
        }
        if (!returnFlight.getDepartureTime().isAfter(outboundFlight.getArrivalTime())) {
            throw new DomainRuleException("Return flight must depart after the outbound flight arrives");
        }
    }
}
