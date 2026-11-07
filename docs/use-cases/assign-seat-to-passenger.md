# AssignSeatToPassenger

**Status:** Implemented
**Class:** `application/usecase/SelectLegSeatUseCase.java`
**Endpoint:** `PUT /api/bookings/{id}/seats`

## Purpose
Assigns an available seat on one leg to a passenger of a `PENDING` booking, marks that seat as occupied and adds the airline's seat price to the booking. The client gets the seats from [GetLegSeatMap](get-leg-seat-map.md).

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `idBooking` | `String` | Booking id. |
| `idLegSeat` | `String` | Id of the `LegSeat` (not the seat code). |
| `idPassenger` | `String` | Passenger document (`person.idDocument`). |
| `idLeg` | `String` | Leg where the seat is assigned. |

## Output
The updated `Booking`.

## Dependencies
- Repositories: `BookingRepository`, `LegRepository`, plus `FlightRepository` and `AirlineRepository` through `SeatAssigner`
- Domain services: `SeatAssigner`

## Steps
1. Find the booking, the passenger in the booking and the leg.
2. `SeatAssigner.assignSeat`:
   1. Check that the booking can be modified (`PENDING` and before `paymentDeadline`).
   2. Check that the passenger belongs to the booking.
   3. Check that the leg is one of the legs of the outbound or return flight.
   4. Build the occupant `PassengerBookingId(booking id, passenger document)`.
   5. `leg.assignSeat(idLegSeat, occupant)`: the new seat becomes `OCCUPIED` with that occupant. If the same passenger of the same booking already had a seat on that leg, the old one is released. Choosing the same seat again changes nothing.
   6. If that passenger of that booking had no seat on that leg, add the airline's `priceSeats` to the booking. Changing seats does not charge again.
3. Save the leg and the booking, then return the booking.

## Rules and errors
- `DomainRuleException`:
  - the booking is `PENDING` and its payment deadline has not passed;
  - the passenger belongs to the booking;
  - the leg belongs to the booking's flights;
  - the seat is not already occupied.
- `NoSuchElementException` (404 in REST): the booking, passenger, leg or seat does not exist.
- `availableSeats` is **not** reduced; it was already reduced when the flight was selected.
- The seat is stored only in the `LegSeat`, as a `PassengerBookingId` (booking id + passenger document). The `Passenger` and the booking response do not include it (see [../pending.md](../pending.md)).
- The same person can be in two bookings that share a leg: each booking has its own seat, and changing, cancelling or expiring one booking does not touch the seat of the other.
