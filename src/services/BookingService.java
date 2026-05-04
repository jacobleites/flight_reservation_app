package services;

import dao.FlightClassInventoryDAO;
import dao.ReservationDAO;
import dao.TicketDAO;
import dao.WaitingLineDAO;
import db.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import models.Ticket;
import models.WaitingLineEntry;

public class BookingService {
    private static final BigDecimal BOOKING_FEE = new BigDecimal("25.00");
    private static final String CLASS_ECONOMY = "Economy";
    private static final String CLASS_BUSINESS = "Business";
    private static final String CLASS_FIRST = "First";

    private final FlightClassInventoryDAO flightClassInventoryDAO;
    private final ReservationDAO reservationDAO;
    private final TicketDAO ticketDAO;
    private final WaitingLineDAO waitingLineDAO;

    public BookingService() {
        this.flightClassInventoryDAO = new FlightClassInventoryDAO();
        this.reservationDAO = new ReservationDAO();
        this.ticketDAO = new TicketDAO();
        this.waitingLineDAO = new WaitingLineDAO();
    }

    public BookingResult bookTrip(
            String customerSsn,
            List<Integer> outboundInstanceIds,
            List<Integer> returnInstanceIds,
            String ticketClass,
            boolean specialMeal,
            String tripType
    ) {
        String normalizedClass = normalizeTicketClass(ticketClass);
        String normalizedTripType = normalizeTripType(tripType);
        if (customerSsn == null || customerSsn.trim().isEmpty()) {
            return BookingResult.failure("Customer SSN is required.");
        }
        if (normalizedClass == null) {
            return BookingResult.failure("Ticket class must be Economy, Business, or First.");
        }
        if (normalizedTripType == null) {
            return BookingResult.failure("Trip type must be One_Way or Round_Trip.");
        }

        List<Integer> outbound = sanitizeInstanceIds(outboundInstanceIds);
        List<Integer> returns = sanitizeInstanceIds(returnInstanceIds);
        if (outbound.isEmpty()) {
            return BookingResult.failure("At least one outbound segment is required.");
        }
        if ("Round_Trip".equals(normalizedTripType) && returns.isEmpty()) {
            return BookingResult.failure("At least one return segment is required for round trip.");
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                BigDecimal totalFare = BigDecimal.ZERO;
                List<Integer> createdTickets = new ArrayList<>();
                Set<Integer> touchedInstances = new HashSet<>();

                int outboundSegmentNum = 1;
                for (Integer instanceId : outbound) {
                    if (instanceId == null || instanceId <= 0) {
                        conn.rollback();
                        return BookingResult.failure("Invalid outbound segment selected.");
                    }
                    if (!touchedInstances.add(instanceId)) {
                        conn.rollback();
                        return BookingResult.failure("Duplicate segment detected in selection.");
                    }

                    BigDecimal basePrice = flightClassInventoryDAO.getBasePrice(conn, instanceId, normalizedClass);
                    if (basePrice == null) {
                        conn.rollback();
                        return BookingResult.failure("Class inventory not found for instance " + instanceId + ".");
                    }

                    int updatedRows = flightClassInventoryDAO.decrementAvailableSeat(conn, instanceId, normalizedClass);
                    if (updatedRows != 1) {
                        conn.rollback();
                        return BookingResult.flightFull(
                                "No available seats in " + normalizedClass + " for instance " + instanceId + ".",
                                instanceId
                        );
                    }

                    totalFare = totalFare.add(basePrice);
                }

                int returnSegmentNum = 1;
                for (Integer instanceId : returns) {
                    if (instanceId == null || instanceId <= 0) {
                        conn.rollback();
                        return BookingResult.failure("Invalid return segment selected.");
                    }
                    if (!touchedInstances.add(instanceId)) {
                        conn.rollback();
                        return BookingResult.failure("Duplicate segment detected in selection.");
                    }

                    BigDecimal basePrice = flightClassInventoryDAO.getBasePrice(conn, instanceId, normalizedClass);
                    if (basePrice == null) {
                        conn.rollback();
                        return BookingResult.failure("Class inventory not found for instance " + instanceId + ".");
                    }

                    int updatedRows = flightClassInventoryDAO.decrementAvailableSeat(conn, instanceId, normalizedClass);
                    if (updatedRows != 1) {
                        conn.rollback();
                        return BookingResult.flightFull(
                                "No available seats in " + normalizedClass + " for instance " + instanceId + ".",
                                instanceId
                        );
                    }

                    totalFare = totalFare.add(basePrice);
                }

                BigDecimal totalPrice = totalFare.add(BOOKING_FEE);
                int reservationId = reservationDAO.createReservation(
                        conn,
                        customerSsn,
                        BOOKING_FEE,
                        totalPrice,
                        normalizedTripType
                );

                for (Integer instanceId : outbound) {
                    BigDecimal basePrice = flightClassInventoryDAO.getBasePrice(conn, instanceId, normalizedClass);
                    int ticketNum = ticketDAO.createTicket(
                            conn,
                            reservationId,
                            instanceId,
                            outboundSegmentNum++,
                            basePrice,
                            specialMeal,
                            "Outbound",
                            normalizedClass
                    );
                    createdTickets.add(ticketNum);
                    waitingLineDAO.markAsBooked(conn, customerSsn, instanceId);
                }

                for (Integer instanceId : returns) {
                    BigDecimal basePrice = flightClassInventoryDAO.getBasePrice(conn, instanceId, normalizedClass);
                    int ticketNum = ticketDAO.createTicket(
                            conn,
                            reservationId,
                            instanceId,
                            returnSegmentNum++,
                            basePrice,
                            specialMeal,
                            "Return",
                            normalizedClass
                    );
                    createdTickets.add(ticketNum);
                    waitingLineDAO.markAsBooked(conn, customerSsn, instanceId);
                }

                conn.commit();
                int firstTicket = createdTickets.isEmpty() ? -1 : createdTickets.get(0);
                String msg = "Booking successful. Reservation #" + reservationId +
                        " with " + createdTickets.size() + " ticket(s).";
                return BookingResult.success(msg, reservationId, firstTicket <= 0 ? null : firstTicket, totalPrice);
            } catch (SQLException inner) {
                conn.rollback();
                return BookingResult.failure("Booking failed: " + inner.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException outer) {
            return BookingResult.failure("Booking failed: " + outer.getMessage());
        }
    }

    public BookingResult bookFlight(String customerSsn, int instanceId, String ticketClass, boolean specialMeal) {
        List<Integer> outbound = new ArrayList<>();
        outbound.add(instanceId);
        return bookTrip(customerSsn, outbound, new ArrayList<>(), ticketClass, specialMeal, "One_Way");
    }

    public BookingResult addToWaitingLine(String customerSsn, int instanceId) {
        if (customerSsn == null || customerSsn.trim().isEmpty()) {
            return BookingResult.failure("Customer SSN is required.");
        }
        if (instanceId <= 0) {
            return BookingResult.failure("Please select a valid flight instance.");
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (waitingLineDAO.existsWaitingEntry(conn, customerSsn, instanceId)) {
                    conn.rollback();
                    return BookingResult.alreadyWaitlisted("You are already on the waiting list for this flight.");
                }

                int priority = waitingLineDAO.getNextPriorityNumber(conn, instanceId);
                boolean inserted = waitingLineDAO.addToWaitingLine(conn, customerSsn, instanceId, priority);
                if (!inserted) {
                    conn.rollback();
                    return BookingResult.failure("Could not add to waiting list.");
                }

                conn.commit();
                return BookingResult.waitlisted("Added to waiting list. Your priority number is " + priority + ".");
            } catch (SQLException inner) {
                conn.rollback();
                if ("23000".equals(inner.getSQLState())) {
                    return BookingResult.alreadyWaitlisted("You are already on the waiting list for this flight.");
                }
                return BookingResult.failure("Waiting list request failed: " + inner.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException outer) {
            return BookingResult.failure("Waiting list request failed: " + outer.getMessage());
        }
    }

    public BookingResult joinWaitingList(String customerSsn, int instanceId) {
        return addToWaitingLine(customerSsn, instanceId);
    }

    public List<WaitingLineEntry> getWaitlistNotificationsForCustomer(String customerSsn) {
        if (customerSsn == null || customerSsn.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return waitingLineDAO.getNotificationsForCustomer(customerSsn);
    }

    public BookingResult cancelReservationAndNotify(int reservationId, BigDecimal cancellationFee) {
        if (reservationId <= 0) {
            return BookingResult.failure("Invalid reservation id.");
        }
        BigDecimal fee = cancellationFee == null ? BigDecimal.ZERO : cancellationFee;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                List<Ticket> tickets = ticketDAO.getTicketsForReservation(conn, reservationId);
                if (tickets.isEmpty()) {
                    conn.rollback();
                    return BookingResult.failure("No tickets found for reservation.");
                }

                boolean cancelled = reservationDAO.cancelReservation(conn, reservationId, fee);
                if (!cancelled) {
                    conn.rollback();
                    return BookingResult.failure("Reservation is already cancelled or not found.");
                }

                ticketDAO.cancelBookedTicketsByReservation(conn, reservationId);

                int notifiedCount = 0;
                for (Ticket ticket : tickets) {
                    if (!"Booked".equalsIgnoreCase(ticket.getStatus())) {
                        continue;
                    }
                    if (ticket.getInstanceId() == null || ticket.getTicketClass() == null) {
                        continue;
                    }

                    flightClassInventoryDAO.incrementAvailableSeat(
                            conn,
                            ticket.getInstanceId(),
                            ticket.getTicketClass()
                    );

                    if (waitingLineDAO.notifyNextWaitingCustomer(conn, ticket.getInstanceId())) {
                        notifiedCount++;
                    }
                }

                conn.commit();
                String message = notifiedCount > 0
                        ? "Reservation cancelled. " + notifiedCount + " waiting customer(s) notified."
                        : "Reservation cancelled.";
                return BookingResult.success(message, reservationId, null, null);
            } catch (SQLException inner) {
                conn.rollback();
                return BookingResult.failure("Cancellation failed: " + inner.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException outer) {
            return BookingResult.failure("Cancellation failed: " + outer.getMessage());
        }
    }

    private String normalizeTicketClass(String ticketClass) {
        if (ticketClass == null) {
            return null;
        }
        String trimmed = ticketClass.trim();
        if (CLASS_ECONOMY.equalsIgnoreCase(trimmed)) {
            return CLASS_ECONOMY;
        }
        if (CLASS_BUSINESS.equalsIgnoreCase(trimmed)) {
            return CLASS_BUSINESS;
        }
        if (CLASS_FIRST.equalsIgnoreCase(trimmed)) {
            return CLASS_FIRST;
        }
        return null;
    }

    private String normalizeTripType(String tripType) {
        if (tripType == null) {
            return null;
        }
        String trimmed = tripType.trim();
        if ("One_Way".equalsIgnoreCase(trimmed) || "One Way".equalsIgnoreCase(trimmed) || "OneWay".equalsIgnoreCase(trimmed)) {
            return "One_Way";
        }
        if ("Round_Trip".equalsIgnoreCase(trimmed) || "Round Trip".equalsIgnoreCase(trimmed) || "RoundTrip".equalsIgnoreCase(trimmed)) {
            return "Round_Trip";
        }
        return null;
    }

    private List<Integer> sanitizeInstanceIds(List<Integer> ids) {
        List<Integer> clean = new ArrayList<>();
        if (ids == null) {
            return clean;
        }
        for (Integer id : ids) {
            if (id != null && id > 0) {
                clean.add(id);
            }
        }
        return clean;
    }
}
