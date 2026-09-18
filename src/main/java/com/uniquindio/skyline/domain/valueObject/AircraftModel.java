package com.uniquindio.skyline.domain.valueObject;

public enum AircraftModel {
    AIRBUS_A319_NEO(160),
    AIRBUS_A320_NEO(194),
    AIRBUS_A321_NEO(244),
    BOEING_737_8_MAX(210),
    BOEING_737_9_MAX(220),
    EMBRAER_E175(88);

    private final int maxSeats;

    AircraftModel(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    public int getMaxSeats() {
        return maxSeats;
    }
}
