package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.LegEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface LegJpaDataRepository extends JpaRepository<LegEntity, String> {

    List<LegEntity> findByAircraftId(String aircraftId);

    List<LegEntity> findByAvailableSeatsGreaterThanEqual(int minimumSeats);
}
