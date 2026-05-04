package ui;

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

    public CustomerDashboardPanel(MainFrame frame, Customer customer) {
        this.frame = frame;
        this.customer = customer;
        this.bookingService = new BookingService();

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

        StringBuilder sb = new StringBuilder();
        for (WaitingLineEntry entry : notifications) {
            sb.append("A seat may be available for flight instance ")
                    .append(entry.getInstanceId())
                    .append(". You may now try booking this flight.")
                    .append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Waitlist Notifications", JOptionPane.INFORMATION_MESSAGE);
    }
}
