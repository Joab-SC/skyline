package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.request.*;
import com.uniquindio.skyline.application.dto.response.BookingResponse;
import com.uniquindio.skyline.application.usecase.*;
import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.infrastructure.rest.mapper.BookingMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final SelectOutboundFlightUseCase selectOutboundFlightUseCase;
    private final SelectReturnFlightUseCase selectReturnFlightUseCase;
    private final AddPassengerUseCase addPassengerUseCase;
    private final SelectLegSeatUseCase selectLegSeatUseCase;
    private final AddLuggageUseCase addLuggageUseCase;
    private final AddExtraServiceUseCase addExtraServiceUseCase;
    private final GetBookingUseCase getBookingUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;
    private final PayBookingUseCase payBookingUseCase;
    private final BookingMapper mapper;

    public BookingController(SelectOutboundFlightUseCase selectOutboundFlightUseCase,
                             SelectReturnFlightUseCase selectReturnFlightUseCase, AddPassengerUseCase addPassengerUseCase, SelectLegSeatUseCase selectLegSeatUseCase, AddLuggageUseCase addLuggageUseCase, AddExtraServiceUseCase addExtraServiceUseCase, GetBookingUseCase getBookingUseCase,
                             CancelBookingUseCase cancelBookingUseCase, PayBookingUseCase payBookingUseCase, BookingMapper mapper) {
        this.selectOutboundFlightUseCase = selectOutboundFlightUseCase;
        this.selectReturnFlightUseCase = selectReturnFlightUseCase;
        this.addPassengerUseCase = addPassengerUseCase;
        this.selectLegSeatUseCase = selectLegSeatUseCase;
        this.addLuggageUseCase = addLuggageUseCase;
        this.addExtraServiceUseCase = addExtraServiceUseCase;
        this.getBookingUseCase = getBookingUseCase;
        this.cancelBookingUseCase = cancelBookingUseCase;
        this.payBookingUseCase = payBookingUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody SelectOutboundFlightRequest request) {
        Booking booking = selectOutboundFlightUseCase.execute(
                request.numPassengers(), request.outboundFlightId(), request.tripType());
        BookingResponse response = mapper.toBookingResponse(booking);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(booking.getId())
                .toUri();
        return ResponseEntity.created(location).body(response); // 201
    }

    @PutMapping("/{id}/return-flight")
    public ResponseEntity<BookingResponse> selectReturnFlight(@PathVariable String id,
                                                              @Valid @RequestBody SelectReturnFlightRequest request) {
        Booking booking = selectReturnFlightUseCase.execute(id, request.returnFlightId());
        return ResponseEntity.ok(mapper.toBookingResponse(booking)); // 200
    }

    @PutMapping("/{id}/passengers")
    public ResponseEntity<BookingResponse> addPassengers(@PathVariable String id,
                                                         @Valid @RequestBody AddPassengersRequest request) {
        Booking booking = addPassengerUseCase.execute(
                id,
                mapper.toPassengerDataList(request.passengers()),
                request.holderIdDocument(),
                request.email(),
                request.phoneNumber(),
                request.phonePrefix());
        return ResponseEntity.ok(mapper.toBookingResponse(booking)); // 200
    }

    @PutMapping("/{id}/seats")
    public ResponseEntity<BookingResponse> selectSeat(@PathVariable String id,
                                                      @Valid @RequestBody SelectSeatRequest request) {
        Booking booking = selectLegSeatUseCase.execute(
                id, request.legSeatId(), request.passengerId(), request.legId());
        return ResponseEntity.ok(mapper.toBookingResponse(booking)); // 200
    }

    @PutMapping("/{id}/luggage")
    public ResponseEntity<BookingResponse> addLuggage(@PathVariable String id,
                                                      @Valid @RequestBody AddLuggageRequest request) {
        Booking booking = addLuggageUseCase.execute(request.passengerId(), id, request.quantity());
        return ResponseEntity.ok(mapper.toBookingResponse(booking)); // 200
    }

    @PutMapping("/{id}/extra-services")
    public ResponseEntity<BookingResponse> addExtraService(@PathVariable String id,
                                                           @Valid @RequestBody AddExtraServiceRequest request) {
        Booking booking = addExtraServiceUseCase.execute(id, request.extraServiceId(), request.quantity());
        return ResponseEntity.ok(mapper.toBookingResponse(booking)); // 200
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(@PathVariable String id){
        Booking booking = getBookingUseCase.execute(id);
        return ResponseEntity.ok(mapper.toBookingResponse(booking)); //200
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancel(@PathVariable String id){
        Booking booking = cancelBookingUseCase.execute(id);
        return ResponseEntity.ok(mapper.toBookingResponse(booking)); // 200
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<BookingResponse> pay(@PathVariable String id,
                                               @Valid @RequestBody PayBookingRequest request) {
        Booking booking = payBookingUseCase.execute(request.paymentToken(), id);
        return ResponseEntity.ok(mapper.toBookingResponse(booking)); // 200
    }
}
