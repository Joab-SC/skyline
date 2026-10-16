package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.service.FlightSearcher;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.entity.Flight;

import java.time.LocalDate;
import java.util.List;

public class SearchFlightsUseCase {

    private final FlightSearcher flightSearcher;

    public SearchFlightsUseCase(FlightSearcher flightSearcher) {
        this.flightSearcher = flightSearcher;
    }

    public List<Flight> execute(String originCode, String destinationCode, LocalDate departureDay, int numberOfPassengers){
        Airport origin = Airport.fromAcronym(originCode);
        Airport destination = Airport.fromAcronym(destinationCode);
        return flightSearcher.searchFlights(origin, destination, departureDay, numberOfPassengers);
    }
}
