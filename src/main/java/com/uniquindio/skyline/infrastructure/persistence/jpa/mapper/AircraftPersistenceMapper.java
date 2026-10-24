package com.uniquindio.skyline.infrastructure.persistence.jpa.mapper;

import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.AircraftEntity;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.SeatEmbeddable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AircraftPersistenceMapper {

    public Aircraft toDomain(AircraftEntity aircraftEntity){
        return Aircraft.reconstruct(aircraftEntity.getId(),aircraftEntity.getAircraftModel(),aircraftEntity.getSeats().stream()
                .map(s -> new Seat(s.getCode())).toList());
    }

    public AircraftEntity toEntity(Aircraft aircraft){
        AircraftEntity aircraftEntity = new AircraftEntity();
        aircraftEntity.setId(aircraft.getId());
        aircraftEntity.setAircraftModel(aircraft.getAircraftModel());
        aircraftEntity.setSeats(new ArrayList<>(aircraft.getSeats().stream().map(s -> new SeatEmbeddable(s.code())).toList()));
        return aircraftEntity;
    }
}
