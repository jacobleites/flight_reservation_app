package ui;

import dao.ReservationDAO;
import dao.TicketDAO;
import java.awt.BorderLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import models.Customer;
import models.Reservation;
import models.Ticket;
import services.BookingResult;
import services.BookingService;

public class ViewReservationsPanel extends JPanel {
    private final MainFrame frame;
    private final Customer customer;
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private final BookingService bookingService = new BookingService();
    private JTable table;
    private DefaultTableModel tableModel;

    public ViewReservationsPanel(MainFrame frame, Customer customer) {
        this.frame = frame;
        this.customer = customer;
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("My Reservations", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] cols = {"ID", "Date", "Status", "Price", "Type"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton viewButton = new JButton("View Selected Reservation");
        JButton cancelButton = new JButton("Cancel Selected Reservation");
        JButton backButton = new JButton("Back to Dashboard");

        viewButton.addActionListener(e -> viewSelectedReservation());
        cancelButton.addActionListener(e -> cancelSelected());
        backButton.addActionListener(e -> frame.showCustomerDashboard(customer));

        buttonPanel.add(viewButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Reservation> list = reservationDAO.getReservationsForCustomer(customer.getSsn());
        for (Reservation r : list) {
            tableModel.addRow(new Object[]{
                    r.getId(), r.getReservationDate(), r.getStatus(), "$" + r.getPrice(), r.getTripType()
            });
        }
    }

    private void viewSelectedReservation() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
            return;
        }

        int resId = (int) tableModel.getValueAt(row, 0);
        List<Ticket> tickets = new ArrayList<>(ticketDAO.getTicketsForReservation(resId));
        if (tickets.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No ticket records found for this reservation.");
            return;
        }

        tickets.sort(
                Comparator
                        .comparingInt((Ticket t) -> "Return".equalsIgnoreCase(t.getDirection()) ? 1 : 0)
                        .thenComparingInt(Ticket::getSegmentNum)
        );

        StringBuilder sb = new StringBuilder();
        sb.append("Reservation #").append(resId).append("\n\n");
        for (Ticket t : tickets) {
            sb.append("Ticket #").append(t.getTicketNum()).append("\n");
            sb.append("Direction: ").append(safe(t.getDirection())).append("\n");
            sb.append("Segment: ").append(t.getSegmentNum()).append("\n");
            sb.append("Instance ID: ").append(t.getInstanceId()).append("\n");
            sb.append("Flight: ").append(formatFlight(t)).append("\n");
            sb.append("Class: ").append(safe(t.getTicketClass())).append("\n");
            sb.append("Fare: $").append(formatMoney(t.getFareAmount())).append("\n");
            sb.append("Special Meal: ").append(t.getSpecialMeal() ? "Yes" : "No").append("\n");
            sb.append("Status: ").append(safe(t.getStatus())).append("\n");
            sb.append("Paid At: ").append(safe(t.getPayDate())).append("\n\n");
        }

        JTextArea area = new JTextArea(sb.toString(), 20, 50);
        area.setEditable(false);
        area.setCaretPosition(0);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(area),
                "Reservation Ticket Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to cancel.");
            return;
        }

        int resId = (int) tableModel.getValueAt(row, 0);
        Reservation selectedRes = reservationDAO.getReservationById(resId);
        List<Ticket> tickets = ticketDAO.getTicketsForReservation(resId);
        String status = selectedRes.getStatus();

        if ("Cancelled".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "This reservation is already cancelled.");
            return;
        }
        if (checkForFee(tickets)) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Cancelling this reservation will incur a $35 cancellation fee. Continue?",
                    "Cancellation Fee",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                BookingResult result = bookingService.cancelReservationAndNotify(resId, BigDecimal.valueOf(35.00));
                if (result.isSuccess()) {
                    JOptionPane.showMessageDialog(this, result.getMessage());
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, result.getMessage());
                }
            }
        } else {
            BookingResult result = bookingService.cancelReservationAndNotify(resId, BigDecimal.ZERO);
            if (result.isSuccess()) {
                JOptionPane.showMessageDialog(this, result.getMessage());
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage());
            }
        }
    }

    private boolean checkForFee(List<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            if (ticket.getTicketClass().equalsIgnoreCase("economy")) {
                return true;
            }
        }
        return false;
    }

    private String formatFlight(Ticket ticket) {
        if (ticket.getAirlineId() == null || ticket.getFlightNum() == null) {
            return "N/A";
        }
        return ticket.getAirlineId() + " " + ticket.getFlightNum();
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String safe(String value) {
        return value == null ? "N/A" : value;
    }
}
