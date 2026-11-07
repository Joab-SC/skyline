# Invariants


* All luggage must have a weight of 23 kg.
* All LegSeats must be initially created as available.
* An occupied LegSeat belongs to one passenger of one booking (`PassengerBookingId`). A passenger of a booking has at most one seat per Leg: choosing another seat releases the old one.
* The origin airport must be different from the destination airport.
* The departure time must be earlier than the arrival time.
* A Leg must have the same number of seats as the seats provided during its creation.
* The aircraft that flies a Leg must belong to the Leg's airline (checked in `CreateLegUseCase` with `Airline.validateHasAircraft`).
