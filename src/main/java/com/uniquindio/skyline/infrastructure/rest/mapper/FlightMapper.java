package com.uniquindio.skyline.infrastructure.rest.mapper;

import com.uniquindio.skyline.application.dto.response.FlightResponse;
import com.uniquindio.skyline.application.dto.response.LayoverResponse;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.valueObject.Layover;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlightMapper {

    public FlightResponse toFlightResponse(Flight flight) {
        return new FlightResponse(
                flight.getId(),
                flight.getAirlineId(),
                flight.getPrice(),
                flight.getOriginAirport().name(),
                flight.getDestinationAirport().name(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getFlightScope().name(),
                flight.getIdLegs(),
                flight.getLayovers().stream().map(this::toLayoverResponse).toList()
        );
    }

    public List<FlightResponse> toFlightResponseList(List<Flight> flights) {
        return flights.stream().map(this::toFlightResponse).toList();
    }

    private LayoverResponse toLayoverResponse(Layover layover) {
        return new LayoverResponse(
                layover.airport().name(),
                layover.duration().toMinutes()
        );
    }
}