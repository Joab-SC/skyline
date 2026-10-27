package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.repository.LegRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetLegSeatMapUseCase {

    private final LegRepository legRepository;

    // Returns the leg with its seats so the client can show the seat map.
    public Leg execute(String legId) {
        return legRepository.findById(legId).orElseThrow();
    }
}