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
import java.util.List;

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

    @Test
    void shouldFindOnlyTheAircraftsWhoseIdIsInTheList() {
        // Arrange
        Aircraft a320 = Aircraft.createAircraft(AircraftModel.AIRBUS_A320_NEO);
        Aircraft e175 = Aircraft.createAircraft(AircraftModel.EMBRAER_E175);
        Aircraft other = Aircraft.createAircraft(AircraftModel.EMBRAER_E175);
        aircraftRepository.save(a320);
        aircraftRepository.save(e175);
        aircraftRepository.save(other);

        // Act
        List<Aircraft> found = aircraftRepository.findAllById(List.of(a320.getId(), e175.getId(), "no-aircraft"));

        // Assert
        assertEquals(new HashSet<>(List.of(a320, e175)), new HashSet<>(found));
    }
}
