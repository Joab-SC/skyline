# CancelBooking

**Status:** Not implemented. `Booking.cancelBooking()` exists, but it only sets `CANCELLED` and does not validate the current status.
**Class:** none yet

## Purpose
Cancels a `PENDING` booking and returns everything it held on the legs.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `bookingId` | `String` | Booking id. |

## Output
The `CANCELLED` `Booking`.

## Dependencies
- Repositories: `BookingRepository`, `FlightRepository`, `LegRepository`

## Steps
1. Find the booking and check that it is `PENDING`. `CONFIRMED` bookings cannot be cancelled.
2. For the outbound flight and, if there is one, the return flight: increase `availableSeats` by `numPassengers` on every leg. `Leg` has no method for this yet.
3. On every one of those legs, release the `LegSeat`s taken by the booking's passengers (`LegSeat.releaseSeat()`: back to `AVAILABLE`, no `passengerId`). Seats are stored only in the legs; the passengers have no seat field.
4. Change the status to `CANCELLED`.
5. Save the legs and the booking, then return the booking.

Soft delete of the booking and its flights is still to be decided (see [pending.md](../pending.md)).

## Expiration (automatic)
`BookingExpirationValidator` is meant to run every 10 s (`@Scheduled`) and find `PENDING` bookings whose `paymentDeadline` has passed. For each one it should reuse steps 2–3 above, but set the status to `EXPIRED` instead of `CANCELLED`. Today the loop body is empty and `@EnableScheduling` is missing, so it never runs.

## Rules and errors (`DomainRuleException`)
- Only a `PENDING` booking can be cancelled or expired.
