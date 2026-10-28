# CreateAircraft

**Status:** Implemented (no use-case test yet)
**Class:** `application/usecase/CreateAircraftUseCase.java`
**Endpoint:** `POST /api/aircrafts`

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
1. Create the aircraft with `Aircraft.createAircraft(aircraftModel)`. It gets a UUID and its seats:
   - `rows = ceil(maxSeats / seatColumns.size())`;
   - seat codes are `row + column` (`1A`, `1B`, …). Generation stops at `maxSeats`.
2. Find the airline by `airlineId`.
3. Link the aircraft with `airline.addAircraft(aircraft.getId())`.
4. Save the aircraft in `AircraftRepository`.
5. Return the aircraft.

The airline is **not saved** after step 3, so the link is lost with JPA. See [pending.md](../pending.md).

## Rules and errors
- `DomainRuleException`:
  - `aircraftModel` cannot be null;
  - the aircraft id cannot already be in the airline, and cannot be empty.
- The airline must exist, or `NoSuchElementException` is thrown (404 in REST).
- Aircraft invariants: see [aircraft-aggregate/invariants.md](../aircraft-aggregate/invariants.md).
