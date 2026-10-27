package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

interface BookingJpaDataRepository extends JpaRepository<BookingEntity, String> {

    List<BookingEntity> findByBookingStatusAndPaymentDeadlineLessThanEqual(BookingStatus bookingStatus,
                                                                          LocalDateTime paymentDeadline);
}
