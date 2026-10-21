package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.valueObject.PassengerData;
import com.uniquindio.skyline.domain.valueObject.PhonePrefix;

import java.util.List;

public class AddPassengerUseCase {

    private final BookingRepository bookingRepository;

    public AddPassengerUseCase(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;

    }

    public void execute(String idBooking, List<PassengerData> passengers,String idHolder, String email,
                        String phoneNumber, PhonePrefix phonePrefix ){
        Booking booking = bookingRepository.findById(idBooking).orElseThrow();
        booking.addPassengers(passengers);
        booking.choseHolder(idHolder, email, phoneNumber,phonePrefix);
        bookingRepository.save(booking);
    }

}
