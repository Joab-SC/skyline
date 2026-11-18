package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAirlineUseCase {

    private final AirlineRepository airlineRepository;

    // Returns the airline with that id, for example to know its maximum luggage amount.
    @Transactional(readOnly = true)
    public Airline execute(String airlineId) {
        return airlineRepository.findById(airlineId).orElseThrow();
    }
}
