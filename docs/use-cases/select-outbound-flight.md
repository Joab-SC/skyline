# SelectOutboundFlight

**Status:** Implemented
**Class:** `application/usecase/SelectOutboundFlightUseCase.java`
**Endpoint:** `POST /api/bookings`

## Purpose
Creates a `PENDING` booking with the outbound flight, reduces the available seats on that flight's legs and returns the booking.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `numPassengers` | `int` | Number of passengers. |
| `idOutboundFlight` | `String` | Id of the chosen flight, from the [SearchFlights](search-flights.md) results. |
| `tripType` | `TripType` | `ONE_WAY` or `ROUND_TRIP`. |

## Output
The `PENDING` `Booking`.

## Dependencies
- Repositories: `BookingRepository`, `FlightRepository`, `FlightSearchCache`, `LegRepository` (through `SeatsAvailableReducer`)
- Domain services: `SeatsAvailableReducer`

## Steps
1. Get the chosen flight from `FlightSearchCache` by its id.
2. Create the booking with `Booking.createPendingBooking(numPassengers, flight.price, flight.id, tripType)`. Its price is `flight.price × numPassengers`, its status is `PENDING` and its `paymentDeadline` is now + 20 minutes.
3. Reduce `availableSeats` by `numPassengers` on every leg of the flight (`SeatsAvailableReducer`). All legs are checked first, so nothing changes if one of them has no room.
4. Save the flight in `FlightRepository`. A flight is saved only when a booking uses it.
5. Save and return the booking.

## Rules and errors
- `DomainRuleException`:
  - `numPassengers > 0`; `idOutboundFlight` is not empty; `tripType` is not null;
  - every leg has enough available seats ("There's not enough available seats for the num of passengers").
- The flight is not in the search cache: `NoSuchElementException` (404 in REST).
