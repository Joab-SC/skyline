
package com.uniquindio.skyline.infrastructure.persistence;

import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FlightRepositoryInMemory implements FlightRepository {

    private final Map<String, Flight> flights = new HashMap<>();

    // Finds a flight by its id.
    @Override
    public Optional<Flight> findById(String id) {
        return Optional.ofNullable(flights.get(id));
    }

    // Stores or replaces a flight.
    @Override
    public void save(Flight flight) {
        flights.put(flight.getId(), flight);
    }
}
