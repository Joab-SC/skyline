package com.uniquindio.skyline.infrastructure.persistence.jpa.mapper;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Passenger;
import com.uniquindio.skyline.domain.valueObject.DetailExtraService;
import com.uniquindio.skyline.domain.valueObject.LuggageDetail;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.BookingEntity;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.DetailExtraServiceEmbeddable;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.LuggageDetailEmbeddable;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.PassengerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingPersistenceMapper {

    private final PersonPersistenceMapper personMapper;

    public BookingEntity toEntity(Booking booking) {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setId(booking.getId());
        bookingEntity.setNumPassengers(booking.getNumPassengers());
        bookingEntity.setPrice(booking.getPrice());
        bookingEntity.setPassengerList(booking.getPassengerList().stream()
                .map(passenger -> toPassengerEntity(passenger, booking.getId()))
                .collect(Collectors.toCollection(ArrayList::new)));
        bookingEntity.setIdOutboundFlight(booking.getIdOutboundFlight());
        bookingEntity.setIdReturnFlight(booking.getIdReturnFlight().orElse(null));
        bookingEntity.setTripType(booking.getTripType());
        bookingEntity.setDetailExtraServices(booking.getDetailExtraServices().stream().
                map(detailExtraService -> new DetailExtraServiceEmbeddable(
                        detailExtraService.idExtraService(), detailExtraService.quantity(), detailExtraService.subtotal())).collect(Collectors.toCollection(ArrayList::new)));
        bookingEntity.setBookingStatus(booking.getBookingStatus());
        bookingEntity.setPaymentDeadline(booking.getPaymentDeadline());
        return bookingEntity;
    }

    public Booking toDomain(BookingEntity bookingEntity) {
        return Booking.reconstruct(
                bookingEntity.getId(),
                bookingEntity.getNumPassengers(),
                bookingEntity.getPrice(),
                bookingEntity.getPassengerList().stream()
                        .map(this::toPassengerDomain)
                        .collect(Collectors.toCollection(ArrayList::new)),
                bookingEntity.getIdOutboundFlight(),
                bookingEntity.getIdReturnFlight(),
                bookingEntity.getTripType(),
                bookingEntity.getDetailExtraServices().stream()
                        .map(detail -> new DetailExtraService(
                                detail.getIdExtraService(), detail.getQuantity(), detail.getSubtotal()))
                        .collect(Collectors.toCollection(ArrayList::new)),
                bookingEntity.getBookingStatus(),
                bookingEntity.getPaymentDeadline());
    }




    private PassengerEntity toPassengerEntity(Passenger passenger, String bookingId) {
        PassengerEntity passengerEntity = new PassengerEntity();
        personMapper.fillEntity(passenger.getPerson(), passengerEntity);
        passengerEntity.setBookingId(bookingId);
        passengerEntity.setHolder(passenger.isHolder());
        passengerEntity.setEmail(passenger.getEmail().orElse(null));
        passengerEntity.setPhoneNumber(passenger.getPhoneNumber().orElse(null));
        passengerEntity.setPhonePrefix(passenger.getPhonePrefix().orElse(null));
        passengerEntity.setLuggageDetail(passenger.getLuggageDetail().isEmpty() ? null
                : toLuggageDetailEmbeddable(passenger.getLuggageDetail().get()));
        return passengerEntity;
    }

    private Passenger toPassengerDomain(PassengerEntity passengerEntity) {
        LuggageDetailEmbeddable luggage = passengerEntity.getLuggageDetail();
        return Passenger.reconstruct(
                passengerEntity.isHolder(),
                passengerEntity.getEmail(),
                passengerEntity.getPhoneNumber(),
                passengerEntity.getPhonePrefix(),
                personMapper.toDomain(passengerEntity),
                luggage == null || luggage.getQuantity() == null ? null : toLuggageDetail(luggage));
    }

    private LuggageDetailEmbeddable toLuggageDetailEmbeddable(LuggageDetail luggageDetail) {
        return new LuggageDetailEmbeddable(
                new ArrayList<>(luggageDetail.luggageIds()),
                luggageDetail.quantity(),
                luggageDetail.subTotal(),
                luggageDetail.maxQuantity());
    }

    private LuggageDetail toLuggageDetail(LuggageDetailEmbeddable embeddable) {
        return new LuggageDetail(
                new ArrayList<>(embeddable.getLuggageIds()),
                embeddable.getQuantity(),
                embeddable.getSubTotal(),
                embeddable.getMaxQuantity());
    }
}
