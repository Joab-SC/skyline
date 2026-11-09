package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.repository.BookingSeat;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.LegEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface LegJpaDataRepository extends JpaRepository<LegEntity, String> {

    List<LegEntity> findByAircraftId(String aircraftId);

    List<LegEntity> findByAvailableSeatsGreaterThanEqual(int minimumSeats);

    @Query("""
    SELECT new com.uniquindio.skyline.domain.repository.BookingSeat(s.id, s.seatCode, s.passengerId, l.id)
    FROM LegEntity l
    JOIN l.seats s
    WHERE s.bookingId = :bookingId
    ORDER BY l.departureTime ASC, s.seatCode ASC
    """)
    List<BookingSeat> findSeatsByBookingId(@Param("bookingId") String bookingId);
}


