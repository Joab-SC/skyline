package com.uniquindio.skyline.domain.valueObject;

import lombok.Getter;

import java.util.List;

@Getter
public enum AircraftModel {

    AIRBUS_A319_NEO(160, List.of("A", "B", "C", "D", "E", "F")),
    AIRBUS_A320_NEO(194, List.of("A", "B", "C", "D", "E", "F")),
    AIRBUS_A321_NEO(244, List.of("A", "B", "C", "D", "E", "F")),

    BOEING_737_8_MAX(210, List.of("A", "B", "C", "D", "E", "F")),
    BOEING_737_9_MAX(220, List.of("A", "B", "C", "D", "E", "F")),

    EMBRAER_E175(88, List.of("A", "B", "C", "D"));

    private final int maxSeats;
    private final List<String> seatColumns;

    AircraftModel(int maxSeats, List<String> seatColumns) {
        this.maxSeats = maxSeats;
        this.seatColumns = seatColumns;
    }

}
