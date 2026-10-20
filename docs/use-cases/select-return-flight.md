# SelectReturnFlight

**Status:** Implemented, but differs from the design (see [pending.md](../pending.md)).
**Class:** `application/usecase/SelectReturnFlightUseCase.java`

## Purpose
Adds the return flight to a `PENDING` `ROUND_TRIP` booking, reduces the available seats on that flight's legs, adds its price and returns the booking.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `idBooking` | `String` | Booking id. |
| `idReturnFlight` | `String` | Id of the chosen return flight. |

## Output
The updated `Booking`.

## Dependencies
- Repositories: `BookingRepository`, `FlightRepository`, `LegRepository` (through `SeatsAvailableReducer`)
- Domain services: `OutboundAndReturnFlightsValidator`, `SeatsAvailableReducer`
- Target design: `FlightSearchCache` (does not exist yet; separate from `FlightRepository`)

## Steps (current code)
1. Find the booking.
2. Find the return flight in `FlightRepository`.
3. Add it with `booking.addReturnFlight(flight.getId(), flight.getPrice())`. This adds the flight price to the booking.
4. Check that the return flight departs from the outbound destination airport (`OutboundAndReturnFlightsValidator`).
5. Reduce `availableSeats` by `numPassengers` on every leg of the return flight.
6. Return the booking. The booking is not saved.

## Target design (pending)
1. Find the booking and check that it is `PENDING`.
2. Get the return flight from `FlightSearchCache` and save it in `FlightRepository`.
3. Validate before changing the booking:
   - the return flight departs from the outbound destination airport;
   - the outbound and return flights belong to the **same airline**.
4. Add the return flight; the price added is `flight.price × numPassengers`.
5. Reduce the available seats.
6. Save and return the booking.

## Rules and errors (`DomainRuleException`)
- A `ONE_WAY` booking cannot have a return flight.
- `idReturnFlight` is not empty; the price is greater than 0.
- Return flight origin = outbound flight destination.
- Every leg must have enough available seats.
- Current code: the "not found" errors for the booking and the flight have an empty message.
