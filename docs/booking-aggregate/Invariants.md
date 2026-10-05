# Booking Aggregate: Invariants

* A booking always has an outbound flight.
* Only when the trip type is ROUND_TRIP, it must have a return flight.
* Once the passengers are registered, exactly one of them is the holder.
* All the passengers in a Booking are unique.
* Only a PENDING booking can be modified, confirmed, canceled, or expired.
* A CANCELLED, EXPIRED or CONFIRMED booking cannot change its status.