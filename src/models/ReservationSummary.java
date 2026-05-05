package models;

import java.math.BigDecimal;

public class ReservationSummary {
    private final int reservationId;
    private final String customerSsn;
    private final String customerFirstName;
    private final String customerLastName;
    private final String reservationDate;
    private final String reservationStatus;
    private final BigDecimal totalPrice;
    private final String airlineId;
    private final int flightNum;

    public ReservationSummary(
            int reservationId,
            String customerSsn,
            String customerFirstName,
            String customerLastName,
            String reservationDate,
            String reservationStatus,
            BigDecimal totalPrice,
            String airlineId,
            int flightNum
    ) {
        this.reservationId = reservationId;
        this.customerSsn = customerSsn;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.reservationDate = reservationDate;
        this.reservationStatus = reservationStatus;
        this.totalPrice = totalPrice;
        this.airlineId = airlineId;
        this.flightNum = flightNum;
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getCustomerSsn() {
        return customerSsn;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getAirlineId() {
        return airlineId;
    }

    public int getFlightNum() {
        return flightNum;
    }
}
