package com.uniquindio.skyline.infrastructure.rest.mapper;

import com.uniquindio.skyline.application.dto.response.AirportResponse;
import com.uniquindio.skyline.domain.valueObject.Airport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AirportMapperTest {

    private final AirportMapper mapper = new AirportMapper();

    @Test
    void shouldMapAirportUsingItsIataCode() {
        // Act
        AirportResponse response = mapper.toAirportResponse(Airport.EL_DORADO);

        // Assert
        assertEquals(new AirportResponse("BOG", "El Dorado International Airport", "Bogotá", "Colombia"), response);
    }
}
