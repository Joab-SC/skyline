# Pending

Open items in the code and docs. Each use case is described in [use-cases/README.md](use-cases/README.md). Items already done were removed; see the git history for them.

## Catalog
- [ ] `CreateAircraftUseCase` and `CreateLegUseCase` call `airline.addAircraft` / `airline.addLeg` but never save the airline, so with JPA the airline loses the link. Call `airlineRepository.save(airline)`. → `CreateAircraftUseCase`, `CreateLegUseCase`

## Close the purchase
- [ ] Decide whether the booking response shows each passenger's seat per leg. Seats live in `LegSeat`, so `GET /api/bookings/{id}` does not show them. → `BookingResponse`, `GetBookingUseCase`
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

## Tests
- [ ] `CreateAircraftUseCase` and `CreateLegUseCase` have no use-case tests. → `application/usecase`
