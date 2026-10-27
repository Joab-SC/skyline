package com.uniquindio.skyline.infrastructure.persistence.jpa.mapper;

import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.valueObject.Layover;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.FlightEntity;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.LayoverEmbeddable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class FlightPersistenceMapper {

    public FlightEntity toEntity(Flight flight) {
        FlightEntity flightEntity = new FlightEntity();
        flightEntity.setId(flight.getId());
        flightEntity.setAirlineId(flight.getAirlineId());
        flightEntity.setPrice(flight.getPrice());
        flightEntity.setIdLegs(flight.getIdLegs());
        flightEntity.setOriginAirport(flight.getOriginAirport());
        flightEntity.setDestinationAirport(flight.getDestinationAirport());
        flightEntity.setArrivalTime(flight.getArrivalTime());
        flightEntity.setDepartureTime(flight.getDepartureTime());
        flightEntity.setLayovers(flight.getLayovers().stream().map(layover ->
                new LayoverEmbeddable(layover.airport(), layover.duration())
                ).collect(Collectors.toCollection(ArrayList::new)));
        flightEntity.setFlightScope(flight.getFlightScope());
        return flightEntity;
    }

    public Flight toDomain(FlightEntity flightEntity) {
        return Flight.reconstruct(flightEntity.getId(), flightEntity.getAirlineId(), flightEntity.getPrice(),
                new ArrayList<>(flightEntity.getIdLegs()), flightEntity.getOriginAirport(),
        flightEntity.getDestinationAirport(), flightEntity.getDepartureTime(), flightEntity.getArrivalTime(),
                flightEntity.getLayovers().stream().map(layoverEmbeddable ->
                        new Layover(layoverEmbeddable.getAirport(), layoverEmbeddable.getDuration())).collect(Collectors.toCollection(ArrayList::new)),
                flightEntity.getFlightScope());
    }
}
