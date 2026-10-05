package com.uniquindio.skyline.domain.repository;

import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.entity.Leg;

import java.util.List;
import java.util.Optional;

public interface AircraftRepository {
    Optional<Aircraft> findById(String id);
    void save(Aircraft aircraft);
}
