package models;

import java.math.BigDecimal;

public class Reservation {
    private int reservation_id;
    private String customer_ssn;
    private String reservation_datetime;
    private String status;
    private BigDecimal total_price;
    private BigDecimal booking_fee;
    private String trip_type;
    
    public Reservation(int id, String ssn, String datetime, String status, float price, float bookingFee, String type){
        this(id, ssn, datetime, status, BigDecimal.valueOf(price), BigDecimal.valueOf(bookingFee), type);
    }

    public Reservation(int id, String ssn, String datetime, String status, BigDecimal price, BigDecimal bookingFee, String type){
        this.reservation_id = id;
        this.customer_ssn = ssn;
        this.reservation_datetime = datetime;
        this.status = status;
        this.total_price = price;
        this.booking_fee = bookingFee;
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
        return this.total_price == null ? 0f : this.total_price.floatValue();
    }

    public float getBookingFee() {
        return this.booking_fee == null ? 0f : this.booking_fee.floatValue();
    }

    public String getTripType(){
        return this.trip_type;
    }

    public BigDecimal getTotalPrice() {
        return this.total_price;
    }

    public BigDecimal getBookingFeeAmount() {
        return this.booking_fee;
    }
}
