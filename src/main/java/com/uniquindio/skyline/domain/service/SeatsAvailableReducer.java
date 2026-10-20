package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.LegRepository;

import java.util.ArrayList;
import java.util.List;

public class SeatsAvailableReducer {

    private final LegRepository legRepository;

    public SeatsAvailableReducer(LegRepository legRepository) {
        this.legRepository = legRepository;
    }

    // Checks the booking can take the return flight before reducing seats, so a failure leaves nothing half done.
    public void reduceAvailableSeats (Booking booking, Flight flight){
        // Check the booking before reducing seats, so a failure leaves nothing half done.
        if (!booking.canAddReturnFlight(flight.getId(), flight.getPrice())){
            throw new DomainRuleException("The return flight cannot be added to this booking");
        }
        // First pass: load and validate every leg, so nothing changes if one of them fails.
        List<Leg> legs = new ArrayList<>();
        for (String idLeg : flight.getIdLegs()){
            Leg leg = legRepository.findById(idLeg).orElseThrow(() -> new DomainRuleException("The leg cannot be found"));
            if (!leg.hasAvailableSeats(booking.getNumPassengers())){
                throw new DomainRuleException("There's not enough available seats for the num of passengers");
            }
            legs.add(leg);
        }

        // Second pass: every leg has seats, so reduce and save them.
        for (Leg leg : legs){
            leg.reduceAvailableSeats(booking.getNumPassengers());
            legRepository.save(leg);
        }
    }
}
