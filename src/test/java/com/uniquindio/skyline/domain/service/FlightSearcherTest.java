package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.City;
import com.uniquindio.skyline.domain.valueObject.Flight;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.infrastructure.persistence.LegRepositoryInMemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FlightSearcherTest {


    private LegRepositoryInMemory legRepository;
    private FlightSearcher flightSearcher;

    private Airport bogota;
    private Airport medellin;
    private Airport cali;
    private Airport cartagena;


    private final LocalDateTime baseTime =
            LocalDateTime.of(2026, 11, 10, 0, 0);

    private final List<Seat> seats =
            List.of(new Seat("A1"), new Seat("A2"));

    @BeforeEach
    void setUp() {
        legRepository = new LegRepositoryInMemory();
        flightSearcher = new FlightSearcher(legRepository, null);

        // 4 aeropuertos
        bogota = new Airport("El Dorado", "BOG", City.BOGOTA);
        medellin = new Airport(
                "Jose Maria Cordova", "MDE", City.MEDELLIN);
        cali = new Airport(
                "Alfonso Bonilla Aragon", "CLO", City.CALI);
        cartagena = new Airport(
                "Rafael Nunez", "CTG", City.CARTAGENA);

        // AEROLINEA 1: AIR1 (4 trayectos)
        saveLeg(bogota, medellin, 6, 8, "AIR1");
        saveLeg(medellin, cali, 10, 12, "AIR1");
        saveLeg(cali, cartagena, 14, 16, "AIR1");
        saveLeg(cartagena, bogota, 18, 20, "AIR1");

        // AEROLINEA 2: AIR2 (4 trayectos)
        saveLeg(bogota, cali, 7, 9, "AIR2");
        saveLeg(cali, medellin, 11, 13, "AIR2");
        saveLeg(medellin, cartagena, 15, 17, "AIR2");
        saveLeg(cartagena, bogota, 19, 21, "AIR2");
    }

    private void saveLeg(
            Airport origin,
            Airport destination,
            int departureHour,
            int arrivalHour,
            String airlineId) {

        legRepository.save(
                Leg.createLeg(
                        origin,
                        destination,
                        baseTime.withHour(departureHour),
                        baseTime.withHour(arrivalHour),
                        "AIRCRAFT-1",
                        airlineId,
                        seats,
                        20000,
                        150000
                )
        );
    }
}
