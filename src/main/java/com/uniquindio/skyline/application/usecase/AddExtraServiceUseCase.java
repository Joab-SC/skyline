package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.valueObject.FlightScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddExtraServiceUseCase {

    private final BookingRepository bookingRepository;
    private final AirlineRepository airlineRepository;
    private final FlightRepository flightRepository;


    public Booking execute(String bookingId,String airlineId, String serviceId, int quantity){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        Airline airline = airlineRepository.findById(airlineId).orElseThrow();
        ExtraService extraService = airline.getExtraService(serviceId).orElseThrow();

        // The outbound flight decides whether the national or international price applies.
        Flight outboundFlight = flightRepository.findById(booking.getIdOutboundFlight()).orElseThrow();
        FlightScope flightScope = outboundFlight.getFlightScope();

        booking.addExtraService(extraService.getId(), quantity, extraService.getPrice(flightScope));
        bookingRepository.save(booking);
        return booking;
    }
}
