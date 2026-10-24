package com.uniquindio.skyline.infrastructure.persistence;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AirlineAircraftCount;
import com.uniquindio.skyline.domain.repository.AirlineRepository;

import java.util.*;

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

    @Override
    public List<AirlineAircraftCount> findTopByAircraftCount(int limit) {
        return airlines.values().stream()
                .filter(a -> !a.getAircraftIds().isEmpty())
                .map(a -> new AirlineAircraftCount(a.getId(), a.getName(), (long) a.getAircraftIds().size()))
                .sorted(Comparator.comparing(AirlineAircraftCount::aircraftCount).reversed()
                        .thenComparing(AirlineAircraftCount::name))
                .limit(limit)
                .toList();
    }
}
