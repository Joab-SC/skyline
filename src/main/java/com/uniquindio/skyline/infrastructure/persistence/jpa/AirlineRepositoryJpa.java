package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.repository.AirlineAircraftCount;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.AirlinePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AirlineRepositoryJpa implements AirlineRepository {

    private final AirlineJpaDataRepository dataRepository;
    private final AirlinePersistenceMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Airline> findById(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional()
    public void save(Airline airline) {
        dataRepository.save(mapper.toEntity(airline));
    }

    @Override
    public List<AirlineAircraftCount> findTopByAircraftCount(int limit) {
        return dataRepository.findTopByAircraftCount(PageRequest.of(0, limit));
    }
}
