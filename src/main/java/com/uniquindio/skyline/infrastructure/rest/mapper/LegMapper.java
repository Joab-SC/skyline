package com.uniquindio.skyline.infrastructure.rest.mapper;

import com.uniquindio.skyline.application.dto.response.LegResponse;
import com.uniquindio.skyline.domain.entity.Leg;
import org.springframework.stereotype.Component;

@Component
public class LegMapper {

    public LegResponse toLegResponse(Leg leg) {
        return new LegResponse(
                leg.getId(),
                leg.getOriginAirport().name(),
                leg.getDestinationAirport().name(),
                leg.getDepartureTime(),
                leg.getArrivalTime(),
                leg.getAirlineId(),
                leg.getAircraftId(),
                leg.getLuggage().getPrice(),
                leg.getPrice(),
                leg.getAvailableSeats()
        );
    }
}