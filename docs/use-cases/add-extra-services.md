# AddExtraServices

**Status:** Implemented
**Class:** `application/usecase/AddExtraServiceUseCase.java`

## Purpose
Adds extra services to a `PENDING` booking as extra service details (service, quantity, subtotal) and adds the subtotal to the booking price. Extra services apply to the **whole booking** (the whole trip), not to a passenger. For example, "bring an instrument" appears on the booking without saying who brings it.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `bookingId` | `String` | Booking id. |
| `serviceId` | `String` | Extra service id. It must belong to the airline of the outbound flight. The client gets the available ids from [ListAirlineExtraServices](list-airline-extra-services.md). |
| `quantity` | `int` | How many units of the service. |

## Output
The updated `Booking`.

## Dependencies
- Repositories: `BookingRepository`, `FlightRepository`, `AirlineRepository`

## Steps
1. Find the booking and check that it is `PENDING`.
2. Find the outbound flight and its airline. The outbound and return flights belong to the same airline.
3. Decide whether the flight is international (see below).
4. For each service:
   1. Find it in the airline. The `ExtraService` entities belong to the Airline aggregate.
   2. Unit price = `internationalPrice` if international, else `nationalPrice`.
   3. Create a `DetailExtraService` value object with `subtotal = quantity × unit price`.
   4. If the booking already has a detail for that service, the new detail **replaces** it: subtract the old subtotal and add the new one. Otherwise add the subtotal.
5. Save and return the booking.

## International flight (proposal, to be confirmed)
- Add a `Country` enum and a country attribute to `City`; an `Airport` gets its country through its `City`.
- A flight is international when the country of the outbound flight's origin airport differs from the country of its destination airport.
- Only the origin and destination count. Layovers do not, so BOG→PTY→MDE is national.
- The rule is global per airline: an airline charges the same international price for any international flight (e.g. Miami or Hong Kong).

## Rules and errors (`DomainRuleException`)
- The service exists in the airline of the booking flights.
- `quantity > 0`.
