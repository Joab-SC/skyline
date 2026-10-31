package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteExtraServiceUseCase {

    private final AirlineRepository airlineRepository;

    // Soft delete: the service is marked as deleted and stays in the airline, so old bookings keep pointing to it.
    @Transactional
    public void execute(String airlineId, String extraServiceId) {
        Airline airline = airlineRepository.findById(airlineId).orElseThrow();
        airline.deleteExtraService(extraServiceId);
        airlineRepository.save(airline);
    }
}