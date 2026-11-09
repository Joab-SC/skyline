package com.uniquindio.skyline.infrastructure.persistence;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.repository.BookingSeat;
import com.uniquindio.skyline.domain.repository.LegRepository;

import java.util.*;

public class LegRepositoryInMemory implements LegRepository {

    private final Map<String, Leg> legs = new HashMap<>();

    // Finds a leg by its id.
    @Override
    public Optional<Leg> findById(String id) {
        return Optional.ofNullable(legs.get(id));
    }

    // Stores or replaces a leg.
    @Override
    public void save(Leg leg) {
        legs.put(leg.getId(), leg);
    }

    @Override
    public List<Leg> findLegsByAircraft(String idAircraft) {
        return legs.values().stream()
                .filter(leg -> leg.getAircraftId().equals(idAircraft))
                .toList();
    }

    @Override
    public List<Leg> findAll() {
        return new ArrayList<>(legs.values());
    }

    @Override
    public List<Leg> findLegsWithAtLeastAvailableSeats(int minimumSeats) {
        return legs.values().stream()
                .filter(leg -> leg.getAvailableSeats() >= minimumSeats)
                .toList();
    }

    // Finds the seats held by the passengers of a booking, with the leg of each seat.
    @Override
    public List<BookingSeat> findSeatsByBookingId(String bookingId) {
        return legs.values().stream()
                .flatMap(leg -> leg.getSeats().stream()
                        .filter(seat -> seat.getPassengerBookingId()
                                .map(occupant -> occupant.belongsToBooking(bookingId))
                                .orElse(false))
                        .map(seat -> new BookingSeat(
                                seat.getId(),
                                seat.getSeatCode(),
                                seat.getPassengerBookingId().orElseThrow().passengerDocument(),
                                leg.getId())))
                .toList();
    }
}
