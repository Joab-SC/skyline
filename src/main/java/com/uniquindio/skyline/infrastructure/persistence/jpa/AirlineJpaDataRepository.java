package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.repository.AirlineAircraftCount;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.AirlineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;   // no java.awt.print.Pageable
import java.util.List;

interface AirlineJpaDataRepository extends JpaRepository<AirlineEntity, String> {

    @Query("""
        SELECT new com.uniquindio.skyline.domain.repository.AirlineAircraftCount(a.id, a.name, COUNT(ac))
        FROM AirlineEntity a
        JOIN a.aircraftIds aid
        JOIN AircraftEntity ac ON ac.id = aid
        GROUP BY a.id, a.name
        ORDER BY COUNT(ac) DESC, a.name ASC 
        """)
    List<AirlineAircraftCount> findTopByAircraftCount(Pageable pageable);
}
