# DeleteExtraService

**Status:** Implemented
**Class:** `application/usecase/DeleteExtraServiceUseCase.java`
**Endpoint:** `DELETE /api/airlines/{id}/extra-services/{extraServiceId}`

## Purpose
An airline stops offering one of its extra services. It is a **soft delete**: the service is marked as `deleted` and stays in the airline and in the `extra_services` table. Old bookings keep the service id in their `DetailExtraService`, so removing the row would leave them pointing to nothing.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `airlineId` | `String` | Airline that owns the service. |
| `extraServiceId` | `String` | Extra service to delete. |

## Output
None. The REST response is `204 No Content`.

## Dependencies
- Repositories: `AirlineRepository`

## Steps
1. Find the airline by `airlineId`.
2. `Airline.deleteExtraService(extraServiceId)` finds the service in the airline and calls `ExtraService.delete()`, which sets `deleted = true`.
3. Save the airline. The `deleted` column of the service is updated.

## After the delete
- [ListAirlineExtraServices](list-airline-extra-services.md) no longer returns it (`Airline.getActiveExtraServices()`).
- [AddExtraServices](add-extra-services.md) cannot add it to a booking (`ExtraService.validateIsNotDeleted()`).
- A booking that already has it keeps its detail and its price.

## Rules and errors
- The airline must exist, or `NoSuchElementException` is thrown (404 in REST).
- The service must belong to the airline, or `NoSuchElementException` is thrown (404 in REST).
- A service that is already deleted cannot be deleted again (`DomainRuleException`, 422).

`data.sql` has a deleted service, `extra-latam-pet`, to try it.
