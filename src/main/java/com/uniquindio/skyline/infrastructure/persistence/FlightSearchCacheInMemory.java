package com.uniquindio.skyline.infrastructure.persistence;

import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.repository.FlightSearchCache;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class FlightSearchCacheInMemory implements FlightSearchCache {

    private final Map<String, Flight> flights = new HashMap<>();

    // Stores the flights returned by a search.
    @Override
    public void saveAll(List<Flight> flightsToSave) {
        for (Flight flight : flightsToSave) {
            flights.put(flight.getId(), flight);
        }
    }

    // Finds a searched flight by its id.
    @Override
    public Optional<Flight> findById(String id) {
        return Optional.ofNullable(flights.get(id));
    }
}
