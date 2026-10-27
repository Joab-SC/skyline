package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.request.AddPassengersRequest;
import com.uniquindio.skyline.application.dto.request.SelectOutboundFlightRequest;
import com.uniquindio.skyline.application.dto.request.SelectReturnFlightRequest;
import com.uniquindio.skyline.application.dto.request.SelectSeatRequest;
import com.uniquindio.skyline.application.dto.response.BookingResponse;
import com.uniquindio.skyline.application.usecase.AddPassengerUseCase;
import com.uniquindio.skyline.application.usecase.SelectLegSeatUseCase;
import com.uniquindio.skyline.application.usecase.SelectOutboundFlightUseCase;
import com.uniquindio.skyline.application.usecase.SelectReturnFlightUseCase;
import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.infrastructure.rest.mapper.BookingMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final SelectOutboundFlightUseCase selectOutboundFlightUseCase;
    private final SelectReturnFlightUseCase selectReturnFlightUseCase;
    private final AddPassengerUseCase addPassengerUseCase;
    private final SelectLegSeatUseCase selectLegSeatUseCase;
    private final BookingMapper mapper;

    public BookingController(SelectOutboundFlightUseCase selectOutboundFlightUseCase,
                             SelectReturnFlightUseCase selectReturnFlightUseCase, AddPassengerUseCase addPassengerUseCase, SelectLegSeatUseCase selectLegSeatUseCase,
                             BookingMapper mapper) {
        this.selectOutboundFlightUseCase = selectOutboundFlightUseCase;
        this.selectReturnFlightUseCase = selectReturnFlightUseCase;
        this.addPassengerUseCase = addPassengerUseCase;
        this.selectLegSeatUseCase = selectLegSeatUseCase;
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
}