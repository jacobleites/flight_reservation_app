package ui;

import dao.AdminDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import models.ActiveFlightSummary;
import models.Customer;
import models.CustomerRevenueSummary;
import models.Employee;
import models.ReservationSummary;
import models.RevenueSummary;
import models.SalesReportRow;

public class AdminDashboardPanel extends JPanel {
    private final MainFrame frame;
    private final Employee admin;
    private final AdminDAO adminDAO;

    private DefaultTableModel customerModel;
    private DefaultTableModel repModel;
    private DefaultTableModel reportModel;
    private JTextArea reportSummaryArea;

    public AdminDashboardPanel(MainFrame frame, Employee employee) {
        this.frame = frame;
        this.admin = employee;
        this.adminDAO = new AdminDAO();

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Admin Dashboard - " + admin.getFirstName(), SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> frame.showScreen("MAIN_MENU"));
        rightButtons.add(logoutButton);

        panel.add(title, BorderLayout.WEST);
        panel.add(rightButtons, BorderLayout.EAST);
        return panel;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Customers", buildCustomerTab());
        tabs.addTab("Customer Reps", buildRepTab());
        tabs.addTab("Reports", buildReportsTab());
        return tabs;
    }

    private JPanel buildCustomerTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        customerModel = new DefaultTableModel(
                new String[]{"SSN", "First Name", "Last Name", "Username", "Email", "Gender", "DOB", "Phone"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(customerModel);
        refreshCustomerTable();

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Customer");
        JButton editButton = new JButton("Edit Customer");
        JButton deleteButton = new JButton("Delete Customer");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addCustomer());
        editButton.addActionListener(e -> editCustomer(table));
        deleteButton.addActionListener(e -> deleteCustomer(table));
        refreshButton.addActionListener(e -> refreshCustomerTable());

        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        actions.add(refreshButton);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildRepTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        repModel = new DefaultTableModel(
                new String[]{"SSN", "First Name", "Last Name", "Username", "Role"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(repModel);
        refreshRepTable();

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Rep");
        JButton editButton = new JButton("Edit Rep");
        JButton deleteButton = new JButton("Delete Rep");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addRep());
        editButton.addActionListener(e -> editRep(table));
        deleteButton.addActionListener(e -> deleteRep(table));
        refreshButton.addActionListener(e -> refreshRepTable());

        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        actions.add(refreshButton);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildReportsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(buildReportControls(), BorderLayout.NORTH);

        reportModel = new DefaultTableModel();
        JTable reportTable = new JTable(reportModel);
        panel.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        reportSummaryArea = new JTextArea(6, 80);
        reportSummaryArea.setEditable(false);
        reportSummaryArea.setLineWrap(true);
        reportSummaryArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(reportSummaryArea), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildReportControls() {
        JPanel wrapper = new JPanel(new GridLayout(4, 1, 6, 6));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField yearField = new JTextField(6);
        JTextField monthField = new JTextField(4);
        JButton monthlySalesButton = new JButton("Monthly Sales");
        monthlySalesButton.addActionListener(e -> runMonthlySales(yearField.getText().trim(), monthField.getText().trim()));
        row1.add(new JLabel("Year:"));
        row1.add(yearField);
        row1.add(new JLabel("Month:"));
        row1.add(monthField);
        row1.add(monthlySalesButton);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField flightNumberField = new JTextField(8);
        JButton reservationsByFlightButton = new JButton("Reservations by Flight #");
        reservationsByFlightButton.addActionListener(e -> runReservationsByFlight(flightNumberField.getText().trim()));
        row2.add(new JLabel("Flight #:"));
        row2.add(flightNumberField);
        row2.add(reservationsByFlightButton);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField firstNameField = new JTextField(10);
        JTextField lastNameField = new JTextField(10);
        JButton reservationsByNameButton = new JButton("Reservations by Customer Name");
        reservationsByNameButton.addActionListener(
                e -> runReservationsByName(firstNameField.getText().trim(), lastNameField.getText().trim())
        );
        row3.add(new JLabel("First Name:"));
        row3.add(firstNameField);
        row3.add(new JLabel("Last Name:"));
        row3.add(lastNameField);
        row3.add(reservationsByNameButton);

        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField limitField = new JTextField("10", 5);
        JButton revenueFlightButton = new JButton("Revenue by Flight");
        JButton revenueAirlineButton = new JButton("Revenue by Airline");
        JButton revenueCustomerButton = new JButton("Revenue by Customer");
        JButton topCustomerButton = new JButton("Top Customer");
        JButton activeFlightsButton = new JButton("Most Active Flights");

        revenueFlightButton.addActionListener(e -> runRevenueByFlight());
        revenueAirlineButton.addActionListener(e -> runRevenueByAirline());
        revenueCustomerButton.addActionListener(e -> runRevenueByCustomer());
        topCustomerButton.addActionListener(e -> runTopCustomer());
        activeFlightsButton.addActionListener(e -> runMostActiveFlights(limitField.getText().trim()));

        row4.add(revenueFlightButton);
        row4.add(revenueAirlineButton);
        row4.add(revenueCustomerButton);
        row4.add(topCustomerButton);
        row4.add(new JLabel("Limit:"));
        row4.add(limitField);
        row4.add(activeFlightsButton);

        wrapper.add(row1);
        wrapper.add(row2);
        wrapper.add(row3);
        wrapper.add(row4);
        return wrapper;
    }

    private void refreshCustomerTable() {
        customerModel.setRowCount(0);
        for (Customer c : adminDAO.getAllCustomers()) {
            customerModel.addRow(new Object[]{
                    c.getSsn(),
                    c.getFirstName(),
                    c.getLastName(),
                    c.getUsername(),
                    c.getEmail(),
                    c.getGender(),
                    c.getDob(),
                    c.getPhone()
            });
        }
    }

    private void refreshRepTable() {
        repModel.setRowCount(0);
        for (Employee e : adminDAO.getAllCustomerRepresentatives()) {
            repModel.addRow(new Object[]{
                    e.getEmployeeSsn(),
                    e.getFirstName(),
                    e.getLastName(),
                    e.getUsername(),
                    e.getRole()
            });
        }
    }

    private void addCustomer() {
        Customer customer = promptCustomerForm(null);
        if (customer == null) {
            return;
        }
        boolean ok = adminDAO.addCustomer(customer);
        JOptionPane.showMessageDialog(this, ok ? "Customer added." : "Failed to add customer.");
        if (ok) {
            refreshCustomerTable();
        }
    }

    private void editCustomer(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer row first.");
            return;
        }
        String ssn = String.valueOf(customerModel.getValueAt(row, 0));
        Customer existing = adminDAO.getCustomerBySsn(ssn);
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "Could not load customer details.");
            return;
        }
        Customer updated = promptCustomerForm(existing);
        if (updated == null) {
            return;
        }
        boolean ok = adminDAO.updateCustomer(updated);
        JOptionPane.showMessageDialog(this, ok ? "Customer updated." : "Failed to update customer.");
        if (ok) {
            refreshCustomerTable();
        }
    }

    private void deleteCustomer(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer row first.");
            return;
        }
        String ssn = String.valueOf(customerModel.getValueAt(row, 0));
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete customer " + ssn + "? This can cascade to reservation data.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        boolean ok = adminDAO.deleteCustomer(ssn);
        JOptionPane.showMessageDialog(this, ok ? "Customer deleted." : "Failed to delete customer.");
        if (ok) {
            refreshCustomerTable();
        }
    }

    private void addRep() {
        Employee rep = promptRepForm(null);
        if (rep == null) {
            return;
        }
        boolean ok = adminDAO.addCustomerRepresentative(rep);
        JOptionPane.showMessageDialog(this, ok ? "Representative added." : "Failed to add representative.");
        if (ok) {
            refreshRepTable();
        }
    }

    private void editRep(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a representative row first.");
            return;
        }
        String ssn = String.valueOf(repModel.getValueAt(row, 0));
        Employee existing = adminDAO.getCustomerRepresentativeBySsn(ssn);
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "Could not load representative details.");
            return;
        }
        Employee updated = promptRepForm(existing);
        if (updated == null) {
            return;
        }
        boolean ok = adminDAO.updateCustomerRepresentative(updated);
        JOptionPane.showMessageDialog(this, ok ? "Representative updated." : "Failed to update representative.");
        if (ok) {
            refreshRepTable();
        }
    }

    private void deleteRep(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a representative row first.");
            return;
        }
        String ssn = String.valueOf(repModel.getValueAt(row, 0));
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete representative " + ssn + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        boolean ok = adminDAO.deleteCustomerRepresentative(ssn);
        JOptionPane.showMessageDialog(this, ok ? "Representative deleted." : "Failed to delete representative.");
        if (ok) {
            refreshRepTable();
        }
    }

    private Customer promptCustomerForm(Customer existing) {
        JTextField ssnField = new JTextField(existing == null ? "" : existing.getSsn(), 14);
        JTextField firstNameField = new JTextField(existing == null ? "" : existing.getFirstName(), 18);
        JTextField lastNameField = new JTextField(existing == null ? "" : existing.getLastName(), 18);
        JTextField emailField = new JTextField(existing == null ? "" : existing.getEmail(), 20);
        JTextField genderField = new JTextField(existing == null ? "" : existing.getGender(), 10);
        JTextField dobField = new JTextField(existing == null ? "" : existing.getDob(), 12);
        JTextField phoneField = new JTextField(existing == null ? "" : existing.getPhone(), 14);
        JTextField usernameField = new JTextField(existing == null ? "" : existing.getUsername(), 14);
        JTextField passwordField = new JTextField(existing == null ? "" : existing.getPassword(), 14);

        if (existing != null) {
            ssnField.setEditable(false);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("SSN:"));
        form.add(ssnField);
        form.add(new JLabel("First Name:"));
        form.add(firstNameField);
        form.add(new JLabel("Last Name:"));
        form.add(lastNameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Gender:"));
        form.add(genderField);
        form.add(new JLabel("DOB (YYYY-MM-DD):"));
        form.add(dobField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                existing == null ? "Add Customer" : "Edit Customer",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        String ssn = ssnField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String gender = genderField.getText().trim();
        String dob = dobField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (ssn.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "SSN, name, username, and password are required.");
            return null;
        }

        if (existing == null) {
            return new Customer(ssn, email, gender, dob, firstName, lastName, phone, username, password);
        }
        return new Customer(ssn, email, gender, dob, firstName, lastName, phone, existing.getAccount_id(), username, password);
    }

    private Employee promptRepForm(Employee existing) {
        JTextField ssnField = new JTextField(existing == null ? "" : existing.getEmployeeSsn(), 14);
        JTextField firstNameField = new JTextField(existing == null ? "" : existing.getFirstName(), 16);
        JTextField lastNameField = new JTextField(existing == null ? "" : existing.getLastName(), 16);
        JTextField usernameField = new JTextField(existing == null ? "" : existing.getUsername(), 14);
        JTextField passwordField = new JTextField(existing == null ? "" : existing.getPassword(), 14);

        if (existing != null) {
            ssnField.setEditable(false);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Employee SSN:"));
        form.add(ssnField);
        form.add(new JLabel("First Name:"));
        form.add(firstNameField);
        form.add(new JLabel("Last Name:"));
        form.add(lastNameField);
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                existing == null ? "Add Representative" : "Edit Representative",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        String ssn = ssnField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        if (ssn.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return null;
        }
        return new Employee(ssn, firstName, lastName, username, password, "CUSTOMER_REPRESENTATIVE");
    }

    private void runMonthlySales(String yearText, String monthText) {
        int year;
        int month;
        try {
            year = Integer.parseInt(yearText);
            month = Integer.parseInt(monthText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Year and month must be numbers.");
            return;
        }

        List<SalesReportRow> rows = adminDAO.getMonthlySalesReport(year, month);
        reportModel.setDataVector(new Object[][]{}, new String[]{
                "Reservation ID", "Date", "Customer SSN", "Ticket Revenue", "Booking Fee", "Total Revenue"
        });

        BigDecimal grandTotal = BigDecimal.ZERO;
        for (SalesReportRow row : rows) {
            reportModel.addRow(new Object[]{
                    row.getReservationId(),
                    row.getReservationDate(),
                    row.getCustomerSsn(),
                    row.getTicketRevenue(),
                    row.getBookingFee(),
                    row.getTotalRevenue()
            });
            grandTotal = grandTotal.add(row.getTotalRevenue());
        }
        reportSummaryArea.setText("Rows: " + rows.size() + "\nGrand Total: " + grandTotal);
    }

    private void runReservationsByFlight(String flightText) {
        int flightNum;
        try {
            flightNum = Integer.parseInt(flightText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Flight number must be a valid integer.");
            return;
        }
        List<ReservationSummary> rows = adminDAO.getReservationsByFlightNumber(flightNum);
        populateReservationSummaryTable(rows);
    }

    private void runReservationsByName(String firstName, String lastName) {
        if (firstName.isEmpty() && lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter at least first or last name.");
            return;
        }
        List<ReservationSummary> rows = adminDAO.getReservationsByCustomerName(firstName, lastName);
        populateReservationSummaryTable(rows);
    }

    private void populateReservationSummaryTable(List<ReservationSummary> rows) {
        reportModel.setDataVector(new Object[][]{}, new String[]{
                "Reservation ID", "Customer", "Customer SSN", "Date", "Status", "Total Price", "Airline", "Flight #"
        });
        for (ReservationSummary row : rows) {
            reportModel.addRow(new Object[]{
                    row.getReservationId(),
                    row.getCustomerFirstName() + " " + row.getCustomerLastName(),
                    row.getCustomerSsn(),
                    row.getReservationDate(),
                    row.getReservationStatus(),
                    row.getTotalPrice(),
                    row.getAirlineId(),
                    row.getFlightNum()
            });
        }
        reportSummaryArea.setText("Rows: " + rows.size());
    }

    private void runRevenueByFlight() {
        List<RevenueSummary> rows = adminDAO.getRevenueByFlight();
        populateRevenueTable(rows, "Flight");
    }

    private void runRevenueByAirline() {
        List<RevenueSummary> rows = adminDAO.getRevenueByAirline();
        populateRevenueTable(rows, "Airline");
    }

    private void populateRevenueTable(List<RevenueSummary> rows, String label) {
        reportModel.setDataVector(new Object[][]{}, new String[]{
                label, "Tickets Sold", "Total Revenue"
        });
        BigDecimal total = BigDecimal.ZERO;
        for (RevenueSummary row : rows) {
            reportModel.addRow(new Object[]{
                    row.getSummaryKey(),
                    row.getTicketsSold(),
                    row.getTotalRevenue()
            });
            total = total.add(row.getTotalRevenue());
        }
        reportSummaryArea.setText("Rows: " + rows.size() + "\nAggregate Revenue: " + total);
    }

    private void runRevenueByCustomer() {
        List<CustomerRevenueSummary> rows = adminDAO.getRevenueByCustomer();
        reportModel.setDataVector(new Object[][]{}, new String[]{
                "Customer SSN", "Customer Name", "Reservation Count", "Total Revenue"
        });
        for (CustomerRevenueSummary row : rows) {
            reportModel.addRow(new Object[]{
                    row.getCustomerSsn(),
                    row.getFirstName() + " " + row.getLastName(),
                    row.getReservationCount(),
                    row.getTotalRevenue()
            });
        }
        reportSummaryArea.setText("Rows: " + rows.size());
    }

    private void runTopCustomer() {
        CustomerRevenueSummary top = adminDAO.getTopCustomerByRevenue();
        reportModel.setDataVector(new Object[][]{}, new String[]{
                "Customer SSN", "Customer Name", "Reservation Count", "Total Revenue"
        });
        if (top != null) {
            reportModel.addRow(new Object[]{
                    top.getCustomerSsn(),
                    top.getFirstName() + " " + top.getLastName(),
                    top.getReservationCount(),
                    top.getTotalRevenue()
            });
            reportSummaryArea.setText("Top customer loaded.");
        } else {
            reportSummaryArea.setText("No top customer found.");
        }
    }

    private void runMostActiveFlights(String limitText) {
        int limit;
        try {
            limit = Integer.parseInt(limitText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Limit must be an integer.");
            return;
        }
        if (limit <= 0) {
            JOptionPane.showMessageDialog(this, "Limit must be greater than 0.");
            return;
        }
        List<ActiveFlightSummary> rows = adminDAO.getMostActiveFlights(limit);
        reportModel.setDataVector(new Object[][]{}, new String[]{
                "Airline", "Flight #", "Tickets Sold"
        });
        for (ActiveFlightSummary row : rows) {
            reportModel.addRow(new Object[]{
                    row.getAirlineId(),
                    row.getFlightNum(),
                    row.getTicketsSold()
            });
        }
        reportSummaryArea.setText("Rows: " + rows.size());
    }
}
