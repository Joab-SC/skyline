package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.service.LuggageDetailCreator;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddLuggageUseCase {
    private final BookingRepository bookingRepository;
    private final LuggageDetailCreator luggageDetailCreator;


    // Registers the luggage of a passenger for every leg of the booking. An amount of 0 removes it.
    @Transactional
    public Booking execute(String passengerId, String bookingId, int amount){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (amount == 0) booking.removePassengerLuggage(passengerId);
        else booking.registerPassengerLuggage(passengerId, luggageDetailCreator.createDetail(booking, amount));
        bookingRepository.save(booking);
        return booking;
    }
}
