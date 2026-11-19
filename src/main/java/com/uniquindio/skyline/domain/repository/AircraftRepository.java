package com.uniquindio.skyline.domain.repository;

import com.uniquindio.skyline.domain.entity.Aircraft;
import java.util.List;
import java.util.Optional;

public interface AircraftRepository {
    Optional<Aircraft> findById(String id);
    void save(Aircraft aircraft);
    // The aircraft whose id is in the list. An id that does not exist is skipped.
    List<Aircraft> findAllById(List<String> ids);
}
