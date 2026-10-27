package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.BookingPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryJpa implements BookingRepository {

    private final BookingJpaDataRepository dataRepository;
    private final BookingPersistenceMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findByStatusAndPaymentDeadlineLessThanEqual(BookingStatus bookingStatus,
                                                                    LocalDateTime localDateTime) {
        return dataRepository.findByBookingStatusAndPaymentDeadlineLessThanEqual(bookingStatus, localDateTime)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> findById(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void save(Booking booking) {
        dataRepository.save(mapper.toEntity(booking));
    }
}
