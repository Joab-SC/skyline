package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightJpaDataRepository extends JpaRepository<FlightEntity, String> {
}
