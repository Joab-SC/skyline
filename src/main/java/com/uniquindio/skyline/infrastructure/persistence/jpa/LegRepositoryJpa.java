package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.LegPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LegRepositoryJpa implements LegRepository {

    private final LegJpaDataRepository dataRepository;
    private final LegPersistenceMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Leg> findById(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void save(Leg leg) {
        dataRepository.save(mapper.toEntity(leg));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Leg> findLegsByAircraft(String idAircraft) {
        return dataRepository.findByAircraftId(idAircraft).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Leg> findAll() {
        return dataRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Leg> findLegsWithAtLeastAvailableSeats(int minimumSeats) {
        return dataRepository.findByAvailableSeatsGreaterThanEqual(minimumSeats).stream()
                .map(mapper::toDomain).toList();
    }
}
