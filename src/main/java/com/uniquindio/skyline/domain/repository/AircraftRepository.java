package com.uniquindio.skyline.domain.repository;

import com.uniquindio.skyline.domain.entity.Aircraft;
import java.util.Optional;

public interface AircraftRepository {
    Optional<Aircraft> findById(String id);
    void save(Aircraft aircraft);
}
