package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.FlightSearchCache;
import com.uniquindio.skyline.domain.service.SeatsAvailableReducer;
import com.uniquindio.skyline.domain.valueObject.TripType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SelectOutboundFlightUseCase {

    private final BookingRepository bookingRepository;
    private final FlightSearchCache flightSearchCache;
    private final SeatsAvailableReducer seatsAvailableReducer;
    private final FlightRepository flightRepository;

    @Transactional
    public Booking execute(int numPassengers, String idOutboundFlight,
                           TripType tripType){
        Flight flight = flightSearchCache.findById(idOutboundFlight).orElseThrow();
        Booking booking = Booking.createPendingBooking(numPassengers, flight.getPrice(), flight.getId(), tripType);
        seatsAvailableReducer.reduceAvailableSeats(flight,numPassengers);
        flightRepository.save(flight);
        bookingRepository.save(booking);

        return booking;
    }
}
