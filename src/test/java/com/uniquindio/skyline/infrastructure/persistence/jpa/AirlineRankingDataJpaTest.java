package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.repository.AirlineAircraftCount;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.AirlinePersistenceMapper;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.ExtraServicePersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({AirlineRepositoryJpa.class, AirlinePersistenceMapper.class, ExtraServicePersistenceMapper.class})
class AirlineRankingDataJpaTest {

    @Autowired
    private AirlineRepository airlineRepository;

    @Test
    void shouldReturnAllAirlinesOrderedByAircraftCount() {
        // Arrange: data.sql has Avianca with 3 aircrafts, LATAM with 2 and Wingo with 1

        // Act
        List<AirlineAircraftCount> ranking = airlineRepository.findTopByAircraftCount(10);

        // Assert
        assertEquals(3, ranking.size());
        assertEquals(new AirlineAircraftCount("airline-avianca", "Avianca", 3L), ranking.get(0));
        assertEquals(new AirlineAircraftCount("airline-latam", "LATAM", 2L), ranking.get(1));
        assertEquals(new AirlineAircraftCount("airline-wingo", "Wingo", 1L), ranking.get(2));
    }

    @Test
    void shouldReturnOnlyTheTopAirlinesWhenLimitIsSmaller() {
        // Arrange: same data.sql values, but we only ask for the top 2

        // Act
        List<AirlineAircraftCount> ranking = airlineRepository.findTopByAircraftCount(2);

        // Assert
        assertEquals(2, ranking.size());
        assertEquals("Avianca", ranking.get(0).name());
        assertEquals("LATAM", ranking.get(1).name());
    }
}
