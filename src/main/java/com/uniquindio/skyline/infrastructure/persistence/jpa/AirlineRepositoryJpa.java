package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.repository.AirlineAircraftCount;
import com.uniquindio.skyline.domain.repository.PageResult;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.ExtraServiceEntity;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.ExtraServicePersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final ExtraServicePersistenceMapper extraServiceMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Airline> findById(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void save(Airline airline) {
        dataRepository.save(mapper.toEntity(airline));
    }

    @Override
    public List<AirlineAircraftCount> findTopByAircraftCount(int limit) {
        return dataRepository.findTopByAircraftCount(PageRequest.of(0, limit));
    }
    
    // Returns every airline, ordered by name.
    @Override
    @Transactional(readOnly = true)
    public List<Airline> findAll() {
        return dataRepository.findAll(Sort.by("name")).stream().map(mapper::toDomain).toList();
    }

    // Spring's Page stays here: it is turned into the domain's PageResult.
    @Override
    @Transactional(readOnly = true)
    public PageResult<ExtraService> searchExtraServices(String text, String airlineId, int page, int size) {
        String pattern = (text == null) ? "" : text.trim();
        String airline = (airlineId == null || airlineId.isBlank()) ? null : airlineId;

        Page<ExtraServiceEntity> result = dataRepository.searchExtraServices(pattern, airline, PageRequest.of(page, size));

        return new PageResult<>(
                result.getContent().stream().map(extraServiceMapper::toDomain).toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize());
    }
}
