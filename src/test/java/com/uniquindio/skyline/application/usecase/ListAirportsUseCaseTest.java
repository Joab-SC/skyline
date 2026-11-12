package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.valueObject.Airport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListAirportsUseCaseTest {

    private final ListAirportsUseCase useCase = new ListAirportsUseCase();

    @Test
    void shouldReturnEveryAirport() {
        // Act
        List<Airport> result = useCase.execute();

        // Assert
        assertEquals(List.of(Airport.values()), result);
    }
}
