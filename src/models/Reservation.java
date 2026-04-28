package models;

public class Reservation {
    private int reservation_id;
    private String customer_ssn;
    private String reservation_datetime;
    private String status;
    private float total_price;
    private String trip_type;
    
    public Reservation(int id, String ssn, String datetime, String status, float price, String type){
        this.reservation_id = id;
        this.customer_ssn = ssn;
        this.reservation_datetime = datetime;
        this.status = status;
        this.total_price = price;
        this.trip_type = type;
    }

    public int getId(){
        return this.reservation_id;
    }

    public String getSSN(){
        return this.customer_ssn;
    }

    public String getReservationDate(){
        return this.reservation_datetime;
    }

    public String getStatus(){
        return this.status;
    }

    public float getPrice(){
        return this.total_price;
    }

    public String getTripType(){
        return this.trip_type;
    }
}
