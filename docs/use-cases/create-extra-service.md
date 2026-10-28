# CreateExtraService

**Status:** Implemented
**Class:** `application/usecase/CreateExtraServiceUseCase.java`
**Endpoint:** `POST /api/extra_services`

## Purpose
Creates an extra service with its national and international prices and adds it to an airline. `ExtraService` is an entity inside the Airline aggregate, so it has no repository of its own: it is saved with the airline.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `airlineId` | `String` | Airline that offers the service. |
| `nameService` | `String` | Name of the service. |
| `description` | `String` | Optional description. A null or blank value is stored as `Optional.empty()`. |
| `nationalPrice` | `double` | Unit price on national flights. |
| `internationalPrice` | `double` | Unit price on international flights. |

## Output
The created `ExtraService`.

## Dependencies
- Repositories: `AirlineRepository`

## Steps
1. Find the airline by `airlineId`.
2. Create the service with `airline.createExtraService(...)`. It uses the `ExtraService.createExtraService(...)` factory, which gives it a UUID, and adds it to the airline's list.
3. Save the airline in `AirlineRepository`.
4. Return the extra service.

## Rules and errors
- The airline must exist, or `NoSuchElementException` is thrown (404 in REST).
- The client lists the services of an airline with [ListAirlineExtraServices](list-airline-extra-services.md).

The following throw `DomainRuleException`:
- The name cannot be null or empty.
- `nationalPrice` and `internationalPrice` cannot be negative (0 is allowed).
- No duplicate check: each service gets a new UUID, and `equals` compares by id.
