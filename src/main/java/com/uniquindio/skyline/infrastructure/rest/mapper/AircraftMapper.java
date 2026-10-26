package com.uniquindio.skyline.infrastructure.rest.mapper;

import com.uniquindio.skyline.application.dto.response.AircraftResponse;
import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.valueObject.Seat;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AircraftMapper {

    public AircraftResponse toAircraftResponse(Aircraft aircraft) {
        List<String> seatCodes = aircraft.getSeats().stream()
                .map(Seat::code)
                .toList();

        return new AircraftResponse(
                aircraft.getId(),
                aircraft.getAircraftModel().name(),
                seatCodes
        );
    }
}
