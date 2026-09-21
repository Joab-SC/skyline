package com.uniquindio.skyline.domain.repository;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.Leg;

import java.util.Optional;

public interface AirlineRepository {
    Optional<Airline> findById(String id);
    void save(Airline airline);
}
