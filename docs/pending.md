# Pending

Open items in the code and docs. Each use case is described in [use-cases/README.md](use-cases/README.md). Items already done were removed; see the git history for them.

## Catalog
- [ ] `CreateAircraftUseCase` and `CreateLegUseCase` call `airline.addAircraft` / `airline.addLeg` but never save the airline, so with JPA the airline loses the link. Call `airlineRepository.save(airline)`. → `CreateAircraftUseCase`, `CreateLegUseCase`

## Close the purchase
- [ ] Decide whether the booking response shows each passenger's seat per leg. Seats live in `LegSeat`, so `GET /api/bookings/{id}` does not show them. → `BookingResponse`, `GetBookingUseCase`
- [ ] `registerPassengerLuggage` checks `PENDING` but not the payment deadline. Use `validateIsModifiable()` like extra services and seats. → `Booking`

## Payments
- [ ] A `PENDING` payment attempt is charged again with the same reference, assuming it was never charged. With a real provider, ask the provider what happened to that reference before charging again, and register its result. → `PaymentGateway`, `PayBooking`, `Booking.startPayment`
- [ ] CancelBooking and ExpireBooking do not look at the payments. A booking with a `PENDING` attempt that the provider did charge can be cancelled or expired, and the money must be returned by hand. → `Booking.cancelBooking`, `Booking.expireBooking`

## Cancellation and expiration
- [ ] Soft delete of the booking and its flights (no field exists). To be decided. → `Booking`, `Flight`

## Seats
- [ ] `LegSeat` stores only the passenger's document. Since the same person can be in two bookings, if both bookings include the same leg, assigning a seat in one booking releases the seat taken in the other. Cancelling or expiring one of them can also release the other booking's seat. Store the booking id with the document. → `LegSeat`, `Leg.assignSeat`, `Leg.hasSeatAssigned`, `Leg.releaseSeatOfPassenger`

## Flight search cache
- [ ] `FlightSearchCacheInMemory` uses a `HashMap` with no expiration: it is not thread-safe and keeps every searched flight forever. Use a `ConcurrentHashMap` and remove entries after ~20 min. → `FlightSearchCacheInMemory`

## Tests
- [ ] `CreateAircraftUseCase` and `CreateLegUseCase` have no use-case tests. → `application/usecase`
