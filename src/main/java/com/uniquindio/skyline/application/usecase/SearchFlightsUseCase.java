package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.repository.FlightSearchCache;
import com.uniquindio.skyline.domain.service.FlightSearcher;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.entity.Flight;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchFlightsUseCase {

    private final FlightSearcher flightSearcher;
    private final FlightSearchCache flightSearchCache;

    @Transactional(readOnly = true)
    public List<Flight> execute(String originCode, String destinationCode, LocalDate departureDay, int numberOfPassengers){
        Airport origin = findAirport(originCode);
        Airport destination = findAirport(destinationCode);
        List<Flight> flights = flightSearcher.searchFlights(origin, destination, departureDay, numberOfPassengers);
        flightSearchCache.saveAll(flights);
        return flights;
    }

    // An unknown code is bad input from the client (400), not a missing resource.
    private static Airport findAirport(String code) {
        return Airport.getByAcronym(code)
                .orElseThrow(() -> new IllegalArgumentException("Unknown airport code: " + code));
    }
}
