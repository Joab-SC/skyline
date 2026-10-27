package com.uniquindio.skyline.infrastructure.rest.mapper;

import com.uniquindio.skyline.application.dto.response.BookingExtraServiceResponse;
import com.uniquindio.skyline.application.dto.response.BookingResponse;
import com.uniquindio.skyline.application.dto.response.LuggageDetailResponse;
import com.uniquindio.skyline.application.dto.response.PassengerResponse;
import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Passenger;
import com.uniquindio.skyline.domain.entity.Person;
import com.uniquindio.skyline.domain.valueObject.DetailExtraService;
import com.uniquindio.skyline.domain.valueObject.LuggageDetail;
import com.uniquindio.skyline.domain.valueObject.PhonePrefix;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getNumPassengers(),
                booking.getPrice(),
                booking.getIdOutboundFlight(),
                booking.getIdReturnFlight().orElse(null),
                booking.getTripType().name(),
                booking.getBookingStatus().name(),
                booking.getPaymentDeadline(),
                booking.getPassengerList().stream().map(this::toPassengerResponse).toList(),
                booking.getDetailExtraServices().stream().map(this::toExtraServiceResponse).toList()
        );
    }

    private PassengerResponse toPassengerResponse(Passenger passenger) {
        Person person = passenger.getPerson();
        return new PassengerResponse(
                person.getIdDocument(),
                person.getDocumentType().name(),
                person.getFirstName(),
                person.getSecondName().orElse(null),
                person.getLastName1(),
                person.getLastName2().orElse(null),
                person.getBirthDate(),
                person.getGender().name(),
                passenger.isHolder(),
                passenger.getEmail().orElse(null),
                passenger.getPhonePrefix().map(PhonePrefix::getPrefix).orElse(null),
                passenger.getPhoneNumber().orElse(null),
                passenger.getLuggageDetail().map(this::toLuggageDetailResponse).orElse(null)
        );
    }

    private LuggageDetailResponse toLuggageDetailResponse(LuggageDetail luggageDetail) {
        return new LuggageDetailResponse(
                luggageDetail.quantity(),
                luggageDetail.subTotal()
        );
    }

    private BookingExtraServiceResponse toExtraServiceResponse(DetailExtraService detail) {
        return new BookingExtraServiceResponse(
                detail.idExtraService(),
                detail.quantity(),
                detail.subtotal()
        );
    }
}