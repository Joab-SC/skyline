# ListAirports

**Status:** Implemented
**Class:** `application/usecase/ListAirportsUseCase.java`
**Endpoint:** `GET /api/airports` (public)

## Purpose
Returns every airport the platform flies to, so the client can choose the origin and destination of a search ([SearchFlights](search-flights.md)) or of a new leg ([CreateLeg](create-leg.md)). The front loads this list instead of keeping its own copy. It only reads data and changes nothing.

## Inputs
None.

## Output
Every value of the `Airport` enum, in the order they are declared. The REST response maps each one to an `AirportResponse` (`code`, `name`, `city`, `country`).

## Dependencies
None. Airports are a fixed catalog in the code (the `Airport` enum), so there is no repository.

## Steps
1. Return `Airport.values()` as a list.

## Rules and errors
- The `code` is the airport's IATA code (e.g. `BOG`). It is the airport's identifier in every request and response of the API.
