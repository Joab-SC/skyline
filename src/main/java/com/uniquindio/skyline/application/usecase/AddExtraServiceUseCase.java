package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.valueObject.FlightScope;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddExtraServiceUseCase {

    private final BookingRepository bookingRepository;
    private final AirlineRepository airlineRepository;
    private final FlightRepository flightRepository;

    @Transactional
    public Booking execute(String bookingId, String serviceId, int quantity) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        Flight outboundFlight = flightRepository.findById(booking.getIdOutboundFlight()).orElseThrow();
        Airline airline = airlineRepository.findById(outboundFlight.getAirlineId()).orElseThrow();
        ExtraService extraService = airline.getExtraService(serviceId).orElseThrow();
        extraService.validateIsNotDeleted();
        booking.addExtraService(extraService.getId(), quantity, extraService.getPrice(outboundFlight.getFlightScope()));
        bookingRepository.save(booking);
        return booking;
    }
}
