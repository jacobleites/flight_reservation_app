package ui;

import dao.CustomerDAO;
import dao.EmployeeDAO;
import java.util.Scanner;

import com.mysql.cj.protocol.x.SyncFlushDeflaterOutputStream;
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
                    handleCreateCustomerAccount;
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
            CustomerMenu.show(customer, scanner); // implement
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
            EmployeeMenu.show(employee, scanner);
        } 
        else {
            System.out.println("Invalid username or password, please try again.");
        }
    }

    private static void handleCreateCustomerAccount(Scanner scanner){
        System.out.println("So you don't have an account yet... All good!");

        

    }


}
