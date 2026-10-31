package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.entity.Passenger;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.service.SeatAssigner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SelectLegSeatUseCase {

    private final LegRepository legRepository;
    private final BookingRepository bookingRepository;
    private final SeatAssigner seatAssigner;

    @Transactional
    public Booking execute(String idBooking, String idLegSeat, String  idPassenger, String idLeg){
        Booking booking = bookingRepository.findById(idBooking).orElseThrow();
        Passenger passenger = booking.getPassenger(idPassenger).orElseThrow();
        Leg leg = legRepository.findById(idLeg).orElseThrow();
        seatAssigner.assignSeat(booking,idLegSeat, passenger,leg);
        legRepository.save(leg);
        bookingRepository.save(booking);
        return booking;
    }
}
