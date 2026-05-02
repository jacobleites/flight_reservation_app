package ui;

import dao.CustomerDAO;
import dao.EmployeeDAO;
import models.Customer;
import models.Employee;

import javax.swing.*;
import java.awt.*;

public class LoginUserPanel extends JPanel {
    private final MainFrame frame;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    private JComboBox<String> userTypeBox;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginUserPanel(MainFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        add(titleLabel, BorderLayout.NORTH);

        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        userTypeBox = new JComboBox<>(new String[]{"Customer", "Employee"});
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        formPanel.add(new JLabel("User Type:"));
        formPanel.add(userTypeBox);

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(formPanel);

        return wrapper;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        loginButton.addActionListener(e -> handleLogin());
        backButton.addActionListener(e -> frame.showScreen("MAIN_MENU"));

        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    private void handleLogin() {
        String userType = (String) userTypeBox.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both username and password.",
                    "Missing Login Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (userType.equals("Customer")) {
            handleCustomerLogin(username, password);
        } else {
            handleEmployeeLogin(username, password);
        }
    }

    private void handleCustomerLogin(String username, String password) {
        Customer customer = customerDAO.login(username, password);

        if (customer != null) {
            JOptionPane.showMessageDialog(this, "Customer login successful!");

            frame.showCustomerDashboard(customer);

            clearFields();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid customer username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleEmployeeLogin(String username, String password) {
        Employee employee = employeeDAO.login(username, password);

        if (employee != null) {
            JOptionPane.showMessageDialog(this, "Employee login successful!");

            // This assumes MainFrame has this method.
            frame.showEmployeeDashboard(employee);

            clearFields();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid employee username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}