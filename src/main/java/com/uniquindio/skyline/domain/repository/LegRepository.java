package com.uniquindio.skyline.domain.repository;

import com.uniquindio.skyline.domain.entity.Leg;

import java.util.Optional;

public interface LegRepository {
    Optional<Leg> findById(String id);
    void save(Leg leg);
}
