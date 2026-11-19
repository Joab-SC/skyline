package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.repository.BookingSeat;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.repository.PageResult;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.LegEntity;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.LegPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Override
    public List<BookingSeat> findSeatsByBookingId(String bookingId) {
        return dataRepository.findSeatsByBookingId(bookingId);
    }

    // Spring's Page stays here: it is turned into the domain's PageResult.
    @Override
    @Transactional(readOnly = true)
    public PageResult<Leg> findByAirline(String airlineId, int page, int size) {
        Page<LegEntity> result = dataRepository.findByAirlineIdOrderByDepartureTimeAsc(airlineId, PageRequest.of(page, size));

        return new PageResult<>(
                result.getContent().stream().map(mapper::toDomain).toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize());
    }
}
