package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.AirlineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface AirlineJpaDataRepository extends JpaRepository<AirlineEntity, String> {
}
