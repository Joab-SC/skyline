package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.service.LuggageDetailCreator;
import com.uniquindio.skyline.domain.valueObject.LuggageDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddLuggageUseCase {
    private final BookingRepository bookingRepository;
    private final LuggageDetailCreator luggageDetailCreator;

    // Registers the luggage of a passenger for every leg of the booking.
    public Booking execute(String passengerId, String bookingId, int amount){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        LuggageDetail luggageDetail = luggageDetailCreator.createDetail(booking, amount);
        booking.registerPassengerLuggage(passengerId, luggageDetail);
        bookingRepository.save(booking);
        return booking;
    }
}
