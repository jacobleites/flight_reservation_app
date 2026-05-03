package ui;

import dao.FlightInstanceDAO;
import java.awt.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Customer;
import models.FlightInstance;
import models.FlightItinerary;
import services.BookingService;

public class SearchFlightsPanel extends JPanel {
    private final MainFrame frame;
    private final Customer customer;
    private final String previousScreen;
    private final FlightInstanceDAO flightInstanceDAO;

    private JComboBox<String> tripTypeBox;
    private JTextField depAirportField;
    private JTextField arrAirportField;
    private JTextField depDateField;
    private JTextField returnDateField;
    private JCheckBox flexibleCheckBox;
    private JComboBox<String> sortByBox;
    private JComboBox<String> airlineFilterBox;

    private JTable outboundTable;
    private JTable returnTable;
    private DefaultTableModel outboundTableModel;
    private DefaultTableModel returnTableModel;

    private List<FlightItinerary> outboundItineraries;
    private List<FlightItinerary> returnItineraries;
    private List<FlightItinerary> displayedOutboundItineraries;
    private List<FlightItinerary> displayedReturnItineraries;

    public SearchFlightsPanel(MainFrame frame, Customer customer, String previousScreen) {
        this.frame = frame;
        this.customer = customer;
        this.previousScreen = previousScreen;
        this.flightInstanceDAO = new FlightInstanceDAO();

        this.outboundItineraries = new ArrayList<>();
        this.returnItineraries = new ArrayList<>();
        this.displayedOutboundItineraries = new ArrayList<>();
        this.displayedReturnItineraries = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        add(createFormPanel(), BorderLayout.NORTH);
        add(createResultsPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        updateReturnDateVisibility();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        tripTypeBox = new JComboBox<>(new String[]{"One Way", "Round Trip"});
        tripTypeBox.setSelectedItem("Round Trip");
        depAirportField = new JTextField();
        arrAirportField = new JTextField();
        depDateField = new JTextField();
        returnDateField = new JTextField();
        flexibleCheckBox = new JCheckBox("Flexible dates");
        sortByBox = new JComboBox<>(new String[]{
                "Price",
                "Take-off Time",
                "Landing Time",
                "Flight Duration",
                "Number of Stops"
        });
        airlineFilterBox = new JComboBox<>(new String[]{"All Airlines"});

        tripTypeBox.addActionListener(e -> updateReturnDateVisibility());
        sortByBox.addActionListener(e -> refreshDisplayedFlights());
        airlineFilterBox.addActionListener(e -> refreshDisplayedFlights());

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
        formPanel.add(new JLabel("Sort By:"));
        formPanel.add(sortByBox);
        formPanel.add(new JLabel("Airline Filter:"));
        formPanel.add(airlineFilterBox);

        return formPanel;
    }

    private JPanel createResultsPanel() {
        JPanel resultsPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        outboundTableModel = new DefaultTableModel(
                new Object[]{
                        "Airline",
                        "Flight #",
                        "From",
                        "To",
                        "Departure",
                        "Arrival",
                        "# Stops",
                        "Duration",
                        "Price",
                        "Seats",
                },
                0
        );

        returnTableModel = new DefaultTableModel(
                new Object[]{
                        "Airline",
                        "Flight #",
                        "From",
                        "To",
                        "Departure",
                        "Arrival",
                        "# Stops",
                        "Duration",
                        "Price",
                        "Seats",
                },
                0
        );

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
            returnItineraries.clear();
        }
    }

    private void searchFlights() {
        String depAirport = depAirportField.getText().trim().toUpperCase();
        String arrAirport = arrAirportField.getText().trim().toUpperCase();
        String depDate = depDateField.getText().trim();
        boolean isFlexible = flexibleCheckBox.isSelected();
        String tripType = (String) tripTypeBox.getSelectedItem();

        if (depAirport.isEmpty() || arrAirport.isEmpty() || depDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all departure details.");
            return;
        }

        outboundItineraries = flightInstanceDAO.searchItineraries(depAirport, arrAirport, depDate, isFlexible);

        if ("Round Trip".equals(tripType)) {
            String retDate = returnDateField.getText().trim();
            if (retDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a return date.");
                return;
            }
            returnItineraries = flightInstanceDAO.searchItineraries(arrAirport, depAirport, retDate, isFlexible);
        } else {
            returnItineraries.clear();
        }

        refreshAirlineFilterOptions();
        refreshDisplayedFlights();

        if (outboundItineraries.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No outbound flights found.");
        }

        if ("Round Trip".equals(tripType) && returnItineraries.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No return flights found.");
        }
    }

    private void refreshDisplayedFlights() {
        outboundTableModel.setRowCount(0);
        returnTableModel.setRowCount(0);

        displayedOutboundItineraries = displayFlights(outboundItineraries, outboundTableModel);
        if ("Round Trip".equals(tripTypeBox.getSelectedItem())) {
            displayedReturnItineraries = displayFlights(returnItineraries, returnTableModel);
        } else {
            displayedReturnItineraries = new ArrayList<>();
        }
    }

    private void refreshAirlineFilterOptions() {
        String selected = (String) airlineFilterBox.getSelectedItem();
        Set<String> airlines = new LinkedHashSet<>();
        airlines.add("All Airlines");

        collectAirlines(outboundItineraries, airlines);
        collectAirlines(returnItineraries, airlines);

        airlineFilterBox.removeAllItems();
        for (String airline : airlines) {
            airlineFilterBox.addItem(airline);
        }

        if (selected != null && airlines.contains(selected)) {
            airlineFilterBox.setSelectedItem(selected);
        } else {
            airlineFilterBox.setSelectedItem("All Airlines");
        }
    }

    private void collectAirlines(List<FlightItinerary> itineraries, Set<String> airlines) {
        for (FlightItinerary itinerary : itineraries) {
            for (FlightInstance segment : itinerary.getSegments()) {
                airlines.add(segment.getAirlineId());
            }
        }
    }

    private Comparator<FlightItinerary> getCurrentSortComparator() {
        String sortBy = (String) sortByBox.getSelectedItem();
        if ("Take-off Time".equals(sortBy)) {
            return Comparator.comparingLong(i -> parseDateTimeToMillis(i.getFirstSegment().getDepartureDateTime()));
        }
        if ("Landing Time".equals(sortBy)) {
            return Comparator.comparingLong(i -> parseDateTimeToMillis(i.getLastSegment().getArrivalDateTime()));
        }
        if ("Flight Duration".equals(sortBy)) {
            return Comparator.comparingLong(this::itineraryDurationMillis);
        }
        if ("Number of Stops".equals(sortBy)) {
            return Comparator.comparingInt(FlightItinerary::getStopsCount);
        }
        return Comparator.comparingDouble(i -> {
            double price = i.getTotalPrice();
            return price < 0 ? Double.MAX_VALUE : price;
        });
    }

    private long parseDateTimeToMillis(String dateTime) {
        return Timestamp.valueOf(dateTime).getTime();
    }

    private long itineraryDurationMillis(FlightItinerary itinerary) {
        long departure = parseDateTimeToMillis(itinerary.getFirstSegment().getDepartureDateTime());
        long arrival = parseDateTimeToMillis(itinerary.getLastSegment().getArrivalDateTime());
        return Math.max(0, arrival - departure);
    }

    private String formatDuration(FlightItinerary itinerary) {
        long durationMillis = itineraryDurationMillis(itinerary);
        long totalMinutes = Math.max(0, durationMillis / 60000);
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return String.format("%dh %02dm", hours, minutes);
    }

    private String formatStops(int stops) {
        if (stops == 0) {
            return "Direct";
        }
        if (stops == 1) {
            return "1 Layover";
        }
        return stops + " Layovers";
    }

    private List<FlightItinerary> displayFlights(List<FlightItinerary> itineraries, DefaultTableModel model) {
        List<FlightItinerary> sortedFlights = new ArrayList<>();
        String selectedAirline = (String) airlineFilterBox.getSelectedItem();

        for (FlightItinerary itinerary : itineraries) {
            if (matchesAirlineFilter(itinerary, selectedAirline)) {
                sortedFlights.add(itinerary);
            }
        }

        sortedFlights.sort(getCurrentSortComparator());

        for (FlightItinerary itinerary : sortedFlights) {
            FlightInstance first = itinerary.getFirstSegment();
            FlightInstance last = itinerary.getLastSegment();
            double price = itinerary.getTotalPrice();
            model.addRow(new Object[]{
                    first.getAirlineId(),
                    first.getFlightNumber(),
                    first.getDepartureAirport(),
                    last.getArrivalAirport(),
                    first.getDepartureDateTime(),
                    last.getArrivalDateTime(),
                    formatStops(itinerary.getStopsCount()),
                    formatDuration(itinerary),
                    price > 0 ? String.format("$%.2f", price) : "N/A",
                    itinerary.getSegments().stream().mapToInt(FlightInstance::getAvailableSeats).min().orElse(0)
            });
        }

        return sortedFlights;
    }

    private boolean matchesAirlineFilter(FlightItinerary itinerary, String selectedAirline) {
        if (selectedAirline == null || "All Airlines".equals(selectedAirline)) {
            return true;
        }

        for (FlightInstance segment : itinerary.getSegments()) {
            if (selectedAirline.equals(segment.getAirlineId())) {
                return true;
            }
        }
        return false;
    }

    private void bookSelectedFlights() {
        int outboundRow = outboundTable.getSelectedRow();
        if (outboundRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an outbound flight.");
            return;
        }

        FlightItinerary outbound = displayedOutboundItineraries.get(outboundRow);
        FlightInstance outboundInstance = outbound.getFirstSegment();
        FlightInstance returnInstance = null;

        if ("Round Trip".equals(tripTypeBox.getSelectedItem())) {
            int returnRow = returnTable.getSelectedRow();
            if (returnRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a return flight.");
                return;
            }
            FlightItinerary ret = displayedReturnItineraries.get(returnRow);
            returnInstance = ret.getFirstSegment();
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Confirm booking for " + customer.getFirstName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        BookingService service = new BookingService();
        if (service.bookFlight(customer, outboundInstance, returnInstance, "Economy")) {
            JOptionPane.showMessageDialog(this, "Reservation successfully created!");
            frame.showScreen(previousScreen);
        } else {
            JOptionPane.showMessageDialog(this, "Booking failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSegmentIds(FlightItinerary itinerary) {
        List<String> ids = new ArrayList<>();
        for (FlightInstance segment : itinerary.getSegments()) {
            ids.add(String.valueOf(segment.getInstanceId()));
        }
        return String.join(", ", ids);
    }
}
