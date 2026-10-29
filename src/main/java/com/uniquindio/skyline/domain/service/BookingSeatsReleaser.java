package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.entity.Passenger;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;

import java.util.ArrayList;
import java.util.List;

// Gives back to the legs everything a booking held: the available seats count
// and the seats assigned to its passengers. The opposite of SeatsAvailableReducer.
public class BookingSeatsReleaser {

    private final FlightRepository flightRepository;
    private final LegRepository legRepository;

    public BookingSeatsReleaser(FlightRepository flightRepository, LegRepository legRepository) {
        this.flightRepository = flightRepository;
        this.legRepository = legRepository;
    }

    public void releaseSeats(Booking booking){
        int numPassengers = booking.getNumPassengers();

        // First pass: load and validate every leg of the outbound and return flights,
        // so nothing changes if one of them fails.
        List<Leg> legs = new ArrayList<>();
        for (String idFlight : booking.getFlightIds()){
            Flight flight = flightRepository.findById(idFlight)
                    .orElseThrow(() -> new DomainRuleException("The flight cannot be found"));
            for (String idLeg : flight.getIdLegs()){
                Leg leg = legRepository.findById(idLeg)
                        .orElseThrow(() -> new DomainRuleException("The leg cannot be found"));
                if (!leg.canRestoreAvailableSeats(numPassengers)){
                    throw new DomainRuleException("Available seats cannot exceed the total seats of the leg");
                }
                legs.add(leg);
            }
        }

        // Second pass: restore the count, free the passengers' seats and save.
        for (Leg leg : legs){
            leg.restoreAvailableSeats(numPassengers);
            for (Passenger passenger : booking.getPassengerList()){
                leg.releaseSeatOfPassenger(passenger.getPerson().getIdDocument());
            }
            legRepository.save(leg);
        }
    }
}
