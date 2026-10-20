# CreateAircraft

**Status:** Implemented
**Class:** `application/usecase/CreateAircraftUseCase.java`

## Purpose
Creates an aircraft from its model, generates its seats and links it to an airline.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `aircraftModel` | `AircraftModel` | Model that defines `maxSeats` and `seatColumns`. |
| `airlineId` | `String` | Airline that owns the aircraft. |

## Output
The created `Aircraft`.

## Dependencies
- Repositories: `AircraftRepository`, `AirlineRepository`

## Steps
1. Create the aircraft with `Aircraft.createAircraft(aircraftModel)`. This generates its seats:
   - `rows = ceil(maxSeats / seatColumns.size())`
   - Seat codes are `row + column` (`1A`, `1B`, …). Generation stops at `maxSeats`.
2. Find the airline by `airlineId`.
3. Link the aircraft with `airline.addAircraft(aircraft.getId())`.
4. Save the aircraft in `AircraftRepository`.
5. Return the aircraft.

## Rules and errors (`DomainRuleException`)
- `aircraftModel` cannot be null.
- The airline must exist ("Airline not found to create the aircraft").
- The aircraft id cannot already be in the airline, and cannot be empty.
- Aircraft invariants: see [aircraft-aggregate/invariants.md](../aircraft-aggregate/invariants.md).
