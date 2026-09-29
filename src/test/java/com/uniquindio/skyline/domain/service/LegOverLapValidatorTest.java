package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.City;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.infrastructure.persistence.LegRepositoryInMemory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LegOverLapValidatorTest {

    @Test
    public void doesNotAceptTwoOverlapingLegs(){
        LegRepository legRepository = new LegRepositoryInMemory();
        LegOverlapValidator validator = new LegOverlapValidator(legRepository);

        Leg existingLeg = Leg.createLeg(
                "001",
                new Airport("El Dorado", "BOG", City.BOGOTA),
                new Airport("Jose Maria Cordova", "MDE", City.MEDELLIN),
                LocalDateTime.of(2026, 9, 23, 8, 0),
                LocalDateTime.of(2026, 9, 23, 10, 0),
                "aircraft-001",
                List.of(new Seat("A1")),
                50.0,
                300.0
        );
        legRepository.save(existingLeg);

        assertThrows(DomainRuleException.class,
                () -> validator.validateNoOverlap(
                        "aircraft-001",
                        LocalDateTime.of(2026, 9, 23, 9, 30),
                        LocalDateTime.of(2026, 9, 23, 11, 0)
                )
        );
    }
}
