package com.uniquindio.skyline.domain.valueObject;

public enum City {

    BOGOTA("Bogotá"),
    MEDELLIN("Medellín"),
    CALI("Cali"),
    CARTAGENA("Cartagena"),
    BARRANQUILLA("Barranquilla"),
    SANTA_MARTA("Santa Marta"),
    PEREIRA("Pereira"),
    SAN_ANDRES("San Andrés"),
    BUCARAMANGA("Bucaramanga"),
    CUCUTA("Cúcuta"),
    MONTERIA("Montería"),
    PASTO("Pasto"),
    ARMENIA("Armenia"),
    RIOHACHA("Riohacha"),
    VALLEDUPAR("Valledupar"),
    NEIVA("Neiva"),
    LETICIA("Leticia"),

    NEW_YORK("New York"),
    MIAMI("Miami"),
    LOS_ANGELES("Los Angeles"),
    ORLANDO("Orlando"),
    CHICAGO("Chicago"),
    TORONTO("Toronto"),
    MEXICO_CITY("Mexico City"),
    PANAMA_CITY("Panama City"),
    PUNTA_CANA("Punta Cana"),
    LIMA("Lima"),
    QUITO("Quito"),
    SANTIAGO("Santiago"),
    BUENOS_AIRES("Buenos Aires"),
    SAO_PAULO("São Paulo"),
    RIO_DE_JANEIRO("Rio de Janeiro"),
    MADRID("Madrid"),
    BARCELONA("Barcelona"),
    PARIS("Paris"),
    LONDON("London"),
    ROME("Rome"),
    AMSTERDAM("Amsterdam"),
    FRANKFURT("Frankfurt"),
    LISBON("Lisbon"),
    ISTANBUL("Istanbul"),
    DUBAI("Dubai"),
    DOHA("Doha"),
    TOKYO("Tokyo"),
    SEOUL("Seoul"),
    BEIJING("Beijing"),
    SINGAPORE("Singapore"),
    SYDNEY("Sydney"),
    MELBOURNE("Melbourne");

    private final String displayName;

    City(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
