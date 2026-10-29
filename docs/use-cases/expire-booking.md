# ExpireBooking

**Status:** Implemented
**Class:** `application/usecase/ExpireBookingUseCase.java`
**Trigger:** no endpoint. `infrastructure/scheduling/BookingExpirationScheduler` runs every 10 s (`@Scheduled(fixedDelay = 10000)`, enabled by `infrastructure/config/SchedulingConfig`).

## Purpose
Expires a `PENDING` booking whose `paymentDeadline` (20 min after it was created) has passed without payment, and returns everything it held on the legs, the same way as [CancelBooking](cancel-booking.md).

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `bookingId` | `String` | Booking id, taken from the list of pending bookings whose deadline passed. |

## Output
None. The booking is saved as `EXPIRED`.

## Dependencies
- Repositories: `BookingRepository`, `FlightRepository`, `LegRepository`
- Domain services: `BookingExpirationValidator`, `BookingSeatsReleaser`

## Steps
1. The scheduler asks `BookingExpirationValidator.findExpiredPendingBookings(now)` for the `PENDING` bookings with `paymentDeadline <= now`.
2. For each one it calls `ExpireBookingUseCase.execute(bookingId)`, in its own transaction:
   1. Find the booking by `bookingId`.
   2. `Booking.expireBooking()` checks that it is `PENDING` and that the deadline passed, then sets `EXPIRED`.
   3. `BookingSeatsReleaser.releaseSeats(booking)` gives the seats back, as in CancelBooking step 3.
   4. Save the booking.
3. If one booking fails, the scheduler logs a warning and goes on with the next one.

## Rules and errors (`DomainRuleException`)
- Only a `PENDING` booking can expire.
- A booking cannot expire before its `paymentDeadline`.
- The seat release rules are the same as in CancelBooking.
