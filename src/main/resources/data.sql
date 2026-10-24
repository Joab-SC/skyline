-- Test data loaded on startup (H2 in memory).
-- Tests that use this data depend on these exact values.

-- Airlines
INSERT INTO airlines (id, name, maximum_luggage_amount, price_seats) VALUES
    ('airline-avianca', 'Avianca', 3, 45000),
    ('airline-latam', 'LATAM', 2, 38000),
    ('airline-wingo', 'Wingo', 1, 25000);

-- Aircraft ids per airline (the aircrafts table does not exist yet)
INSERT INTO airline_aircrafts (airline_id, aircraft_id) VALUES
    ('airline-avianca', 'aircraft-a320-01'),
    ('airline-avianca', 'aircraft-a320-02'),
    ('airline-avianca', 'aircraft-b787-01'),
    ('airline-latam', 'aircraft-a321-01'),
    ('airline-latam', 'aircraft-a320-03'),
    ('airline-wingo', 'aircraft-b737-01');

-- Leg ids per airline (the legs table does not exist yet)
INSERT INTO airline_legs (airline_id, leg_id) VALUES
    ('airline-avianca', 'leg-bog-mde-01'),
    ('airline-avianca', 'leg-mde-ctg-01'),
    ('airline-latam', 'leg-bog-cli-01');

-- Extra services (description is optional)
INSERT INTO extra_services (id, name_service, description, national_price, international_price, airline_id) VALUES
    ('extra-avianca-meal', 'Meal', 'Hot meal on board', 30000, 60000, 'airline-avianca'),
    ('extra-avianca-wifi', 'Wifi', NULL, 20000, 40000, 'airline-avianca'),
    ('extra-latam-priority', 'Priority boarding', 'Board before other passengers', 25000, 50000, 'airline-latam');
