package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.PageResult;
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
    void shouldLoadDeletedExtraServiceFromDataSqlButLeaveItOutOfActiveOnes() {
        // Arrange: "extra-latam-pet" is deleted in data.sql

        // Act
        Airline latam = airlineRepository.findById("airline-latam").orElseThrow();

        // Assert
        assertTrue(latam.getExtraService("extra-latam-pet").orElseThrow().isDeleted());
        assertEquals(List.of("extra-latam-priority"),
                latam.getActiveExtraServices().stream().map(ExtraService::getId).toList());
    }

    @Test
    void shouldReturnEmptyWhenAirlineDoesNotExist() {
        // Act
        Optional<Airline> result = airlineRepository.findById("airline-does-not-exist");

        // Assert
        assertTrue(result.isEmpty());
    }

    // searchExtraServices. data.sql has Meal, Wifi (Avianca), Priority boarding and a deleted Pet in cabin (Latam).

    @Test
    void shouldSearchEveryActiveExtraServiceOrderedByName() {
        // Act
        PageResult<ExtraService> page = airlineRepository.searchExtraServices(null, null, 0, 10);

        // Assert
        assertEquals(List.of("extra-avianca-meal", "extra-latam-priority", "extra-avianca-wifi"), idsOf(page));
        assertEquals(3, page.totalElements());
        assertEquals(1, page.totalPages());
    }

    @Test
    void shouldSearchByNameIgnoringCase() {
        // Act
        PageResult<ExtraService> page = airlineRepository.searchExtraServices("WIFI", null, 0, 10);

        // Assert
        assertEquals(List.of("extra-avianca-wifi"), idsOf(page));
    }

    @Test
    void shouldSearchByDescription() {
        // Act: "board" is in the description of Meal ("Hot meal on board") and in the name of Priority boarding
        PageResult<ExtraService> page = airlineRepository.searchExtraServices("board", null, 0, 10);

        // Assert
        assertEquals(List.of("extra-avianca-meal", "extra-latam-priority"), idsOf(page));
    }

    @Test
    void shouldFilterByAirlineAndLeaveOutDeletedServices() {
        // Act
        PageResult<ExtraService> latam = airlineRepository.searchExtraServices(null, "airline-latam", 0, 10);
        PageResult<ExtraService> pet = airlineRepository.searchExtraServices("pet", null, 0, 10);

        // Assert
        assertEquals(List.of("extra-latam-priority"), idsOf(latam));
        assertTrue(pet.content().isEmpty());
        assertEquals(0, pet.totalElements());
    }

    @Test
    void shouldSplitResultsInPages() {
        // Act
        PageResult<ExtraService> first = airlineRepository.searchExtraServices(null, null, 0, 2);
        PageResult<ExtraService> second = airlineRepository.searchExtraServices(null, null, 1, 2);

        // Assert
        assertEquals(List.of("extra-avianca-meal", "extra-latam-priority"), idsOf(first));
        assertEquals(List.of("extra-avianca-wifi"), idsOf(second));
        assertEquals(3, first.totalElements());
        assertEquals(2, first.totalPages());
        assertEquals(1, second.number());
    }

    private List<String> idsOf(PageResult<ExtraService> page) {
        return page.content().stream().map(ExtraService::getId).toList();
    }
}