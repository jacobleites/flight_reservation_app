# some seed data to play around with, sets up:
# 	6 airports
# 	3 airlines
# 	6 aircraft
# 	several flights
# 	flight operating days
#	domestic/international flight labels
# 	3 customers
# 	4 reservations
#	multiple tickets, including one-way, round-trip, and connecting flights

# =========================
# AIRPORT DATA
# =========================

INSERT INTO Airport (airport_id, airport_name, airport_city)
VALUES
('EWR', 'Newark Liberty International Airport', 'Newark'),
('JFK', 'John F. Kennedy International Airport', 'New York'),
('LAX', 'Los Angeles International Airport', 'Los Angeles'),
('DFW', 'Dallas/Fort Worth International Airport', 'Dallas'),
('ORD', 'O Hare International Airport', 'Chicago'),
('LHR', 'Heathrow Airport', 'London');


# =========================
# AIRLINE DATA
# =========================

INSERT INTO Airline (airline_id, airline_name)
VALUES
('UA', 'United Airlines'),
('AA', 'American Airlines'),
('DL', 'Delta Air Lines');


# =========================
# AIRCRAFT DATA
# =========================

INSERT INTO Aircraft (aircraft_id, airline_id, capacity, model)
VALUES
(1001, 'UA', 180, 'Boeing 737-900'),
(1002, 'UA', 250, 'Boeing 787-9'),
(2001, 'AA', 160, 'Airbus A320'),
(2002, 'AA', 285, 'Boeing 777-300ER'),
(3001, 'DL', 170, 'Airbus A321'),
(3002, 'DL', 220, 'Boeing 767-300');


# =========================
# OPERATES DATA
# Airlines operating out of airports
# =========================

INSERT INTO Operates (airport_id, airline_id)
VALUES
('EWR', 'UA'),
('JFK', 'UA'),
('LAX', 'UA'),
('ORD', 'UA'),
('LHR', 'UA'),

('EWR', 'AA'),
('JFK', 'AA'),
('LAX', 'AA'),
('DFW', 'AA'),
('ORD', 'AA'),
('LHR', 'AA'),

('JFK', 'DL'),
('LAX', 'DL'),
('ORD', 'DL'),
('EWR', 'DL');


# =========================
# FLIGHT DATA
# Primary key is (airline_id, flight_num)
# =========================

INSERT INTO Flight (
    airline_id,
    flight_num,
    aircraft_id,
    dep_time,
    arr_time,
    dep_airport,
    arr_airport
)
VALUES
# United domestic flights
('UA', 101, 1001, '08:00:00', '10:45:00', 'EWR', 'DFW'),
('UA', 102, 1001, '12:00:00', '14:10:00', 'DFW', 'LAX'),
('UA', 103, 1001, '09:30:00', '12:30:00', 'EWR', 'ORD'),
('UA', 104, 1001, '15:00:00', '18:15:00', 'ORD', 'LAX'),

# United international flights
('UA', 201, 1002, '19:30:00', '07:45:00', 'EWR', 'LHR'),
('UA', 202, 1002, '10:30:00', '13:45:00', 'LHR', 'EWR'),

# American domestic flights
('AA', 301, 2001, '07:15:00', '10:00:00', 'JFK', 'DFW'),
('AA', 302, 2001, '11:20:00', '13:30:00', 'DFW', 'LAX'),
('AA', 303, 2001, '16:00:00', '22:30:00', 'LAX', 'JFK'),

# American international flights
('AA', 401, 2002, '18:00:00', '06:30:00', 'JFK', 'LHR'),
('AA', 402, 2002, '09:00:00', '12:15:00', 'LHR', 'JFK'),

# Delta domestic flights
('DL', 501, 3001, '06:45:00', '09:30:00', 'JFK', 'ORD'),
('DL', 502, 3001, '13:00:00', '16:20:00', 'ORD', 'LAX'),
('DL', 503, 3002, '20:00:00', '06:00:00', 'LAX', 'JFK');


# =========================
# FLIGHT DAYS DATA
# =========================

INSERT INTO Flight_days (airline_id, flight_num, dotw)
VALUES
# UA 101: EWR -> DFW
('UA', 101, 'Mon'),
('UA', 101, 'Wed'),
('UA', 101, 'Fri'),

# UA 102: DFW -> LAX
('UA', 102, 'Mon'),
('UA', 102, 'Wed'),
('UA', 102, 'Fri'),

# UA 103: EWR -> ORD
('UA', 103, 'Tues'),
('UA', 103, 'Thurs'),

# UA 104: ORD -> LAX
('UA', 104, 'Tues'),
('UA', 104, 'Thurs'),

# UA international
('UA', 201, 'Mon'),
('UA', 201, 'Fri'),
('UA', 202, 'Tues'),
('UA', 202, 'Sat'),

# AA domestic
('AA', 301, 'Mon'),
('AA', 301, 'Wed'),
('AA', 301, 'Fri'),
('AA', 302, 'Mon'),
('AA', 302, 'Wed'),
('AA', 302, 'Fri'),
('AA', 303, 'Sun'),

# AA international
('AA', 401, 'Tues'),
('AA', 401, 'Thurs'),
('AA', 402, 'Wed'),
('AA', 402, 'Sun'),

# DL domestic
('DL', 501, 'Mon'),
('DL', 501, 'Tues'),
('DL', 502, 'Mon'),
('DL', 502, 'Tues'),
('DL', 503, 'Fri'),
('DL', 503, 'Sun');


# =========================
# DOMESTIC FLIGHT DATA
# =========================

INSERT INTO DomesticFlight (airline_id, flight_num)
VALUES
('UA', 101),
('UA', 102),
('UA', 103),
('UA', 104),
('AA', 301),
('AA', 302),
('AA', 303),
('DL', 501),
('DL', 502),
('DL', 503);


# =========================
# INTERNATIONAL FLIGHT DATA
# =========================

INSERT INTO InternationalFlight (airline_id, flight_num)
VALUES
('UA', 201),
('UA', 202),
('AA', 401),
('AA', 402);


# =========================
# CUSTOMER DATA
# =========================

INSERT INTO Customer (
    customer_ssn,
    email,
    gender,
    dob,
    customer_name,
    phone,
    account_id,
    username,
    acc_password
)
VALUES
('111-11-1111', 'john@example.com', 'Male', '2005-04-12', 'John Smith', '908-555-1000', 1, 'johns', 'pass123'),
('222-22-2222', 'alex@example.com', 'Female', '2002-09-18', 'Alex Geppert', '732-555-2000', 2, 'alexg', 'pass456'),
('333-33-3333', 'dana@example.com', 'Male', '1999-01-25', 'Dana Neibert', '201-555-3000', 3, 'danan', 'pass789');


# =================
# TICKET CLASS DATA 
# =================

INSERT INTO Ticket_Class (ticket_class, change_fee)
VALUES
('Economy', 35.00),
('Business', 0.00),
('First', 0.00);


# =========================
# RESERVATION DATA
# Explicit reservation_id values are used so tickets can reference them.
# =========================

INSERT INTO Reservations (
    reservation_id,
    customer_ssn,
    reservation_date,
    status,
    total_price,
    trip_type
)
VALUES
# John books one-way EWR -> DFW -> LAX
(1, '111-11-1111', '2026-04-20 10:15:00', 'Booked', 420.00, 'One_Way'),

# Alex books round trip JFK -> DFW -> LAX and LAX -> JFK
(2, '222-22-2222', '2026-04-21 14:30:00', 'Booked', 780.00, 'Round_Trip'),

# Dana books international round trip EWR -> LHR and LHR -> EWR
(3, '333-33-3333', '2026-04-22 09:00:00', 'Booked', 1350.00, 'Round_Trip'),

# Jason has a cancelled domestic reservation
(4, '111-11-1111', '2026-04-23 16:45:00', 'Cancelled', 260.00, 'One_Way');


# =========================
# TICKET DATA
# Each ticket is one flight segment.
# =========================

INSERT INTO Ticket (
    ticket_num,
    reservation_id,
    airline_id,
    flight_num,
    segment_num,
    seat_num,
    fare,
    pay_date,
    special_meal,
    direction,
    ticket_class
)

VALUES
# Reservation 1: John, one-way EWR -> DFW -> LAX
(1, 1, 'UA', 101, 1, '12A', 180.00, '2026-04-20 10:20:00', FALSE, 'Outbound', 'Economy'),
(2, 1, 'UA', 102, 2, '14C', 240.00, '2026-04-20 10:20:00', FALSE, 'Outbound', 'Economy'),

# Reservation 2: Alex, round trip JFK -> DFW -> LAX, then LAX -> JFK
(3, 2, 'AA', 301, 1, '03A', 210.00, '2026-04-21 14:40:00', TRUE, 'Outbound', 'Business'),
(4, 2, 'AA', 302, 2, '04B', 250.00, '2026-04-21 14:40:00', TRUE, 'Outbound', 'Business'),
(5, 2, 'AA', 303, 3, '05C', 320.00, '2026-04-21 14:40:00', TRUE, 'Return', 'Business'),

# Reservation 3: Dana, international round trip EWR -> LHR, then LHR -> EWR
(6, 3, 'UA', 201, 1, '01A', 700.00, '2026-04-22 09:05:00', TRUE, 'Outbound', 'First'),
(7, 3, 'UA', 202, 2, '01B', 650.00, '2026-04-22 09:05:00', TRUE, 'Return', 'First'),

# Reservation 4: John, cancelled one-way JFK -> ORD
(8, 4, 'DL', 501, 1, '22D', 260.00, '2026-04-23 16:50:00', FALSE, 'Outbound', 'Economy');