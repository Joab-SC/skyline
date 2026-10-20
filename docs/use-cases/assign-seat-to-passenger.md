# AssignSeatToPassenger

**Status:** Not implemented
**Class:** none yet

## Purpose
Assigns an available seat on one leg to a passenger of a `PENDING` booking and marks that seat as occupied.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `bookingId` | `String` | Booking id. |
| `idDocument` | `String` | Passenger document (`person.idDocument`). |
| `legId` | `String` | Leg where the seat is assigned. |
| `seatCode` | `String` | Seat code (e.g. `12C`). |

## Output
The updated `Booking`.

## Dependencies
- Repositories: `BookingRepository`, `LegRepository`, `FlightRepository` (to check that the leg belongs to the booking's flights)

## Steps
1. Find the booking and check that it is `PENDING`.
2. Find the passenger in the booking by `idDocument`.
3. Check that `legId` is a leg of the booking's outbound or return flight.
4. Find the leg and its `LegSeat` with `seatCode`. The seat must be `AVALIABLE`.
5. If the passenger already has a seat on that leg, release it (`AVALIABLE`, no `passengerId`).
6. Mark the new seat as `OCUPPIED` with `passengerId = idDocument`.
7. Store the reference in the passenger: `legId → seatCode`. The passenger keeps one seat per leg, by id rather than the `LegSeat` object, because `LegSeat` belongs to the Leg aggregate.
8. Save the leg and the booking, then return the booking.

## Rules and errors (`DomainRuleException`)
- The passenger belongs to the booking, and the leg belongs to the booking's flights.
- The seat belongs to that leg and is available.
- `availableSeats` is **not** reduced; it was already reduced when the flight was selected.
