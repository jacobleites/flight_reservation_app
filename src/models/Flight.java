package models;

public class Flight {
    private int flight_num;
    private String airline_id;
    private int aircraft_id;
    private String dep_time;
    private String arr_time; 
    private String arr_airport; // airport_id 
    private String dep_airport; // airport_id
    
    public Flight ( int flight_num, String airline_id, int aircraft_id, String dep_time, 
                                    String arr_time, String arr_airport, String dep_airport)  {
        this.flight_num = flight_num;
        this.airline_id = airline_id;
        this.aircraft_id = aircraft_id;
        this.dep_time = dep_time;
        this.arr_time = arr_time;
        this.arr_airport = arr_airport;
        this.dep_airport = dep_airport;
    }
    
    public int getFlightNumber(){
        return this.flight_num;
    }

    public String getAirlineId(){
        return this.airline_id;
    }

    public int getAircraftId(){
        return this.aircraft_id;
    }
    
    public String getDepartureTime(){
        return this.dep_time;
    }

    public String getArrivalTime(){
        return this.arr_time;
    }

    public String getArrivalAirport(){
        return this.arr_airport;
    }

    public String getDepartureAirport(){
        return this.dep_airport;
    }
}
