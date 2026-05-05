package models;

import java.math.BigDecimal;

public class RevenueSummary {
    private final String summaryType;
    private final String summaryKey;
    private final BigDecimal totalRevenue;
    private final int ticketsSold;

    public RevenueSummary(String summaryType, String summaryKey, BigDecimal totalRevenue, int ticketsSold) {
        this.summaryType = summaryType;
        this.summaryKey = summaryKey;
        this.totalRevenue = totalRevenue == null ? BigDecimal.ZERO : totalRevenue;
        this.ticketsSold = ticketsSold;
    }

    public String getSummaryType() {
        return summaryType;
    }

    public String getSummaryKey() {
        return summaryKey;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }
}
