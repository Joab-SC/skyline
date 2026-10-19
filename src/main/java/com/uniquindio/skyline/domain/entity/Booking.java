package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import com.uniquindio.skyline.domain.valueObject.TripType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
public class Booking {

    private String id;
    private int numPassengers;
    private double price;
    private List<Passenger> passengerList;
    private String idOutboundFlight;
    private Optional<String> idReturnFlight;
    private TripType tripType;
    private List<String> extraServices;
    private BookingStatus bookingStatus;
    private LocalDateTime paymentDeadline;

    private Booking(String id, int numPassengers, double price, List<Passenger> passengerList, String idOutboundFlight, Optional<String> idReturnFlight, TripType tripType, List<String> extraServices, LocalDateTime paymentDeadline) {
        this.id = id;
        this.numPassengers = numPassengers;
        this.price = price;
        this.passengerList = passengerList;
        this.idOutboundFlight = idOutboundFlight;
        this.idReturnFlight = idReturnFlight;
        this.tripType = tripType;
        this.extraServices = extraServices;
        this.bookingStatus = BookingStatus.PENDING;
        this.paymentDeadline = paymentDeadline;
    }

    public static Booking createPendingBooking(int numPassengers, double price, String idOutboundFlight,
                                               TripType tripType){
        validateBookingData(
                numPassengers,
                idOutboundFlight,
                tripType
        );

        return new Booking(UUID.randomUUID().toString(), numPassengers, price, new ArrayList<>(), idOutboundFlight, Optional.empty(),
                tripType, new ArrayList<>(), LocalDateTime.now().plusMinutes(20));
    }



    private Passenger getPassenger(String id){
        return passengerList.stream()
                .filter(passenger -> passenger.getPerson().getIdDocument().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new DomainRuleException("Passenger not found"));
    }
    public void addReturnFlight(String idReturnFlight, double price){
        if (this.tripType == TripType.ONE_WAY){
            throw new DomainRuleException("There's no return flight on one way trip");
        }
        if (idReturnFlight == null || idReturnFlight.isBlank()){
            throw new DomainRuleException("id return flight cannot be null or empty");
        }
        if (price <= 0 ){
            throw new DomainRuleException("Price cannot be zero or less");
        }
        this.idReturnFlight=Optional.of(idReturnFlight);
        this.price = this.price + price;
    }
    private static void validateBookingData(
            int numPassengers,
            String idOutboundFlight,
            TripType tripType) {

        if (numPassengers <= 0) {
            throw new DomainRuleException(
                    "The number of passengers must be greater than zero");
        }

        if (idOutboundFlight == null || idOutboundFlight.isBlank()) {
            throw new DomainRuleException(
                    "The outbound flight ID cannot be null or empty");
        }

        if (tripType == null) {
            throw new DomainRuleException(
                    "The trip type cannot be null");
        }
    }

    public void cancelBooking(){
        this.bookingStatus = BookingStatus.CANCELLED;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking other = (Booking) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
