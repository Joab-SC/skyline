# SearchExtraServices

**Status:** Implemented
**Class:** `application/usecase/SearchExtraServicesUseCase.java`
**Endpoint:** `GET /api/extra_services?search=&airlineId=&page=0&size=10`

## Purpose
Paginated search of the extra services that are still offered. The admin of an airline uses it to browse and find the services in its catalog, and the client uses it to look for a service of the flight's airline before adding it with [AddExtraServices](add-extra-services.md). It only reads data and changes nothing.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `search` | `String` | Optional. Text to look for in the name or the description, ignoring case. Empty or missing returns every service. |
| `airlineId` | `String` | Optional. Only services of this airline. Missing searches every airline. |
| `page` | `int` | Page number, starting at 0. Default `0`. |
| `size` | `int` | Services per page, from 1 to `MAX_PAGE_SIZE` (50). Default `10`. |

## Output
A `PageResult<ExtraService>` (domain record in `domain/repository`): `content`, `totalElements`, `totalPages`, `number` and `size`. The REST response is a `PageResponse<ExtraServiceResponse>` with the same fields.

## Dependencies
- Repositories: `AirlineRepository` (`searchExtraServices`). `ExtraService` belongs to the Airline aggregate, so there is no repository of its own.

## Steps
1. Check `page >= 0` and `1 <= size <= 50`.
2. `AirlineRepository.searchExtraServices(text, airlineId, page, size)`. In `AirlineRepositoryJpa`:
   1. A null text becomes `""` (matches everything), and a blank `airlineId` becomes `null` (no filter).
   2. JPQL query with named params (`:text`, `:airlineId`) on `AirlineJpaDataRepository`:
      ```sql
      SELECT e FROM AirlineEntity a JOIN a.extraServices e
      WHERE e.deleted = false
      AND (:airlineId IS NULL OR a.id = :airlineId)
      AND (LOWER(e.nameService) LIKE LOWER(CONCAT('%', :text, '%'))
           OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))
      ORDER BY e.nameService ASC, e.id ASC
      ```
   3. Spring's `Page` is turned into the domain's `PageResult`, so the domain does not import Spring.
3. Return the page.

## Rules and errors
- Deleted services ([DeleteExtraService](delete-extra-service.md)) never appear.
- `page < 0`, or `size` outside 1–50: `IllegalArgumentException` (400 in REST). These are input checks, not business rules.
- `page` or `size` that is not a number: 400.
- An `airlineId` that does not exist, or a text with no matches, returns an empty page. This is not an error.
