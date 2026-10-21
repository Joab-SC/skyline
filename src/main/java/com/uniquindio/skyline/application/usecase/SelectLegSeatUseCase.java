package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.entity.Passenger;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.service.SeatAssigner;

public class SelectLegSeatUseCase {

    private final LegRepository legRepository;
    private final BookingRepository bookingRepository;
    private final SeatAssigner seatAssigner;


    public SelectLegSeatUseCase(LegRepository legRepository, BookingRepository bookingRepository, SeatAssigner seatAssigner) {
        this.legRepository = legRepository;
        this.bookingRepository = bookingRepository;
        this.seatAssigner = seatAssigner;
    }

    public Leg execute(String idBooking, String idLegSeat, String  idPassenger, String idLeg){
        Booking booking = bookingRepository.findById(idBooking).orElseThrow();
        Passenger passenger = booking.getPassenger(idPassenger).orElseThrow();
        Leg leg = legRepository.findById(idLeg).orElseThrow();
        seatAssigner.assignSeat(booking,idLegSeat, passenger,leg);
        legRepository.save(leg);
        bookingRepository.save(booking);
        return leg;
    }
}
