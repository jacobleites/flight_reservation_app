package models;

public class Airport{
    private String airport_id;
    private String airport_name;
    private String airport_city;
    
    public Airport(String id, String name, String city){
        this.airport_id = id;
        this.airport_name = name;
        this.airport_city = city;
    }

    public String getId(){
        return this.airport_id;
    }
    
    public String getName(){
        return this.airport_name;
    }

    public String getCity(){
        return this.airport_city;
    }
}
