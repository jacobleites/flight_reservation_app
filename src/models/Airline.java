package models;

public class Airline {
    private String airline_id;
    private String airline_name;
    
    public Airline(String id, String name){
        this.airline_id = id;
        this.airline_name = name;
    }

    public String getId(){
        return this.airline_id;
    }

    public String getName(){
        return this.airline_name;
    }
}
