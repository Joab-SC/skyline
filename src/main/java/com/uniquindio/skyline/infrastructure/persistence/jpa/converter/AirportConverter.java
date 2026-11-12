package com.uniquindio.skyline.infrastructure.persistence.jpa.converter;

import com.uniquindio.skyline.domain.valueObject.Airport;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// Stores an Airport as its IATA code ("BOG"), not as the enum constant name,
// so renaming a constant does not break the stored rows.
// autoApply: Hibernate uses it on every Airport field of every entity; nobody calls it by hand.
@Converter(autoApply = true)
public class AirportConverter implements AttributeConverter<Airport, String> {

    @Override
    public String convertToDatabaseColumn(Airport airport) {
        return airport == null ? null : airport.getCode();
    }

    @Override
    public Airport convertToEntityAttribute(String code) {
        return code == null ? null : Airport.getByAcronym(code)
                .orElseThrow(() -> new IllegalStateException("Unknown airport code in the database: " + code));
    }
}
