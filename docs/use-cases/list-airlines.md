# ListAirlines

**Status:** Implemented
**Class:** `application/usecase/ListAirlinesUseCase.java`
**Endpoint:** `GET /api/airlines`

## Purpose
Returns every airline so the client can choose one, for example before creating an aircraft ([CreateAircraft](create-aircraft.md)) or a leg ([CreateLeg](create-leg.md)), which need an `airlineId`. It only reads data and changes nothing.

## Inputs
None.

## Output
The `Airline` list, ordered by name. The REST response maps each one to an `AirlineResponse` (`id`, `name`, `maximumLuggageAmount`, `priceSeats`).

## Dependencies
- Repositories: `AirlineRepository` (`findAll`)

## Steps
1. `AirlineRepository.findAll()` returns every airline ordered by name. In JPA this is `findAll(Sort.by("name"))`.
2. Return the list.

## Rules and errors
- With no airlines the list is empty. This is not an error.
- The use case is `@Transactional(readOnly = true)`, since it only reads.
