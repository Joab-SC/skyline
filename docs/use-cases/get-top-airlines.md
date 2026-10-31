# GetTopAirlines

**Status:** Implemented
**Class:** `application/usecase/GetTopAirlinesUseCase.java`
**Endpoint:** `GET /api/airlines/top?limit=5`

## Purpose
Returns a ranking of the airlines with the most aircraft. It is a report: it only reads data and changes nothing.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `limit` | `int` | How many airlines to return. Optional query param, from 1 to 20, 5 by default. |

## Output
A list of `AirlineAircraftCount` (`airlineId`, `name`, `aircraftCount`), with the most aircraft first. The REST response maps each one to a `TopAirlineResponse`.

## Dependencies
- Repositories: `AirlineRepository`

## Steps
1. Check that `limit` is between 1 and `MAX_RANKING` (20).
2. `AirlineRepository.findTopByAircraftCount(limit)` counts the aircraft of each airline. In JPA this is the `@Query` of `AirlineJpaDataRepository`, which joins `airline_aircrafts` with the aircraft, groups by airline and pages with `PageRequest.of(0, limit)`.
3. Return the list.

## Rules and errors
- The order is by number of aircraft, most first. A tie is ordered by airline name.
- Airlines without aircraft are not included. If no airline has aircraft, the list is empty. This is not an error.
- The count uses the aircraft linked to the airline (`airline_aircrafts`), which [CreateAircraft](create-aircraft.md) saves.
- `limit` outside 1–20 throws `IllegalArgumentException` (400 in REST), and the repository is not called. It is an input check, not a business rule, so it is in the use case (as in the professor's `ConsultarCatalogoUseCase.masVendidos`) and is not a `DomainRuleException`.
- The use case is `@Transactional(readOnly = true)`, since it only reads.
