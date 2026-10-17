package com.uniquindio.skyline.domain.repository;


import com.uniquindio.skyline.domain.entity.Flight;

import java.util.Optional;

public interface FlightRepository {
    Optional<Flight> findById(String id);
    void save( Flight flight);
}
