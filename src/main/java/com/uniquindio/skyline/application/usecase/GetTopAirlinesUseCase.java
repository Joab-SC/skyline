package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AirlineAircraftCount;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTopAirlinesUseCase {

    private static final int MAX_RANKING = 20;

    private final AirlineRepository airlineRepository;

    // Returns the airlines with the most aircraft, most first. Airlines without aircraft are not included.
    @Transactional(readOnly = true)
    public List<AirlineAircraftCount> execute(int limit) {
        if (limit < 1 || limit > MAX_RANKING) {
            throw new IllegalArgumentException("The limit must be between 1 and " + MAX_RANKING);
        }
        return airlineRepository.findTopByAircraftCount(limit);
    }
}