# Pending

Open items in the code and docs. Each use case is described in [use-cases/README.md](use-cases/README.md). Items already done were removed; see the git history for them.

## Close the purchase
- [ ] GetBooking: return a booking by id with `GET /api/bookings/{id}`. `POST /api/bookings` already returns a `Location` header that points there, and it answers 404 today. → new use case, `BookingController`
- [ ] Decide whether the booking response shows each passenger's seat per leg. Seats live in `LegSeat`, so the booking alone does not know them. → `BookingResponse`, GetBooking
- [ ] Implement PayBooking: `Booking.confirm()` checks `PENDING` and the payment deadline, then sets `CONFIRMED`. Add the endpoint. → `Booking`, new use case, `BookingController`
- [ ] PayBooking with a passed deadline: only throw `DomainRuleException`. Expiring the booking and releasing its seats belongs to the expiration job. → `pay-booking.md` (step 2 says to expire it there)
- [ ] `registerPassengerLuggage` checks `PENDING` but not the payment deadline. Use `validateIsModifiable()` like extra services and seats. → `Booking`

## Cancellation and expiration
- [ ] `cancelBooking()` must validate `PENDING`. → `Booking`
- [ ] Method to restore available seats, the opposite of `reduceAvailableSeats`. → `Leg`, a domain service like `SeatsAvailableReducer`
- [ ] Release the `LegSeat`s assigned to the booking's passengers on every leg of its outbound and return flights. → `Leg`, `LegSeat`
- [ ] Implement CancelBooking with its endpoint. → new use case, `BookingController`
- [ ] Expire bookings: the loop body is empty. Reuse the cancel logic with `EXPIRED`. → `BookingExpirationValidator`
- [ ] `@Scheduled` never runs because `@EnableScheduling` is missing. → `SkylineApplication` or a config class
- [ ] `BookingExpirationValidator` is in `domain/` but imports Spring's `@Scheduled`. Move the scheduling to `infrastructure/` and keep the rule in the domain. → `BookingExpirationValidator`
- [ ] Soft delete of the booking and its flights (no field exists). To be decided. → `Booking`, `Flight`

## Seats
- [ ] `LegSeat` stores only the passenger's document. Since the same person can be in two bookings, if both bookings include the same leg, assigning a seat in one booking releases the seat taken in the other. Store the booking id with the document. → `LegSeat`, `Leg.assignSeat`, `Leg.hasSeatAssigned`

## Flight search cache
- [ ] `FlightSearchCacheInMemory` uses a `HashMap` with no expiration: it is not thread-safe and keeps every searched flight forever. Use a `ConcurrentHashMap` and remove entries after ~20 min. → `FlightSearchCacheInMemory`

## Extra services
- [ ] Flight scope: the code marks a flight international when **any** leg crosses a border, so layovers count. `add-extra-services.md` says only origin and destination count (BOG→PTY→MDE would be national). Decide one rule and align code and doc. → `Flight.determineFlightScope`, `add-extra-services.md`

## Tests
- [ ] `CreateAircraftUseCase` and `CreateLegUseCase` have no use-case tests. → `application/usecase`

## Docs
- [ ] AddPassengersToBooking and AssignSeatToPassenger say "Not implemented". They exist as `AddPassengerUseCase` (`PUT /api/bookings/{id}/passengers`) and `SelectLegSeatUseCase` (`PUT /api/bookings/{id}/seats`). → `use-cases/README.md`, `add-passengers-to-booking.md`, `assign-seat-to-passenger.md`
- [ ] SearchFlights, SelectOutboundFlight and SelectReturnFlight say they differ from the design or that the cache is pending. Those items are done. → `search-flights.md`, `select-outbound-flight.md`, `select-return-flight.md`, `use-cases/README.md`
- [ ] Add `EXPIRED` to the booking states. → `glosario_lenguaje_obicuo.md`
- [ ] Change the invariant "one seat per booking" to "one seat per leg". → `diagrama-agregado.md`
- [ ] Remove the invariant "an extra service must be available on the booking flights" (now global per airline). → `diagrama-agregado.md`
- [ ] Mark the check-in invariant as out of scope for now. → `diagrama-agregado.md`
- [ ] Update the diagram to match the code (`double` instead of `BigDecimal`, no `GroupLuggageDetail`, methods on `Booking`). → `docs/booking-aggregate/booking-aggregate-code.md`
- [ ] The tables of implemented use cases are out of date (seat and luggage endpoints exist, `EXPIRED` exists). → `docs/plan-casos-de-uso.md`
