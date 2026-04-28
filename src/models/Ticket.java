package models;

public class Ticket {
    private int ticket_num;
    private int reservation_id;
    private String airline_id;
    private int flight_num;
    private int segment_num;
    private String seat_num;
    private float fare;
    private String pay_date;
    private boolean special_meal;
    private String direction;
    private String ticket_class;

    public Ticket(int ticket_num, int reservation_id, String airline_id, int flight_num, int segment_num,
                  String seat_num, float fare, String pay_date, boolean special_meal, String direction,
                  String ticket_class) {
        
        this.ticket_num = ticket_num;
        this.reservation_id = reservation_id;
        this.airline_id = airline_id;
        this.flight_num = flight_num;
        this.segment_num = segment_num;
        this.seat_num = seat_num;
        this.fare = fare;
        this.pay_date = pay_date;
        this.special_meal = special_meal;
        this.direction = direction;
        this.ticket_class = ticket_class;
    }

    public int getTicketNum() {
        return ticket_num;
    }

    public int getReservationId() {
        return reservation_id;
    }

    public String getAirlineId() {
        return airline_id;
    }

    public int getFlightNum() {
        return flight_num;
    }

    public int getSegmentNum() {
        return segment_num;
    }

    public String getSeatNum() {
        return seat_num;
    }

    public float getFare() {
        return fare;
    }

    public String getPayDate() {
        return pay_date;
    }

    public boolean getSpecialMeal() {
        return special_meal;
    }

    public String getDirection() {
        return direction;
    }

    public String getTicketClass() {
        return ticket_class;
    }

}
