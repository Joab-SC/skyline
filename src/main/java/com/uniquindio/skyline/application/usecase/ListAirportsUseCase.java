package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.valueObject.Airport;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListAirportsUseCase {

    // Returns every airport the platform flies to, so the client can choose an origin and a destination.
    // Airports are a fixed catalog in the code (enum), so no repository is needed.
    public List<Airport> execute() {
        return List.of(Airport.values());
    }
}
