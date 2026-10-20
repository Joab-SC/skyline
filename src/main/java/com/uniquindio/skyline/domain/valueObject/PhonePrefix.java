package com.uniquindio.skyline.domain.valueObject;

public enum PhonePrefix {
    COLOMBIA("+57"),
    UNITED_STATES("+1"),
    CANADA("+1"),
    MEXICO("+52"),
    ARGENTINA("+54"),
    BRAZIL("+55"),
    CHILE("+56"),
    PERU("+51"),
    ECUADOR("+593"),
    VENEZUELA("+58"),
    SPAIN("+34"),
    UNITED_KINGDOM("+44"),
    FRANCE("+33"),
    GERMANY("+49"),
    ITALY("+39"),
    PORTUGAL("+351"),
    CHINA("+86"),
    JAPAN("+81"),
    SOUTH_KOREA("+82"),
    AUSTRALIA("+61");

    private final String prefix;

    PhonePrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}