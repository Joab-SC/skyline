package com.uniquindio.skyline.infrastructure.rest.mapper;

import com.uniquindio.skyline.application.dto.response.AirportResponse;
import com.uniquindio.skyline.domain.valueObject.Airport;
import org.springframework.stereotype.Component;

@Component
public class AirportMapper {

    public AirportResponse toAirportResponse(Airport airport) {
        return new AirportResponse(
                airport.getCode(),
                airport.getName(),
                airport.getCity().getDisplayName(),
                airport.getCity().getCountry().getDisplayName()
        );
    }
}
