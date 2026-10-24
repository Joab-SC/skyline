
package com.uniquindio.skyline.domain.valueObject;

public enum City {

    // Colombia
    BOGOTA("Bogotá", Country.COLOMBIA),
    MEDELLIN("Medellín", Country.COLOMBIA),
    CALI("Cali", Country.COLOMBIA),
    CARTAGENA("Cartagena", Country.COLOMBIA),
    BARRANQUILLA("Barranquilla", Country.COLOMBIA),
    SANTA_MARTA("Santa Marta", Country.COLOMBIA),
    PEREIRA("Pereira", Country.COLOMBIA),
    SAN_ANDRES("San Andrés", Country.COLOMBIA),
    BUCARAMANGA("Bucaramanga", Country.COLOMBIA),
    CUCUTA("Cúcuta", Country.COLOMBIA),
    MONTERIA("Montería", Country.COLOMBIA),
    PASTO("Pasto", Country.COLOMBIA),
    ARMENIA("Armenia", Country.COLOMBIA),
    RIOHACHA("Riohacha", Country.COLOMBIA),
    VALLEDUPAR("Valledupar", Country.COLOMBIA),
    NEIVA("Neiva", Country.COLOMBIA),
    LETICIA("Leticia", Country.COLOMBIA),
    BARRANCABERMEJA("Barrancabermeja", Country.COLOMBIA),
    IBAGUE("Ibagué", Country.COLOMBIA),
    TUMACO("Tumaco", Country.COLOMBIA),
    POPAYAN("Popayán", Country.COLOMBIA),
    VILLAVICENCIO("Villavicencio", Country.COLOMBIA),
    QUIBDO("Quibdó", Country.COLOMBIA),
    JURADO("Juradó", Country.COLOMBIA),
    SAN_JOSE_DEL_GUAVIARE("San José del Guaviare", Country.COLOMBIA),
    PUERTO_ASIS("Puerto Asís", Country.COLOMBIA),
    TAME("Tame", Country.COLOMBIA),
    FLORENCIA("Florencia", Country.COLOMBIA),
    GIRARDOT("Girardot", Country.COLOMBIA),
    COROZAL("Corozal", Country.COLOMBIA),
    TOLU("Tolú", Country.COLOMBIA),

    // Estados Unidos y Canadá
    NEW_YORK("New York", Country.UNITED_STATES),
    MIAMI("Miami", Country.UNITED_STATES),
    LOS_ANGELES("Los Angeles", Country.UNITED_STATES),
    ORLANDO("Orlando", Country.UNITED_STATES),
    CHICAGO("Chicago", Country.UNITED_STATES),
    TORONTO("Toronto", Country.CANADA),
    NEWARK("Newark", Country.UNITED_STATES),
    SAN_FRANCISCO("San Francisco", Country.UNITED_STATES),
    ATLANTA("Atlanta", Country.UNITED_STATES),
    DALLAS("Dallas", Country.UNITED_STATES),
    VANCOUVER("Vancouver", Country.CANADA),
    MONTREAL("Montreal", Country.CANADA),

    // América Latina y el Caribe
    MEXICO_CITY("Mexico City", Country.MEXICO),
    PANAMA_CITY("Panama City", Country.PANAMA),
    PUNTA_CANA("Punta Cana", Country.DOMINICAN_REPUBLIC),
    LIMA("Lima", Country.PERU),
    QUITO("Quito", Country.ECUADOR),
    SANTIAGO("Santiago", Country.CHILE),
    BUENOS_AIRES("Buenos Aires", Country.ARGENTINA),
    SAO_PAULO("São Paulo", Country.BRAZIL),
    RIO_DE_JANEIRO("Rio de Janeiro", Country.BRAZIL),
    CANCUN("Cancún", Country.MEXICO),
    GUAYAQUIL("Guayaquil", Country.ECUADOR),
    LA_PAZ("La Paz", Country.BOLIVIA),
    MONTEVIDEO("Montevideo", Country.URUGUAY),
    ASUNCION("Asunción", Country.PARAGUAY),
    SANTO_DOMINGO("Santo Domingo", Country.DOMINICAN_REPUBLIC),
    SAN_JUAN("San Juan", Country.PUERTO_RICO),
    BRIDGETOWN("Bridgetown", Country.BARBADOS),
    GEORGETOWN("Georgetown", Country.GUYANA),

    // Europa
    MADRID("Madrid", Country.SPAIN),
    BARCELONA("Barcelona", Country.SPAIN),
    PARIS("Paris", Country.FRANCE),
    LONDON("London", Country.UNITED_KINGDOM),
    ROME("Rome", Country.ITALY),
    AMSTERDAM("Amsterdam", Country.NETHERLANDS),
    FRANKFURT("Frankfurt", Country.GERMANY),
    LISBON("Lisbon", Country.PORTUGAL),
    ISTANBUL("Istanbul", Country.TURKEY),
    ZURICH("Zürich", Country.SWITZERLAND),
    VIENNA("Vienna", Country.AUSTRIA),
    ATHENS("Athens", Country.GREECE),
    COPENHAGEN("Copenhagen", Country.DENMARK),
    STOCKHOLM("Stockholm", Country.SWEDEN),
    OSLO("Oslo", Country.NORWAY),
    BRUSSELS("Brussels", Country.BELGIUM),
    PRAGUE("Prague", Country.CZECH_REPUBLIC),
    WARSAW("Warsaw", Country.POLAND),
    BUDAPEST("Budapest", Country.HUNGARY),
    MUNICH("Munich", Country.GERMANY),

    // Medio Oriente y Asia
    DUBAI("Dubai", Country.UNITED_ARAB_EMIRATES),
    DOHA("Doha", Country.QATAR),
    TOKYO("Tokyo", Country.JAPAN),
    SEOUL("Seoul", Country.SOUTH_KOREA),
    BEIJING("Beijing", Country.CHINA),
    SINGAPORE("Singapore", Country.SINGAPORE),
    NARITA("Narita", Country.JAPAN),
    HONG_KONG("Hong Kong", Country.HONG_KONG),
    BANGKOK("Bangkok", Country.THAILAND),
    KUALA_LUMPUR("Kuala Lumpur", Country.MALAYSIA),
    NEW_DELHI("New Delhi", Country.INDIA),
    MUMBAI("Mumbai", Country.INDIA),
    JEDDAH("Jeddah", Country.SAUDI_ARABIA),
    TEL_AVIV("Tel Aviv", Country.ISRAEL),

    // Oceanía
    SYDNEY("Sydney", Country.AUSTRALIA),
    MELBOURNE("Melbourne", Country.AUSTRALIA),
    AUCKLAND("Auckland", Country.NEW_ZEALAND),
    BRISBANE("Brisbane", Country.AUSTRALIA),
    PERTH("Perth", Country.AUSTRALIA);

    private final String displayName;
    private final Country country;

    City(String displayName, Country country) {
        this.displayName = displayName;
        this.country = country;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Country getCountry() {
        return country;
    }
}
