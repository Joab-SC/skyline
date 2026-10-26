package com.uniquindio.skyline.infrastructure.persistence.jpa.mapper;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.LegEntity;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.LuggageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LegPersistenceMapper {

    private final LegSeatPersistenceMapper legSeatPersistenceMapper;
    private final LuggagePersistenceMapper luggagePersistenceMapper;

    public LegEntity toEntity(Leg leg) {
        LegEntity legEntity = new LegEntity();
        legEntity.setId(leg.getId());
        legEntity.setOriginAirport(leg.getOriginAirport());
        legEntity.setDestinationAirport(leg.getDestinationAirport());
        legEntity.setDepartureTime(leg.getDepartureTime());
        legEntity.setArrivalTime(leg.getArrivalTime());
        legEntity.setAircraftId(leg.getAircraftId());
        legEntity.setAirlineId(leg.getAirlineId());
        legEntity.setPrice(leg.getPrice());
        legEntity.setAvailableSeats(leg.getAvailableSeats());
        legEntity.setSeats(leg.getSeats().stream()
                .map(legSeatPersistenceMapper::toEntity)
                .collect(Collectors.toCollection(ArrayList::new)));

        LuggageEntity luggageEntity = luggagePersistenceMapper.toEntity(leg.getLuggage());
        luggageEntity.setLeg(legEntity);
        legEntity.setLuggage(luggageEntity);
        return legEntity;
    }

    public Leg toDomain(LegEntity legEntity) {
        return Leg.reconstruct(legEntity.getId(), legEntity.getOriginAirport(), legEntity.getDestinationAirport(),
                legEntity.getDepartureTime(), legEntity.getArrivalTime(), legEntity.getAircraftId(),
                legEntity.getAirlineId(), legEntity.getPrice(),
                luggagePersistenceMapper.toDomain(legEntity.getLuggage()),
                legEntity.getSeats().stream()
                        .map(legSeatPersistenceMapper::toDomain)
                        .collect(Collectors.toCollection(ArrayList::new)),
                legEntity.getAvailableSeats());
    }
}
