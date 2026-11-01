package com.uniquindio.skyline.infrastructure.persistence;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.repository.AirlineAircraftCount;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.PageResult;

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

    @Override
    public PageResult<ExtraService> searchExtraServices(String text, String airlineId, int page, int size) {
        String pattern = (text == null) ? "" : text.trim().toLowerCase();
        List<ExtraService> matches = airlines.values().stream()
                .filter(a -> airlineId == null || a.getId().equals(airlineId))
                .flatMap(a -> a.getActiveExtraServices().stream())
                .filter(e -> e.getNameService().toLowerCase().contains(pattern)
                        || e.getDescription().map(d -> d.toLowerCase().contains(pattern)).orElse(false))
                .sorted(Comparator.comparing(ExtraService::getNameService).thenComparing(ExtraService::getId))
                .toList();
        List<ExtraService> content = matches.stream().skip((long) page * size).limit(size).toList();
        int totalPages = (int) Math.ceil((double) matches.size() / size);
        return new PageResult<>(content, matches.size(), totalPages, page, size);
    }

    // Returns every airline, ordered by name.
    @Override
    public List<Airline> findAll() {
        return airlines.values().stream()
                .sorted(Comparator.comparing(Airline::getName))
                .toList();
    }
}
