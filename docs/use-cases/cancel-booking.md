# CancelBooking

**Status:** Implemented
**Class:** `application/usecase/CancelBookingUseCase.java`
**Endpoint:** `PUT /api/bookings/{id}/cancel`

## Purpose
Cancels a `PENDING` booking when the person leaves the booking process, and returns everything it held on the legs. [ExpireBooking](expire-booking.md) reuses the same seat release with status `EXPIRED`.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `bookingId` | `String` | Booking id. |

## Output
The `CANCELLED` `Booking`.

## Dependencies
- Repositories: `BookingRepository`, `FlightRepository`, `LegRepository`
- Domain services: `BookingSeatsReleaser`

## Steps
1. Find the booking by `bookingId`.
2. `Booking.cancelBooking()` checks that it is `PENDING` and changes the status to `CANCELLED`.
3. `BookingSeatsReleaser.releaseSeats(booking)`, for every leg of the outbound flight and, if there is one, the return flight (`Booking.getFlightIds()`):
   1. First pass: load every flight and leg, and check that `availableSeats + numPassengers` does not exceed the leg's total seats. If one fails, nothing changes.
   2. Second pass: `Leg.restoreAvailableSeats(numPassengers)`, release the `LegSeat` of each passenger (`Leg.releaseSeatOfPassenger`) and save the leg.
4. Save the booking and return it.

The use case is `@Transactional`: if the seat release fails, the booking is not saved as cancelled.

Soft delete of the booking and its flights is still to be decided (see [pending.md](../pending.md)).

## Rules and errors
- The booking must exist, or `NoSuchElementException` is thrown (404 in REST).
- Only a `PENDING` booking can be cancelled (`DomainRuleException`, 422). A `CONFIRMED`, `CANCELLED` or `EXPIRED` booking cannot.
- A flight or leg of the booking that cannot be found throws `DomainRuleException`.
- Available seats can never exceed the leg's total seats (`DomainRuleException`).
- Known issue: `LegSeat` stores only the passenger's document, so if the same person is in two bookings that share a leg, the seat released may belong to the other booking (see [pending.md](../pending.md), Seats).
