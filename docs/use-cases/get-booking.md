# GetBooking

**Status:** Implemented
**Class:** `application/usecase/GetBookingUseCase.java`
**Endpoint:** `GET /api/bookings/{id}`

## Purpose
Returns a booking so the client can see its current state at any step of the booking flow. It only reads data and changes nothing. `POST /api/bookings` returns a `Location` header that points to this endpoint.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `bookingId` | `String` | Booking that is requested. |

## Output
A `BookingDetail` with two parts:
- The `Booking`, with its flights, status, price, payment deadline, passengers, luggage, extra services and payment attempts.
- The seats its passengers hold (`List<BookingSeat>`): leg seat id, seat code, passenger document and leg id. Seats are stored in the legs (`LegSeat`), not in the booking, so they are read from `LegRepository`.

In REST it is answered as `BookingDetailResponse`, where each passenger has the list of their seats (one per leg at most). A passenger with no seat has an empty list.

## Dependencies
- Repositories: `BookingRepository`, `LegRepository`

## Steps
1. Find the booking by `bookingId`.
2. Find the seats held by the booking's passengers (`LegRepository.findSeatsByBookingId`), ordered by the leg's departure time.
3. Return the booking and its seats.

## Rules and errors
- The booking must exist, or `NoSuchElementException` is thrown (404 in REST).
- A booking can be read in any status (`PENDING`, `CONFIRMED`, `CANCELLED`, `EXPIRED`).
