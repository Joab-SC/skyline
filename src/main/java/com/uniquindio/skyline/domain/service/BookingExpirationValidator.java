package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
public class BookingExpirationValidator {

    private final BookingRepository bookingRepository;

    @Scheduled(fixedDelay = 10000)
    public void expirePendingBookings() {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> expiredBookings =
                bookingRepository.findByStatusAndPaymentDeadlineLessThanEqual(
                        BookingStatus.PENDING, now);

        for (Booking booking : expiredBookings) {
            //cancelar reserva
        }
    }
}
