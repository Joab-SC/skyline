package com.uniquindio.skyline.infrastructure.rest.mapper;

import com.uniquindio.skyline.application.dto.request.PassengerRequest;
import com.uniquindio.skyline.application.dto.response.*;
import com.uniquindio.skyline.application.dto.result.BookingDetail;
import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Passenger;
import com.uniquindio.skyline.domain.entity.Payment;
import com.uniquindio.skyline.domain.repository.BookingSeat;
import com.uniquindio.skyline.domain.valueObject.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                booking.getDetailExtraServices().stream().map(this::toExtraServiceResponse).toList(),
                booking.getPayments().stream().map(this::toPaymentResponse).toList()
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

    public BookingDetailResponse toBookingDetailResponse(BookingDetail detail) {
        Booking booking = detail.booking();
        Map<String, List<BookingSeat>> seatsByPassenger = detail.bookingSeats().stream()
                .collect(Collectors.groupingBy(BookingSeat::passengerId));

        return new BookingDetailResponse(
                booking.getId(),
                booking.getNumPassengers(),
                booking.getPrice(),
                booking.getIdOutboundFlight(),
                booking.getIdReturnFlight().orElse(null),
                booking.getTripType().name(),
                booking.getBookingStatus().name(),
                booking.getPaymentDeadline(),
                booking.getPassengerList().stream()
                        .map(passenger -> toPassengerDetailResponse(passenger,
                                seatsByPassenger.getOrDefault(passenger.getPerson().getIdDocument(), List.of())))
                        .toList(),
                booking.getDetailExtraServices().stream().map(this::toExtraServiceResponse).toList(),
                booking.getPayments().stream().map(this::toPaymentResponse).toList()
        );
    }

    private PassengerDetailResponse toPassengerDetailResponse(Passenger passenger, List<BookingSeat> seats) {
        Person person = passenger.getPerson();
        return new PassengerDetailResponse(
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
                passenger.getLuggageDetail().map(this::toLuggageDetailResponse).orElse(null),
                seats.stream().map(this::toPassengerSeatResponse).toList()
        );
    }

    private PassengerSeatResponse toPassengerSeatResponse(BookingSeat seat) {
        return new PassengerSeatResponse(
                seat.legId(),
                seat.legSeatId(),
                seat.seatCode()
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

    private PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getTransactionId().orElse(null),
                payment.getMessage().orElse(null),
                payment.getCreatedAt()
        );
    }

    public List<PassengerData> toPassengerDataList(List<PassengerRequest> requests) {
        return requests.stream()
                .map(request -> new PassengerData(
                        request.idDocument(),
                        request.documentType(),
                        request.firstName(),
                        request.secondName(),
                        request.lastName1(),
                        request.lastName2(),
                        request.birthDate(),
                        request.gender()))
                .toList();
    }
}