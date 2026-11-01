package com.uniquindio.skyline.domain.repository;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.ExtraService;

import java.util.List;
import java.util.Optional;

public interface AirlineRepository {
    Optional<Airline> findById(String id);
    List<Airline> findAll();
    void save(Airline airline);
    List<AirlineAircraftCount> findTopByAircraftCount(int limit);
    // Extra services that are not deleted, whose name or description contains the text, ordered by name.
    // airlineId is optional: null searches every airline.
    PageResult<ExtraService> searchExtraServices(String text, String airlineId, int page, int size);
}
