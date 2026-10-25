package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.repository.AircraftRepository;
import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.AircraftPersistenceMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({AircraftRepositoryJpa.class, AircraftPersistenceMapper.class})
class AircraftRepositoryDataJpaTest {

    @Autowired
    private AircraftRepository aircraftRepository;

    @Test
    void shouldLoadAircraftFromDataSqlWithItsModel() {
        // Arrange: "aircraft-b787-01" comes from data.sql without seats

        // Act
        Aircraft aircraft = aircraftRepository.findById("aircraft-b787-01").orElseThrow();

        // Assert
        assertEquals("aircraft-b787-01", aircraft.getId());
        assertEquals(AircraftModel.BOEING_737_9_MAX, aircraft.getAircraftModel());
        assertTrue(aircraft.getSeats().isEmpty());
    }

    @Test
    void shouldSaveAndRecoverAircraftWithItsSeats() {
        // Arrange
        Aircraft aircraft = Aircraft.createAircraft(AircraftModel.EMBRAER_E175);

        // Act
        aircraftRepository.save(aircraft);
        Aircraft recovered = aircraftRepository.findById(aircraft.getId()).orElseThrow();

        // Assert
        assertEquals(AircraftModel.EMBRAER_E175, recovered.getAircraftModel());
        assertEquals(88, recovered.getSeats().size());
        assertEquals(new HashSet<>(aircraft.getSeats()), new HashSet<>(recovered.getSeats()));
        assertTrue(recovered.getSeats().contains(new Seat("22D")));
    }

    @Test
    void shouldReturnEmptyWhenAircraftDoesNotExist() {
        // Act
        Optional<Aircraft> result = aircraftRepository.findById("aircraft-does-not-exist");

        // Assert
        assertTrue(result.isEmpty());
    }
}
