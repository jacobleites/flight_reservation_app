package models;

import java.math.BigDecimal;

public class SalesReportRow {
    private final int reservationId;
    private final String reservationDate;
    private final String customerSsn;
    private final BigDecimal ticketRevenue;
    private final BigDecimal bookingFee;
    private final BigDecimal totalRevenue;

    public SalesReportRow(
            int reservationId,
            String reservationDate,
            String customerSsn,
            BigDecimal ticketRevenue,
            BigDecimal bookingFee
    ) {
        this.reservationId = reservationId;
        this.reservationDate = reservationDate;
        this.customerSsn = customerSsn;
        this.ticketRevenue = ticketRevenue == null ? BigDecimal.ZERO : ticketRevenue;
        this.bookingFee = bookingFee == null ? BigDecimal.ZERO : bookingFee;
        this.totalRevenue = this.ticketRevenue.add(this.bookingFee);
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public String getCustomerSsn() {
        return customerSsn;
    }

    public BigDecimal getTicketRevenue() {
        return ticketRevenue;
    }

    public BigDecimal getBookingFee() {
        return bookingFee;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
}
