package com.uniquindio.skyline.infrastructure.persistence;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.repository.LegRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LegRepositoryInMemory implements LegRepository {

    private final Map<String, Leg> legs = new HashMap<>();

    @Override
    public Optional<Leg> findById(String id) {
        return Optional.ofNullable(legs.get(id));
    }

    @Override
    public void save(Leg leg) {
        legs.put(leg.getId(), leg);
    }
}
