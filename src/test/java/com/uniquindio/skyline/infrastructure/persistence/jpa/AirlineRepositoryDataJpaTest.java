package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.AirlinePersistenceMapper;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.ExtraServicePersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({AirlineRepositoryJpa.class, AirlinePersistenceMapper.class, ExtraServicePersistenceMapper.class})
class AirlineRepositoryDataJpaTest {

    @Autowired
    private AirlineRepository airlineRepository;

    @Test
    void shouldLoadAirlineFromDataSqlWithItsListsAndExtraServices() {
        // Arrange: "airline-avianca" comes from data.sql

        // Act
        Airline avianca = airlineRepository.findById("airline-avianca").orElseThrow();

        // Assert
        assertEquals("Avianca", avianca.getName());
        assertEquals(3, avianca.getMaximumLuggageAmount());
        assertEquals(45000, avianca.getPriceSeats(), 0.001);
        assertEquals(3, avianca.getAircraftIds().size());
        assertEquals(List.of("leg-bog-mde-01", "leg-mde-ctg-01"), avianca.getLegIds());
        assertEquals(2, avianca.getExtraServices().size());

        ExtraService wifi = avianca.getExtraService("extra-avianca-wifi").orElseThrow();
        assertEquals(Optional.empty(), wifi.getDescription());
    }

    @Test
    void shouldReturnEmptyWhenAirlineDoesNotExist() {
        // Act
        Optional<Airline> result = airlineRepository.findById("airline-does-not-exist");

        // Assert
        assertTrue(result.isEmpty());
    }
}