package com.uniquindio.skyline.infrastructure.rest.mapper;

import com.uniquindio.skyline.application.dto.response.AirlineResponse;
import com.uniquindio.skyline.application.dto.response.TopAirlineResponse;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AirlineAircraftCount;
import org.springframework.stereotype.Component;

@Component
public class AirlineMapper {

    public AirlineResponse toAirlineResponse(Airline airline) {
        return new AirlineResponse(
                airline.getId(),
                airline.getName(),
                airline.getMaximumLuggageAmount(),
                airline.getPriceSeats()
        );
    }

    public TopAirlineResponse toTopAirlineResponse(AirlineAircraftCount airlineCount) {
        return new TopAirlineResponse(
                airlineCount.airlineId(),
                airlineCount.name(),
                airlineCount.aircraftCount()
        );
    }
}