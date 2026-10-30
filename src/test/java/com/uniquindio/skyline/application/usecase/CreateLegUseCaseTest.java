package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AircraftRepository;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.service.LegOverlapValidator;
import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import com.uniquindio.skyline.domain.valueObject.Airport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateLegUseCaseTest {

    private static final String AIRLINE_ID = "airline-1";
    private static final LocalDateTime DEPARTURE = LocalDateTime.of(2027, 3, 10, 8, 0);
    private static final LocalDateTime ARRIVAL = LocalDateTime.of(2027, 3, 10, 9, 0);

    @Mock
    private LegRepository legRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private LegOverlapValidator legOverlapValidator;

    @Mock
    private AircraftRepository aircraftRepository;

    @InjectMocks
    private CreateLegUseCase useCase;

    private Airline airline;
    private Aircraft aircraft;

    @BeforeEach
    void setUp() {
        airline = Airline.createAirline("Avianca", 3, 45000);
        aircraft = Aircraft.createAircraft(AircraftModel.EMBRAER_E175);
    }

    @Test
    void shouldCreateLegAndSaveItLinkedToTheAirline() {
        // Arrange
        airline.addAircraft(aircraft.getId());
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.of(airline));
        when(aircraftRepository.findById(aircraft.getId())).thenReturn(Optional.of(aircraft));

        // Act
        Leg result = createLeg();

        // Assert
        assertEquals(List.of(result.getId()), airline.getLegIds());
        assertEquals(aircraft.getSeats().size(), result.getAvailableSeats());
        verify(legOverlapValidator).validateNoOverlap(aircraft.getId(), DEPARTURE, ARRIVAL);
        verify(legRepository).save(result);
        verify(airlineRepository).save(airline);
    }

    @Test
    void shouldThrowExceptionWhenAircraftBelongsToAnotherAirline() {
        // Arrange: the aircraft was never added to this airline.
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.of(airline));
        when(aircraftRepository.findById(aircraft.getId())).thenReturn(Optional.of(aircraft));

        // Act & Assert
        assertThrows(DomainRuleException.class, this::createLeg);

        verifyNoInteractions(legOverlapValidator, legRepository);
        verify(airlineRepository, never()).save(any());
        assertTrue(airline.getLegIds().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenLegOverlapsAnotherLegOfTheAircraft() {
        // Arrange
        airline.addAircraft(aircraft.getId());
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.of(airline));
        when(aircraftRepository.findById(aircraft.getId())).thenReturn(Optional.of(aircraft));
        doThrow(new DomainRuleException("The aircraft already has a leg at that time"))
                .when(legOverlapValidator).validateNoOverlap(aircraft.getId(), DEPARTURE, ARRIVAL);

        // Act & Assert
        assertThrows(DomainRuleException.class, this::createLeg);

        verifyNoInteractions(legRepository);
        verify(airlineRepository, never()).save(any());
        assertTrue(airline.getLegIds().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenAirlineDoesNotExist() {
        // Arrange
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, this::createLeg);

        verifyNoInteractions(aircraftRepository, legOverlapValidator, legRepository);
        verify(airlineRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAircraftDoesNotExist() {
        // Arrange
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.of(airline));
        when(aircraftRepository.findById(aircraft.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, this::createLeg);

        verifyNoInteractions(legOverlapValidator, legRepository);
        verify(airlineRepository, never()).save(any());
    }

    private Leg createLeg() {
        return useCase.execute(Airport.EL_DORADO, Airport.EL_EDEN, DEPARTURE, ARRIVAL,
                AIRLINE_ID, aircraft.getId(), 100, 500);
    }
}