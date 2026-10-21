package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAirlineUseCaseTest {

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private Airline airline;

    @InjectMocks
    private CreateAirlineUseCase useCase;

    @Test
    void shouldCreateAirlineAndSaveIt() {
        try (MockedStatic<Airline> airlineMock = mockStatic(Airline.class)) {
            // Arrange
            airlineMock.when(() -> Airline.createAirline("Skyline Air", 3, 100.0)).thenReturn(airline);

            // Act
            Airline result = useCase.execute("Skyline Air", 3, 100.0);

            // Assert
            assertSame(airline, result);
            airlineMock.verify(() -> Airline.createAirline("Skyline Air", 3, 100.0));
            verify(airlineRepository).save(airline);
        }
    }
}
