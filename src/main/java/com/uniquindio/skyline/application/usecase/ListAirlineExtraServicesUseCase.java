package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListAirlineExtraServicesUseCase {

    private final AirlineRepository airlineRepository;

    // Returns the extra services of the airline. The list can be empty.
    @Transactional(readOnly = true)
    public List<ExtraService> execute(String airlineId) {
        Airline airline = airlineRepository.findById(airlineId).orElseThrow();
        return airline.getExtraServices();
    }
}
