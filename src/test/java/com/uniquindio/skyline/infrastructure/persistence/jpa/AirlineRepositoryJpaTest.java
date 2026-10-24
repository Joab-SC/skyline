package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AirlineRepositoryJpaTest {

    @Autowired
    AirlineRepository airlineRepository;

    @Test
    void shoudSaveAndRecoverAirlineWithAllItsData(){
        //Arrange
        Airline airline = Airline.createAirline("Mega airline", 10, 10000);
        airline.addAircraft("air-1");
        airline.addLeg("leg-1");

        //Act
        airlineRepository.save(airline);
        Airline recovered = airlineRepository.findById(airline.getId()).orElseThrow();

        //Assert
        assertEquals(airline, recovered);
        assertEquals(airline.getId(), recovered.getId());
        assertEquals("Mega airline", recovered.getName());
        assertEquals(10, recovered.getMaximumLuggageAmount());
        assertEquals(10000, recovered.getPriceSeats(), 0.001);;
        assertEquals(List.of("air-1"), recovered.getAircraftIds());
        assertEquals(List.of("leg-1"), recovered.getLegIds());

    }

    @Test
    void shoudSaveAndRecoverAirlineExtraServiceWithAllItsData(){
        //Arrange
        Airline airline = Airline.createAirline("Mega airline", 10, 10000);
        ExtraService extraService = airline.createExtraService("Food", null, 1000, 2000);

        //Act
        airlineRepository.save(airline);
        Airline recovered = airlineRepository.findById(airline.getId()).orElseThrow();
        ExtraService extraServiceRecovered = recovered.getExtraService(extraService.getId()).orElseThrow();

        //Assert
        assertEquals(extraService, extraServiceRecovered);
        assertEquals("Food", extraServiceRecovered.getNameService());
        assertNull(extraServiceRecovered.getDescription().orElse(null));
        assertEquals(1000, extraServiceRecovered.getNationalPrice(), 0.001);
        assertEquals(2000, extraServiceRecovered.getInternationalPrice(), 0.001);
    }
}
