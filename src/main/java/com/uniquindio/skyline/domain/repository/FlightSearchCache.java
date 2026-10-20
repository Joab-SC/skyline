package com.uniquindio.skyline.domain.repository;

import com.uniquindio.skyline.domain.entity.Flight;

import java.util.List;
import java.util.Optional;

// Temporary store for the flights returned by a search, so a selection can find them by id.
// Separate from FlightRepository: cached flights are not persisted until they are selected.
public interface FlightSearchCache {
    void saveAll(List<Flight> flights);
    Optional<Flight> findById(String id);
}
