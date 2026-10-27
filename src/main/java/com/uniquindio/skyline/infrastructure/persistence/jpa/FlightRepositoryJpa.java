package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.FlightPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FlightRepositoryJpa implements FlightRepository {
    private final FlightJpaDataRepository dataRepository;
    private final FlightPersistenceMapper mapper;

    @Override
    public Optional<Flight> findById(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void save(Flight flight) {
        dataRepository.save(mapper.toEntity(flight));
    }
}
