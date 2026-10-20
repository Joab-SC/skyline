package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.repository.FlightSearchCache;
import com.uniquindio.skyline.domain.service.FlightSearcher;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.entity.Flight;

import java.time.LocalDate;
import java.util.List;

public class SearchFlightsUseCase {

    private final FlightSearcher flightSearcher;
    private final FlightSearchCache flightSearchCache;

    public SearchFlightsUseCase(FlightSearcher flightSearcher, FlightSearchCache flightSearchCache) {
        this.flightSearcher = flightSearcher;
        this.flightSearchCache = flightSearchCache;
    }

    public List<Flight> execute(String originCode, String destinationCode, LocalDate departureDay, int numberOfPassengers){
        Airport origin = Airport.getByAcronym(originCode);
        Airport destination = Airport.getByAcronym(destinationCode);
        List<Flight> flights = flightSearcher.searchFlights(origin, destination, departureDay, numberOfPassengers);
        flightSearchCache.saveAll(flights);
        return flights;
    }
}
