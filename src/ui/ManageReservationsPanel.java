package ui;

import dao.CustomerDAO;
import dao.ReservationDAO;
import dao.TicketDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Customer;
import models.Employee;
import models.Reservation;
import models.Ticket;

public class ManageReservationsPanel extends JPanel {
    private final MainFrame frame;
    private final Employee employee;
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    private JTextField customerIdSearchField;
    private JTable reservationTable;
    private DefaultTableModel tableModel;

    public ManageReservationsPanel(MainFrame frame, Employee employee) {
        this.frame = frame;
        this.employee = employee;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setupHeader();
        setupCenterContent();
        setupBottomActions();
    }

    private void setupHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Reservation Management Portal");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> frame.showEmployeeDashboard(employee));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void setupCenterContent() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerIdSearchField = new JTextField(12);
        JButton searchBtn = new JButton("Fetch History");
        searchBtn.addActionListener(e -> refreshReservationTable());
        searchPanel.add(new JLabel("Lookup Customer (Customer ID):"));
        searchPanel.add(customerIdSearchField);
        searchPanel.add(searchBtn);

        tableModel = new DefaultTableModel(new String[]{"ID", "Date", "Status", "Total Price", "Trip Type"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        reservationTable = new JTable(tableModel);
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(reservationTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupBottomActions() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton createBtn = new JButton("Create New Reservation");
        JButton viewBtn = new JButton("View Ticket Details");
        JButton cancelBtn = new JButton("Cancel Selected");

        createBtn.addActionListener(e -> initiateNewBooking());
        viewBtn.addActionListener(e -> viewTicketDetails());
        cancelBtn.addActionListener(e -> cancelSelectedReservation());

        actionPanel.add(createBtn);
        actionPanel.add(viewBtn);
        actionPanel.add(cancelBtn);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void refreshReservationTable() {
        String customerIdText = customerIdSearchField.getText().trim();
        if (customerIdText.isEmpty()) return;

        int customerId;
        try {
            customerId = Integer.parseInt(customerIdText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Customer ID must be a number.");
            return;
        }

        Customer target = customerDAO.findByAccountId(customerId);
        if (target == null) {
            JOptionPane.showMessageDialog(this, "Customer not found.");
            tableModel.setRowCount(0);
            return;
        }

        tableModel.setRowCount(0);
        List<Reservation> list = reservationDAO.getReservationsForCustomer(target.getSsn());
        for (Reservation r : list) {
            tableModel.addRow(new Object[]{r.getId(), r.getReservationDate(), r.getStatus(), "$" + r.getPrice(), r.getTripType()});
        }
    }

    private void initiateNewBooking() {
        String customerIdText = JOptionPane.showInputDialog(this, "Enter Customer ID:");
        if (customerIdText == null || customerIdText.trim().isEmpty()) return;

        int customerId;
        try {
            customerId = Integer.parseInt(customerIdText.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Customer ID must be a number.");
            return;
        }

        Customer target = customerDAO.findByAccountId(customerId);
        if (target != null) {
            // Pass the current screen name so the Back button works correctly
            frame.showSearchFlightsScreen(target, "MANAGE_RESERVATIONS");
        } else {
            JOptionPane.showMessageDialog(this, "Customer not found.");
        }
    }

    private void viewTicketDetails() {
        int row = reservationTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
            return;
        }

        int resId = (int) tableModel.getValueAt(row, 0);
        List<Ticket> tickets = ticketDAO.getTicketsForReservation(resId);

        // Insightful Check: Verify if the list is empty before building the UI string
        if (tickets == null || tickets.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No ticket records found for Reservation #" + resId, 
                 "Data Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Flight Details for Reservation #" + resId + ":\n\n");
        for (Ticket t : tickets) {
            sb.append(String.format("- %s | %s %d | Class: %s | Fare: $%.2f\n", 
                t.getDirection(), t.getAirlineId(), t.getFlightNum(), t.getTicketClass(), t.getFare()));
        }
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    private void cancelSelectedReservation() {
        int row = reservationTable.getSelectedRow();
        if (row == -1) return;
        int resId = (int) tableModel.getValueAt(row, 0);
        if (reservationDAO.cancelReservation(resId)) {
            JOptionPane.showMessageDialog(this, "Cancelled.");
            refreshReservationTable();
        }
    }
}
