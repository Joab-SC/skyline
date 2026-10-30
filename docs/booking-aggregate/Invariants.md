# Booking Aggregate: Invariants

* A booking always has an outbound flight.
* Only when the trip type is ROUND_TRIP, it must have a return flight.
* Once the passengers are registered, exactly one of them is the holder.
* All the passengers in a Booking are unique.
* Only a PENDING booking can be modified, confirmed, canceled, or expired.
* A booking cannot be modified after its payment deadline. Every change (return flight, passengers, holder, seats, luggage, extra services) checks it with `validateIsModifiable()`.
* A CANCELLED, EXPIRED or CONFIRMED booking cannot change its status.
* A booking becomes CONFIRMED only when one of its payments is APPROVED.
* A payment can start only when the booking is PENDING, before its payment deadline, with every passenger and a holder.
* A booking has at most one PENDING payment.
* A payment gets its result only once, and an APPROVED payment always has a transaction id.