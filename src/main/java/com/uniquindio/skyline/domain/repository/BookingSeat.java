package com.uniquindio.skyline.domain.repository;

// A seat held by a passenger of a booking, with the leg it belongs to. The passenger is identified by their document.
public record BookingSeat(String legSeatId, String seatCode, String passengerId, String legId) {
}
