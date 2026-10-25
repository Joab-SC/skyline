package com.uniquindio.skyline.infrastructure.config;

import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.service.BookingExpirationValidator;
import com.uniquindio.skyline.domain.service.FlightSearcher;
import com.uniquindio.skyline.domain.service.LegOverlapValidator;
import com.uniquindio.skyline.domain.service.LuggageDetailCreator;
import com.uniquindio.skyline.domain.service.OutboundAndReturnFlightsValidator;
import com.uniquindio.skyline.domain.service.SeatAssigner;
import com.uniquindio.skyline.domain.service.SeatsAvailableReducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public BookingExpirationValidator bookingExpirationValidator(BookingRepository  bookingRepository) {
        return new BookingExpirationValidator(bookingRepository);
    }

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
}
