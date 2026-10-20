package com.uniquindio.skyline.infrastructure.persistence;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AirlineRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AirlineRepositoryInMemory implements AirlineRepository {

    private final Map<String, Airline> airlines = new HashMap<>();

    // Finds an airline by its id.
    @Override
    public Optional<Airline> findById(String id) {
        return Optional.ofNullable(airlines.get(id));
    }

    // Stores or replaces an airline.
    @Override
    public void save(Airline airline) {
        airlines.put(airline.getId(), airline);
    }
}
