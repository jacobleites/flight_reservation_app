package ui;

import java.awt.*;
import javax.swing.*;
import models.Customer;
import models.Employee;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("Flight Reservation System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new MainMenuPanel(this), "MAIN_MENU");
        mainPanel.add(new LoginUserPanel(this), "LOGIN");
        mainPanel.add(new CreateCustomerPanel(this), "CREATE_CUSTOMER");

        add(mainPanel);

        showScreen("MAIN_MENU");
    }

    public void showScreen(String screenName) {
        cardLayout.show(mainPanel, screenName);
    }

    public void showCustomerDashboard(Customer customer) {
        CustomerDashboardPanel panel = new CustomerDashboardPanel(this, customer);

        mainPanel.add(panel, "CUSTOMER_DASHBOARD");
        showScreen("CUSTOMER_DASHBOARD");
    }

    public void showEmployeeDashboard(Employee employee) {

    // Check the role from the Employee object
        if ("Admin".equalsIgnoreCase(employee.getRole())) {
            AdminDashboardPanel panel = new AdminDashboardPanel(this, employee);
            mainPanel.add(panel, "ADMIN_DASHBOARD");
            showScreen("ADMIN_DASHBOARD");            

        } else {
            CustomerRepDashboardPanel panel = new CustomerRepDashboardPanel(this, employee);
            mainPanel.add(panel, "CUSTOMER_REP_DASHBOARD");
            showScreen("CUSTOMER_REP_DASHBOARD");

        }
    }

    // Standard entry from Customer Dashboard
    public void showSearchFlightsScreen(Customer customer) {
        showSearchFlightsScreen(customer, "CUSTOMER_DASHBOARD");
    }

    // New overloaded method to specify navigation origin
    public void showSearchFlightsScreen(Customer customer, String origin) {
        SearchFlightsPanel panel = new SearchFlightsPanel(this, customer, origin);
        mainPanel.add(panel, "SEARCH_FLIGHTS");
        showScreen("SEARCH_FLIGHTS");
    }

    public void showManageReservationsScreen(Employee employee) {
        ManageReservationsPanel panel = new ManageReservationsPanel(this, employee);
        mainPanel.add(panel, "MANAGE_RESERVATIONS");
       showScreen("MANAGE_RESERVATIONS"); 
    }

    public void showCustomerQuestionsScreen(Employee employee) {
        CustomerQuestionsPanel panel = new CustomerQuestionsPanel(this, employee);
        mainPanel.add(panel, "CUSTOMER_QUESTIONS");
        showScreen("CUSTOMER_QUESTIONS");
    }

    public void showViewReservationsScreen(Customer customer) {
        ViewReservationsPanel panel = new ViewReservationsPanel(this, customer);
        mainPanel.add(panel, "VIEW_RESERVATIONS");
        showScreen("VIEW_RESERVATIONS");
    }

    public void showCustomerChatScreen(Customer customer) {
        CustomerChatPanel panel = new CustomerChatPanel(this, customer);
        mainPanel.add(panel, "CUSTOMER_CHAT");
        showScreen("CUSTOMER_CHAT");
    }

    
}
