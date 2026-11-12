package com.uniquindio.skyline.infrastructure.persistence.jpa.converter;

import com.uniquindio.skyline.domain.valueObject.Airport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AirportConverterTest {

    private final AirportConverter converter = new AirportConverter();

    @Test
    void shouldStoreTheAirportAsItsIataCode() {
        // Act
        String column = converter.convertToDatabaseColumn(Airport.EL_DORADO);

        // Assert
        assertEquals("BOG", column);
    }

    @Test
    void shouldReadTheAirportFromItsIataCode() {
        // Act
        Airport airport = converter.convertToEntityAttribute("BOG");

        // Assert
        assertEquals(Airport.EL_DORADO, airport);
    }

    @Test
    void shouldKeepNullInBothDirections() {
        // Act & Assert
        assertNull(converter.convertToDatabaseColumn(null));
        assertNull(converter.convertToEntityAttribute(null));
    }

    @Test
    void shouldFailWhenTheStoredCodeIsUnknown() {
        // Act & Assert: the old format (constant name) is not a valid code.
        assertThrows(IllegalStateException.class, () -> converter.convertToEntityAttribute("EL_DORADO"));
    }
}
