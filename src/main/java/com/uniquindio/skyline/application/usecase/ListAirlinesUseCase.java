package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListAirlinesUseCase {

    private final AirlineRepository airlineRepository;

    // Returns every airline, ordered by name. The list can be empty.
    @Transactional(readOnly = true)
    public List<Airline> execute() {
        return airlineRepository.findAll();
    }
}