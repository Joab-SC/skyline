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
        PassengerBookingId occupant = validateSeatChange(booking, passenger, leg);
        boolean hadSeat = leg.hasSeatAssigned(occupant);
        leg.assignSeat(idLegSeat, occupant);
        if (!hadSeat) booking.addPriceSeats(priceSeats(leg));
    }

    public void releaseSeat(Booking booking, Passenger passenger, Leg leg){
        PassengerBookingId occupant = validateSeatChange(booking, passenger, leg);
        if (!leg.hasSeatAssigned(occupant)) return;          // nothing to free
        leg.releaseSeatOfPassenger(occupant);                // same method BookingSeatsReleaser uses
        booking.subtractPriceSeats(priceSeats(leg));
    }

    // Checks shared by assigning and releasing a seat: the booking can be modified,
// the passenger is in it and the leg belongs to one of its flights.
    private PassengerBookingId validateSeatChange(Booking booking, Passenger passenger, Leg leg){
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

        return new PassengerBookingId(booking.getId(), passenger.getPerson().getIdDocument());
    }

    private double priceSeats(Leg leg){
        return airlineRepository.findById(leg.getAirlineId()).orElseThrow().getPriceSeats();
    }
}
