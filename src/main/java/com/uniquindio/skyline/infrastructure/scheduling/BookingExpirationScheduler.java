package com.uniquindio.skyline.infrastructure.scheduling;

import com.uniquindio.skyline.application.usecase.ExpireBookingUseCase;
import com.uniquindio.skyline.application.usecase.FindExpiredBookingsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingExpirationScheduler {

    private final FindExpiredBookingsUseCase findExpiredBookingsUseCase;
    private final ExpireBookingUseCase expireBookingUseCase;

    // Every 10 s, expires the pending bookings whose payment deadline passed.
    @Scheduled(fixedDelay = 10000)
    public void expirePendingBookings() {
        for (String bookingId : findExpiredBookingsUseCase.execute(LocalDateTime.now())) {
            try {
                expireBookingUseCase.execute(bookingId);
            } catch (RuntimeException e) {
                // One failing booking must not stop the others.
                log.warn("Could not expire booking {}: {}", bookingId, e.getMessage());
            }
        }
    }
}