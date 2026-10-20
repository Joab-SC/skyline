# SelectOutboundFlight

**Status:** Implemented, but differs from the design (see [pending.md](../pending.md)).
**Class:** `application/usecase/SelectOutboundFlightUseCase.java`

## Purpose
Creates a `PENDING` booking with the outbound flight, reduces the available seats on that flight's legs and returns the booking.

## Inputs (current code)
| Name | Type | Description |
|------|------|-------------|
| `numPassengers` | `int` | Number of passengers. |
| `price` | `double` | Booking price (sent by the caller). |
| `idOutboundFlight` | `String` | Id of the chosen flight. |
| `tripType` | `TripType` | `ONE_WAY` or `ROUND_TRIP`. |

## Output
The `PENDING` `Booking`.

## Dependencies
- Repositories: `BookingRepository`, `FlightRepository`, `LegRepository` (through `SeatsAvailableReducer`)
- Domain services: `SeatsAvailableReducer`
- Target design: `FlightSearchCache` (does not exist yet; separate from `FlightRepository`)

## Steps (current code)
1. Find the flight in `FlightRepository`.
2. Create the booking with `Booking.createPendingBooking(numPassengers, price, idOutboundFlight, tripType)`. Its status is `PENDING` and its `paymentDeadline` is now + 20 minutes.
3. Reduce `availableSeats` by `numPassengers` on every leg of the flight (`SeatsAvailableReducer`).
4. Save the booking.
5. Return the booking.

## Target design (pending)
1. Get the chosen flight from `FlightSearchCache` by its id.
2. Save the flight in `FlightRepository`. A flight is saved only when a booking uses it.
3. Booking price = `flight.price × numPassengers`. The `price` input is removed.
4. Same steps 2–5 as above.

## Rules and errors (`DomainRuleException`)
- The flight must exist ("OutboundFlight cannot be found").
- `numPassengers > 0`; `idOutboundFlight` is not empty; `tripType` is not null.
- Every leg must have enough available seats ("There's not enough available seats for the num of passengers").
