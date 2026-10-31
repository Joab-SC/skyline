# ListAirlineExtraServices

**Status:** Implemented
**Class:** `application/usecase/ListAirlineExtraServicesUseCase.java`
**Endpoint:** `GET /api/airlines/{id}/extra-services`

## Purpose
Returns the extra services an airline still offers so the client can show them before adding them to a booking with [AddExtraServices](add-extra-services.md) (`PUT /api/bookings/{id}/extra-services`). It only reads data and changes nothing.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `airlineId` | `String` | Airline whose extra services are requested. The client takes it from the `airlineId` of the selected flight (`FlightResponse`). |

## Output
The airline's `ExtraService` list, without the deleted ones (see [DeleteExtraService](delete-extra-service.md)). The REST response maps each one to an `ExtraServiceResponse`.

## Dependencies
- Repositories: `AirlineRepository`

## Steps
1. Find the airline by `airlineId`.
2. Return `Airline.getActiveExtraServices()`: its extra services that are not deleted.

## Rules and errors
- The airline must exist, or `NoSuchElementException` is thrown (404 in REST).
- An airline with no extra services, or with all of them deleted, returns an empty list. This is not an error.
