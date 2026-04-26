CREATE TABLE Airport (
	airport_id char(3), #changed airport id to their respective 3 character label
    airport_name varchar(45),
    airport_city varchar(45),
    PRIMARY KEY(airport_id)
);

CREATE TABLE Airline (
	airline_id char(2), # changed this datatype to char 2 (example, united is UA)
    airline_name varchar(45),
    PRIMARY KEY(airline_id) # dropped the aircraft_id in this primary key
);

CREATE TABLE Aircraft ( 
	airline_id char(2) NOT NULL, # airline that owns this aircraft 
    aircraft_id int,
    capacity int,
    model varchar(45),
    PRIMARY KEY (aircraft_id),
    FOREIGN KEY (airline_id) REFERENCES Airline(airline_id)
);

CREATE TABLE Operates ( #removed aircraft_id from operates
	airport_id char(3),
    airline_id char(2),
    PRIMARY KEY(airport_id, airline_id),
    FOREIGN KEY (airport_id) REFERENCES Airport(airport_id),
    FOREIGN KEY (airline_id) REFERENCES Airline(airline_id)
);

CREATE TABLE Flight ( #removed dotw_op and added flight_days as a sub table, also removed seq_num
	flight_num int,
    airline_id CHAR(2) NOT NULL,
    aircraft_id int NOT NULL,
    dep_time TIME,
    arr_time TIME,
    arr_airport char(3) NOT NULL,
    dep_airport char(3) NOT NULL,
    PRIMARY KEY (airline_id, flight_num), # removed aircraft_id from the primary key, unnecssary
    FOREIGN KEY (airline_id) REFERENCES Airline(airline_id),
    FOREIGN KEY (aircraft_id) REFERENCES Aircraft(aircraft_id),
    FOREIGN KEY (arr_airport) REFERENCES Airport(airport_id), # added this foreign key referencing airport
    FOREIGN KEY (dep_airport) REFERENCES Airport(airport_id) # added this foreign key referencing airport
); 

CREATE TABLE Flight_days (
	airline_id char(2),
    flight_num int,
    dotw ENUM('Mon','Tues','Wed','Thurs','Fri','Sat','Sun'),
    PRIMARY KEY (airline_id, flight_num, dotw),
    FOREIGN KEY (airline_id, flight_num) REFERENCES Flight(airline_id, flight_num) ON DELETE CASCADE
);

CREATE TABLE DomesticFlight ( # not neccesary to have this table make references to everything, all that matters is flights 
	airline_id CHAR(2) NOT NULL,
    flight_num int NOT NULL,
    PRIMARY KEY (airline_id, flight_num),
    FOREIGN KEY (airline_id, flight_num) REFERENCES Flight(airline_id, flight_num)
		ON DELETE CASCADE 
);

CREATE TABLE InternationalFlight ( # not neccesary to have this table make references to everything, all that matters is flights 
	airline_id CHAR(2) NOT NULL,
    flight_num int NOT NULL, 
    PRIMARY KEY (airline_id, flight_num),
    FOREIGN KEY (airline_id, flight_num) REFERENCES Flight(airline_id, flight_num)
		ON DELETE CASCADE
);

CREATE TABLE Customer (
	customer_ssn char(11),
    email varchar(30),
    gender varchar(10),
    dob date,
    customer_name varchar(45),
    phone varchar(13),
    account_id int UNIQUE,
    username varchar(20) UNIQUE,
    acc_password varchar(30),
    PRIMARY KEY (customer_ssn)
);

CREATE TABLE Reservations ( # added reservations table to keep track of customer flight history
    reservation_id int AUTO_INCREMENT,
	customer_ssn char(11) NOT NULL,
    reservation_date datetime,
    status VARCHAR(20),
    total_price DECIMAL (10,2),
    trip_type ENUM('One_Way', 'Round_Trip') NOT NULL, # moved trip_type (round/oneway) into reservations table 
    PRIMARY KEY (reservation_id),
    FOREIGN KEY (customer_ssn) REFERENCES Customer(customer_ssn)
);

CREATE TABLE Ticket_Class (
	ticket_class VARCHAR(20),
    change_fee DECIMAL(10,2) NOT NULL,
    PRIMARY KEY(ticket_class)
);

CREATE TABLE Ticket ( # replaced sequence_num with seqment_num; added reservation table to keep track of all flight information
	ticket_num int AUTO_INCREMENT,
    reservation_id int NOT NULL,
    airline_id CHAR(2) NOT NULL, # added airline_id to ticket
    flight_num int NOT NULL, # added flight_num
    segment_num int NOT NULL,
    seat_num char(3),
    fare DECIMAL(10, 2),
    pay_date datetime, # removed pay_time and just storing this as datetime
    special_meal boolean,
    direction ENUM('Outbound', 'Return') NOT NULL, # added direction for roundtrip/oneway table tracking.
    ticket_class varchar(20) NOT NULL, # merged class with tickets, 
    PRIMARY KEY (ticket_num),
	FOREIGN KEY (reservation_id) REFERENCES Reservations(reservation_id),
    FOREIGN KEY (airline_id, flight_num) REFERENCES Flight(airline_id, flight_num),
    FOREIGN KEY (ticket_class) REFERENCES Ticket_Class(ticket_class),
    UNIQUE (reservation_id, segment_num)
);





