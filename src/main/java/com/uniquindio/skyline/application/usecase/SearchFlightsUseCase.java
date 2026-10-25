package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.repository.FlightSearchCache;
import com.uniquindio.skyline.domain.service.FlightSearcher;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.entity.Flight;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchFlightsUseCase {

    private final FlightSearcher flightSearcher;
    private final FlightSearchCache flightSearchCache;

    public List<Flight> execute(String originCode, String destinationCode, LocalDate departureDay, int numberOfPassengers){
        Airport origin = Airport.getByAcronym(originCode).orElseThrow();
        Airport destination = Airport.getByAcronym(destinationCode).orElseThrow();
        List<Flight> flights = flightSearcher.searchFlights(origin, destination, departureDay, numberOfPassengers);
        flightSearchCache.saveAll(flights);
        return flights;
    }
}
