package ui;

import dao.FlightInstanceDAO;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Customer;
import models.FlightInstance;
import services.BookingService;

public class SearchFlightsPanel extends JPanel {
    private final MainFrame frame;
    private final Customer customer;
    private final String previousScreen; // Track navigation origin
    private final FlightInstanceDAO flightInstanceDAO;

    private JComboBox<String> tripTypeBox;
    private JTextField depAirportField;
    private JTextField arrAirportField;
    private JTextField depDateField;
    private JTextField returnDateField;
    private JCheckBox flexibleCheckBox;

    private JTable outboundTable;
    private JTable returnTable;
    private DefaultTableModel outboundTableModel;
    private DefaultTableModel returnTableModel;

    private List<FlightInstance> outboundFlights;
    private List<FlightInstance> returnFlights;

    public SearchFlightsPanel(MainFrame frame, Customer customer, String previousScreen) {
        this.frame = frame;
        this.customer = customer;
        this.previousScreen = previousScreen; // Initialize origin
        this.flightInstanceDAO = new FlightInstanceDAO();
        this.outboundFlights = new ArrayList<>();
        this.returnFlights = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        add(createFormPanel(), BorderLayout.NORTH);
        add(createResultsPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        updateReturnDateVisibility();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        tripTypeBox = new JComboBox<>(new String[]{"One Way", "Round Trip"});
        tripTypeBox.setSelectedItem("Round Trip");
        depAirportField = new JTextField();
        arrAirportField = new JTextField();
        depDateField = new JTextField();
        returnDateField = new JTextField();
        flexibleCheckBox = new JCheckBox("Flexible dates");
        tripTypeBox.addActionListener(e -> updateReturnDateVisibility());

        formPanel.add(new JLabel("Trip Type:"));
        formPanel.add(tripTypeBox);
        formPanel.add(new JLabel("Departure Airport:"));
        formPanel.add(depAirportField);
        formPanel.add(new JLabel("Arrival Airport:"));
        formPanel.add(arrAirportField);
        formPanel.add(new JLabel("Departure Date (YYYY-MM-DD):"));
        formPanel.add(depDateField);
        formPanel.add(new JLabel("Return Date (YYYY-MM-DD):"));
        formPanel.add(returnDateField);
        formPanel.add(new JLabel("Flexible?"));
        formPanel.add(flexibleCheckBox);

        return formPanel;
    }

    private JPanel createResultsPanel() {
        JPanel resultsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        String[] cols = {"Instance ID", "Airline", "Flight #", "From", "To", "Departure", "Arrival", "Seats", "Status"};
        outboundTableModel = new DefaultTableModel(cols, 0);
        returnTableModel = new DefaultTableModel(cols, 0);
        outboundTable = new JTable(outboundTableModel);
        returnTable = new JTable(returnTableModel);

        JPanel outboundPanel = new JPanel(new BorderLayout());
        outboundPanel.add(new JLabel("Outbound Flights"), BorderLayout.NORTH);
        outboundPanel.add(new JScrollPane(outboundTable), BorderLayout.CENTER);

        JPanel returnPanel = new JPanel(new BorderLayout());
        returnPanel.add(new JLabel("Return Flights"), BorderLayout.NORTH);
        returnPanel.add(new JScrollPane(returnTable), BorderLayout.CENTER);

        resultsPanel.add(outboundPanel);
        resultsPanel.add(returnPanel);
        return resultsPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = new JButton("Search Flights");
        JButton bookButton = new JButton("Book Selected Flight(s)");
        JButton backButton = new JButton("Back");

        searchButton.addActionListener(e -> searchFlights());
        bookButton.addActionListener(e -> bookSelectedFlights());
        // Dynamic navigation back to origin
        backButton.addActionListener(e -> frame.showScreen(previousScreen));

        buttonPanel.add(searchButton);
        buttonPanel.add(bookButton);
        buttonPanel.add(backButton);
        return buttonPanel;
    }

    private void updateReturnDateVisibility() {
        boolean isRoundTrip = "Round Trip".equals(tripTypeBox.getSelectedItem());
        returnDateField.setEditable(isRoundTrip);
        if (!isRoundTrip) {
            returnDateField.setText("");
            if (returnTableModel != null) returnTableModel.setRowCount(0);
        }
    }

    private void searchFlights() {
        String depAirport = depAirportField.getText().trim().toUpperCase();
        String arrAirport = arrAirportField.getText().trim().toUpperCase();
        String depDate = depDateField.getText().trim();
        if (depAirport.isEmpty() || arrAirport.isEmpty() || depDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all departure details.");
            return;
        }

        outboundTableModel.setRowCount(0);
        outboundFlights = flightInstanceDAO.searchFlights(depAirport, arrAirport, depDate, flexibleCheckBox.isSelected());
        displayFlights(outboundFlights, outboundTableModel);

        if ("Round Trip".equals(tripTypeBox.getSelectedItem())) {
            String retDate = returnDateField.getText().trim();
            if (retDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a return date.");
                return;
            }
            returnTableModel.setRowCount(0);
            returnFlights = flightInstanceDAO.searchFlights(arrAirport, depAirport, retDate, flexibleCheckBox.isSelected());
            displayFlights(returnFlights, returnTableModel);
        }
    }

    private void displayFlights(List<FlightInstance> flights, DefaultTableModel model) {
        for (FlightInstance f : flights) {
            model.addRow(new Object[]{f.getInstanceId(), f.getAirlineId(), f.getFlightNumber(), f.getDepartureAirport(), f.getArrivalAirport(), f.getDepartureDateTime(), f.getArrivalDateTime(), f.getAvailableSeats(), f.getStatus()});
        }
    }

    private void bookSelectedFlights() {
        int outboundRow = outboundTable.getSelectedRow();
        if (outboundRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an outbound flight.");
            return;
        }

        FlightInstance outboundInstance = outboundFlights.get(outboundRow);
        FlightInstance returnInstance = null;

        if ("Round Trip".equals(tripTypeBox.getSelectedItem())) {
            int returnRow = returnTable.getSelectedRow();
            if (returnRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a return flight.");
                return;
            }
            returnInstance = returnFlights.get(returnRow);
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Confirm booking for " + customer.getFirstName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        BookingService service = new BookingService();
        if (service.bookFlight(customer, outboundInstance, returnInstance, "Economy")) {
            JOptionPane.showMessageDialog(this, "Reservation successfully created!");
            frame.showScreen(previousScreen); // Return to origin
        } else {
            JOptionPane.showMessageDialog(this, "Booking failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
