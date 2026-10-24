package com.uniquindio.skyline.domain.valueObject;

public enum Country {

    // América
    COLOMBIA("Colombia"),
    UNITED_STATES("United States"),
    CANADA("Canada"),
    MEXICO("Mexico"),
    PANAMA("Panama"),
    DOMINICAN_REPUBLIC("Dominican Republic"),
    PERU("Peru"),
    ECUADOR("Ecuador"),
    CHILE("Chile"),
    ARGENTINA("Argentina"),
    BRAZIL("Brazil"),
    BOLIVIA("Bolivia"),
    URUGUAY("Uruguay"),
    PARAGUAY("Paraguay"),
    PUERTO_RICO("Puerto Rico"),
    BARBADOS("Barbados"),
    GUYANA("Guyana"),

    // Europa
    SPAIN("Spain"),
    FRANCE("France"),
    UNITED_KINGDOM("United Kingdom"),
    ITALY("Italy"),
    NETHERLANDS("Netherlands"),
    GERMANY("Germany"),
    PORTUGAL("Portugal"),
    TURKEY("Turkey"),
    SWITZERLAND("Switzerland"),
    AUSTRIA("Austria"),
    GREECE("Greece"),
    DENMARK("Denmark"),
    SWEDEN("Sweden"),
    NORWAY("Norway"),
    BELGIUM("Belgium"),
    CZECH_REPUBLIC("Czech Republic"),
    POLAND("Poland"),
    HUNGARY("Hungary"),

    // Medio Oriente y Asia
    UNITED_ARAB_EMIRATES("United Arab Emirates"),
    QATAR("Qatar"),
    JAPAN("Japan"),
    SOUTH_KOREA("South Korea"),
    CHINA("China"),
    SINGAPORE("Singapore"),
    HONG_KONG("Hong Kong"),
    THAILAND("Thailand"),
    MALAYSIA("Malaysia"),
    INDIA("India"),
    SAUDI_ARABIA("Saudi Arabia"),
    ISRAEL("Israel"),

    // Oceanía
    AUSTRALIA("Australia"),
    NEW_ZEALAND("New Zealand");

    private final String displayName;

    Country(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
