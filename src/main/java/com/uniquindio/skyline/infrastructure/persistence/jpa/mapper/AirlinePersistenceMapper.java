package com.uniquindio.skyline.infrastructure.persistence.jpa.mapper;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.AirlineEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class AirlinePersistenceMapper {

    private final ExtraServicePersistenceMapper extraServicePersistenceMapper;
    public AirlinePersistenceMapper(ExtraServicePersistenceMapper extraServicePersistenceMapper) {
        this.extraServicePersistenceMapper = extraServicePersistenceMapper;
    }
    public AirlineEntity toEntity(Airline airline) {
        AirlineEntity entity = new AirlineEntity();
        entity.setId(airline.getId());
        entity.setName(airline.getName());
        entity.setAircraftIds(airline.getAircraftIds());
        entity.setLegIds(airline.getLegIds());
        entity.setExtraServices(airline.getExtraServices().stream().map(
                extraServicePersistenceMapper::toEntity).collect(Collectors.toCollection(ArrayList::new)));
        entity.setMaximumLuggageAmount(airline.getMaximumLuggageAmount());
        entity.setPriceSeats(airline.getPriceSeats());
        return entity;
    }

    public Airline toDomain(AirlineEntity airlineEntity) {
        return Airline.reconstruct(airlineEntity.getId(),airlineEntity.getName(), airlineEntity.getMaximumLuggageAmount(), airlineEntity.getPriceSeats(),
                airlineEntity.getExtraServices().stream().map(extraServicePersistenceMapper::toDomain).collect(Collectors.toCollection(ArrayList::new)),
                new ArrayList<>(airlineEntity.getLegIds()), new ArrayList<>(airlineEntity.getAircraftIds()));
    }
}
