# Use cases

One file per use case. To add a new one, create `<use-case-name>.md` in this folder with the same sections (Status, Purpose, Inputs, Output, Dependencies, Steps, Rules and errors) and add a row below. Open items are tracked in [../pending.md](../pending.md).

Out of scope for now: booking pre-confirmation and tax calculation (CalculateBooking), check-in and boarding pass (CheckInPassenger).

| # | Use case | What it does | Repositories | Endpoint | Status |
|---|----------|--------------|--------------|----------|--------|
| 1 | [CreateAircraft](create-aircraft.md) | Creates an aircraft from its model, generates its seats and links it to an airline. | `AircraftRepository`, `AirlineRepository` | `POST /api/aircrafts` | Implemented |
| 2 | [CreateLeg](create-leg.md) | Creates a leg with its seats and luggage and links it to an airline. | `LegRepository`, `AirlineRepository`, `AircraftRepository` | `POST /api/legs` | Implemented |
| 3 | [SearchFlights](search-flights.md) | Finds flights (leg combinations) by origin, destination, day and passengers and keeps them in the search cache. | `LegRepository`, `FlightSearchCache` | `GET /api/flights` | Implemented |
| 4 | [SelectOutboundFlight](select-outbound-flight.md) | Creates a `PENDING` booking with the outbound flight and reduces available seats. | `BookingRepository`, `FlightRepository`, `FlightSearchCache`, `LegRepository` | `POST /api/bookings` | Implemented |
| 5 | [SelectReturnFlight](select-return-flight.md) | Adds the return flight to the booking, reduces available seats and adds its price. | `BookingRepository`, `FlightRepository`, `FlightSearchCache`, `LegRepository` | `PUT /api/bookings/{id}/return-flight` | Implemented |
| 6 | [AddPassengersToBooking](add-passengers-to-booking.md) | Registers all passengers of the booking in one call and chooses the holder. | `BookingRepository` | `PUT /api/bookings/{id}/passengers` | Implemented |
| 7 | [AssignSeatToPassenger](assign-seat-to-passenger.md) | Assigns an available seat on a leg to a passenger, marks it occupied and adds the seat price. | `BookingRepository`, `LegRepository`, `FlightRepository`, `AirlineRepository` | `PUT /api/bookings/{id}/seats` | Implemented |
| 8 | [AddLuggage](add-luggage.md) | Sets the passenger's luggage detail for the whole booking and adds its subtotal. | `BookingRepository`, `FlightRepository`, `LegRepository`, `AirlineRepository` | `PUT /api/bookings/{id}/luggage` | Implemented |
| 9 | [AddExtraServices](add-extra-services.md) | Adds booking-level extra service details and adds their subtotal (national or international price). | `BookingRepository`, `FlightRepository`, `AirlineRepository` | `PUT /api/bookings/{id}/extra-services` | Implemented |
| 10 | [PayBooking](pay-booking.md) | Charges a `PENDING` booking through the payment gateway, keeps every attempt and confirms the booking when a payment is approved. | `BookingRepository` (plus the `PaymentGateway` port) | `POST /api/bookings/{id}/payment` | Implemented |
| 11 | [CancelBooking](cancel-booking.md) | Cancels a `PENDING` booking and returns its seats. | `BookingRepository`, `FlightRepository`, `LegRepository` | `PUT /api/bookings/{id}/cancel` | Implemented |
| 12 | [CreateExtraService](create-extra-service.md) | Creates an extra service with national and international prices and adds it to an airline. | `AirlineRepository` | `POST /api/extra_services` | Implemented |
| 13 | [GetLegSeatMap](get-leg-seat-map.md) | Returns a leg's seats with their status so the client can pick one. | `LegRepository` | `GET /api/legs/{id}/seats` | Implemented |
| 14 | [ListAirlineExtraServices](list-airline-extra-services.md) | Returns an airline's extra services so the client can choose them. | `AirlineRepository` | `GET /api/airlines/{id}/extra-services` | Implemented |
| 15 | [GetBooking](get-booking.md) | Returns a booking with its current state. | `BookingRepository` | `GET /api/bookings/{id}` | Implemented |
| 16 | [CreateAirline](create-airline.md) | Creates an airline with its maximum luggage amount and seat price. | `AirlineRepository` | `POST /api/airlines` | Implemented |
| 17 | [ExpireBooking](expire-booking.md) | Expires a `PENDING` booking whose payment deadline passed and returns its seats, like CancelBooking. | `BookingRepository`, `FlightRepository`, `LegRepository` | — (scheduled every 10 s) | Implemented |
| 18 | [GetTopAirlines](get-top-airlines.md) | Returns the airlines with the most aircraft, most first (report). | `AirlineRepository` | `GET /api/airlines/top` | Implemented |
| 19 | [ListAirlines](list-airlines.md) | Returns every airline, ordered by name, so the client can choose one. | `AirlineRepository` | `GET /api/airlines` | Implemented |
| 20 | [DeleteExtraService](delete-extra-service.md) | Soft delete of an airline's extra service: it is no longer offered, but old bookings keep it. | `AirlineRepository` | `DELETE /api/airlines/{id}/extra-services/{extraServiceId}` | Implemented |

## Booking flow

`SearchFlights` → `SelectOutboundFlight` → `SelectReturnFlight` (round trip only) → `AddPassengersToBooking` → `GetLegSeatMap` → `AssignSeatToPassenger` / `AddLuggage` / `ListAirlineExtraServices` → `AddExtraServices` → `PayBooking`

`GetBooking` can be called at any step to read the booking again.

A `PENDING` booking can be cancelled at any point (`CancelBooking`), and it expires when `paymentDeadline` (20 min) passes (`ExpireBooking`). Only a `PENDING` booking can be modified.
