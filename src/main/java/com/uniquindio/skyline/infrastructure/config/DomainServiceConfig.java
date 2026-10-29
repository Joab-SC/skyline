package com.uniquindio.skyline.infrastructure.config;

import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public LegOverlapValidator legOverlapValidator(LegRepository legRepository) {
        return new LegOverlapValidator(legRepository);
    }

    @Bean
    public FlightSearcher flightSearcher(LegRepository legRepository) {
        return new FlightSearcher(legRepository);
    }

    @Bean
    public SeatsAvailableReducer seatsAvailableReducer(LegRepository legRepository) {
        return new SeatsAvailableReducer(legRepository);
    }

    @Bean
    public SeatAssigner seatAssigner(FlightRepository flightRepository, AirlineRepository airlineRepository) {
        return new SeatAssigner(flightRepository, airlineRepository);
    }

    @Bean
    public LuggageDetailCreator luggageDetailCreator(FlightRepository flightRepository, LegRepository legRepository,
                                                     AirlineRepository airlineRepository) {
        return new LuggageDetailCreator(flightRepository, legRepository, airlineRepository);
    }

    @Bean
    public OutboundAndReturnFlightsValidator outboundAndReturnFlightsValidator() {
        return new OutboundAndReturnFlightsValidator();
    }

    @Bean
    public BookingSeatsReleaser bookingSeatsReleaser(FlightRepository flightRepository, LegRepository legRepository) {
        return new BookingSeatsReleaser(flightRepository, legRepository);
    }

    @Bean
    public BookingExpirationValidator bookingExpirationValidator(BookingRepository bookingRepository,
                                                                 BookingSeatsReleaser bookingSeatsReleaser) {
        return new BookingExpirationValidator(bookingRepository, bookingSeatsReleaser);
    }
}
