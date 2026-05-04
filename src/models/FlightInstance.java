package models;

public class FlightInstance {
    private int instance_id;
    private int flight_num;
    private String airline_id;
    private String dep_datetime;
    private String arr_datetime;
    private String dep_airport;
    private String arr_airport;
    private int available_seats;
    private double price;
    private String status;
    private String dep_airport_name;
    private String arr_airport_name;
    private String aircraft_model;

    public FlightInstance(int instance_id, int flight_num, String airline_id, String dep_datetime,
                          String arr_datetime, String dep_airport, String arr_airport,
                          int available_seats, double price, String status) {
        this(instance_id, flight_num, airline_id, dep_datetime, arr_datetime, dep_airport, arr_airport,
                available_seats, price, status, null, null, null);
    }

    public FlightInstance(int instance_id, int flight_num, String airline_id, String dep_datetime,
                          String arr_datetime, String dep_airport, String arr_airport,
                          int available_seats, double price, String status,
                          String dep_airport_name, String arr_airport_name, String aircraft_model) {
        this.instance_id = instance_id;
        this.flight_num = flight_num;
        this.airline_id = airline_id;
        this.dep_datetime = dep_datetime;
        this.arr_datetime = arr_datetime;
        this.dep_airport = dep_airport;
        this.arr_airport = arr_airport;
        this.available_seats = available_seats;
        this.price = price;
        this.status = status;
        this.dep_airport_name = dep_airport_name;
        this.arr_airport_name = arr_airport_name;
        this.aircraft_model = aircraft_model;
    }

    public int getInstanceId() {
        return this.instance_id;
    }

    public int getFlightNumber() {
        return this.flight_num;
    }

    public String getAirlineId() {
        return this.airline_id;
    }

    public String getDepartureDateTime() {
        return this.dep_datetime;
    }

    public String getArrivalDateTime() {
        return this.arr_datetime;
    }

    public String getDepartureAirport() {
        return this.dep_airport;
    }

    public String getArrivalAirport() {
        return this.arr_airport;
    }

    public int getAvailableSeats() {
        return this.available_seats;
    }

    public double getPrice() {
        return this.price;
    }

    public String getStatus() {
        return this.status;
    }

    public String getDepartureAirportName() {
        return this.dep_airport_name;
    }

    public String getArrivalAirportName() {
        return this.arr_airport_name;
    }

    public String getAircraftModel() {
        return this.aircraft_model;
    }
}
