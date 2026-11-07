package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.*;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.valueObject.PassengerBookingId;

import java.util.List;

public class SeatAssigner {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;


    public SeatAssigner(FlightRepository flightRepository, AirlineRepository airlineRepository) {
        this.flightRepository = flightRepository;
        this.airlineRepository = airlineRepository;
    }

    public void assignSeat(Booking booking, String idLegSeat, Passenger passenger, Leg leg){
        booking.validateIsModifiable();
        booking.isPassengerInBooking(passenger);
        List<String> idLegsOutbound = flightRepository.findById(booking.getIdOutboundFlight()).orElseThrow().getIdLegs();
        List<String> idLegsReturn;
        if (booking.getIdReturnFlight().isPresent()){
            idLegsReturn = flightRepository.findById(booking.getIdReturnFlight().get()).orElseThrow().getIdLegs();
        }
        else {
            idLegsReturn = List.of();
        }
        if (!idLegsOutbound.contains(leg.getId()) && !idLegsReturn.contains(leg.getId())){
            throw new DomainRuleException("Leg have to be on the booking");
        }
        Airline airline = airlineRepository.findById(leg.getAirlineId()).orElseThrow();
        PassengerBookingId passengerBookingId = new PassengerBookingId(booking.getId(),passenger.getPerson().getIdDocument());
        boolean hadSeat = leg.hasSeatAssigned(passengerBookingId);
        double priceSeats = airline.getPriceSeats();
        leg.assignSeat(idLegSeat, passengerBookingId);
        if (!hadSeat){
            booking.addPriceSeats(airline.getPriceSeats());
        }
    }
}
