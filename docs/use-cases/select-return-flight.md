# SelectReturnFlight

**Status:** Implemented
**Class:** `application/usecase/SelectReturnFlightUseCase.java`
**Endpoint:** `PUT /api/bookings/{id}/return-flight`

## Purpose
Adds the return flight to a `PENDING` `ROUND_TRIP` booking, reduces the available seats on that flight's legs, adds its price and returns the booking.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `idBooking` | `String` | Booking id. |
| `idReturnFlight` | `String` | Id of the chosen return flight, from the [SearchFlights](search-flights.md) results. |

## Output
The updated `Booking`.

## Dependencies
- Repositories: `BookingRepository`, `FlightRepository`, `FlightSearchCache`, `LegRepository` (through `SeatsAvailableReducer`)
- Domain services: `OutboundAndReturnFlightsValidator`, `SeatsAvailableReducer`

## Steps
1. Find the booking.
2. Get the return flight from `FlightSearchCache` and the outbound flight from `FlightRepository`.
3. Validate before changing anything:
   1. `OutboundAndReturnFlightsValidator`: the two flights are compatible (see rules).
   2. `booking.validateReturnFlight`: the booking accepts a return flight (see rules).
4. Reduce `availableSeats` by `numPassengers` on every leg of the return flight.
5. `booking.addReturnFlight`: store the flight id and add `flight.price × numPassengers` to the price.
6. Save the return flight and the booking, then return the booking.

## Rules and errors
- `DomainRuleException`:
  - the outbound and return flights belong to the **same airline**;
  - the return flight departs from the outbound destination and arrives at the outbound origin;
  - the return flight departs after the outbound flight arrives;
  - the booking is `PENDING`, is `ROUND_TRIP` and has no return flight yet;
  - `idReturnFlight` is not empty and the price is greater than 0;
  - every leg has enough available seats.
- The booking or the outbound flight does not exist, or the return flight is not in the search cache: `NoSuchElementException` (404 in REST).
