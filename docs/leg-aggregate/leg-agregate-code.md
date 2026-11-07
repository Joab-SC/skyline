```mermaid
---
config:
  layout: elk
---
classDiagram
direction TB
    namespace LegAggregate {
        class Leg {
            - String id
            - Airport originAirport
            - Airport destinationAirport
            - LocalDateTime departureTime
            - LocalDateTime arrivalTime
            - String aircraftId
            - String airlineId
            - double price
            - Luggage luggage
            - List~LegSeat~ seats
            - int availableSeats
            + createLeg(origin, destination, departure, arrival, aircraftId, airlineId, aircraftSeats, luggagePrice, price) Leg$
            + reconstruct(...) Leg$
            + hasAvailableSeats(int numPassengers) boolean
            + reduceAvailableSeats(int numPassengers) void
            + canRestoreAvailableSeats(int numPassengers) boolean
            + restoreAvailableSeats(int numPassengers) void
            + releaseSeatOfPassenger(PassengerBookingId occupant) void
            + getLegSeatById(String idLegSeat) Optional~LegSeat~
            + assignSeat(String idLegSeat, PassengerBookingId occupant) void
            + hasSeatAssigned(PassengerBookingId occupant) boolean
            + toSummary() LegSummary
        }

        class LegSeat {
            - String id
            - String seatCode
            - SeatStatus seatStatus
            - Optional~PassengerBookingId~ passengerBookingId
            + createLegSeat(String id, String seatCode) LegSeat$
            + reconstruct(id, seatCode, seatStatus, passengerId, bookingId) LegSeat$
            + assignPassengerToLegSeat(PassengerBookingId occupant) void
            + releaseSeat() void
            + isOccupiedBy(PassengerBookingId occupant) boolean
        }

        class PassengerBookingId {
            - String bookingId
            - String passengerDocument
            + belongsToBooking(String bookingId) boolean
        }

        class SeatStatus {
            AVAILABLE
            OCCUPIED
        }

        class Luggage {
            - int maxWeight = 23
            - String id
            - double price
            + createLuggage(String id, Double luggagePrice) Luggage$
        }

        class Airport {
            - String name
            - String code
            - City city
        }

        class City {
            - String displayName
            - Country country
        }
    }

    class Aircraft {
        - String id
        - AircraftModel aircraftModel
        - List~Seat~ seats
    }

    class Airline {
        - String id
    }

    Leg :<<AggregateRoot>> Leg
    LegSeat :<<Entity>> LegSeat
    SeatStatus :<<enumeration>> SeatStatus
    PassengerBookingId :<<ValueObject>> PassengerBookingId
    Luggage :<<Entity>> Luggage
    Airport :<<enumeration>> Airport
    City :<<enumeration>> City

    Leg "1" *-- "1" Luggage : luggage
    Leg "1" *-- "1..*" LegSeat : seats
    LegSeat "1" --> "1" SeatStatus : seatStatus
    LegSeat "1" --> "0..1" PassengerBookingId : passengerBookingId
    Leg "1" --> "1" Airport : originAirport
    Leg "1" --> "1" Airport : destinationAirport
    Airport "1" --> "1" City : city
    Leg ..> Aircraft : aircraftId
    Leg ..> Airline : airlineId
```

`LegSeat.passengerBookingId` holds who took the seat: the booking id and the passenger's document. The document alone is not enough, because the same person can be in two bookings on the same leg. An `OCCUPIED` seat with no `passengerBookingId` is blocked (the seed seats in `data.sql`). `availableSeats` is reduced when a flight is selected, before any `LegSeat` is assigned.
