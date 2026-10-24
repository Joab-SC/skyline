package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.repository.AircraftRepository;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.AircraftPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AircraftRepositoryJpa implements AircraftRepository {

    private final AircraftJpaDataRepository dataRepository;
    private final AircraftPersistenceMapper mapper;

    @Override
    public Optional<Aircraft> findById(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void save(Aircraft aircraft) {
        dataRepository.save(mapper.toEntity(aircraft));
    }
}
