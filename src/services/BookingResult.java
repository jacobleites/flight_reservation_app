package services;

import java.math.BigDecimal;

public class BookingResult {
    private final boolean success;
    private final boolean waitlisted;
    private final boolean alreadyWaitlisted;
    private final boolean flightFull;
    private final String message;
    private final Integer reservationId;
    private final Integer ticketNum;
    private final Integer fullInstanceId;
    private final BigDecimal totalPrice;

    public BookingResult(
            boolean success,
            boolean waitlisted,
            boolean alreadyWaitlisted,
            boolean flightFull,
            String message,
            Integer reservationId,
            Integer ticketNum,
            Integer fullInstanceId,
            BigDecimal totalPrice
    ) {
        this.success = success;
        this.waitlisted = waitlisted;
        this.alreadyWaitlisted = alreadyWaitlisted;
        this.flightFull = flightFull;
        this.message = message;
        this.reservationId = reservationId;
        this.ticketNum = ticketNum;
        this.fullInstanceId = fullInstanceId;
        this.totalPrice = totalPrice;
    }

    public static BookingResult success(String message, Integer reservationId, Integer ticketNum, BigDecimal totalPrice) {
        return new BookingResult(true, false, false, false, message, reservationId, ticketNum, null, totalPrice);
    }

    public static BookingResult failure(String message) {
        return new BookingResult(false, false, false, false, message, null, null, null, null);
    }

    public static BookingResult flightFull(String message, Integer instanceId) {
        return new BookingResult(false, false, false, true, message, null, null, instanceId, null);
    }

    public static BookingResult waitlisted(String message) {
        return new BookingResult(true, true, false, false, message, null, null, null, null);
    }

    public static BookingResult alreadyWaitlisted(String message) {
        return new BookingResult(false, false, true, false, message, null, null, null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isWaitlisted() {
        return waitlisted;
    }

    public boolean isAlreadyWaitlisted() {
        return alreadyWaitlisted;
    }

    public boolean isFlightFull() {
        return flightFull;
    }

    public String getMessage() {
        return message;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public Integer getTicketNum() {
        return ticketNum;
    }

    public Integer getFullInstanceId() {
        return fullInstanceId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}
