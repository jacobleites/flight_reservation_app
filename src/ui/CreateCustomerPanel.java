package ui;

import dao.CustomerDAO;
import java.awt.*;
import javax.swing.*;
import models.Customer;

public class CreateCustomerPanel extends JPanel {
    private final MainFrame frame;
    private final CustomerDAO customerDAO = new CustomerDAO();

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField dobField;
    private JTextField genderField;
    private JTextField ssnField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public CreateCustomerPanel(MainFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Create Customer Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        add(titleLabel, BorderLayout.NORTH);

        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));

        firstNameField = new JTextField();
        lastNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        dobField = new JTextField();
        genderField = new JTextField();
        ssnField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Phone Number (XXX-XXX-XXXX):"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        formPanel.add(dobField);

        formPanel.add(new JLabel("Gender:"));
        formPanel.add(genderField);

        formPanel.add(new JLabel("SSN (XXX-XX-XXXX):"));
        formPanel.add(ssnField);

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

        JButton createButton = new JButton("Create Account");
        JButton backButton = new JButton("Back");

        createButton.addActionListener(e -> handleCreateCustomer());
        backButton.addActionListener(e -> frame.showScreen("MAIN_MENU"));

        buttonPanel.add(createButton);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    private void handleCreateCustomer() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String dob = dobField.getText().trim();
        String gender = genderField.getText().trim();
        String ssn = ssnField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (
                firstName.isEmpty() ||
                lastName.isEmpty() ||
                email.isEmpty() ||
                phone.isEmpty() ||
                dob.isEmpty() ||
                gender.isEmpty() ||
                ssn.isEmpty() ||
                username.isEmpty() ||
                password.isEmpty()
        ) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please fill out all fields.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Customer customer = new Customer(
                ssn,
                email,
                gender,
                dob,
                firstName,
                lastName,
                phone,
                username,
                password
        );

        boolean created = customerDAO.createCustomer(customer);

        if (created) {
            JOptionPane.showMessageDialog(this, "Account created successfully!");

            clearFields();

            // Send new customer directly into customer dashboard.
            frame.showCustomerDashboard(customer);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to create account. Username, SSN, or email may already exist.",
                    "Account Creation Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        dobField.setText("");
        genderField.setText("");
        ssnField.setText("");
        usernameField.setText("");
        passwordField.setText("");
    }
}