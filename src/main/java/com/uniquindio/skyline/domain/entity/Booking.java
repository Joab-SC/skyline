package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.*;
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
    private List<DetailExtraService> detailExtraServices;
    private BookingStatus bookingStatus;
    private LocalDateTime paymentDeadline;
    private List<Payment> payments;

    private Booking(String id, int numPassengers, double price, List<Passenger> passengerList, String idOutboundFlight,
                    Optional<String> idReturnFlight, TripType tripType, List<DetailExtraService> detailExtraServices, LocalDateTime paymentDeadline, List<Payment> payments) {
        this.id = id;
        this.numPassengers = numPassengers;
        this.price = price;
        this.passengerList = passengerList;
        this.idOutboundFlight = idOutboundFlight;
        this.idReturnFlight = idReturnFlight;
        this.tripType = tripType;
        this.detailExtraServices = detailExtraServices;
        this.payments = payments;
        this.bookingStatus = BookingStatus.PENDING;
        this.paymentDeadline = paymentDeadline;
    }

    // Receives every field, including the status. Used only by reconstruct.
    private Booking(String id, int numPassengers, double price, List<Passenger> passengerList, String idOutboundFlight,
                    Optional<String> idReturnFlight, TripType tripType, List<DetailExtraService> detailExtraServices, BookingStatus bookingStatus, LocalDateTime paymentDeadline, List<Payment> payments) {
        this.id = id;
        this.numPassengers = numPassengers;
        this.price = price;
        this.passengerList = passengerList;
        this.idOutboundFlight = idOutboundFlight;
        this.idReturnFlight = idReturnFlight;
        this.tripType = tripType;
        this.detailExtraServices = detailExtraServices;
        this.bookingStatus = bookingStatus;
        this.paymentDeadline = paymentDeadline;
        this.payments = payments;
    }

    // Rebuilds a booking already stored, without running the creation rules again.
    public static Booking reconstruct(String id, int numPassengers, double price, List<Passenger> passengerList,
                                      String idOutboundFlight, String idReturnFlight, TripType tripType,
                                      List<DetailExtraService> detailExtraServices, BookingStatus bookingStatus,
                                      LocalDateTime paymentDeadline, List<Payment> payments) {
        return new Booking(id, numPassengers, price, passengerList, idOutboundFlight,
                Optional.ofNullable(idReturnFlight), tripType, detailExtraServices, bookingStatus, paymentDeadline, payments);
    }

    public static Booking createPendingBooking(int numPassengers, double price, String idOutboundFlight,
                                               TripType tripType){
        validateBookingData(
                numPassengers,
                idOutboundFlight,
                tripType
        );

        return new Booking(UUID.randomUUID().toString(), numPassengers, price * numPassengers, new ArrayList<>(), idOutboundFlight, Optional.empty(),
                tripType, new ArrayList<>(), LocalDateTime.now().plusMinutes(20), new ArrayList<>());
    }

    // Starts a payment attempt. If one is still pending, it is resumed instead of creating another,
    // assuming it was never charged. The booking must be payable.
    public Payment startPayment() {
        validateCanBeConfirmed();
        Optional<Payment> pending = findPendingPayment();
        if (pending.isPresent()) {
            return pending.get();
        }
        Payment payment = Payment.createPending(this.price);
        payments.add(payment);
        return payment;
    }

    // Saves the provider's answer in the attempt. An approved payment confirms the booking.
    // The deadline is not checked here: it was checked when the attempt started, and the money was already charged.
    public void registerPaymentResult(String paymentId, PaymentStatus status, String transactionId, String message) {
        Payment payment = getPayment(paymentId);
        payment.registerResult(status, transactionId, message);
        if (payment.isApproved()) {
            this.bookingStatus = BookingStatus.CONFIRMED;
        }
    }

    public Optional<Payment> findPendingPayment() {
        return payments.stream().filter(Payment::isPending).findFirst();
    }

    // The payment provider sends the receipt to the holder.
    public String getHolderEmail() {
        return passengerList.stream()
                .filter(Passenger::isHolder)
                .findFirst()
                .flatMap(Passenger::getEmail)
                .orElseThrow(() -> new DomainRuleException("The booking has no holder"));
    }

    private Payment getPayment(String paymentId) {
        return payments.stream()
                .filter(payment -> payment.getId().equals(paymentId))
                .findFirst()
                .orElseThrow();
    }

    // A booking can be paid when it is pending, before its deadline, with every passenger and a holder.
    private void validateCanBeConfirmed() {
        if (bookingStatus == BookingStatus.CONFIRMED) {
            throw new DomainRuleException("The booking is already paid");
        }
        validateIsModifiable();
        if (passengerList.size() != numPassengers) {
            throw new DomainRuleException("All passengers must be added before paying");
        }
        if (passengerList.stream().noneMatch(Passenger::isHolder)) {
            throw new DomainRuleException("The booking must have a holder before paying");
        }
    }


    // Adds an extra service to the booking. If the service was already added,
    // the new detail replaces the old one and its subtotal is swapped in the price.
    public void addExtraService(String extraServiceId, int quantity, double unitPrice){
        validateIsModifiable();
        DetailExtraService newDetail =
                new DetailExtraService(extraServiceId, quantity, unitPrice * quantity);

        Optional<DetailExtraService> currentDetail = detailExtraServices.stream()
                .filter(detail -> detail.idExtraService().equals(extraServiceId))
                .findFirst();
        if (currentDetail.isPresent()){
            this.price -= currentDetail.get().subtotal();
            detailExtraServices.remove(currentDetail.get());
        }

        detailExtraServices.add(newDetail);
        this.price += newDetail.subtotal();
    }



    public Optional<Passenger> getPassenger(String id){
        return passengerList.stream()
                .filter(passenger -> passenger.getPerson().getIdDocument().equals(id))
                .findFirst();
    }

    public void validateIsModifiable(){
        if (bookingStatus != BookingStatus.PENDING){
            throw new DomainRuleException("Only pending bookings can be modified");
        }
        if (LocalDateTime.now().isAfter(paymentDeadline)){
            throw new DomainRuleException("The booking payment deadline has expired");
        }
    }

    public void addPriceSeats(double priceSeats){
        if (priceSeats <0){
            throw new DomainRuleException("Pice cannot be negative");
        }
        this.price += priceSeats;
    }

    // Creates a luggage detail for a passenger of this booking.
    public LuggageDetail createLuggageDetail(List<String> luggageIds, int quantity, double subTotal, int maxQuantity){
        return new LuggageDetail(luggageIds, quantity, subTotal, maxQuantity);
    }

    public void registerPassengerLuggage(String idDocument, LuggageDetail luggageDetail){
        validateIsModifiable();
        if (luggageDetail == null){
            throw new DomainRuleException("The luggage detail cannot be null");
        }
        Passenger passenger = getPassenger(idDocument).orElseThrow();
        if (passenger.getLuggageDetail().isPresent()){
            this.price = this.price - passenger.getLuggageDetail().get().subTotal();
        }
        passenger.registerLuggage(luggageDetail);
        this.price = this.price + luggageDetail.subTotal();
    }

    public void addReturnFlight(String idReturnFlight, double price){
        validateReturnFlight(idReturnFlight, price);
        this.idReturnFlight=Optional.of(idReturnFlight);
        this.price = this.price + price * this.numPassengers;
    }

    // Public so callers can check the booking before changing other aggregates (e.g. reducing seats).
    public void validateReturnFlight(String idReturnFlight, double price){
        validateIsModifiable();
        if (!hasNoReturnFlight()){
            throw new DomainRuleException("The booking already has a return flight");
        }
        if (!isRoundTrip()){
            throw new DomainRuleException("There's no return flight on one way trip");
        }
        if (!isValidReturnFlightId(idReturnFlight)){
            throw new DomainRuleException("id return flight cannot be null or empty");
        }
        if (!isValidReturnFlightPrice(price)){
            throw new DomainRuleException("Price cannot be zero or less");
        }
    }

    private boolean isPending(){
        return this.bookingStatus == BookingStatus.PENDING;
    }

    private boolean hasNoReturnFlight(){
        return this.idReturnFlight.isEmpty();
    }

    private boolean isRoundTrip(){
        return this.tripType != TripType.ONE_WAY;
    }

    private boolean isValidReturnFlightId(String idReturnFlight){
        return idReturnFlight != null && !idReturnFlight.isBlank();
    }

    private boolean isValidReturnFlightPrice(double price){
        return price > 0;
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
        validateIsModifiable();
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
        validateIsModifiable();
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

    public void isPassengerInBooking(Passenger passenger){
        if (!passengerList.stream().anyMatch(found -> found.equals(passenger))){
            throw new DomainRuleException("Passenger not found");
        }
    }



    // The person leaves the booking process. Only a pending booking can be cancelled.
    public void cancelBooking(){
        validateCanBeClosed();
        this.bookingStatus = BookingStatus.CANCELLED;
    }

    // The payment deadline passed without payment. Only a pending booking can expire.
    public void expireBooking(){
        validateCanBeClosed();
        if (LocalDateTime.now().isBefore(paymentDeadline)){
            throw new DomainRuleException("The booking payment deadline has not passed yet");
        }
        this.bookingStatus = BookingStatus.EXPIRED;
    }

    private void validateCanBeClosed(){
        if (!isPending()){
            throw new DomainRuleException("Only a pending booking can be cancelled or expired");
        }
    }

    // Ids of the outbound flight and, if there is one, the return flight.
    public List<String> getFlightIds(){
        List<String> flightIds = new ArrayList<>();
        flightIds.add(idOutboundFlight);
        idReturnFlight.ifPresent(flightIds::add);
        return flightIds;
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
