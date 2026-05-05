package models;

public class ActiveFlightSummary {
    private final String airlineId;
    private final int flightNum;
    private final int ticketsSold;

    public ActiveFlightSummary(String airlineId, int flightNum, int ticketsSold) {
        this.airlineId = airlineId;
        this.flightNum = flightNum;
        this.ticketsSold = ticketsSold;
    }

    public String getAirlineId() {
        return airlineId;
    }

    public int getFlightNum() {
        return flightNum;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }
}
