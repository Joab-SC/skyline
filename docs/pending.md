# Pending

Open items in the code and docs. Each use case is described in [use-cases/README.md](use-cases/README.md).

## Flight selection
- [ ] Create the `FlightSearchCache` port (`saveAll`, `findById`), separate from `FlightRepository`. → `domain/repository/FlightSearchCache`
- [ ] Implement it in memory with a `ConcurrentHashMap` and expire entries after ~20 min. → `infrastructure/.../FlightSearchCacheInMemory`
- [ ] Save the search results in the cache. → `SearchFlightsUseCase`
- [ ] On selection, read the flight from `FlightSearchCache` and save it in `FlightRepository` before reducing seats. → `SelectOutboundFlightUseCase`, `SelectReturnFlightUseCase`
- [ ] Booking price = `flight.price × numPassengers`; remove the `price` input. → `SelectOutboundFlightUseCase`, `Booking.createPendingBooking`
- [ ] Return price added = `flight.price × numPassengers`. → `SelectReturnFlightUseCase`, `Booking.addReturnFlight`
- [ ] Save the booking. → `SelectReturnFlightUseCase`
- [ ] Validate that the booking is `PENDING`. → `SelectReturnFlightUseCase`
- [ ] Validate airports before `addReturnFlight`, not after. → `SelectReturnFlightUseCase`
- [ ] Add messages to the empty `DomainRuleException("")`. → `SelectReturnFlightUseCase`
- [ ] Rule: outbound and return flights from the same airline. → `OutboundAndReturnFlightsValidator`

## Passengers and seats
- [ ] Replace `Seat seat` with one seat reference per leg (`legId → seatCode`). → `Passenger`
- [ ] Validations in the factories (`createPerson` and `createPassenger` exist without validations). → `Passenger`, `Person`
- [ ] Implement AddPassengersToBooking. → new use case, `Booking`
- [ ] Define how the passenger list is received (DTO or other). → AddPassengersToBooking
- [ ] Implement AssignSeatToPassenger. → new use case
- [ ] Methods to occupy and release a seat. → `Leg`, `LegSeat`
- [ ] Change the invariant "one seat per booking" to "one seat per leg". → `diagrama-agregado.md`

## Luggage
- [x] AddLuggage implemented: `LuggageDetail` validates `maxQuantity`, `Booking.createLuggageDetail` / `registerPassengerLuggage`, `LuggageDetailCreator`, `AddLuggageUseCase`, with tests.
- [ ] Validations in `createAirline` (name, `maximumLuggageAmount > 0`). → `Airline`
- [ ] Validations in `createPerson` / `createPassenger` (defined in AddPassengersToBooking). → `Person`, `Passenger`
- [ ] Replace `booking.getPassengerList().add(...)` in tests with `addPassengers` once it exists. → `BookingTest`, `AddLuggageUseCaseTest`

## Extra services
- [ ] `ExtraService` as part of the Airline aggregate (list in `Airline`, getters). → `Airline`, `ExtraService`
- [ ] `List<String> extraServices` → `List<DetailExtraService>`. → `Booking`
- [ ] Make `DetailExtraService` a value object: no id; its factory is commented out. → `DetailExtraService`
- [ ] Implement AddExtraServices. → new use case
- [ ] International flight, **proposal to be confirmed**: `Country` enum in `City`; international = origin country ≠ destination country. → `City`, `Airport`, `Flight`
- [ ] Remove the invariant "an extra service must be available on the booking flights" (now global per airline). → `diagrama-agregado.md`

## Payment, cancellation and expiration
- [ ] Implement PayBooking, including the confirm method. → new use case, `Booking`
- [ ] Implement CancelBooking. → new use case
- [ ] `cancelBooking()` must validate `PENDING`. → `Booking`
- [ ] Method to restore available seats. → `Leg`
- [ ] Expire bookings: the loop body is empty. → `BookingExpirationValidator`
- [ ] Add `EXPIRED` to the booking states. → `glosario_lenguaje_obicuo.md`
- [ ] Soft delete of the booking and its flights (no field exists). → `Booking`, `Flight`

## Docs
- [ ] Update the diagram to match the code (`BigDecimal`, `GroupLuggageDetail`, methods on `Booking`). → `docs/booking-aggregate/booking-aggregate-code.md`
- [ ] Mark the check-in invariant as out of scope for now. → `diagrama-agregado.md`
