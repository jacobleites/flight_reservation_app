package ui;

import dao.FlightInstanceDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import models.Customer;
import models.FlightInstance;
import models.FlightItinerary;
import services.BookingResult;
import services.BookingService;

public class BookFlightPanel extends JPanel {
    private final MainFrame mainFrame;
    private final Customer loggedInCustomer;
    private final String previousScreen;
    private final FlightInstanceDAO flightInstanceDAO;
    private final BookingService bookingService;

    private JTextField depAirportField;
    private JTextField arrAirportField;
    private JTextField depDateField;
    private JTable flightsTable;
    private DefaultTableModel flightsModel;
    private JComboBox<String> classComboBox;
    private JCheckBox specialMealCheckBox;

    public BookFlightPanel(MainFrame mainFrame, Customer loggedInCustomer) {
        this(mainFrame, loggedInCustomer, "CUSTOMER_DASHBOARD", null, null, null);
    }

    public BookFlightPanel(
            MainFrame mainFrame,
            Customer loggedInCustomer,
            String previousScreen,
            String tripType,
            FlightItinerary outboundItinerary,
            FlightItinerary returnItinerary
    ) {
        this.mainFrame = mainFrame;
        this.loggedInCustomer = loggedInCustomer;
        this.previousScreen = previousScreen == null ? "CUSTOMER_DASHBOARD" : previousScreen;
        this.flightInstanceDAO = new FlightInstanceDAO();
        this.bookingService = new BookingService();

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        preloadFromItineraries(tripType, outboundItinerary, returnItinerary);
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JLabel title = new JLabel("Book Flight", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        depAirportField = new JTextField(6);
        arrAirportField = new JTextField(6);
        depDateField = new JTextField(12);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchFlights());

        filters.add(new JLabel("Departure Airport:"));
        filters.add(depAirportField);
        filters.add(new JLabel("Arrival Airport:"));
        filters.add(arrAirportField);
        filters.add(new JLabel("Departure Date (YYYY-MM-DD):"));
        filters.add(depDateField);
        filters.add(searchButton);
        panel.add(filters, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        String[] cols = {
                "Direction",
                "Instance ID",
                "Airline",
                "Flight #",
                "From",
                "To",
                "Departure",
                "Arrival",
                "Status",
                "Aircraft Model"
        };
        flightsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsTable = new JTable(flightsModel);
        flightsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        panel.add(new JScrollPane(flightsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel options = new JPanel(new FlowLayout(FlowLayout.LEFT));
        classComboBox = new JComboBox<>(new String[]{"Economy", "Business", "First"});
        specialMealCheckBox = new JCheckBox("Special Meal");
        options.add(new JLabel("Ticket Class:"));
        options.add(classComboBox);
        options.add(specialMealCheckBox);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookButton = new JButton("Book Flight");
        JButton backButton = new JButton("Back");
        bookButton.addActionListener(e -> bookSelectedFlight());
        backButton.addActionListener(e -> mainFrame.showScreen(previousScreen));
        actions.add(bookButton);
        actions.add(backButton);

        panel.add(options, BorderLayout.WEST);
        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private void searchFlights() {
        String depAirport = depAirportField.getText().trim().toUpperCase();
        String arrAirport = arrAirportField.getText().trim().toUpperCase();
        String depDate = depDateField.getText().trim();

        if (depAirport.isEmpty() || arrAirport.isEmpty() || depDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter departure airport, arrival airport, and departure date.");
            return;
        }

        try {
            LocalDate.parse(depDate);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Departure date must be in YYYY-MM-DD format.");
            return;
        }

        List<FlightInstance> flights = flightInstanceDAO.searchBookableFlights(depAirport, arrAirport, depDate);
        flightsModel.setRowCount(0);
        for (FlightInstance fi : flights) {
            String from = fi.getDepartureAirport();
            if (fi.getDepartureAirportName() != null) {
                from += " - " + fi.getDepartureAirportName();
            }

            String to = fi.getArrivalAirport();
            if (fi.getArrivalAirportName() != null) {
                to += " - " + fi.getArrivalAirportName();
            }

            flightsModel.addRow(new Object[]{
                    "Outbound",
                    fi.getInstanceId(),
                    fi.getAirlineId(),
                    fi.getFlightNumber(),
                    from,
                    to,
                    fi.getDepartureDateTime(),
                    fi.getArrivalDateTime(),
                    fi.getStatus(),
                    fi.getAircraftModel()
            });
        }

        if (flights.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No flight instances found for the selected criteria.");
        }
    }

    private void bookSelectedFlight() {
        int[] selectedRows = flightsTable.getSelectedRows();
        if (selectedRows == null || selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one flight segment.");
            return;
        }

        String ticketClass = (String) classComboBox.getSelectedItem();
        boolean specialMeal = specialMealCheckBox.isSelected();
        List<Integer> outboundIds = new ArrayList<>();
        List<Integer> returnIds = new ArrayList<>();

        boolean tableContainsReturn = false;
        for (int i = 0; i < flightsModel.getRowCount(); i++) {
            Object dir = flightsModel.getValueAt(i, 0);
            if (dir != null && "Return".equalsIgnoreCase(dir.toString())) {
                tableContainsReturn = true;
                break;
            }
        }

        for (int row : selectedRows) {
            String direction = String.valueOf(flightsModel.getValueAt(row, 0));
            int instanceId = (int) flightsModel.getValueAt(row, 1);
            if ("Return".equalsIgnoreCase(direction)) {
                returnIds.add(instanceId);
            } else {
                outboundIds.add(instanceId);
            }
        }

        if (tableContainsReturn && (outboundIds.isEmpty() || returnIds.isEmpty())) {
            JOptionPane.showMessageDialog(
                    this,
                    "For round-trip booking, select both outbound and return flight segment(s)."
            );
            return;
        }

        String tripType = tableContainsReturn ? "Round_Trip" : "One_Way";
        BookingResult result = bookingService.bookTrip(
                loggedInCustomer.getSsn(),
                outboundIds,
                returnIds,
                ticketClass,
                specialMeal,
                tripType
        );

        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(
                    this,
                    result.getMessage() + "\nTotal Price: $" + result.getTotalPrice()
            );
            return;
        }

        if (result.isFlightFull()) {
            Integer instanceId = result.getFullInstanceId();
            String msg = result.getMessage() == null ? "No seats are available for this flight." : result.getMessage();
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "No seats are available for this flight. Would you like to join the waiting list?",
                    "Flight Full",
                    JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                if (instanceId == null) {
                    JOptionPane.showMessageDialog(this, msg);
                    return;
                }
                BookingResult waitlistResult = bookingService.joinWaitingList(loggedInCustomer.getSsn(), instanceId);
                JOptionPane.showMessageDialog(this, waitlistResult.getMessage());
            }
            return;
        }

        String msg = result.getMessage() == null ? "Booking failed." : result.getMessage();
        JOptionPane.showMessageDialog(this, msg);
    }

    private void preloadFromItineraries(
            String tripType,
            FlightItinerary outboundItinerary,
            FlightItinerary returnItinerary
    ) {
        if (outboundItinerary == null) {
            return;
        }

        FlightInstance outboundFirst = outboundItinerary.getFirstSegment();
        FlightInstance outboundLast = outboundItinerary.getLastSegment();
        depAirportField.setText(outboundFirst.getDepartureAirport());
        arrAirportField.setText(outboundLast.getArrivalAirport());
        depDateField.setText(extractDate(outboundFirst.getDepartureDateTime()));

        List<FlightInstance> rows = new ArrayList<>();
        rows.addAll(outboundItinerary.getSegments());
        if ("Round Trip".equals(tripType) && returnItinerary != null) {
            rows.addAll(returnItinerary.getSegments());
        }

        flightsModel.setRowCount(0);
        for (FlightInstance fi : rows) {
            boolean isReturn = returnItinerary != null && returnItinerary.getSegments().contains(fi);
            String direction = isReturn ? "Return" : "Outbound";
            String from = fi.getDepartureAirport();
            if (fi.getDepartureAirportName() != null) {
                from += " - " + fi.getDepartureAirportName();
            }
            String to = fi.getArrivalAirport();
            if (fi.getArrivalAirportName() != null) {
                to += " - " + fi.getArrivalAirportName();
            }

            flightsModel.addRow(new Object[]{
                    direction,
                    fi.getInstanceId(),
                    fi.getAirlineId(),
                    fi.getFlightNumber(),
                    from,
                    to,
                    fi.getDepartureDateTime(),
                    fi.getArrivalDateTime(),
                    fi.getStatus(),
                    fi.getAircraftModel()
            });
        }

        if (flightsModel.getRowCount() > 0) {
            flightsTable.setRowSelectionInterval(0, flightsModel.getRowCount() - 1);
        }
    }

    private String extractDate(String dateTime) {
        if (dateTime == null || dateTime.length() < 10) {
            return "";
        }
        return dateTime.substring(0, 10);
    }
}
