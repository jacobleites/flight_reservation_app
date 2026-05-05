-- ============================================================
-- TEST FLIGHT: Fully Booked EWR -> CMH
-- ============================================================

-- Add CMH airport
INSERT INTO Airport (
    airport_id,
    airport_name,
    airport_city
)
VALUES (
    'CMH',
    'John Glenn Columbus International Airport',
    'Columbus'
);

-- Add a tiny test aircraft with only 1 Economy seat
INSERT INTO Aircraft (
    airline_id,
    aircraft_id,
    capacity,
    economy_class,
    business_class,
    first_class,
    model
)
VALUES (
    'UA',
    999,
    1,
    1,
    0,
    0,
    'Test Aircraft - One Economy Seat'
);

-- Add unique EWR -> CMH test flight
INSERT INTO Flight (
    flight_num,
    airline_id,
    aircraft_id,
    dep_time,
    arr_time,
    arr_airport,
    dep_airport
)
VALUES (
    9999,
    'UA',
    999,
    '14:00:00',
    '15:45:00',
    'CMH',
    'EWR'
);

-- Add one specific flight instance for testing
INSERT INTO Flight_Instance (
    airline_id,
    flight_num,
    dep_datetime,
    arr_datetime,
    aircraft_id,
    fare,
    status
)
VALUES (
    'UA',
    9999,
    '2026-05-20 14:00:00',
    '2026-05-20 15:45:00',
    999,
    100.00,
    'Scheduled'
);

-- Store the generated test flight instance id
SET @test_ewr_cmh_instance_id = LAST_INSERT_ID();

-- Create inventory for the test flight.
-- Economy is fully booked because available_seats = 0.
INSERT INTO Flight_Class_Inventory (
    instance_id,
    ticket_class,
    total_seats,
    available_seats,
    base_price
)
VALUES
    (@test_ewr_cmh_instance_id, 'Economy', 1, 0, 100.00),
    (@test_ewr_cmh_instance_id, 'Business', 0, 0, 200.00),
    (@test_ewr_cmh_instance_id, 'First', 0, 0, 300.00);

-- Customer who already has the only ticket.
INSERT INTO Customer (
    customer_ssn,
    email,
    gender,
    dob,
    firstName,
    lastName,
    phone,
    account_id,
    username,
    acc_password
)
VALUES (
    '444-44-4444',
    'bookedcustomer@test.com',
    'Other',
    '2000-01-01',
    'Booked',
    'Customer',
    '555-444-4444',
    2001,
    'bookedcustomer',
    'password123'
);

-- Create reservation for the booked customer
INSERT INTO Reservations (
    customer_ssn,
    reservation_date,
    status,
    booking_fee,
    total_price,
    trip_type
)
VALUES (
    '444-44-4444',
    CURRENT_TIMESTAMP,
    'Booked',
    25.00,
    125.00,
    'One_Way'
);

SET @test_ewr_cmh_reservation_id = LAST_INSERT_ID();

-- Ticket occupying the only Economy seat
INSERT INTO Ticket (
    reservation_id,
    instance_id,
    segment_num,
    fare,
    pay_date,
    special_meal,
    direction,
    ticket_class,
    status
)
VALUES (
    @test_ewr_cmh_reservation_id,
    @test_ewr_cmh_instance_id,
    1,
    100.00,
    CURRENT_TIMESTAMP,
    FALSE,
    'Outbound',
    'Economy',
    'Booked'
);

SELECT * FROM Flight_instance
WHERE Flight_num = 9999;