# SearchFlights

**Status:** Implemented. The design adds `FlightSearchCache`, which is pending (see [pending.md](../pending.md)).
**Class:** `application/usecase/SearchFlightsUseCase.java`

## Purpose
Returns the possible flights (combinations of legs) from an origin to a destination on a given day, for a number of passengers. The resulting `Flight`s exist only in memory; they are not saved in `FlightRepository`.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `originCode` | `String` | Origin airport code (e.g. `BOG`), case-insensitive. |
| `destinationCode` | `String` | Destination airport code. |
| `departureDay` | `LocalDate` | Day the first leg departs. |
| `numberOfPassengers` | `int` | Every leg must have at least this many available seats. |

## Output
`List<Flight>`, with an empty list when there are no combinations.

## Dependencies
- Repositories: `LegRepository` (through `FlightSearcher`)
- Domain services: `FlightSearcher`

## Steps
1. Convert the codes to `Airport` with `Airport.getByAcronym`.
2. `FlightSearcher.searchFlights`:
   1. Load the legs with at least `numberOfPassengers` available seats (`findLegsWithAtLeastAvailableSeats`) and group them by origin airport.
   2. Start a path with each leg that leaves the origin on `departureDay`.
   3. Extend the paths breadth-first. A path that reaches the destination becomes a `Flight` (`Flight.createFlight` + `addLeg`).
3. Return the flights.

## Rules for extending a path
- All legs belong to the same airline.
- The next leg leaves from the airport where the previous one arrives, after its arrival.
- A flight has at most 4 legs (`Flight.MAX_LEGS`).
- Each layover lasts between 1 and 12 hours (`Layover`).
- A path does not pass through the same origin airport twice.
- The flight price is the sum of its leg prices.

## Errors (`DomainRuleException`)
- Unknown airport code ("There is no airport with the code: …").

## Target design (pending)
- The search results are saved in `FlightSearchCache` (a temporary cache, separate from `FlightRepository`) so that `SelectOutboundFlight` and `SelectReturnFlight` can find the chosen flight by its id.
