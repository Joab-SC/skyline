package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListCustomerBookingsUseCase {

    private final BookingRepository bookingRepository;

    // Bookings made by a logged-in customer, newest first. Guest bookings are not included.
    @Transactional(readOnly = true)
    public List<Booking> execute(String idCustomer) {
        return bookingRepository.findByIdCustomer(idCustomer);
    }
}
