package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.BookingSeat;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.application.dto.result.BookingDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetBookingUseCase {

    private final BookingRepository bookingRepository;
    private final LegRepository legRepository;

    // Returns the booking with its flights, passengers, luggage and extra services.
    @Transactional(readOnly = true)
    public BookingDetail execute(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        List<BookingSeat> bookingSeats = legRepository.findSeatsByBookingId(booking.getId());
        return new BookingDetail(booking, bookingSeats);
    }
}