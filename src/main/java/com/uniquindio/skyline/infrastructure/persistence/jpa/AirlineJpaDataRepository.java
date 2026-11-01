package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.repository.AirlineAircraftCount;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.AirlineEntity;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.ExtraServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;   // no java.awt.print.Pageable
import org.springframework.data.repository.query.Param;

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

    @Query("""
        SELECT e FROM AirlineEntity a
        JOIN a.extraServices e
        WHERE e.deleted = false
        AND (:airlineId IS NULL OR a.id = :airlineId)
        AND (LOWER(e.nameService) LIKE LOWER(CONCAT('%', :text, '%'))
             OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))
        ORDER BY e.nameService ASC, e.id ASC
        """)
    Page<ExtraServiceEntity> searchExtraServices(@Param("text") String text,
                                                 @Param("airlineId") String airlineId,
                                                 Pageable pageable);
}
