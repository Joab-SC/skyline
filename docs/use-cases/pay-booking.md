# PayBooking

**Status:** In progress (no code in the repository yet)
**Class:** none yet

## Purpose
Confirms a `PENDING` booking: its status changes to `CONFIRMED`.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `bookingId` | `String` | Booking id. |

## Output
The `CONFIRMED` `Booking`.

## Dependencies
- Repositories: `BookingRepository`, plus `FlightRepository` and `LegRepository` to return seats when the booking has expired

## Steps
1. Find the booking and check that it is `PENDING`.
2. If `paymentDeadline` has passed:
   1. expire the booking (same seat release as [CancelBooking](cancel-booking.md), with status `EXPIRED`);
   2. save it;
   3. throw a `DomainRuleException`.
3. Check that the booking has `numPassengers` passengers with exactly one holder.
4. Change the status to `CONFIRMED`.
5. Save and return the booking.

## Rules and errors (`DomainRuleException`)
- Only a `PENDING` booking can be confirmed. A `CANCELLED`, `EXPIRED` or `CONFIRMED` booking cannot change its status.
- The payment deadline has not passed.
- Passengers are complete, with one holder.
- Seats, luggage and extra services are **not** required.
