package models;

public class Aircraft {
    private String airline_id;
    private int aircraft_id;
    private int capacity;
    private String model;

    public Aircraft(String airline_id, int aircraft_id, int capacity, String model){
        this.airline_id = airline_id;
        this.aircraft_id = aircraft_id;
        this.capacity = capacity;
        this.model = model;
    }

    public String getAirlineId(){
        return this.airline_id;
    }

    public int getAircraftId(){
        return this.aircraft_id;
    }

    public int getCapacity(){
        return this.capacity;
    } 

    public String getModel(){
        return this.model;
    }
}   
