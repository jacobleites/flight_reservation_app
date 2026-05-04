package models;

import java.math.BigDecimal;

public class Ticket {
    private int ticket_num;
    private int reservation_id;
    private Integer instance_id;
    private String airline_id;
    private Integer flight_num;
    private int segment_num;
    private String seat_num;
    private BigDecimal fare;
    private String pay_date;
    private boolean special_meal;
    private String direction;
    private String ticket_class;
    private String status;

    public Ticket(int ticket_num, int reservation_id, String airline_id, int flight_num, int segment_num,
                  String seat_num, float fare, String pay_date, boolean special_meal, String direction,
                  String ticket_class) {
        this(ticket_num, reservation_id, null, airline_id, flight_num, segment_num, seat_num,
                BigDecimal.valueOf(fare), pay_date, special_meal, direction, ticket_class, "Booked");
    }

    public Ticket(
            int ticket_num,
            int reservation_id,
            Integer instance_id,
            String airline_id,
            Integer flight_num,
            int segment_num,
            String seat_num,
            BigDecimal fare,
            String pay_date,
            boolean special_meal,
            String direction,
            String ticket_class,
            String status
    ) {
        this.ticket_num = ticket_num;
        this.reservation_id = reservation_id;
        this.instance_id = instance_id;
        this.airline_id = airline_id;
        this.flight_num = flight_num;
        this.segment_num = segment_num;
        this.seat_num = seat_num;
        this.fare = fare;
        this.pay_date = pay_date;
        this.special_meal = special_meal;
        this.direction = direction;
        this.ticket_class = ticket_class;
        this.status = status;
    }

    public int getTicketNum() {
        return ticket_num;
    }

    public int getReservationId() {
        return reservation_id;
    }

    public Integer getInstanceId() {
        return instance_id;
    }

    public String getAirlineId() {
        return airline_id;
    }

    public Integer getFlightNum() {
        return flight_num;
    }

    public int getSegmentNum() {
        return segment_num;
    }

    public String getSeatNum() {
        return seat_num;
    }

    public float getFare() {
        return fare == null ? 0f : fare.floatValue();
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

    public String getStatus() {
        return status;
    }

    public BigDecimal getFareAmount() {
        return fare;
    }
}
