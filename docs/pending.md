# Pending

Open items in the code and docs. Each use case is described in [use-cases/README.md](use-cases/README.md). Items already done were removed; see the git history for them.

No new features for now: the goal is to fix what exists so the front can run the whole booking flow.

## Close the purchase
- [ ] Show each passenger's seat per leg in the booking response. Seats live in `LegSeat`, so `GET /api/bookings/{id}` does not show them, and the front cannot tell "my seat" from "taken by someone else" in the seat map. `LegSeat` now stores the booking id, so the seats of a booking can be found by it. → `BookingResponse`, `GetBookingUseCase`

## Changing a booking (overwrite instead of error)
Selecting the outbound or return flight again stays an error (the return flight already throws "The booking already has a return flight"). Seats, extra services and luggage are overwritten, and an empty value removes what was chosen, because the user may have chosen by mistake or wants to change it.
- [ ] Assign seat with no seat removes the passenger's seat on that leg and subtracts the seat price. Today `legSeatId` is `@NotBlank`, so it answers 400. → `SelectSeatRequest`, `SelectLegSeatUseCase`, `SeatAssigner`, `Leg`
- [ ] Add extra service with quantity 0 removes that service from the booking and subtracts its subtotal. Today `quantity` is `@Positive`, so it answers 400. → `AddExtraServiceRequest`, `Booking.addExtraService`
- [ ] Decide whether luggage with quantity 0 also removes the passenger's luggage, so the three work the same way. Today it answers 400 (`@Positive`). → `AddLuggageRequest`, `Booking.registerPassengerLuggage`

## Users
- [ ] The email is case sensitive: `laura@x.com` and `LAURA@x.com` can be registered as two accounts. Save it in lowercase (and trimmed) when registering and when logging in. → `User`, `RegisterCustomerUseCase`, `AuthenticationService`
- [ ] Decide whether a customer must be an adult. Today a person born a few years ago can register. The booking holder already must be an adult. → `User.registerCustomer`
- [ ] Decide whether two accounts can have the same document. Today they can. → `UserRepository`, `RegisterCustomerUseCase`

## Payments
- [ ] A `PENDING` payment attempt is charged again with the same reference, assuming it was never charged. With a real provider, ask the provider what happened to that reference before charging again, and register its result. → `PaymentGateway`, `PayBooking`, `Booking.startPayment`
- [ ] CancelBooking and ExpireBooking do not look at the payments. A booking with a `PENDING` attempt that the provider did charge can be cancelled or expired, and the money must be returned by hand. → `Booking.cancelBooking`, `Booking.expireBooking`

## Cancellation and expiration
- [ ] Soft delete of the booking and its flights (no field exists). To be decided. → `Booking`, `Flight`

## Flight search cache
- [ ] `FlightSearchCacheInMemory` uses a `HashMap` with no expiration: it is not thread-safe and keeps every searched flight forever. Use a `ConcurrentHashMap` and remove entries after ~20 min. → `FlightSearchCacheInMemory`

## Postponed
- Check-in and boarding pass (CheckInPassenger). The front comes first; check-in is done later only if there is time.
