package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.valueObject.PassengerData;
import com.uniquindio.skyline.domain.valueObject.PhonePrefix;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddPassengerUseCase {

    private final BookingRepository bookingRepository;

    public Booking execute(String idBooking, List<PassengerData> passengers,String idHolder, String email,
                        String phoneNumber, PhonePrefix phonePrefix ){
        Booking booking = bookingRepository.findById(idBooking).orElseThrow();
        booking.addPassengers(passengers);
        booking.choseHolder(idHolder, email, phoneNumber,phonePrefix);
        bookingRepository.save(booking);
        return booking;
    }

}
