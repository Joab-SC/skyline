package com.uniquindio.skyline.infrastructure.persistence;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.repository.LegRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LegRepositoryInMemory implements LegRepository {

    private final Map<String, Leg> legs = new HashMap<>();

    // Finds a leg by its id.
    @Override
    public Optional<Leg> findById(String id) {
        return Optional.ofNullable(legs.get(id));
    }

    // Stores or replaces a leg.
    @Override
    public void save(Leg leg) {
        legs.put(leg.getId(), leg);
    }

    @Override
    public List<Leg> findLegsByAircraft(String idAircraft) {
        return legs.values().stream()
                .filter(leg -> leg.getAircraftId().equals(idAircraft))
                .toList();
    }
}
