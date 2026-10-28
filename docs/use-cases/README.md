# Use cases

One file per use case. To add a new one, create `<use-case-name>.md` in this folder with the same sections (Status, Purpose, Inputs, Output, Dependencies, Steps, Rules and errors) and add a row below. Open items are tracked in [../pending.md](../pending.md).

Out of scope for now: booking pre-confirmation and tax calculation (CalculateBooking), check-in and boarding pass (CheckInPassenger).

| # | Use case | What it does | Repositories | Status                           |
|---|----------|--------------|--------------|----------------------------------|
| 1 | [CreateAircraft](create-aircraft.md) | Creates an aircraft from its model, generates its seats and links it to an airline. | `AircraftRepository`, `AirlineRepository` | Implemented                      |
| 2 | [CreateLeg](create-leg.md) | Creates a leg with its seats and luggage and links it to an airline. | `LegRepository`, `AirlineRepository`, `AircraftRepository` | Implemented                      |
| 3 | [SearchFlights](search-flights.md) | Finds flights (leg combinations) by origin, destination, day and passengers and keeps them in the search cache. | `LegRepository`, `FlightSearchCache` | Implemented                      |
| 4 | [SelectOutboundFlight](select-outbound-flight.md) | Creates a `PENDING` booking with the outbound flight and reduces available seats. | `BookingRepository`, `FlightRepository`, `FlightSearchCache`, `LegRepository` | Implemented                      |
| 5 | [SelectReturnFlight](select-return-flight.md) | Adds the return flight to the booking, reduces available seats and adds its price. | `BookingRepository`, `FlightRepository`, `FlightSearchCache`, `LegRepository` | Implemented                      |
| 6 | [AddPassengersToBooking](add-passengers-to-booking.md) | Registers all passengers of the booking in one call and chooses the holder. | `BookingRepository` | Implemented                      |
| 7 | [AssignSeatToPassenger](assign-seat-to-passenger.md) | Assigns an available seat on a leg to a passenger, marks it occupied and adds the seat price. | `BookingRepository`, `LegRepository`, `FlightRepository`, `AirlineRepository` | Implemented                      |
| 8 | [AddLuggage](add-luggage.md) | Sets the passenger's luggage detail for the whole booking and adds its subtotal. | `BookingRepository`, `FlightRepository`, `LegRepository`, `AirlineRepository` | Implemented                      |
| 9 | [AddExtraServices](add-extra-services.md) | Adds booking-level extra service details and adds their subtotal (national or international price). | `BookingRepository`, `FlightRepository`, `AirlineRepository` | Implemented                      |
| 10 | [PayBooking](pay-booking.md) | Confirms a `PENDING` booking. | `BookingRepository`, `FlightRepository`, `LegRepository` | In progress                      |
| 11 | [CancelBooking](cancel-booking.md) | Cancels a `PENDING` booking and returns its seats. Expiration reuses this logic with `EXPIRED`. | `BookingRepository`, `FlightRepository`, `LegRepository` | Not implemented                  |
| 12 | [CreateExtraService](create-extra-service.md) | Creates an extra service with national and international prices and adds it to an airline. | `AirlineRepository` | Implemented                      |
| 13 | [GetLegSeatMap](get-leg-seat-map.md) | Returns a leg's seats with their status so the client can pick one. | `LegRepository` | Implemented                      |
| 14 | [ListAirlineExtraServices](list-airline-extra-services.md) | Returns an airline's extra services so the client can choose them. | `AirlineRepository` | Implemented                      |

## Booking flow

`SearchFlights` → `SelectOutboundFlight` → `SelectReturnFlight` (round trip only) → `AddPassengersToBooking` → `GetLegSeatMap` → `AssignSeatToPassenger` / `AddLuggage` / `ListAirlineExtraServices` → `AddExtraServices` → `PayBooking`

A `PENDING` booking can be cancelled at any point (`CancelBooking`), and it expires when `paymentDeadline` (20 min) passes. Only a `PENDING` booking can be modified.
