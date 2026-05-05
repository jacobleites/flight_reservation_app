package ui;

import dao.WaitingLineDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import models.Customer;
import models.WaitingLineEntry;
import services.BookingService;

public class CustomerDashboardPanel extends JPanel {
    private final MainFrame frame;
    private final Customer customer;
    private final BookingService bookingService;
    private final WaitingLineDAO waitingLineDAO;

    public CustomerDashboardPanel(MainFrame frame, Customer customer) {
        this.frame = frame;
        this.customer = customer;
        this.bookingService = new BookingService();
        this.waitingLineDAO = new WaitingLineDAO();

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(
                "Welcome, " + customer.getFirstName(),
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));

        JButton searchFlightsButton = new JButton("Search Flights");
        JButton bookFlightButton = new JButton("Book Flight");
        JButton waitlistNotificationsButton = new JButton("View Waitlist Notifications");
        JButton viewReservationsButton = new JButton("View/Cancel My Reservations");
        JButton askQuestionButton = new JButton("Ask Customer Service Question");
        JButton logoutButton = new JButton("Logout");

        searchFlightsButton.addActionListener(e -> {
            frame.showSearchFlightsScreen(customer);
        });
        bookFlightButton.addActionListener(e -> {
            frame.showBookFlightScreen(customer);
        });
        waitlistNotificationsButton.addActionListener(e -> showWaitlistNotifications());

        viewReservationsButton.addActionListener(e -> {
            frame.showViewReservationsScreen(customer);
        });
        askQuestionButton.addActionListener(e -> {
            frame.showCustomerChatScreen(customer);
        });

        logoutButton.addActionListener(e -> {
            frame.showScreen("MAIN_MENU");
        });

        buttonPanel.add(searchFlightsButton);
        buttonPanel.add(bookFlightButton);
        buttonPanel.add(waitlistNotificationsButton);
        buttonPanel.add(viewReservationsButton);
        buttonPanel.add(askQuestionButton);
        buttonPanel.add(logoutButton);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(buttonPanel);

        add(titleLabel, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);
    }

    private void showWaitlistNotifications() {
        List<WaitingLineEntry> notifications = bookingService.getWaitlistNotificationsForCustomer(customer.getSsn());
        if (notifications.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no waitlist notifications.");
            return;
        }

        List<Object[]> notificationDetails = waitingLineDAO.getNotificationDetailsForCustomer(customer.getSsn());
        if (notificationDetails.isEmpty()) {
            StringBuilder fallback = new StringBuilder();
            for (WaitingLineEntry entry : notifications) {
                fallback.append("A seat may be available for a flight you requested.\n")
                        .append("Instance ID: ").append(entry.getInstanceId()).append("\n")
                        .append("You may now try booking this flight.\n\n");
            }
            JOptionPane.showMessageDialog(this, fallback.toString(), "Waitlist Notifications", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Object[] row : notificationDetails) {
            int instanceId = (Integer) row[0];
            String airlineId = (String) row[1];
            Integer flightNum = row[2] == null ? null : ((Number) row[2]).intValue();
            String depDateTime = (String) row[3];
            String arrDateTime = (String) row[4];
            String depAirport = (String) row[5];
            String arrAirport = (String) row[6];
            String depAirportName = (String) row[7];
            String arrAirportName = (String) row[8];
            String aircraftModel = (String) row[9];

            sb.append("A seat may be available for a flight you requested.\n");
            sb.append("Instance ID: ").append(instanceId).append("\n");
            if (airlineId != null && flightNum != null) {
                sb.append("Flight: ").append(airlineId).append(" ").append(flightNum).append("\n");
            }
            if (depAirport != null || arrAirport != null) {
                sb.append("Route: ")
                        .append(formatAirport(depAirportName, depAirport))
                        .append(" -> ")
                        .append(formatAirport(arrAirportName, arrAirport))
                        .append("\n");
            }
            if (depDateTime != null) {
                sb.append("Departure: ").append(depDateTime).append("\n");
            }
            if (arrDateTime != null) {
                sb.append("Arrival: ").append(arrDateTime).append("\n");
            }
            if (aircraftModel != null && !aircraftModel.trim().isEmpty()) {
                sb.append("Aircraft: ").append(aircraftModel).append("\n");
            }
            sb.append("You may now try booking this flight.\n\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Waitlist Notifications", JOptionPane.INFORMATION_MESSAGE);
    }

    private String formatAirport(String airportName, String airportId) {
        if (airportName == null || airportName.trim().isEmpty()) {
            return airportId;
        }
        if (airportId == null || airportId.trim().isEmpty()) {
            return airportName;
        }
        return airportName + " (" + airportId + ")";
    }
}
