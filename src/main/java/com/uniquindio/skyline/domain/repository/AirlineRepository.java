package com.uniquindio.skyline.domain.repository;

import com.uniquindio.skyline.domain.entity.Airline;

import java.util.List;
import java.util.Optional;

public interface AirlineRepository {
    Optional<Airline> findById(String id);
    List<Airline> findAll();
    void save(Airline airline);
    List<AirlineAircraftCount> findTopByAircraftCount(int limit);

}
