package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.repository.AircraftRepository;
import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import com.uniquindio.skyline.domain.valueObject.Seat;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AircraftRepositoryJpaTest {

    @Autowired
    private AircraftRepository aircraftRepository;

    @Test
    void shouldSaveAndRecoverAircraftWithItsModelAndSeats() {
        // Arrange
        Aircraft aircraft = Aircraft.createAircraft(AircraftModel.AIRBUS_A320_NEO);

        // Act
        aircraftRepository.save(aircraft);
        Aircraft recovered = aircraftRepository.findById(aircraft.getId()).orElseThrow();

        // Assert
        assertEquals(aircraft.getId(), recovered.getId());
        assertEquals(AircraftModel.AIRBUS_A320_NEO, recovered.getAircraftModel());
        assertEquals(194, recovered.getSeats().size());
        assertEquals(new HashSet<>(aircraft.getSeats()), new HashSet<>(recovered.getSeats()));
        assertTrue(recovered.getSeats().contains(new Seat("1A")));
    }
}
