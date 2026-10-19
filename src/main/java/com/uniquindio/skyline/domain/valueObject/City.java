
package com.uniquindio.skyline.domain.valueObject;

public enum City {

    // Colombia
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
    BARRANCABERMEJA("Barrancabermeja"),
    IBAGUE("Ibagué"),
    TUMACO("Tumaco"),
    POPAYAN("Popayán"),
    VILLAVICENCIO("Villavicencio"),
    QUIBDO("Quibdó"),
    JURADO("Juradó"),
    SAN_JOSE_DEL_GUAVIARE("San José del Guaviare"),
    PUERTO_ASIS("Puerto Asís"),
    TAME("Tame"),
    FLORENCIA("Florencia"),
    GIRARDOT("Girardot"),
    COROZAL("Corozal"),
    TOLU("Tolú"),

    // Estados Unidos y Canadá
    NEW_YORK("New York"),
    MIAMI("Miami"),
    LOS_ANGELES("Los Angeles"),
    ORLANDO("Orlando"),
    CHICAGO("Chicago"),
    TORONTO("Toronto"),
    NEWARK("Newark"),
    SAN_FRANCISCO("San Francisco"),
    ATLANTA("Atlanta"),
    DALLAS("Dallas"),
    VANCOUVER("Vancouver"),
    MONTREAL("Montreal"),

    // América Latina y el Caribe
    MEXICO_CITY("Mexico City"),
    PANAMA_CITY("Panama City"),
    PUNTA_CANA("Punta Cana"),
    LIMA("Lima"),
    QUITO("Quito"),
    SANTIAGO("Santiago"),
    BUENOS_AIRES("Buenos Aires"),
    SAO_PAULO("São Paulo"),
    RIO_DE_JANEIRO("Rio de Janeiro"),
    CANCUN("Cancún"),
    GUAYAQUIL("Guayaquil"),
    LA_PAZ("La Paz"),
    MONTEVIDEO("Montevideo"),
    ASUNCION("Asunción"),
    SANTO_DOMINGO("Santo Domingo"),
    SAN_JUAN("San Juan"),
    BRIDGETOWN("Bridgetown"),
    GEORGETOWN("Georgetown"),

    // Europa
    MADRID("Madrid"),
    BARCELONA("Barcelona"),
    PARIS("Paris"),
    LONDON("London"),
    ROME("Rome"),
    AMSTERDAM("Amsterdam"),
    FRANKFURT("Frankfurt"),
    LISBON("Lisbon"),
    ISTANBUL("Istanbul"),
    ZURICH("Zürich"),
    VIENNA("Vienna"),
    ATHENS("Athens"),
    COPENHAGEN("Copenhagen"),
    STOCKHOLM("Stockholm"),
    OSLO("Oslo"),
    BRUSSELS("Brussels"),
    PRAGUE("Prague"),
    WARSAW("Warsaw"),
    BUDAPEST("Budapest"),
    MUNICH("Munich"),

    // Medio Oriente y Asia
    DUBAI("Dubai"),
    DOHA("Doha"),
    TOKYO("Tokyo"),
    SEOUL("Seoul"),
    BEIJING("Beijing"),
    SINGAPORE("Singapore"),
    NARITA("Narita"),
    HONG_KONG("Hong Kong"),
    BANGKOK("Bangkok"),
    KUALA_LUMPUR("Kuala Lumpur"),
    NEW_DELHI("New Delhi"),
    MUMBAI("Mumbai"),
    JEDDAH("Jeddah"),
    TEL_AVIV("Tel Aviv"),

    // Oceanía
    SYDNEY("Sydney"),
    MELBOURNE("Melbourne"),
    AUCKLAND("Auckland"),
    BRISBANE("Brisbane"),
    PERTH("Perth");

    private final String displayName;

    City(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
