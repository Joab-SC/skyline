package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import com.uniquindio.skyline.domain.valueObject.PassengerData;
import com.uniquindio.skyline.domain.valueObject.PhonePrefix;
import com.uniquindio.skyline.domain.valueObject.LuggageDetail;
import com.uniquindio.skyline.domain.valueObject.TripType;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
public class Booking {

    private static final int ADULT_AGE = 18;

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

        return new Booking(UUID.randomUUID().toString(), numPassengers, price * numPassengers, new ArrayList<>(), idOutboundFlight, Optional.empty(),
                tripType, new ArrayList<>(), LocalDateTime.now().plusMinutes(20));
    }



    private Passenger getPassenger(String id){
        return passengerList.stream()
                .filter(passenger -> passenger.getPerson().getIdDocument().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new DomainRuleException("Passenger not found"));
    }

    // Creates a luggage detail for a passenger of this booking.
    public LuggageDetail createLuggageDetail(List<String> luggageIds, int quantity, double subTotal, int maxQuantity){
        return new LuggageDetail(luggageIds, quantity, subTotal, maxQuantity);
    }

    public void registerPassengerLuggage(String idDocument, LuggageDetail luggageDetail){
        if (this.bookingStatus != BookingStatus.PENDING){
            throw new DomainRuleException("Only a pending booking can be modified");
        }
        if (luggageDetail == null){
            throw new DomainRuleException("The luggage detail cannot be null");
        }
        Passenger passenger = getPassenger(idDocument);
        if (passenger.getLuggageDetail().isPresent()){
            this.price = this.price - passenger.getLuggageDetail().get().subTotal();
        }
        passenger.registerLuggage(luggageDetail);
        this.price = this.price + luggageDetail.subTotal();
    }

    public void addReturnFlight(String idReturnFlight, double price){
        if (this.bookingStatus != BookingStatus.PENDING){
            throw new DomainRuleException("Only a pending booking can be modified");
        }
        if (this.idReturnFlight.isPresent()){
            throw new DomainRuleException("The booking already has a return flight");
        }
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
        this.price = this.price + price * this.numPassengers;
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

    public void addPassengers(List<PassengerData> passengerListParameter){
        if (passengerListParameter == null || passengerListParameter.stream().anyMatch(Objects::isNull)){
            throw new DomainRuleException("Passenger list cannot be null or contain null passengers");
        }
        if (bookingStatus != BookingStatus.PENDING){
            throw new DomainRuleException("Passengers can only be added to a pending booking");
        }
        if (!passengerList.isEmpty()){
            throw new DomainRuleException("Passengers have already been added to this booking");
        }
        if (passengerListParameter.size() != numPassengers){
            throw new DomainRuleException("Number of passengers must be equal to the size of the list");
        }
        Set<String> documents = new HashSet<>();
        for (PassengerData data: passengerListParameter){
            if (!documents.add(data.documentType() + ":" + data.idDocument())){
                throw new DomainRuleException("There cannot be two passengers with the same document");
            }
        }
        for (PassengerData data: passengerListParameter){
            Passenger passengerBooking = Passenger.createPassenger(data.idDocument(),data.documentType(),
                    data.firstName(),data.secondName(), data.lastName1(),
                    data.lastName2(),data.birthDate(),data.gender());
            passengerList.add(passengerBooking);
        }
    }

    public void choseHolder(String idDocument, String email,
                            String phoneNumber, PhonePrefix phonePrefix){

        if (idDocument == null || idDocument.isBlank()) {
            throw new DomainRuleException("Document ID cannot be empty");
        }
        if (passengerList.isEmpty()) {
            throw new DomainRuleException("Passengers must be added before choosing the holder");
        }
        if (passengerList.stream().anyMatch(Passenger::isHolder)) {
            throw new DomainRuleException("The booking already has a holder");
        }
        Passenger holderCandidate = passengerList.stream()
                .filter(passenger -> passenger.getPerson().getIdDocument().equals(idDocument))
                .findFirst()
                .orElseThrow(() -> new DomainRuleException("Holder must be one of the booking passengers"));
        if (holderCandidate.getPerson().getBirthDate().plusYears(ADULT_AGE).isAfter(LocalDate.now())) {
            throw new DomainRuleException("The holder must be at least " + ADULT_AGE + " years old");
        }
        int counter = 0;
        for (Passenger passenger: passengerList){
            if (passenger.getPerson().getIdDocument().equals(idDocument)){
                passenger.addInfoHolder(email,phoneNumber,phonePrefix);
                counter ++;
            }
        }
        if (counter != 1){
            throw new DomainRuleException("There must be an unique holder");
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
