package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.FlightSearchCache;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.service.OutboundAndReturnFlightsValidator;
import com.uniquindio.skyline.domain.service.SeatsAvailableReducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SelectReturnFlightUseCase {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final OutboundAndReturnFlightsValidator outboundAndReturnFlightsValidator;
    private final SeatsAvailableReducer seatsAvailableReducer;
    private final FlightSearchCache flightSearchCache;

    @Transactional
    public Booking execute(String idBooking, String idReturnFlight){
        Booking booking = bookingRepository.findById(idBooking).orElseThrow();

        Flight returnFlight = flightSearchCache.findById(idReturnFlight).orElseThrow();
        Flight outboundFlight = flightRepository.findById(booking.getIdOutboundFlight()).orElseThrow();
        outboundAndReturnFlightsValidator.validateOutboundAndReturnFlightCompatibility(outboundFlight,  returnFlight);

        booking.validateReturnFlight(returnFlight.getId(), returnFlight.getPrice());
        seatsAvailableReducer.reduceAvailableSeats(returnFlight, booking.getNumPassengers());
        booking.addReturnFlight(returnFlight.getId(), returnFlight.getPrice());

        flightRepository.save(returnFlight);
        bookingRepository.save(booking);
        return booking;
    }
}
