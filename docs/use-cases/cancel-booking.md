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
2. For the outbound flight and, if there is one, the return flight: increase `availableSeats` by `numPassengers` on every leg.
3. For every passenger and every leg where they have a seat:
   1. set the `LegSeat` back to `AVALIABLE` and clear its `passengerId`;
   2. set the passenger's seat on that leg to null.
4. Change the status to `CANCELLED`.
5. Soft delete the booking and its flights. They stay in the repositories but are marked as deleted.
6. Save the legs and the booking, then return the booking.

## Expiration (automatic)
`BookingExpirationValidator` runs every 10 s (`@Scheduled`) and finds `PENDING` bookings whose `paymentDeadline` has passed. For each one it should reuse steps 2–3 and 5 above, but set the status to `EXPIRED` instead of `CANCELLED`. The loop body is empty today.

## Rules and errors (`DomainRuleException`)
- Only a `PENDING` booking can be cancelled or expired.
