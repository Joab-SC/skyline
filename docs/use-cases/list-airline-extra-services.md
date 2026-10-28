# ListAirlineExtraServices

**Status:** Implemented
**Class:** `application/usecase/ListAirlineExtraServicesUseCase.java`
**Endpoint:** `GET /api/airlines/{id}/extra-services`

## Purpose
Returns the extra services of an airline so the client can show them before adding them to a booking with AddExtraServices. It only reads data and changes nothing.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `airlineId` | `String` | Airline whose extra services are requested. |

## Output
The airline's `ExtraService` list. The REST response maps each one to an `ExtraServiceResponse`.

## Dependencies
- Repositories: `AirlineRepository`

## Steps
1. Find the airline by `airlineId`.
2. Return its extra services.

## Rules and errors
- The airline must exist, or `NoSuchElementException` is thrown (404 in REST).
- An airline with no extra services returns an empty list. This is not an error.
