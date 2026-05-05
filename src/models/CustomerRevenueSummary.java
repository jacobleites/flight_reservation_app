package models;

import java.math.BigDecimal;

public class CustomerRevenueSummary {
    private final String customerSsn;
    private final String firstName;
    private final String lastName;
    private final BigDecimal totalRevenue;
    private final int reservationCount;

    public CustomerRevenueSummary(
            String customerSsn,
            String firstName,
            String lastName,
            BigDecimal totalRevenue,
            int reservationCount
    ) {
        this.customerSsn = customerSsn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalRevenue = totalRevenue == null ? BigDecimal.ZERO : totalRevenue;
        this.reservationCount = reservationCount;
    }

    public String getCustomerSsn() {
        return customerSsn;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public int getReservationCount() {
        return reservationCount;
    }
}
