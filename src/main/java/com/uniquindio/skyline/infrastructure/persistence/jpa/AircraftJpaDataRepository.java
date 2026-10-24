package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.AircraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface AircraftJpaDataRepository extends JpaRepository<AircraftEntity, String> {
}
