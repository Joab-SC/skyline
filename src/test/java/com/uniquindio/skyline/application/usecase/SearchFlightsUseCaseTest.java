package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.repository.FlightSearchCache;
import com.uniquindio.skyline.domain.service.FlightSearcher;
import com.uniquindio.skyline.domain.valueObject.Airport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchFlightsUseCaseTest {

    private static final LocalDate DEPARTURE_DAY = LocalDate.of(2026, 12, 20);

    @Mock
    private FlightSearcher flightSearcher;

    @Mock
    private FlightSearchCache flightSearchCache;


    @InjectMocks
    private SearchFlightsUseCase useCase;

    @Test
    void shouldReturnFlightsFoundBySearcherWhenCodesAreValid() {

        Airport origin = Airport.EL_DORADO;
        Airport destination = Airport.JOSE_MARIA_CORDOVA;

        List<Flight> expectedFlights = List.of(mock(Flight.class), mock(Flight.class));

        try (MockedStatic<Airport> airportMock = mockStatic(Airport.class)) {
            airportMock.when(() -> Airport.getByAcronym("BOG")).thenReturn(Optional.of(origin));
            airportMock.when(() -> Airport.getByAcronym("MDE")).thenReturn(Optional.of(destination));
            when(flightSearcher.searchFlights(origin, destination, DEPARTURE_DAY, 2)).thenReturn(expectedFlights);

            List<Flight> flights = useCase.execute("BOG", "MDE", DEPARTURE_DAY, 2);

            assertEquals(flights, expectedFlights);
            airportMock.verify(() -> Airport.getByAcronym("BOG"));
            airportMock.verify(() -> Airport.getByAcronym("MDE"));
            verify(flightSearcher).searchFlights(origin, destination, DEPARTURE_DAY, 2);
        }
    }

    @Test
    void shouldSaveFoundFlightsInSearchCache() {
        // Arrange
        Airport origin = Airport.EL_DORADO;
        Airport destination = Airport.JOSE_MARIA_CORDOVA;
        List<Flight> expectedFlights = List.of(mock(Flight.class), mock(Flight.class));
        when(flightSearcher.searchFlights(origin, destination, DEPARTURE_DAY, 2)).thenReturn(expectedFlights);

        // Act
        useCase.execute("BOG", "MDE", DEPARTURE_DAY, 2);

        // Assert
        verify(flightSearchCache).saveAll(expectedFlights);
    }
}