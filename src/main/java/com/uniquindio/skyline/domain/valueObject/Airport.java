
package com.uniquindio.skyline.domain.valueObject;

import com.uniquindio.skyline.domain.exception.DomainRuleException;

import java.util.Arrays;

public enum Airport {

    // Colombia
    EL_DORADO("El Dorado International Airport", "BOG", City.BOGOTA),
    JOSE_MARIA_CORDOVA("José María Córdova International Airport", "MDE", City.MEDELLIN),
    ALFONSO_BONILLA_ARAGON("Alfonso Bonilla Aragón International Airport", "CLO", City.CALI),
    RAFAEL_NUNEZ("Rafael Núñez International Airport", "CTG", City.CARTAGENA),
    ERNESTO_CORTISSOZ("Ernesto Cortissoz International Airport", "BAQ", City.BARRANQUILLA),
    SIMON_BOLIVAR("Simón Bolívar International Airport", "SMR", City.SANTA_MARTA),
    MATECANA("Matecaña International Airport", "PEI", City.PEREIRA),
    GUSTAVO_ROJAS_PINILLA("Gustavo Rojas Pinilla International Airport", "ADZ", City.SAN_ANDRES),
    PALONEGRO("Palonegro International Airport", "BGA", City.BUCARAMANGA),
    CAMILO_DAZA("Camilo Daza International Airport", "CUC", City.CUCUTA),
    LOS_GARZONES("Los Garzones Airport", "MTR", City.MONTERIA),
    ANTONIO_NARINO("Antonio Nariño Airport", "PSO", City.PASTO),
    EL_EDEN("El Edén International Airport", "AXM", City.ARMENIA),
    ALMIRANTE_PADILLA("Almirante Padilla Airport", "RCH", City.RIOHACHA),
    ALFONSO_LOPEZ_PUMAREJO("Alfonso López Pumarejo Airport", "VUP", City.VALLEDUPAR),
    BENITO_SALAS("Benito Salas Airport", "NVA", City.NEIVA),
    ALFREDO_VASQUEZ_COBO("Alfredo Vásquez Cobo International Airport", "LET", City.LETICIA),
    YARIGUIES("Yariguíes Airport", "EJA", City.BARRANCABERMEJA),
    PERALES("Perales Airport", "IBE", City.IBAGUE),
    LA_FLORIDA("La Florida Airport", "TCO", City.TUMACO),
    GUILLERMO_LEON_VALENCIA("Guillermo León Valencia Airport", "PPN", City.POPAYAN),
    VANGUARDIA("Vanguardia Airport", "VVC", City.VILLAVICENCIO),
    EL_CARAÑO("El Caraño Airport", "UIB", City.QUIBDO),
    JURADO("Juradó Airport", "JUO", City.JURADO),
    SAN_JOSE_DEL_GUAVIARE("San José del Guaviare Airport", "SJE", City.SAN_JOSE_DEL_GUAVIARE),
    TRES_DE_MAYO("Tres de Mayo Airport", "PUU", City.PUERTO_ASIS),
    CESAR_GAVIRIA_TRUJILLO("César Gaviria Trujillo Airport", "TME", City.TAME),
    GUSTAVO_ARTUNDUAGA("Gustavo Artunduaga Paredes Airport", "FLA", City.FLORENCIA),
    SANTIAGO_VILA("Santiago Vila Airport", "GIR", City.GIRARDOT),
    LAS_BRUJAS("Las Brujas Airport", "CZU", City.COROZAL),
    GOLFO_DE_MORROSQUILLO("Golfo de Morrosquillo Airport", "TLU", City.TOLU),

    // United States and Canada
    JFK("John F. Kennedy International Airport", "JFK", City.NEW_YORK),
    MIAMI_INTERNATIONAL("Miami International Airport", "MIA", City.MIAMI),
    LAX("Los Angeles International Airport", "LAX", City.LOS_ANGELES),
    ORLANDO_INTERNATIONAL("Orlando International Airport", "MCO", City.ORLANDO),
    O_HARE("O'Hare International Airport", "ORD", City.CHICAGO),
    TORONTO_PEARSON("Toronto Pearson International Airport", "YYZ", City.TORONTO),
    NEWARK_LIBERTY("Newark Liberty International Airport", "EWR", City.NEWARK),
    SAN_FRANCISCO_INTERNATIONAL("San Francisco International Airport", "SFO", City.SAN_FRANCISCO),
    HARTSFIELD_JACKSON("Hartsfield-Jackson Atlanta International Airport", "ATL", City.ATLANTA),
    DALLAS_FORT_WORTH("Dallas/Fort Worth International Airport", "DFW", City.DALLAS),
    VANCOUVER_INTERNATIONAL("Vancouver International Airport", "YVR", City.VANCOUVER),
    MONTREAL_TRUDEAU("Montréal-Trudeau International Airport", "YUL", City.MONTREAL),

    // Latin America and the Caribbean
    MEXICO_CITY_INTERNATIONAL("Mexico City International Airport", "MEX", City.MEXICO_CITY),
    TOCUMEN("Tocumen International Airport", "PTY", City.PANAMA_CITY),
    PUNTA_CANA_INTERNATIONAL("Punta Cana International Airport", "PUJ", City.PUNTA_CANA),
    JORGE_CHAVEZ("Jorge Chávez International Airport", "LIM", City.LIMA),
    MARISCAL_SUCRE("Mariscal Sucre International Airport", "UIO", City.QUITO),
    ARTURO_MERINO_BENITEZ("Arturo Merino Benítez International Airport", "SCL", City.SANTIAGO),
    EZEIZA("Ministro Pistarini International Airport", "EZE", City.BUENOS_AIRES),
    GUARULHOS("São Paulo/Guarulhos International Airport", "GRU", City.SAO_PAULO),
    GALEAO("Rio de Janeiro/Galeão International Airport", "GIG", City.RIO_DE_JANEIRO),
    CANCUN("Cancún International Airport", "CUN", City.CANCUN),
    JOSE_JOAQUIN_DE_OLMEDO("José Joaquín de Olmedo International Airport", "GYE", City.GUAYAQUIL),
    EL_ALTO("El Alto International Airport", "LPB", City.LA_PAZ),
    CARRASCO("Carrasco International Airport", "MVD", City.MONTEVIDEO),
    SILVIO_PETTIROSSI("Silvio Pettirossi International Airport", "ASU", City.ASUNCION),
    LAS_AMERICAS("Las Américas International Airport", "SDQ", City.SANTO_DOMINGO),
    LUIS_MUNOZ_MARIN("Luis Muñoz Marín International Airport", "SJU", City.SAN_JUAN),
    GRANTLEY_ADAMS("Grantley Adams International Airport", "BGI", City.BRIDGETOWN),
    CHEddi_JAGAN("Cheddi Jagan International Airport", "GEO", City.GEORGETOWN),

    // Europe
    ADOLFO_SUAREZ_MADRID("Adolfo Suárez Madrid-Barajas Airport", "MAD", City.MADRID),
    BARCELONA_EL_PRAT("Barcelona-El Prat Airport", "BCN", City.BARCELONA),
    CHARLES_DE_GAULLE("Charles de Gaulle Airport", "CDG", City.PARIS),
    HEATHROW("Heathrow Airport", "LHR", City.LONDON),
    LEONARDO_DA_VINCI("Leonardo da Vinci–Fiumicino Airport", "FCO", City.ROME),
    SCHIPHOL("Amsterdam Airport Schiphol", "AMS", City.AMSTERDAM),
    FRANKFURT_MAIN("Frankfurt Airport", "FRA", City.FRANKFURT),
    HUMBERTO_DELGADO("Humberto Delgado Airport", "LIS", City.LISBON),
    ISTANBUL_AIRPORT("Istanbul Airport", "IST", City.ISTANBUL),
    ZURICH("Zurich Airport", "ZRH", City.ZURICH),
    VIENNA("Vienna International Airport", "VIE", City.VIENNA),
    ATHENS("Athens International Airport", "ATH", City.ATHENS),
    COPENHAGEN("Copenhagen Airport", "CPH", City.COPENHAGEN),
    STOCKHOLM_ARLANDA("Stockholm Arlanda Airport", "ARN", City.STOCKHOLM),
    OSLO_GARDERMOEN("Oslo Airport", "OSL", City.OSLO),
    BRUSSELS("Brussels Airport", "BRU", City.BRUSSELS),
    PRAGUE("Václav Havel Airport Prague", "PRG", City.PRAGUE),
    WARSAW_CHOPIN("Warsaw Chopin Airport", "WAW", City.WARSAW),
    BUDAPEST("Budapest Ferenc Liszt International Airport", "BUD", City.BUDAPEST),
    MUNICH("Munich Airport", "MUC", City.MUNICH),

    // Middle East and Asia
    DUBAI_INTERNATIONAL("Dubai International Airport", "DXB", City.DUBAI),
    HAMAD("Hamad International Airport", "DOH", City.DOHA),
    TOKYO_HANEDA("Tokyo Haneda Airport", "HND", City.TOKYO),
    INCHEON("Incheon International Airport", "ICN", City.SEOUL),
    BEIJING_CAPITAL("Beijing Capital International Airport", "PEK", City.BEIJING),
    CHANGI("Singapore Changi Airport", "SIN", City.SINGAPORE),
    NARITA("Narita International Airport", "NRT", City.NARITA),
    HONG_KONG("Hong Kong International Airport", "HKG", City.HONG_KONG),
    BANGKOK_SUVARNABHUMI("Suvarnabhumi Airport", "BKK", City.BANGKOK),
    KUALA_LUMPUR("Kuala Lumpur International Airport", "KUL", City.KUALA_LUMPUR),
    INDIRA_GANDHI("Indira Gandhi International Airport", "DEL", City.NEW_DELHI),
    CHHATRAPATI_SHIVAJI("Chhatrapati Shivaji Maharaj International Airport", "BOM", City.MUMBAI),
    KING_ABDULAZIZ("King Abdulaziz International Airport", "JED", City.JEDDAH),
    BEN_GURION("Ben Gurion Airport", "TLV", City.TEL_AVIV),

    // Oceania
    SYDNEY_KINGSFORD_SMITH("Sydney Kingsford Smith Airport", "SYD", City.SYDNEY),
    MELBOURNE_TULLAMARINE("Melbourne Airport", "MEL", City.MELBOURNE),
    AUCKLAND("Auckland Airport", "AKL", City.AUCKLAND),
    BRISBANE("Brisbane Airport", "BNE", City.BRISBANE),
    PERTH("Perth Airport", "PER", City.PERTH);

    private final String name;
    private final String code;
    private final City city;

    Airport(String name, String code, City city) {
        this.name = name;
        this.code = code;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public City getCity() {
        return city;
    }

    public static Airport getByAcronym(String acronym) {
        return Arrays.stream(values())
                .filter(airport -> airport.getCode().equalsIgnoreCase(acronym))
                .findFirst()
                .orElseThrow(() -> new DomainRuleException(
                        "There is no airport with the code: " + acronym));
    }
}
