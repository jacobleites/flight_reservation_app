package ui;

import dao.CustomerDAO;
import dao.EmployeeDAO;
import java.util.Scanner;
import models.Customer;
import models.Employee;

public class Main {
    private static final CustomerDAO customerDAO = new CustomerDAO();
    private static final EmployeeDAO employeeDAO = new EmployeeDAO();
    
    public static void main(String [] args){
        Scanner scanner = new Scanner(System.in);

        boolean running = true;

        while(running){
            System.out.println();
            System.out.println("Welcome to the flight reservation system");
            System.out.println();
            System.out.println("1. Customer Login");
            System.out.println("2. Employee Login");
            System.out.println("3. Create customer account");
            System.out.println("4. Exit");
            System.out.println("Choose an option");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": 
                    handleCustomerLogin(scanner);
                    break;
                case "2": 
                    handleEmployeeLogin(scanner);
                    break;
                case "3":
                    handleCreateCustomerAccount(scanner);
                    break;
                case "4":
                    running = false;
                    System.out.println("goodbye. :(");
                    break;
                default:
                    System.out.println("invalid option. Please try again.");
            
            }
        }

        scanner.close();
    }

    private static void handleCustomerLogin(Scanner scanner){
        System.out.println();
        System.out.println("Customer Login");
        System.out.println();
        
        System.out.println("Username: ");
        String username = scanner.nextLine();

        System.out.println("Password");
        String password = scanner.nextLine();

        Customer customer = customerDAO.login(username, password);

        if (customer != null) {
            System.out.println("Login Successful!");
            //CustomerMenu.show(customer, scanner); // implement
        }
        else {
            System.out.println("Invalid username or password, please try again.");
        }
    }

    private static void handleEmployeeLogin(Scanner scanner){
        System.out.println();
        System.out.println("Employee Login");
        
        System.out.println("Username: ");
        String username = scanner.nextLine();
        
        System.out.println("Password: ");
        String password = scanner.nextLine();

        Employee employee = employeeDAO.login(username, password);
        if (employee != null){
            System.out.println("Login successful");
            //EmployeeMenu.show(employee, scanner); // implement
        } 
        else {
            System.out.println("Invalid username or password, please try again.");
        }
    }

    private static void handleCreateCustomerAccount(Scanner scanner){
        System.out.println("So you don't have an account yet... All good!");

        System.out.println("Please provide the information, that follows: ");
        System.out.println();

        System.out.println("First name: e.g; John");
        String firstName = scanner.nextLine();
        System.out.println("Last Name: e.g; Smith");
        String lastName = scanner.nextLine();
        System.out.println("Email: ");
        String email = scanner.nextLine();
        System.out.println("Phone number: e.g; 123-456-7890");
        String phone = scanner.nextLine();
        System.out.println("Date of birth: e.g; YYYY-MM-DD");
        String dob = scanner.nextLine();
        System.out.println("Gender: ");
        String gender = scanner.nextLine();
        System.out.println("SSN: e.g; 123-45-6879  ");
        String ssn = scanner.nextLine();
        System.out.println("Username: ");
        String username = scanner.nextLine();
        System.out.println("Password: ");
        String password = scanner.nextLine();

        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = new Customer(ssn, email, gender, dob, firstName, lastName, phone, username, password);
        if (customerDAO.createCustomer(customer)){
            System.out.println("Account created successfully!");
            //CustomerMenu.show(scanner); // implement
        }
        else
            System.out.println("Failed to create account, exiting...");

    }


}
